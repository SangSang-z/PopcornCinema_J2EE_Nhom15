package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.PaymentTransaction;
import com.example.PopcornCinema.repository.PaymentTransactionRepository;
import com.example.PopcornCinema.repository.ShowtimeRepository;
import com.example.PopcornCinema.repository.projection.CheckoutShowtimeProjection;
import com.example.PopcornCinema.service.PaymentTransactionService;
import com.example.PopcornCinema.dto.PaymentTransactionStatusResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class SepayHostedController {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PaymentTransactionService paymentTransactionService;

    @Value("${sepay.merchant-id:}")
    private String merchantId;

    @Value("${sepay.secret-key:}")
    private String secretKey;

    @Value("${sepay.gateway-url:https://pgapi-sandbox.sepay.vn/v1/checkout/init}")
    private String gatewayUrl;

    @Value("${sepay.payment-method:BANK_TRANSFER}")
    private String paymentMethod;

    @Value("${sepay.auto-confirm-on-return:true}")
    private boolean autoConfirmOnReturn;

    @Value("${app.public-base-url:}")
    private String publicBaseUrl;

    @Value("${app.local-base-url:}")
    private String localBaseUrl;

    public SepayHostedController(PaymentTransactionRepository paymentTransactionRepository,
                                 ShowtimeRepository showtimeRepository,
                                 PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.showtimeRepository = showtimeRepository;
        this.paymentTransactionService = paymentTransactionService;
    }

    @GetMapping("/sepay/checkout")
    public String checkout(@RequestParam("orderCode") String orderCode,
                           HttpServletRequest request,
                           Model model) {
        PaymentTransaction tx = paymentTransactionRepository.findByOrderCode(orderCode)
                .orElse(null);

        if (tx == null) {
            return redirectFailed("Không tìm thấy giao dịch");
        }

        PaymentTransactionStatusResponse statusResponse = paymentTransactionService.getStatus(orderCode);
        String status = statusResponse.getStatus();
        if ("PAID".equals(status)) {
            return "redirect:/ticket-success?orderCode=" + urlEncode(orderCode);
        }

        if ("CANCELLED".equals(status) || "EXPIRED".equals(status) || "REJECTED".equals(status) || "FAILED".equals(status)) {
            return redirectFailed("Giao dịch đã bị hủy hoặc hết hạn");
        }

        String merchant = trimToNull(merchantId);
        String secret = trimToNull(secretKey);
        String method = trimToNull(paymentMethod);
        String baseFromConfig = trimToNull(publicBaseUrl);

        if (isBlank(merchant) || isBlank(secret)) {
            return redirectFailed("Chưa cấu hình SePay merchant hoặc secret key");
        }

        String baseUrl = resolveBaseUrl(request, baseFromConfig);
        if (isBlank(baseUrl)) {
            return redirectFailed("Chưa cấu hình URL public cho SePay");
        }
        if (isLocalhost(baseUrl)) {
            return redirectFailed("SePay yêu cầu URL callback public, không dùng localhost");
        }

        String orderAmount = normalizeAmount(tx.getAmount());
        String operation = "PURCHASE";
        String currency = "VND";
        String orderDescription = buildOrderDescription(tx.getShowtimeId(), orderCode);
        String orderInvoiceNumber = orderCode;
        String customerId = tx.getUserId() == null ? null : String.valueOf(tx.getUserId());
        String resolvedPaymentMethod = isBlank(method) ? null : method;

        String returnUrlBase = baseUrl + "/sepay/return?orderCode=" + urlEncode(orderCode)
                + "&ngrok-skip-browser-warning=1";
        String successUrl = returnUrlBase + "&result=success";
        String errorUrl = returnUrlBase + "&result=error";
        String cancelUrl = returnUrlBase + "&result=cancel";

        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("order_amount", orderAmount);
        fields.put("merchant", merchant);
        fields.put("currency", currency);
        fields.put("operation", operation);
        fields.put("order_description", orderDescription);
        fields.put("order_invoice_number", orderInvoiceNumber);
        if (!isBlank(customerId)) {
            fields.put("customer_id", customerId);
        }
        if (!isBlank(resolvedPaymentMethod)) {
            fields.put("payment_method", resolvedPaymentMethod);
        }
        fields.put("success_url", successUrl);
        fields.put("error_url", errorUrl);
        fields.put("cancel_url", cancelUrl);

        String signature = signFields(fields, secret);

        model.addAttribute("actionUrl", gatewayUrl);
        model.addAttribute("orderAmount", orderAmount);
        model.addAttribute("merchant", merchant);
        model.addAttribute("currency", currency);
        model.addAttribute("operation", operation);
        model.addAttribute("orderDescription", orderDescription);
        model.addAttribute("orderInvoiceNumber", orderInvoiceNumber);
        model.addAttribute("customerId", customerId);
        model.addAttribute("paymentMethod", resolvedPaymentMethod);
        model.addAttribute("successUrl", successUrl);
        model.addAttribute("errorUrl", errorUrl);
        model.addAttribute("cancelUrl", cancelUrl);
        model.addAttribute("signature", signature);

        return "booking/sepay-hosted";
    }

    @GetMapping("/sepay/return")
    public String returnFromSepay(@RequestParam("orderCode") String orderCode,
                                  @RequestParam(value = "result", required = false) String result) {
        PaymentTransaction tx = paymentTransactionRepository.findByOrderCode(orderCode)
                .orElse(null);
        if (tx == null) {
            return redirectFailed("Không tìm thấy giao dịch");
        }

        PaymentTransactionStatusResponse statusResponse = paymentTransactionService.getStatus(orderCode);
        String status = statusResponse.getStatus();
        String normalizedStatus = status == null ? "" : status.toUpperCase();
        String normalizedResult = normalizeResult(result);

        if ("PAID".equals(normalizedStatus)) {
            return "redirect:" + buildTicketSuccessUrl(resolveClientBaseUrl(), orderCode);
        }

        if (isCancelResult(normalizedResult)) {
            paymentTransactionService.cancel(orderCode);
            return redirectFailed("Giao dá»‹ch Ä‘Ã£ bá»‹ há»§y");
        }

        if (isErrorResult(normalizedResult)) {
            paymentTransactionService.cancel(orderCode);
            return redirectFailed("Thanh toÃ¡n khÃ´ng thÃ nh cÃ´ng");
        }

        if (autoConfirmOnReturn
                && isSuccessResult(normalizedResult)
                && !"PAID".equals(normalizedStatus)
                && !isFailureStatus(normalizedStatus)) {
            boolean confirmed = paymentTransactionService.confirmBySepay(orderCode, tx.getAmount());
            if (confirmed) {
                status = "PAID";
                normalizedStatus = "PAID";
            }
        }

        if ("EXPIRED".equals(normalizedStatus)) {
            return redirectFailed("Giao dịch đã hết hạn");
        }

        if (isFailureStatus(normalizedStatus)) {
            return redirectFailed("Giao dịch đã bị hủy hoặc thất bại");
        }

        // Trạng thái PENDING hoặc chưa đồng bộ kịp: đưa về trang xuất thông tin vé
        // để người dùng có thể tải lại khi thanh toán chờ xác nhận.
        return "redirect:" + buildTicketSuccessUrl(resolveClientBaseUrl(), orderCode);
    }

    private String resolveBaseUrl(HttpServletRequest request, String base) {
        if (isBlank(base)) {
            String scheme = request.getScheme();
            String host = request.getServerName();
            int port = request.getServerPort();
            base = scheme + "://" + host + ((port == 80 || port == 443) ? "" : ":" + port);
        }
        if (base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }

    private String resolveBaseUrlFallback() {
        String base = trimToNull(publicBaseUrl);
        if (base == null) {
            return "";
        }
        if (base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }

    private String resolveClientBaseUrl() {
        String local = trimToNull(localBaseUrl);
        if (local != null) {
            if (local.endsWith("/")) {
                return local.substring(0, local.length() - 1);
            }
            return local;
        }

        return resolveBaseUrlFallback();
    }

    private String buildTicketSuccessUrl(String baseUrl, String orderCode) {
        if (isBlank(baseUrl)) {
            return "/ticket-success?orderCode=" + urlEncode(orderCode);
        }
        return baseUrl + "/ticket-success?orderCode=" + urlEncode(orderCode);
    }

    private String signFields(Map<String, String> fields, String secret) {
        String[] allowedOrder = new String[] {
                "order_amount",
                "merchant",
                "currency",
                "operation",
                "order_description",
                "order_invoice_number",
                "customer_id",
                "payment_method",
                "success_url",
                "error_url",
                "cancel_url"
        };

        StringBuilder signed = new StringBuilder();
        for (String key : allowedOrder) {
            String value = fields.get(key);
            if (value == null) continue;
            if (signed.length() > 0) signed.append(",");
            signed.append(key).append("=").append(value);
        }
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = hmac.doFinal(signed.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo chữ ký SePay", ex);
        }
    }


    private String normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return amount.setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    private String redirectFailed(String message) {
        return "redirect:/payment-failed?message=" + urlEncode(message);
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isLocalhost(String baseUrl) {
        String lower = baseUrl.toLowerCase();
        return lower.contains("localhost") || lower.contains("127.0.0.1");
    }

    private boolean isFailureStatus(String status) {
        return "CANCELLED".equals(status)
                || "EXPIRED".equals(status)
                || "REJECTED".equals(status)
                || "FAILED".equals(status);
    }

    private String normalizeResult(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase();
    }

    private boolean isCancelResult(String value) {
        return "cancel".equals(value)
                || "cancelled".equals(value)
                || "canceled".equals(value);
    }

    private boolean isErrorResult(String value) {
        return "error".equals(value)
                || "failed".equals(value)
                || "failure".equals(value);
    }

    private boolean isSuccessResult(String value) {
        return value.isBlank() || "success".equals(value);
    }

    private String buildOrderDescription(Long showtimeId, String orderCode) {
        if (showtimeId == null) {
            return "Thanh toan don hang " + orderCode;
        }

        CheckoutShowtimeProjection info = showtimeRepository.findCheckoutInfoByShowtimeId(showtimeId);
        if (info == null) {
            return "Thanh toan don hang " + orderCode;
        }

        String title = trimToNull(info.getMovieTitle());
        String cinema = trimToNull(info.getCinemaName());
        String auditorium = trimToNull(info.getAuditoriumName());
        String timeText = info.getStartTime() != null
                ? info.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))
                : null;

        StringBuilder builder = new StringBuilder();
        builder.append("Ve phim");
        if (title != null) {
            builder.append(" ").append(title);
        }
        if (timeText != null) {
            builder.append(" - ").append(timeText);
        }
        if (cinema != null) {
            builder.append(" - ").append(cinema);
        }
        if (auditorium != null) {
            builder.append(" (Phong ").append(auditorium).append(")");
        }

        return builder.toString();
    }
}
