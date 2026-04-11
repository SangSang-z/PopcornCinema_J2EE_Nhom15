package com.example.PopcornCinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SepayIpnPayload {
    private Long timestamp;

    @JsonProperty("notification_type")
    private String notificationType;

    private Order order;
    private Transaction transaction;

    public Long getTimestamp() { return timestamp; }
    public String getNotificationType() { return notificationType; }
    public Order getOrder() { return order; }
    public Transaction getTransaction() { return transaction; }

    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
    public void setOrder(Order order) { this.order = order; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public static class Order {
        @JsonProperty("order_invoice_number")
        private String orderInvoiceNumber;

        @JsonProperty("order_amount")
        private String orderAmount;

        @JsonProperty("order_status")
        private String orderStatus;

        public String getOrderInvoiceNumber() { return orderInvoiceNumber; }
        public String getOrderAmount() { return orderAmount; }
        public String getOrderStatus() { return orderStatus; }

        public void setOrderInvoiceNumber(String orderInvoiceNumber) { this.orderInvoiceNumber = orderInvoiceNumber; }
        public void setOrderAmount(String orderAmount) { this.orderAmount = orderAmount; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    }

    public static class Transaction {
        @JsonProperty("transaction_amount")
        private String transactionAmount;

        @JsonProperty("transaction_status")
        private String transactionStatus;

        public String getTransactionAmount() { return transactionAmount; }
        public String getTransactionStatus() { return transactionStatus; }

        public void setTransactionAmount(String transactionAmount) { this.transactionAmount = transactionAmount; }
        public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
    }
}
