package com.example.KLTN.Controller.Vnpay;

import com.example.KLTN.dto.Apireponsi;
import com.example.KLTN.Entity.UsersEntity;
import com.example.KLTN.Entity.WalletsEntity;
import com.example.KLTN.Service.UserService;
import com.example.KLTN.Service.WallettService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.KLTN.Service.withdrawhistoryService;
import com.example.KLTN.dto.withDrawDTO;
import com.example.KLTN.Config.HTTPstatus.HttpResponseUtil;
import com.example.KLTN.Entity.withDrawHistoryEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/withdraw")
@RequiredArgsConstructor
public class withdrawmoneyController {
    private final withdrawhistoryService withdrawhistoryService;

    @PostMapping("/create")
    public ResponseEntity<Apireponsi<withDrawHistoryEntity>> create(@RequestBody withDrawDTO dto) {
        return withdrawhistoryService.createWithdraw(dto);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Apireponsi<withDrawHistoryEntity>> approve(@PathVariable Long id) {
        return withdrawhistoryService.approveWithdraw(id);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<Apireponsi<withDrawHistoryEntity>> reject(@PathVariable Long id) {
        return withdrawhistoryService.rejectWithdraw(id);
    }
}
