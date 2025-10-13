package com.example.KLTN.Service;

import com.example.KLTN.Config.HTTPstatus.HttpResponseUtil;
import com.example.KLTN.Entity.TransactitonsEntity;
import com.example.KLTN.Entity.UsersEntity;
import com.example.KLTN.Entity.WalletsEntity;
import com.example.KLTN.Repository.TransactitonsRepository;
import com.example.KLTN.Service.Impl.TransactitonsServiceImpl;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Service
public class TransactitonsService implements TransactitonsServiceImpl {
    private final UserService userService;
    private final HttpResponseUtil responseUtil;

    private final WallettService walletService;

    @Override
    @Transactional
    public void failedPayment(HttpServletRequest request) {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String amountStr = request.getParameter("vnp_Amount");

        // Ghi log tất cả tham số callback
        System.out.println("===== VNPAY CALLBACK (FAILED) =====");
        request.getParameterMap().forEach((k, v) ->
                System.out.println(k + " = " + Arrays.toString(v))
        );
        System.out.println("==================================");

        // Nếu VNPay không gửi amount hoặc mã đơn hàng → không lưu giao dịch
        if (amountStr == null || vnp_TxnRef == null) {
            System.err.println("⚠ VNPay callback thất bại không đủ dữ liệu -> Bỏ qua lưu DB");
            return;
        }

        BigDecimal amount = new BigDecimal(amountStr).divide(BigDecimal.valueOf(100));

        // Nếu cần, có thể truy xuất user theo vnp_TxnRef thay vì SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "unknown";
        UsersEntity users = userService.FindByUsername(username);
        if (users == null) {
            System.err.println("⚠ Không tìm thấy user -> callback có thể đến từ VNPay server");
            return;
        }

        WalletsEntity walletsEntity = walletService.GetWallet(users);
        if (walletsEntity == null) {
            responseUtil.notFound("Không tồn tại ví");
            return;
        }

        TransactitonsEntity transactitonsEntity = new TransactitonsEntity();
        transactitonsEntity.setWallet(walletsEntity);
        transactitonsEntity.setAmount(amount);
        transactitonsEntity.setCreatedAt(LocalDateTime.now());
        transactitonsEntity.setVnpTxnRef(vnp_TxnRef);
        transactitonsEntity.setVnpOrderInfo(orderInfo);
        transactitonsEntity.setStatus(TransactitonsEntity.Status.failed);
        transactitonsEntity.setType(TransactitonsEntity.statustype.withdraw);
        SaveTransactions(transactitonsEntity);
    }

    @Override
    @Transactional
    public void SucseccPayment(HttpServletRequest request) {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String amountStr = request.getParameter("vnp_Amount");

        // Ghi log tất cả tham số callback
        System.out.println("===== VNPAY CALLBACK (FAILED) =====");
        request.getParameterMap().forEach((k, v) ->
                System.out.println(k + " = " + Arrays.toString(v))
        );
        System.out.println("==================================");

        // Nếu VNPay không gửi amount hoặc mã đơn hàng → không lưu giao dịch
        if (amountStr == null || vnp_TxnRef == null) {
            System.err.println("⚠ VNPay callback thất bại không đủ dữ liệu -> Bỏ qua lưu DB");
            return;
        }

        BigDecimal amount = new BigDecimal(amountStr).divide(BigDecimal.valueOf(100));

        // Nếu cần, có thể truy xuất user theo vnp_TxnRef thay vì SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "unknown";
        UsersEntity users = userService.FindByUsername(username);
        if (users == null) {
            System.err.println("⚠ Không tìm thấy user -> callback có thể đến từ VNPay server");
            return;
        }

        WalletsEntity walletsEntity = walletService.GetWallet(users);
        if (walletsEntity == null) {
            responseUtil.notFound("Không tồn tại ví");
            return;
        }

        TransactitonsEntity transactitonsEntity = new TransactitonsEntity();
        transactitonsEntity.setWallet(walletsEntity);
        transactitonsEntity.setAmount(amount);
        transactitonsEntity.setCreatedAt(LocalDateTime.now());
        transactitonsEntity.setVnpTxnRef(vnp_TxnRef);
        transactitonsEntity.setVnpOrderInfo(orderInfo);
        transactitonsEntity.setStatus(TransactitonsEntity.Status.success);
        transactitonsEntity.setType(TransactitonsEntity.statustype.withdraw);
        SaveTransactions(transactitonsEntity);
    }


    private final TransactitonsRepository transactitonsRepository;

    public TransactitonsService(UserService userService, HttpResponseUtil responseUtil, WallettService walletService, TransactitonsRepository transactitonsRepository) {
        this.userService = userService;
        this.responseUtil = responseUtil;

        this.walletService = walletService;
        this.transactitonsRepository = transactitonsRepository;
    }

    @Override
    public void SaveTransactions(TransactitonsEntity transactitonsEntity) {
        transactitonsRepository.save(transactitonsEntity);
    }
}
