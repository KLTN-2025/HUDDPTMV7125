package com.example.KLTN.Controller.Vnpay;

import com.example.KLTN.DTO.Apireponsi;
import com.example.KLTN.Entity.UsersEntity;
import com.example.KLTN.Entity.WalletsEntity;
import com.example.KLTN.Service.UserService;
import com.example.KLTN.Service.WallettService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.KLTN.Service.withdrawhistoryService;
import com.example.KLTN.DTO.withDrawDTO;
import com.example.KLTN.Config.HTTPstatus.HttpResponseUtil;
import com.example.KLTN.Entity.withDrawHistoryEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/withdraw")
@RequiredArgsConstructor
public class withdrawmoneyController {
    private final withdrawhistoryService withdrawservice;
    private final HttpResponseUtil httpResponseUtil;
    private final UserService userservice;
    private final WallettService wallettService;

    @PostMapping("/create")
    public ResponseEntity<Apireponsi<withDrawHistoryEntity>> create(@RequestBody withDrawDTO DTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            UsersEntity user = userservice.FindByUsername(username);
            WalletsEntity wallets = wallettService.GetWallet(user);
            if (wallets == null) {
                return httpResponseUtil.badRequest("Không tồn ta walles");
            }
            BigDecimal amout1 = BigDecimal.valueOf(DTO.getAmount());
            if (wallets.getBalance().compareTo(amout1) <= 0) {
                return httpResponseUtil.badRequest("Số tền không đủ");
            }
            withDrawHistoryEntity withDraw = new withDrawHistoryEntity();
            withDraw.setAmount(DTO.getAmount());
            withDraw.setCreate_AT(LocalDateTime.now());
            withDraw.setUpdate_AT(null);
            withDraw.setBankName(DTO.getBankName());
            withDraw.setAccountNumber(DTO.getAccountNumber());
            withDraw.setWalletsEntity(wallets);
            withDraw.setStatus(withDrawHistoryEntity.Status.pending);
            withDraw.setAccountHolderName(DTO.getAccountHolderName());
            withdrawservice.saveWithdraw(withDraw);
            return httpResponseUtil.created("Tạo mới thành công giao dịch ", withDraw);

        } catch (Exception e) {
            return httpResponseUtil.error("Create Erorr", e);
        }
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Apireponsi<withDrawHistoryEntity>> approve(@PathVariable Long id) {
        try {
            withDrawHistoryEntity withdraw = withdrawservice.findByid(id);
            if (withdraw == null) {
                return httpResponseUtil.badRequest("Không tồn tại WithdrawHistory");
            }
            if (withdraw.getStatus().equals(withDrawHistoryEntity.Status.resolved)) {
                return httpResponseUtil.badRequest("Đã Được Xử lí");
            }
            if (withdraw.getStatus().equals(withDrawHistoryEntity.Status.refuse)) {
                return httpResponseUtil.badRequest("Đã Được Xử lí");
            }
          WalletsEntity wallets = withdraw.getWalletsEntity();
            double amout = withdraw.getAmount();
            BigDecimal amout1 = BigDecimal.valueOf(amout);
            if (wallets.getBalance().compareTo(amout1) <= 0) {
                return httpResponseUtil.badRequest("Số tiền không đủ để rút");
            }
            wallets.setBalance(wallets.getBalance().subtract(amout1));
            withdraw.setStatus(withDrawHistoryEntity.Status.resolved);
            withdraw.setUpdate_AT(LocalDateTime.now());
            withdrawservice.saveWithdraw(withdraw);
return httpResponseUtil.ok("Đã phê duyệt rút tiền" +"");
        } catch (Exception e) {
            return httpResponseUtil.error("resolved Erorr", e);
        }
    }
    @PutMapping("/reject/{id}")
    public ResponseEntity<Apireponsi<withDrawHistoryEntity>> reject(@PathVariable Long id) {
        try {
            withDrawHistoryEntity withdraw = withdrawservice.findByid(id);
            if (withdraw == null) {
                return httpResponseUtil.badRequest("Không tồn tại WithdrawHistory");
            }
            if (withdraw.getStatus().equals(withDrawHistoryEntity.Status.refuse)) {
                return httpResponseUtil.badRequest("Đã Được Xử lí");
            }
            if (withdraw.getStatus().equals(withDrawHistoryEntity.Status.resolved)) {
                return httpResponseUtil.badRequest("Đã Được Xử lí");
            }
            else {
            withdraw.setStatus(withDrawHistoryEntity.Status.refuse);
            withdraw.setUpdate_AT(LocalDateTime.now());
            withdrawservice.saveWithdraw(withdraw);
            return httpResponseUtil.ok("Đã từ chối phê duyệt");}
        } catch (Exception e) {
            return httpResponseUtil.error("refuse Erorr", e);
        }
    }
}
