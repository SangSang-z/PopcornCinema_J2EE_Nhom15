package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.SepayIpnPayload;
import com.example.PopcornCinema.dto.SepayWebhookPayload;
import com.example.PopcornCinema.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/sepay")
public class SepayWebhookController {

    private static final Pattern ORDER_CODE_PATTERN = Pattern.compile("(ORD[A-Z0-9]{10})");

    private final PaymentTransactionService paymentTransactionService;

    @Value("${sepay.api-key:}")
    private String sepayApiKey;

    @Value("${sepay.secret-key:}")
    private String sepaySecretKey;

    public SepayWebhookController(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> webhookJson(
            @RequestBody SepayWebhookPayload payload,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return handleWebhook(payload, authorization);
    }

    @PostMapping(value = "/webhook", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> webhookForm(
            SepayWebhookPayload payload,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return handleWebhook(payload, authorization);
    }

    @PostMapping(value = "/ipn", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> ipn(
            @RequestBody SepayIpnPayload payload,
            @RequestHeader(value = "X-Secret-Key", required = false) String secretKeyHeader) {
        if (!isIpnAuthorized(secretKeyHeader)) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }

        if (payload == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Empty payload"));
        }

        if (payload.getNotificationType() == null
                || !"ORDER_PAID".equalsIgnoreCase(payload.getNotificationType())) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Ignore non-paid notification"));
        }

        String orderCode = payload.getOrder() != null ? payload.getOrder().getOrderInvoiceNumber() : null;
        if (orderCode == null || orderCode.isBlank()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Order code not found"));
        }

        BigDecimal amount = parseAmount(
                payload.getOrder() != null ? payload.getOrder().getOrderAmount() : null,
                payload.getTransaction() != null ? payload.getTransaction().getTransactionAmount() : null
        );

        boolean confirmed = paymentTransactionService.confirmBySepay(orderCode.trim(), amount);
        if (!confirmed) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Not confirmed"));
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    private ResponseEntity<Map<String, Object>> handleWebhook(SepayWebhookPayload payload, String authorization) {
        if (!isAuthorized(authorization)) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }

        if (payload == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Empty payload"));
        }

        if (payload.getTransferType() == null || !"in".equalsIgnoreCase(payload.getTransferType())) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Ignore non-in transfer"));
        }

        String orderCode = extractOrderCode(payload);
        if (orderCode == null || orderCode.isBlank()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Order code not found"));
        }

        BigDecimal amount = payload.getTransferAmount() == null
                ? null
                : BigDecimal.valueOf(payload.getTransferAmount());

        boolean confirmed = paymentTransactionService.confirmBySepay(orderCode, amount);
        if (!confirmed) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Not confirmed"));
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    private boolean isAuthorized(String authorization) {
        String apiKey = normalizeKey(sepayApiKey);
        if (apiKey == null || apiKey.isBlank()) {
            return true;
        }
        if (authorization == null || authorization.isBlank()) {
            return false;
        }
        String trimmed = authorization.trim();
        String apiKeyHeader = "Apikey " + apiKey;
        if (trimmed.equals(apiKey)) {
            return true;
        }
        return trimmed.equalsIgnoreCase(apiKeyHeader);
    }

    private boolean isIpnAuthorized(String secretKeyHeader) {
        String secretKey = normalizeKey(sepaySecretKey);
        if (secretKey == null || secretKey.isBlank()) {
            return true;
        }
        if (secretKeyHeader == null || secretKeyHeader.isBlank()) {
            return false;
        }
        return secretKeyHeader.trim().equals(secretKey);
    }

    private String extractOrderCode(SepayWebhookPayload payload) {
        if (payload.getCode() != null && !payload.getCode().isBlank()) {
            return payload.getCode().trim();
        }

        String content = payload.getContent();
        if (content == null) {
            return null;
        }
        Matcher matcher = ORDER_CODE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private BigDecimal parseAmount(String orderAmount, String transactionAmount) {
        String raw = orderAmount != null && !orderAmount.isBlank() ? orderAmount : transactionAmount;
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String cleaned = raw.replaceAll("[^0-9.]", "");
        if (cleaned.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeKey(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
