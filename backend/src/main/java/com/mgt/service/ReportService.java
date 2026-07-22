package com.mgt.service;

import com.mgt.dao.ReportDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    ReportDAO reportDAO;

    public Map<String, Object> getSalesReport(String fromStr, String toStr) {
        LocalDate from = LocalDate.parse(fromStr);
        LocalDate to   = LocalDate.parse(toStr);

        List<Object[]> rows = reportDAO.getSalesData(from, to);
        List<Map<String, Object>> items = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("orderCode",     row[0]);
            item.put("customerName",  row[1]);
            item.put("orderDate",     row[2] != null ? row[2].toString() : null);
            item.put("totalAmount",   row[3]);
            item.put("paymentMethod", row[4]);
            item.put("status",        row[5]);
            items.add(item);
        }

        Double totalRevenue = reportDAO.getTotalRevenueBetween(from, to);
        Long   totalOrders  = reportDAO.countOrdersBetween(from, to);
        double avg = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items",             items);
        result.put("totalOrders",       totalOrders);
        result.put("totalRevenue",      totalRevenue);
        result.put("averageOrderValue", Math.round(avg * 100.0) / 100.0);
        result.put("dateFrom",          fromStr);
        result.put("dateTo",            toStr);
        return result;
    }

    public Map<String, Object> getRevenueReport() {
        List<Object[]> rows = reportDAO.getMonthlyRevenue();
        List<Map<String, Object>> monthly = new ArrayList<>();

        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun",
                               "Jul","Aug","Sep","Oct","Nov","Dec"};

        for (Object[] row : rows) {
            int monthNum = ((Number) row[0]).intValue();
            int year     = ((Number) row[1]).intValue();
            double rev   = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
            long orders  = row[3] != null ? ((Number) row[3]).longValue()   : 0;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("month",        monthNames[monthNum - 1] + " " + year);
            m.put("monthNum",     monthNum);
            m.put("year",         year);
            m.put("totalRevenue", rev);
            m.put("orderCount",   orders);
            monthly.add(m);
        }

        LocalDate now   = LocalDate.now();
        LocalDate first = now.withDayOfMonth(1);
        LocalDate lastMonthFirst = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd   = first.minusDays(1);

        double thisMonth = reportDAO.getTotalRevenueBetween(first, now);
        double lastMonth = reportDAO.getTotalRevenueBetween(lastMonthFirst, lastMonthEnd);
        double growth    = lastMonth > 0 ? ((thisMonth - lastMonth) / lastMonth) * 100 : 0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("monthly",            monthly);
        result.put("totalRevenue",       reportDAO.getTotalRevenueAllTime());
        result.put("revenueThisMonth",   thisMonth);
        result.put("revenueLastMonth",   lastMonth);
        result.put("growthPercent",      Math.round(growth * 10.0) / 10.0);
        return result;
    }

    public Map<String, Object> getProductReport() {
        List<Object[]> rows = reportDAO.getProductSalesData();
        List<Map<String, Object>> items = new ArrayList<>();
        String topProduct = rows.isEmpty() ? "N/A" : (String) rows.get(0)[1];

        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("productId",    row[0]);
            item.put("productName",  row[1]);
            item.put("category",     "—");   
            item.put("totalSold",    row[2] != null ? ((Number) row[2]).longValue()   : 0);
            item.put("totalRevenue", row[3] != null ? ((Number) row[3]).doubleValue() : 0);
            items.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items",             items);
        result.put("totalProductsSold", reportDAO.getTotalUnitsSold());
        result.put("topProduct",        topProduct);
        return result;
    }

    public Map<String, Object> getCustomerReport() {
        List<Object[]> spendRows   = reportDAO.getCustomerSpendingData();
        List<Object[]> customerRows = reportDAO.getCustomerFullList();

        Map<String, long[]> spendingMap = new HashMap<>();
        for (Object[] row : spendRows) {
            String phone  = (String) row[1];
            long   orders = row[2] != null ? ((Number) row[2]).longValue() : 0;
            double spent  = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            spendingMap.put(phone, new long[]{ orders, (long)(spent * 100) });
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (Object[] row : customerRows) {
            String phone = (String) row[3];
            long[] spend = spendingMap.getOrDefault(phone, new long[]{0, 0});

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("customerId",   row[0]);
            item.put("customerName", row[1]);
            item.put("email",        row[2]);
            item.put("phone",        phone);
            item.put("type",         row[4]);
            item.put("joinDate",     row[5] != null ? row[5].toString() : null);
            item.put("totalOrders",  spend[0]);
            item.put("totalSpent",   spend[1] / 100.0);
            items.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items",         items);
        result.put("totalCustomers",reportDAO.countTotalCustomers());
        result.put("activeCount",   reportDAO.countActiveCustomers());
        result.put("vipCount",      reportDAO.countVipCustomers());
        result.put("newThisMonth",  reportDAO.countNewCustomersThisMonth());
        return result;
    }
}
