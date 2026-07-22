package com.mgt.service;

import com.mgt.dao.OrderReturnDAO;
import com.mgt.dao.OrderDAO;
import com.mgt.dao.RefundDAO;
import com.mgt.model.Order;
import com.mgt.model.OrderItem;
import com.mgt.model.OrderReturn;
import com.mgt.model.Refund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderReturnService {

    @Autowired
    OrderReturnDAO returnDAO;

    @Autowired
    OrderDAO orderDAO;

    @Autowired
    RefundDAO refundDAO;

    @Autowired
    CommissionService commissionService;

    public void create(long orderId, String productName, String reason, String refundMethod,
                       String refundAccountNumber, String refundAccountName,
                       String refundBankName, String refundBranch) {
        Order order = orderDAO.getById(orderId);
        if (order == null) throw new RuntimeException("Order not found: " + orderId);
        if (!"DELIVERED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Return request is allowed only after delivery");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("Return reason is required");
        }
        validateRefundDetails(order, refundMethod, refundAccountNumber, refundAccountName, refundBankName);
        if (returnDAO.hasActiveReturn(orderId, productName)) {
            throw new RuntimeException("Return request already exists for this product");
        }

        OrderReturn ret = new OrderReturn();
        ret.setOrder(order);
        ret.setProductName(productName.trim());
        ret.setReason(reason.trim());
        ret.setStatus("PENDING");
        ret.setRefundStatus("PENDING");
        ret.setRefundAmount(calculateRefundAmount(order, productName));
        ret.setRefundMethod(clean(refundMethod));
        ret.setRefundAccountNumber(clean(refundAccountNumber));
        ret.setRefundAccountName(clean(refundAccountName));
        ret.setRefundBankName(clean(refundBankName));
        ret.setRefundBranch(clean(refundBranch));
        ret.setRequestDate(LocalDate.now());
        returnDAO.save(ret);
    }

    public List<OrderReturn> getAll() { return returnDAO.getAll(); }

    public OrderReturn getById(long id) { return returnDAO.getById(id); }

    public List<OrderReturn> getByStatus(String status) { return returnDAO.getByStatus(status); }

    public List<OrderReturn> getByOrderId(long orderId) { return returnDAO.getByOrderId(orderId); }

    public void approve(long id, String adminNote) {
        approve(id, adminNote, null, null);
    }

    public void approve(long id, String adminNote, Double requestedRefundAmount, String refundMethod) {
        OrderReturn ret = returnDAO.getById(id);
        if (ret == null) throw new RuntimeException("Return request not found: " + id);
        if (!"PENDING".equalsIgnoreCase(ret.getStatus())) {
            throw new RuntimeException("Only pending return requests can be approved");
        }

        Order order = ret.getOrder();
        double calculatedAmount = calculateRefundAmount(order, ret.getProductName());
        double refundAmount = requestedRefundAmount != null && requestedRefundAmount > 0
                ? requestedRefundAmount
                : calculatedAmount;

        ret.setStatus("APPROVED");
        ret.setAdminNote(adminNote);
        ret.setResolvedDate(LocalDate.now());
        ret.setRefundAmount(refundAmount);
        ret.setRefundStatus("PENDING");
        returnDAO.update(ret);

        if (order != null) {
            order.setRefundStatus("RETURN_APPROVED");
            orderDAO.update(order);
        }

        Refund refund = refundDAO.getByReturnId(id);
        if (refund == null) {
            refund = new Refund();
            refund.setOrderReturn(ret);
            refund.setOrder(order);
            refund.setStatus("PENDING");
        }
        refund.setRefundAmount(refundAmount);
        refund.setRefundMethod(ret.getRefundMethod());
        refund.setRefundAccountNumber(ret.getRefundAccountNumber());
        refund.setRefundAccountName(ret.getRefundAccountName());
        refund.setRefundBankName(ret.getRefundBankName());
        refund.setRefundBranch(ret.getRefundBranch());
        refund.setAdminNote(adminNote);

        if (refund.getId() > 0) refundDAO.update(refund);
        else refundDAO.save(refund);
    }

    public void advanceStatus(long id, String status, String note, String refundMethod, String transactionRef) {
        OrderReturn ret = returnDAO.getById(id);
        if (ret == null) throw new RuntimeException("Return request not found: " + id);
        String next = status == null ? "" : status.trim().toUpperCase();
        List<String> allowed = java.util.Arrays.asList("RETURN_PICKED", "ITEM_RECEIVED", "REFUND_INITIATED", "REFUNDED");
        if (!allowed.contains(next)) throw new RuntimeException("Invalid return status");
        ret.setStatus(next);
        if (note != null && !note.trim().isEmpty()) ret.setAdminNote(note.trim());
        if (refundMethod != null && !refundMethod.trim().isEmpty()) ret.setRefundMethod(refundMethod.trim());
        if (transactionRef != null && !transactionRef.trim().isEmpty()) ret.setTransactionRef(transactionRef.trim());
        Order order = ret.getOrder();
        if ("REFUND_INITIATED".equals(next)) {
            ret.setRefundStatus("INITIATED");
            if (order != null) order.setRefundStatus("REFUND_INITIATED");
        }
        if ("REFUNDED".equals(next) && (transactionRef == null || transactionRef.trim().isEmpty())) {
            throw new RuntimeException("Transaction reference is required to complete the refund");
        }
        if ("REFUNDED".equals(next)) {
            ret.setRefundStatus("PROCESSED");
            ret.setRefundDate(LocalDate.now());
            ret.setResolvedDate(LocalDate.now());
            if (order != null) {
                order.setRefundStatus("REFUNDED");
                order.setPaymentStatus("REFUNDED");
                commissionService.markOrderRefunded(order);
            }
            Refund refund = refundDAO.getByReturnId(id);
            if (refund == null) { refund = new Refund(); refund.setOrderReturn(ret); refund.setOrder(order); }
            refund.setRefundAmount(ret.getRefundAmount());
            refund.setRefundMethod(ret.getRefundMethod());
            refund.setRefundAccountNumber(ret.getRefundAccountNumber());
            refund.setRefundAccountName(ret.getRefundAccountName());
            refund.setRefundBankName(ret.getRefundBankName());
            refund.setRefundBranch(ret.getRefundBranch());
            refund.setTransactionRef(ret.getTransactionRef());
            refund.setStatus("PROCESSED");
            refund.setProcessedDate(LocalDate.now());
            refund.setAdminNote(ret.getAdminNote());
            if (refund.getId() > 0) refundDAO.update(refund); else refundDAO.save(refund);
        }
        returnDAO.update(ret);
        if (order != null) orderDAO.update(order);
    }

    public void reject(long id, String adminNote) {
        OrderReturn ret = returnDAO.getById(id);
        if (ret == null) throw new RuntimeException("Return request not found: " + id);
        if (!"PENDING".equalsIgnoreCase(ret.getStatus())) {
            throw new RuntimeException("Only pending return requests can be rejected");
        }
        returnDAO.updateStatus(id, "REJECTED", adminNote);
    }

    public void delete(long id) { returnDAO.delete(id); }


    private void validateRefundDetails(Order order, String method, String accountNumber,
                                       String accountName, String bankName) {
        String normalized = clean(method).toUpperCase();
        if (normalized.isEmpty()) throw new RuntimeException("Refund method is required");
        boolean original = "ORIGINAL_PAYMENT".equals(normalized);
        boolean cod = order != null && "COD".equalsIgnoreCase(order.getPaymentMethod());
        if (original && cod) throw new RuntimeException("COD orders require a bKash, Nagad, Rocket, Bank or Wallet refund account");
        if (!original) {
            if (clean(accountNumber).isEmpty()) throw new RuntimeException("Refund account number is required");
            if (clean(accountName).isEmpty()) throw new RuntimeException("Refund account holder name is required");
            if ("BANK".equals(normalized) && clean(bankName).isEmpty()) {
                throw new RuntimeException("Bank name is required for bank refund");
            }
        }
    }

    private String clean(String value) { return value == null ? "" : value.trim(); }

    private double calculateRefundAmount(Order order, String productName) {
        if (order == null) return 0.0;
        if ("Full order".equalsIgnoreCase(productName)) {
            return value(order.getTotalAmount());
        }
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getProductName() != null && item.getProductName().equalsIgnoreCase(productName)) {
                    double total = value(item.getTotalPrice());
                    if (total > 0) return total;
                    return value(item.getUnitPrice()) * item.getQuantity();
                }
            }
        }
        return value(order.getTotalAmount());
    }

    private double value(Double amount) {
        return amount != null ? amount : 0.0;
    }
}
