package com.mgt.service;

import com.mgt.dao.AddProductDAO;
import com.mgt.dao.OrderDAO;
import com.mgt.model.AddProduct;
import com.mgt.model.Order;
import com.mgt.model.OrderItem;
import com.mgt.model.Seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    OrderDAO orderDAO;

    @Autowired
    AddProductDAO addProductDAO;

    @Autowired
    CommissionService commissionService;

    @Autowired
    EmailNotificationService emailService;

    // ================= CREATE ORDER =================
    public Order create(Order order) {

        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");
        order.setPaymentStatus("UNPAID");

        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randPart = String.format("%04d", (int)(Math.random() * 9000) + 1000);
        order.setOrderCode("RURAL-" + datePart + "-" + randPart);
        order.setInvoiceNo("INV-" + datePart + "-" + randPart);
        order.setScanToken(UUID.randomUUID().toString().replace("-", ""));

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
                item.setTotalPrice(item.getUnitPrice() * item.getQuantity());
            }
        }

        double subtotal = order.getItems() != null
                ? order.getItems().stream().mapToDouble(OrderItem::getTotalPrice).sum()
                : 0.0;

        order.setSubtotal(subtotal);

        double discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : 0.0;
        order.setTotalAmount(subtotal - discount);

        orderDAO.save(order);

        generateCommissions(order);

        // EMAIL: order confirmation
        if (order.getCustomerEmail() != null) {
            String name = order.getCustomerName() != null
                    ? order.getCustomerName()
                    : "Customer";

            emailService.sendOrderConfirmation(
                    order.getCustomerEmail(),
                    name,
                    order.getOrderCode(),
                    order.getTotalAmount() != null ? order.getTotalAmount() : 0.0
            );
        }

        return order;
    }

    // ================= COMMISSION =================
    private void generateCommissions(Order order) {

        if (order.getItems() == null) return;

        Map<Long, Double> sellerGrossMap = new LinkedHashMap<>();
        Map<Long, Seller> sellerMap = new LinkedHashMap<>();

        for (OrderItem item : order.getItems()) {

            AddProduct product = addProductDAO.getById((int) item.getProductId());
            if (product == null || product.getSeller() == null) continue;

            Seller seller = product.getSeller();
            Long sellerId = seller.getId();

            double itemTotal = item.getTotalPrice() != null ? item.getTotalPrice() : 0.0;

            sellerGrossMap.put(
                    sellerId,
                    sellerGrossMap.getOrDefault(sellerId, 0.0) + itemTotal
            );

            sellerMap.put(sellerId, seller);
        }

        for (Map.Entry<Long, Double> entry : sellerGrossMap.entrySet()) {
            commissionService.create(order, sellerMap.get(entry.getKey()), entry.getValue());
        }
    }

    // ================= UPDATE STATUS =================
    public void updateStatus(long id, String status) {

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Order status is required");
        }

        status = status.trim().toUpperCase();

        Order order = orderDAO.getById(id);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + id);
        }

        String currentStatus = order.getStatus() != null
                ? order.getStatus().trim().toUpperCase()
                : "PENDING";

        validateStatusTransition(currentStatus, status);

        // ===== DELIVERED FLOW =====
        if ("DELIVERED".equals(status)) {

            if (!"DELIVERED".equals(currentStatus)) {
                reduceStockForOrder(order);
            }

            order.setDeliveredDate(LocalDate.now());
            order.setStatus(status);
            if (!"COD".equalsIgnoreCase(order.getPaymentMethod())) {
                order.setPaymentStatus("PAID");
            } else if (order.getCodCollectedAt() == null) {
                order.setPaymentStatus("COD_PENDING");
            }

            orderDAO.update(order);
            if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) commissionService.markOrderPayable(order);
            else commissionService.markOrderOnHold(order);

            if (order.getCustomerEmail() != null) {

                String name = order.getCustomerName() != null
                        ? order.getCustomerName()
                        : "Customer";

                emailService.sendOrderDelivered(
                        order.getCustomerEmail(),
                        name,
                        order.getOrderCode()
                );
            }

            return;
        }

        // ===== NORMAL UPDATE =====
        orderDAO.updateStatus(id, status);

        // ===== SHIPPED EMAIL =====
        if ("SHIPPED".equals(status) && order.getCustomerEmail() != null) {

            String name = order.getCustomerName() != null
                    ? order.getCustomerName()
                    : "Customer";

            emailService.sendOrderShipped(
                    order.getCustomerEmail(),
                    name,
                    order.getOrderCode(),
                    "N/A",
                    "Courier"
            );
        }
    }

    public void scanUpdate(long id, String status, String note, String token) {
        Order order = orderDAO.getById(id);
        if (order == null) throw new IllegalArgumentException("Order not found: " + id);
        if (order.getScanToken() != null && !order.getScanToken().isEmpty()
                && (token == null || !order.getScanToken().equals(token))) {
            throw new IllegalArgumentException("Invalid QR scan token");
        }
        if ("COD_COLLECTED".equalsIgnoreCase(status)) {
            markCodCollected(id);
            return;
        }
        updateStatus(id, status);
    }

    public void markCodCollected(long id) {
        Order order = orderDAO.getById(id);
        if (order == null) throw new IllegalArgumentException("Order not found: " + id);
        if (!"COD".equalsIgnoreCase(order.getPaymentMethod())) {
            throw new IllegalArgumentException("This is not a cash on delivery order");
        }
        if (!"DELIVERED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException("COD can be collected only after delivery");
        }
        order.setCodCollectedAt(LocalDateTime.now());
        order.setPaymentStatus("COD_COLLECTED");
        orderDAO.update(order);
    }

    public void verifyCodPayment(long id) {
        Order order = orderDAO.getById(id);
        if (order == null) throw new IllegalArgumentException("Order not found: " + id);
        if (!"COD_COLLECTED".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new IllegalArgumentException("COD collection must be recorded first");
        }
        order.setCodVerifiedAt(LocalDateTime.now());
        order.setPaymentStatus("PAID");
        orderDAO.update(order);
        commissionService.markOrderPayable(order);
    }

    // ================= STOCK REDUCE =================
    private void reduceStockForOrder(Order order) {

        if (order.getItems() == null) return;

        for (OrderItem item : order.getItems()) {

            AddProduct product = addProductDAO.getById((int) item.getProductId());
            if (product == null) continue;

            int currentStock = product.getStock() != null ? product.getStock() : 0;
            int quantity = item.getQuantity();

            product.setStock(Math.max(0, currentStock - quantity));
            addProductDAO.update(product);
        }
    }

    // ================= VALIDATION =================
    private void validateStatusTransition(String currentStatus, String nextStatus) {

        if (currentStatus.equals(nextStatus)) return;

        if ("PENDING".equals(currentStatus)
                && (nextStatus.equals("ACCEPTED") || nextStatus.equals("PROCESSING") || nextStatus.equals("CANCELLED")))
            return;

        if ("ACCEPTED".equals(currentStatus)
                && (nextStatus.equals("PROCESSING") || nextStatus.equals("CANCELLED")))
            return;

        if ("PROCESSING".equals(currentStatus)
                && (nextStatus.equals("SHIPPED") || nextStatus.equals("CANCELLED")))
            return;

        if ("SHIPPED".equals(currentStatus) && nextStatus.equals("DELIVERED"))
            return;

        throw new IllegalArgumentException("Invalid status flow: " + currentStatus + " → " + nextStatus);
    }

    // ================= OTHER METHODS =================
    public List<Order> getAll() { return orderDAO.getAll(); }

    public Order getById(long id) { return orderDAO.getById(id); }

    public Order getByOrderCode(String code) { return orderDAO.getByOrderCode(code); }

    public List<Order> getByStatus(String status) { return orderDAO.getByStatus(status); }

    public List<Order> getBySellerId(long sellerId) {
        return filterOrdersForSeller(orderDAO.getBySellerId(sellerId), sellerId);
    }

    public List<Order> search(String keyword) { return orderDAO.search(keyword); }

    public void update(Order order) { orderDAO.update(order); }

    public void delete(long id) { orderDAO.delete(id); }

    // ================= PAYMENT (SSLCommerz) =================
    /**
     * Online payment success হলে call হয় — paymentStatus = PAID set করো
     * এবং customer-কে confirmation email পাঠাও।
     */
    public void markPaymentPaid(long orderId, String transactionId) {
        Order order = orderDAO.getById(orderId);
        if (order == null) return;

        orderDAO.updatePaymentStatus(orderId, "PAID");

        if (order.getCustomerEmail() != null) {
            String name = order.getCustomerName() != null ? order.getCustomerName() : "Customer";
            emailService.sendOrderConfirmation(
                    order.getCustomerEmail(),
                    name,
                    order.getOrderCode(),
                    order.getTotalAmount() != null ? order.getTotalAmount() : 0.0
            );
        }
    }

    // ================= SELLER FILTER =================
    private List<Order> filterOrdersForSeller(List<Order> orders, long sellerId) {

        List<Order> filtered = new ArrayList<>();
        if (orders == null) return filtered;

        for (Order order : orders) {

            if (order.getItems() == null) continue;

            List<OrderItem> sellerItems = new ArrayList<>();
            double total = 0.0;

            for (OrderItem item : order.getItems()) {

                AddProduct product = addProductDAO.getById((int) item.getProductId());

                if (product != null
                        && product.getSeller() != null
                        && product.getSeller().getId() == sellerId) {

                    sellerItems.add(item);
                    total += item.getTotalPrice() != null ? item.getTotalPrice() : 0.0;
                }
            }

            if (!sellerItems.isEmpty()) {
                order.setItems(sellerItems);
                order.setSubtotal(total);
                order.setTotalAmount(total);
                filtered.add(order);
            }
        }

        return filtered;
    }

    public Map<String, Object> getAnalytics() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalOrders", orderDAO.countTotal());
        data.put("pendingOrders", orderDAO.countByStatus("PENDING"));
        data.put("acceptedOrders", orderDAO.countByStatus("ACCEPTED"));
        data.put("processingOrders", orderDAO.countByStatus("PROCESSING"));
        data.put("shippedOrders", orderDAO.countByStatus("SHIPPED"));
        data.put("deliveredOrders", orderDAO.countByStatus("DELIVERED"));
        data.put("cancelledOrders", orderDAO.countByStatus("CANCELLED"));
        data.put("refundedOrders", orderDAO.countByStatus("REFUNDED"));

        List<Order> allOrders = orderDAO.getAll();
        double totalRevenue = 0.0;
        for (Order o : allOrders) {
            if ("DELIVERED".equalsIgnoreCase(o.getStatus()) && o.getTotalAmount() != null) {
                totalRevenue += o.getTotalAmount();
            }
        }
        data.put("totalRevenue", totalRevenue);

        return data;
    }

    // ================= SELLER DASHBOARD =================
    public Map<String, Object> getSellerDashboard(long sellerId) {
        List<Order> orders = filterOrdersForSeller(orderDAO.getBySellerId(sellerId), sellerId);

        Map<String, Object> data = new HashMap<>();

        long totalOrders = orders.size();
        long pendingCount = 0, processingCount = 0, shippedCount = 0, deliveredCount = 0, cancelledCount = 0;
        double totalSales = 0.0;
        double deliveredSales = 0.0;

        for (Order o : orders) {
            String status = o.getStatus() != null ? o.getStatus().toUpperCase() : "";
            double amount = o.getTotalAmount() != null ? o.getTotalAmount() : 0.0;
            totalSales += amount;

            switch (status) {
                case "PENDING":    pendingCount++;    break;
                case "PROCESSING":
                case "ACCEPTED":   processingCount++; break;
                case "SHIPPED":    shippedCount++;    break;
                case "DELIVERED":  deliveredCount++; deliveredSales += amount; break;
                case "CANCELLED":  cancelledCount++;  break;
                default: break;
            }
        }

        data.put("totalOrders", totalOrders);
        data.put("pendingOrders", pendingCount);
        data.put("processingOrders", processingCount);
        data.put("shippedOrders", shippedCount);
        data.put("deliveredOrders", deliveredCount);
        data.put("cancelledOrders", cancelledCount);
        data.put("totalSales", totalSales);
        data.put("deliveredSales", deliveredSales);

        // Recent orders (latest 5, by order date desc)
        List<Order> recent = new ArrayList<>(orders);
        recent.sort((a, b) -> {
            LocalDate da = a.getOrderDate();
            LocalDate db = b.getOrderDate();
            if (da == null || db == null) return 0;
            return db.compareTo(da);
        });
        if (recent.size() > 5) recent = recent.subList(0, 5);
        data.put("recentOrders", recent);

        return data;
    }
}
