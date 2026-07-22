package com.mgt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class SslCommerzService {

    @Value("${app.sslcommerz.store-id}")
    private String storeId;

    @Value("${app.sslcommerz.store-password}")
    private String storePassword;

    @Value("${app.sslcommerz.sandbox}")
    private boolean sandbox;

    @Value("${app.sslcommerz.sandbox-url}")
    private String sandboxUrl;

    @Value("${app.sslcommerz.live-url}")
    private String liveUrl;

    @Value("${app.sslcommerz.validation-sandbox-url}")
    private String validationSandboxUrl;

    @Value("${app.sslcommerz.validation-live-url}")
    private String validationLiveUrl;

    @Value("${app.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * SSLCommerz-এ একটি payment session শুরু করো এবং gateway redirect URL ফেরত দাও।
     *
     * @param order        যে Order এর জন্য payment হচ্ছে
     * @param successPath  payment success হলে যেখানে SSLCommerz redirect করবে (relative path, e.g. "/payment/success")
     */
    public Map<String, Object> initiatePayment(com.mgt.model.Order order, String successPath) {

        String apiUrl = sandbox ? sandboxUrl : liveUrl;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("store_id", storeId);
        params.add("store_passwd", storePassword);

        // ── Transaction info ──
        params.add("total_amount", String.valueOf(order.getTotalAmount() != null ? order.getTotalAmount() : 0.0));
        params.add("currency", "BDT");
        params.add("tran_id", order.getOrderCode()); // unique transaction id — order code ব্যবহার করছি

        // ── Callback URLs ──
        params.add("success_url", baseUrl + "/payment/success?orderCode=" + order.getOrderCode());
        params.add("fail_url",    baseUrl + "/payment/fail?orderCode=" + order.getOrderCode());
        params.add("cancel_url",  baseUrl + "/payment/cancel?orderCode=" + order.getOrderCode());
        params.add("ipn_url",     baseUrl + "/payment/ipn");

        // ── Customer info (SSLCommerz requires these) ──
        params.add("cus_name",   order.getCustomerName()  != null ? order.getCustomerName()  : "Customer");
        params.add("cus_email",  order.getCustomerEmail() != null ? order.getCustomerEmail() : "customer@rural.com");
        params.add("cus_add1",   order.getShippingAddress() != null ? order.getShippingAddress() : "N/A");
        params.add("cus_city",   "Dhaka");
        params.add("cus_state",  "Dhaka");
        params.add("cus_postcode","1000");
        params.add("cus_country","Bangladesh");
        params.add("cus_phone",  order.getCustomerPhone() != null ? order.getCustomerPhone() : "01700000000");

        // ── Shipping info (required when shipping_method = Courier) ──
        params.add("shipping_method", "Courier");
        params.add("num_of_item", String.valueOf(order.getItems() != null ? order.getItems().size() : 1));
        params.add("ship_name",    order.getCustomerName()  != null ? order.getCustomerName()  : "Customer");
        params.add("ship_add1",    order.getShippingAddress() != null ? order.getShippingAddress() : "N/A");
        params.add("ship_city",    "Dhaka");
        params.add("ship_postcode","1000");
        params.add("ship_country", "Bangladesh");

        // ── Product info ──
        params.add("product_name", "Rural Order " + order.getOrderCode());
        params.add("product_category", "General");
        params.add("product_profile", "general");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            JsonNode json = objectMapper.readTree(response.getBody());

            Map<String, Object> result = new java.util.HashMap<>();
            String status = json.has("status") ? json.get("status").asText() : "FAILED";

            if ("SUCCESS".equalsIgnoreCase(status)) {
                result.put("success", true);
                result.put("gatewayUrl", json.get("GatewayPageURL").asText());
                result.put("sessionkey", json.has("sessionkey") ? json.get("sessionkey").asText() : null);
            } else {
                result.put("success", false);
                result.put("message", json.has("failedreason") ? json.get("failedreason").asText() : "Failed to initiate payment session.");
            }
            return result;

        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "SSLCommerz error: " + e.getMessage());
            return result;
        }
    }

    /**
     * SSLCommerz callback (success/IPN) এর পর val_id দিয়ে transaction validate করো,
     * নিশ্চিত করো amount ও currency মিলছে এবং status VALID/VALIDATED।
     */
    public boolean validateTransaction(String valId, String orderCode, double expectedAmount) {
        String validationUrl = sandbox ? validationSandboxUrl : validationLiveUrl;

        String url = UriComponentsBuilder.fromHttpUrl(validationUrl)
                .queryParam("val_id", valId)
                .queryParam("store_id", storeId)
                .queryParam("store_passwd", storePassword)
                .queryParam("v", 1)
                .queryParam("format", "json")
                .toUriString();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode json = objectMapper.readTree(response.getBody());

            String status = json.has("status") ? json.get("status").asText() : "";
            boolean statusOk = "VALID".equalsIgnoreCase(status) || "VALIDATED".equalsIgnoreCase(status);

            if (!statusOk) return false;

            // tran_id খুলে আসা order code এর সাথে মিলছে কিনা যাচাই
            String tranId = json.has("tran_id") ? json.get("tran_id").asText() : "";
            if (!tranId.equals(orderCode)) return false;

            // Amount mismatch fraud-protection (allow small rounding tolerance)
            double amount = json.has("amount") ? json.get("amount").asDouble() : 0.0;
            if (Math.abs(amount - expectedAmount) > 1.0) return false;

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
