package com.mgt.controller;

import com.mgt.model.Order;
import com.mgt.service.EmailNotificationService;
import com.mgt.service.OrderService;
import com.mgt.service.SslCommerzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class PaymentController {

    @Autowired private SslCommerzService sslCommerzService;
    @Autowired private OrderService orderService;
    @Autowired private EmailNotificationService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Frontend থেকে call হবে — order place করার পর "Pay Online" বাটনে।
     * Body: { "orderCode": "RURAL-20260614-0001" }
     * Response: { success, gatewayUrl }  — frontend এই gatewayUrl-এ redirect করবে
     */
    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiate(@RequestBody Map<String, String> body) {
        String orderCode = body.get("orderCode");
        Map<String, Object> result = new HashMap<>();

        if (orderCode == null || orderCode.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "orderCode is required.");
            return ResponseEntity.badRequest().body(result);
        }

        Order order = orderService.getByOrderCode(orderCode);
        if (order == null) {
            result.put("success", false);
            result.put("message", "Order not found: " + orderCode);
            return ResponseEntity.status(404).body(result);
        }

        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            result.put("success", false);
            result.put("message", "This order has already been paid.");
            return ResponseEntity.badRequest().body(result);
        }

        Map<String, Object> session = sslCommerzService.initiatePayment(order, "/payment/success");

        if (Boolean.TRUE.equals(session.get("success"))) {
            result.put("success", true);
            result.put("gatewayUrl", session.get("gatewayUrl"));
        } else {
            result.put("success", false);
            result.put("message", session.get("message"));
        }
        return ResponseEntity.ok(result);
    }

    /**
     * SSLCommerz POST করে user-এর browser-কে এখানে redirect করায় payment success হলে.
     * আমরা val_id দিয়ে server-to-server validate করি, তারপর frontend success page-এ redirect করি.
     */
    @PostMapping("/success")
    public ResponseEntity<Void> paymentSuccess(
            @RequestParam(required = false) String val_id,
            @RequestParam(required = false) String tran_id,
            @RequestParam(required = false) String amount,
            @RequestParam String orderCode) {

        return handleCallback(val_id, tran_id != null ? tran_id : orderCode, amount, orderCode, "success");
    }

    @PostMapping("/fail")
    public ResponseEntity<Void> paymentFail(@RequestParam String orderCode) {
        return redirectToFrontend(orderCode, "fail");
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> paymentCancel(@RequestParam String orderCode) {
        return redirectToFrontend(orderCode, "cancel");
    }

    /**
     * SSLCommerz IPN (Instant Payment Notification) — server-to-server, browser involved না.
     * এটা backup confirmation হিসেবে কাজ করে, success_url miss হলেও payment status update হয়।
     */
    @PostMapping("/ipn")
    public ResponseEntity<String> ipn(
            @RequestParam(required = false) String val_id,
            @RequestParam(required = false) String tran_id,
            @RequestParam(required = false) String amount,
            @RequestParam(required = false) String status) {

        if (val_id == null || tran_id == null) {
            return ResponseEntity.badRequest().body("Missing parameters");
        }

        Order order = orderService.getByOrderCode(tran_id);
        if (order == null) {
            return ResponseEntity.status(404).body("Order not found");
        }

        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            return ResponseEntity.ok("Already processed");
        }

        double expectedAmount = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
        boolean valid = sslCommerzService.validateTransaction(val_id, tran_id, expectedAmount);

        if (valid) {
            orderService.markPaymentPaid(order.getId(), val_id);
        }

        return ResponseEntity.ok("OK");
    }

    // ── Helpers ─────────────────────────────────────────────

    private ResponseEntity<Void> handleCallback(String valId, String tranId, String amount, String orderCode, String outcome) {
        Order order = orderService.getByOrderCode(orderCode);

        if (order == null) {
            return redirectToFrontend(orderCode, "fail");
        }

        if ("success".equals(outcome) && valId != null) {
            double expectedAmount = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
            boolean valid = sslCommerzService.validateTransaction(valId, orderCode, expectedAmount);

            if (valid) {
                if (!"PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                    orderService.markPaymentPaid(order.getId(), valId);
                }
                return redirectToFrontend(orderCode, "success");
            } else {
                return redirectToFrontend(orderCode, "fail");
            }
        }

        return redirectToFrontend(orderCode, outcome);
    }

    /** Browser-কে frontend-এর payment result page-এ পাঠানো */
    private ResponseEntity<Void> redirectToFrontend(String orderCode, String outcome) {
        String url = frontendUrl + "/payment/" + outcome + "?orderCode=" + orderCode;
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
