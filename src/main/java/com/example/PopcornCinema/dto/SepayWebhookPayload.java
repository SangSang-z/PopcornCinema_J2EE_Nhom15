package com.example.PopcornCinema.dto;

public class SepayWebhookPayload {
    private Long id;
    private String gateway;
    private String transactionDate;
    private String accountNumber;
    private String code;
    private String content;
    private String transferType;
    private Long transferAmount;
    private Long accumulated;
    private String subAccount;
    private String referenceCode;
    private String description;

    public Long getId() { return id; }
    public String getGateway() { return gateway; }
    public String getTransactionDate() { return transactionDate; }
    public String getAccountNumber() { return accountNumber; }
    public String getCode() { return code; }
    public String getContent() { return content; }
    public String getTransferType() { return transferType; }
    public Long getTransferAmount() { return transferAmount; }
    public Long getAccumulated() { return accumulated; }
    public String getSubAccount() { return subAccount; }
    public String getReferenceCode() { return referenceCode; }
    public String getDescription() { return description; }

    public void setId(Long id) { this.id = id; }
    public void setGateway(String gateway) { this.gateway = gateway; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setCode(String code) { this.code = code; }
    public void setContent(String content) { this.content = content; }
    public void setTransferType(String transferType) { this.transferType = transferType; }
    public void setTransferAmount(Long transferAmount) { this.transferAmount = transferAmount; }
    public void setAccumulated(Long accumulated) { this.accumulated = accumulated; }
    public void setSubAccount(String subAccount) { this.subAccount = subAccount; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
    public void setDescription(String description) { this.description = description; }
}
