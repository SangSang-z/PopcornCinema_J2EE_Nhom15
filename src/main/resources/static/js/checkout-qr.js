const PAYMENT_TX_KEY = "paymentTx";
const LAST_PAID_ORDER_KEY = "lastPaidOrderCode";
const DEMO_BANK = {
    bankId: "MB",
    accountNo: "123456789",
    accountName: "POPCORN CINEMA"
};

let countdownTimer = null;
let pollingTimer = null;

window.addEventListener("beforeunload", stopBackgroundTimers);

document.addEventListener("DOMContentLoaded", async () => {
    try {
        const tx = await resolveTransaction();
        if (!tx) {
            redirectToFailed("Thiếu thông tin giao dịch");
            return;
        }

        renderTransaction(tx);
        enhanceQrPanel(tx);
        bindActions(tx);
        startPaymentCountdown(tx);
        startPaymentPolling(tx.orderCode);
    } catch (error) {
        console.error("Không khởi tạo được trang QR:", error);
        redirectToFailed("Không tải được giao dịch thanh toán");
    }
});

async function resolveTransaction() {
    const url = new URL(window.location.href);
    const orderCodeFromUrl = url.searchParams.get("orderCode");
    const showtimeIdFromUrl = url.searchParams.get("showtimeId");
    const storedTx = readStoredTransaction();

    if (storedTx && (!orderCodeFromUrl || storedTx.orderCode === orderCodeFromUrl)) {
        syncCheckoutQrUrl(storedTx.showtimeId || showtimeIdFromUrl, storedTx.orderCode);
        return normalizeTransaction(storedTx);
    }

    if (!orderCodeFromUrl) {
        return null;
    }

    const statusRes = await fetch(`/api/payment-transactions/${encodeURIComponent(orderCodeFromUrl)}/status`);
    if (!statusRes.ok) {
        return null;
    }

    const statusData = await statusRes.json();
    const tx = normalizeTransaction({
        orderCode: statusData.orderCode,
        showtimeId: showtimeIdFromUrl,
        amount: statusData.amount,
        status: statusData.status,
        expiresAt: statusData.expiresAt,
        qrContent: buildTransferContent(statusData.orderCode),
        qrImageUrl: buildVietQrImageUrl(statusData.orderCode, statusData.amount)
    });

    sessionStorage.setItem(PAYMENT_TX_KEY, JSON.stringify(tx));
    syncCheckoutQrUrl(tx.showtimeId || showtimeIdFromUrl, tx.orderCode);
    return tx;
}

function normalizeTransaction(tx) {
    if (!tx) return null;

    return {
        ...tx,
        qrContent: tx.qrContent || buildTransferContent(tx.orderCode),
        qrImageUrl: tx.qrImageUrl || buildVietQrImageUrl(tx.orderCode, tx.amount)
    };
}

function renderTransaction(tx) {
    document.getElementById("order-code").textContent = tx.orderCode || "---";
    document.getElementById("payment-amount").textContent = formatCurrency(tx.amount);

    const qrImage = document.getElementById("qr-image");
    qrImage.src = tx.qrImageUrl;
    qrImage.alt = `QR thanh toán ${tx.orderCode}`;
}

function enhanceQrPanel(tx) {
    const ticketInfo = document.querySelector(".ticket-info");
    if (!ticketInfo || document.getElementById("payment-bank-info")) {
        return;
    }

    const wrapper = document.createElement("div");
    wrapper.id = "payment-bank-info";
    wrapper.style.marginTop = "16px";
    wrapper.innerHTML = `
        <p>Ngân hàng nhận: <b>${DEMO_BANK.bankId}</b></p>
        <p>Số tài khoản: <b>${DEMO_BANK.accountNo}</b></p>
        <p>Chủ tài khoản: <b>${DEMO_BANK.accountName}</b></p>
        <p>Nội dung chuyển khoản: <b id="payment-transfer-note">${tx.orderCode}</b></p>
    `;
    ticketInfo.appendChild(wrapper);

    const actionWrapper = document.createElement("div");
    actionWrapper.style.display = "flex";
    actionWrapper.style.flexWrap = "wrap";
    actionWrapper.style.gap = "12px";
    actionWrapper.style.marginTop = "18px";

    const copyBtn = document.createElement("button");
    copyBtn.type = "button";
    copyBtn.id = "copy-transfer-content-btn";
    copyBtn.className = "btn-next";
    copyBtn.textContent = "Sao chép nội dung CK";

    const mockPaidBtn = document.createElement("button");
    mockPaidBtn.type = "button";
    mockPaidBtn.id = "mock-paid-btn";
    mockPaidBtn.className = "btn-next";
    mockPaidBtn.textContent = "Tôi đã thanh toán";

    actionWrapper.append(copyBtn, mockPaidBtn);
    ticketInfo.appendChild(actionWrapper);
}

function bindActions(tx) {
    document.getElementById("back-to-checkout-btn").addEventListener("click", async () => {
        try {
            await fetch(`/api/payment-transactions/${encodeURIComponent(tx.orderCode)}/cancel`, { method: "POST" });
        } catch (error) {
            console.error("Không hủy được giao dịch:", error);
        } finally {
            clearCheckoutState();
            const fallbackUrl = tx.showtimeId ? `/checkout?showtimeId=${tx.showtimeId}` : "/checkout";
            window.location.href = fallbackUrl;
        }
    });

    const copyBtn = document.getElementById("copy-transfer-content-btn");
    if (copyBtn) {
        copyBtn.addEventListener("click", async () => {
            try {
                await navigator.clipboard.writeText(tx.orderCode);
                copyBtn.textContent = "Đã sao chép";
                setTimeout(() => copyBtn.textContent = "Sao chép nội dung CK", 1500);
            } catch (error) {
                console.error("Không sao chép được nội dung chuyển khoản:", error);
                alert(`Nội dung chuyển khoản: ${tx.orderCode}`);
            }
        });
    }

    const mockPaidBtn = document.getElementById("mock-paid-btn");
    if (mockPaidBtn) {
        mockPaidBtn.addEventListener("click", async () => {
            mockPaidBtn.disabled = true;
            mockPaidBtn.textContent = "Đang xác nhận...";

            try {
                const response = await fetch(`/api/payment-transactions/${encodeURIComponent(tx.orderCode)}/mock-paid`, {
                    method: "POST"
                });

                if (!response.ok) {
                    throw new Error(await response.text());
                }
            } catch (error) {
                console.error("Không xác nhận được thanh toán:", error);
                alert("Không xác nhận được thanh toán. Bạn thử lại nhé.");
                mockPaidBtn.disabled = false;
                mockPaidBtn.textContent = "Tôi đã thanh toán";
            }
        });
    }
}

function startPaymentCountdown(tx) {
    const countdownEl = document.getElementById("payment-countdown");
    const expiredTime = new Date(tx.expiresAt).getTime();

    const render = () => {
        const diff = expiredTime - Date.now();
        const remain = Math.floor(diff / 1000);

        if (remain <= 0) {
            countdownEl.textContent = "00:00";
            handleExpiredTransaction(tx.orderCode);
            return false;
        }

        const mm = String(Math.floor(remain / 60)).padStart(2, "0");
        const ss = String(remain % 60).padStart(2, "0");
        countdownEl.textContent = `${mm}:${ss}`;
        return true;
    };

    if (!render()) return;

    countdownTimer = setInterval(() => {
        if (!render()) {
            clearInterval(countdownTimer);
            countdownTimer = null;
        }
    }, 1000);
}

function startPaymentPolling(orderCode) {
    pollingTimer = setInterval(async () => {
        try {
            const res = await fetch(`/api/payment-transactions/${encodeURIComponent(orderCode)}/status`);
            if (!res.ok) return;

            const data = await res.json();

            if (data.status === "PAID") {
                stopBackgroundTimers();
                sessionStorage.setItem(LAST_PAID_ORDER_KEY, orderCode);
                sessionStorage.removeItem(PAYMENT_TX_KEY);
                window.location.href = `/ticket-success?orderCode=${encodeURIComponent(orderCode)}`;
                return;
            }

            if (data.status === "EXPIRED" || data.status === "CANCELLED") {
                stopBackgroundTimers();
                clearCheckoutState();
                redirectToFailed("Giao dịch đã hết hạn hoặc đã bị hủy", orderCode);
            }
        } catch (error) {
            console.error("Lỗi kiểm tra trạng thái thanh toán:", error);
        }
    }, 3000);
}

async function handleExpiredTransaction(orderCode) {
    stopBackgroundTimers();
    clearCheckoutState();

    try {
        await fetch(`/api/payment-transactions/${encodeURIComponent(orderCode)}/cancel`, { method: "POST" });
    } catch (error) {
        console.error("Không cập nhật được trạng thái hết hạn:", error);
    }

    redirectToFailed("Giao dịch đã hết hạn", orderCode);
}

function stopBackgroundTimers() {
    if (countdownTimer) {
        clearInterval(countdownTimer);
        countdownTimer = null;
    }
    if (pollingTimer) {
        clearInterval(pollingTimer);
        pollingTimer = null;
    }
}

function clearCheckoutState() {
    sessionStorage.removeItem(PAYMENT_TX_KEY);
}

function readStoredTransaction() {
    try {
        return JSON.parse(sessionStorage.getItem(PAYMENT_TX_KEY) || "null");
    } catch (error) {
        console.error("Không đọc được paymentTx trong sessionStorage:", error);
        sessionStorage.removeItem(PAYMENT_TX_KEY);
        return null;
    }
}

function syncCheckoutQrUrl(showtimeId, orderCode) {
    const url = new URL(window.location.href);
    if (showtimeId) {
        url.searchParams.set("showtimeId", showtimeId);
    }
    if (orderCode) {
        url.searchParams.set("orderCode", orderCode);
    }
    window.history.replaceState({}, "", url);
}

function redirectToFailed(message, orderCode = "") {
    const url = new URL("/payment-failed", window.location.origin);
    if (message) url.searchParams.set("message", message);
    if (orderCode) url.searchParams.set("orderCode", orderCode);
    window.location.href = url.toString();
}

function buildTransferContent(orderCode) {
    return `CK ${orderCode}`;
}

function buildVietQrImageUrl(orderCode, amount) {
    const params = new URLSearchParams({
        amount: normalizeAmount(amount),
        addInfo: orderCode || "",
        accountName: DEMO_BANK.accountName
    });

    return `https://img.vietqr.io/image/${DEMO_BANK.bankId}-${DEMO_BANK.accountNo}-compact2.png?${params.toString()}`;
}

function normalizeAmount(value) {
    if (value === null || value === undefined || value === "") return "0";
    const numeric = Number(value);
    if (Number.isNaN(numeric)) return String(value).replace(/[^\d]/g, "") || "0";
    return Math.max(0, Math.round(numeric)).toString();
}

function formatCurrency(value) {
    return Number(value || 0).toLocaleString("vi-VN") + " đ";
}
