package com.example.KLTN.Controller.Auth;

import com.example.KLTN.Config.HTTPstatus.HttpResponseUtil;
import com.example.KLTN.Config.config.JwtUtill;
import com.example.KLTN.Config.Email.EmaiCl;
import com.example.KLTN.Config.Email.RandomOTP;
import com.example.KLTN.dto.*;
import com.example.KLTN.Entity.RoleEntity;
import com.example.KLTN.Entity.UsersEntity;
import com.example.KLTN.Entity.WalletsEntity;
import com.example.KLTN.Service.RoleService;
import com.example.KLTN.Service.UserService;
import com.example.KLTN.Service.WallettService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class authController {
    private final UserService userService;
    private final RoleService roleService;
    private final JwtUtill jwtUtil;
    private final EmaiCl emailUtil;
    private final AuthenticationManager authenticationManager;
    private final HttpResponseUtil responseUtil;
    private final WallettService wallettService;



    // ===================== REGISTER =====================
    @PostMapping("/register")
    public ResponseEntity<Apireponsi<UsersEntity>> registerUser(@RequestBody RegisterUserDto dto) {
        try {
            if (userService.Exists(dto.getUsername())) {
                return responseUtil.conflict("Username đã tồn tại");
            }
            if (userService.ExistsEmail(dto.getEmail())) {
                return responseUtil.conflict("Email đã tồn tại");
            }
            RoleEntity role = roleService.finByRolename("USER");
            if (role == null) {
                return responseUtil.badRequest("ROLE không tồn tại");
            }
            UsersEntity user = new UsersEntity();

            user.setUsername(dto.getUsername());
            user.setPassword(jwtUtil.passwordEncoder().encode(dto.getPassword()));
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            user.setRole(role);
            user.setVerified(false);
            userService.SaveUser(user);
            WalletsEntity wallet = new WalletsEntity();
            wallet.setUser(user);
            wallet.setBalance(BigDecimal.ZERO);
            wallettService.SaveWallet(wallet);
            return responseUtil.created("Đăng ký thành công. Vui lòng gửi OTP để xác nhận Gmail.", user);
        } catch (Exception e) {
            return responseUtil.error("Lỗi khi đăng ký tài khoản", e);
        }
    }
    @PostMapping("/registerOwner")
    public ResponseEntity<Apireponsi<UsersEntity>> registerOwner(@RequestBody RegisterUserDto dto) {
        try {
            if (userService.Exists(dto.getUsername())) {
                return responseUtil.conflict("Username đã tồn tại");
            }
            if (userService.ExistsEmail(dto.getEmail())) {
                return responseUtil.conflict("Email đã tồn tại");
            }
            RoleEntity role = roleService.finByRolename("OWNER");
            if (role == null) {
                return responseUtil.badRequest("ROLE không tồn tại");
            }
            UsersEntity user = new UsersEntity();

            user.setUsername(dto.getUsername());
            user.setPassword(jwtUtil.passwordEncoder().encode(dto.getPassword()));
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            user.setRole(role);
            user.setVerified(false);
            userService.SaveUser(user);
            WalletsEntity wallet = new WalletsEntity();
            wallet.setUser(user);
            wallet.setBalance(BigDecimal.ZERO);
            wallettService.SaveWallet(wallet);
            return responseUtil.created("Đăng ký thành công. Vui lòng gửi OTP để xác nhận Gmail.", user);
        } catch (Exception e) {
            return responseUtil.error("Lỗi khi đăng ký tài khoản", e);
        }
    }

    // ===================== SEND OTP =====================
    @PostMapping("/send-otp")
    public ResponseEntity<Apireponsi<String>> sendOtp(@RequestParam String email) {
        try {
            UsersEntity user = userService.findByEmail(email);
            if (user == null) {
                return responseUtil.notFound("Email không tồn tại");
            }
            if (user.isVerified()) {
                return responseUtil.badRequest("Email đã xác thực, không cần gửi lại OTP");
            }
            String otp = RandomOTP.generateOTP(6);
            user.setOtp(otp);
            user.setTimeExpired(LocalDateTime.now().plusMinutes(5));
            userService.SaveUser(user);
            emailUtil.sendOTP(user.getEmail(), otp);
            return responseUtil.ok("OTP đã được gửi đến email.");
        } catch (Exception e) {
            return responseUtil.error("Gửi OTP thất bại", e);
        }
    }

    // ===================== VERIFY OTP =====================
    @PostMapping("/verify-otp")
    public ResponseEntity<Apireponsi<String>> verifyOtp(@RequestBody VerifyDTO dto) {
        try {
            UsersEntity user = userService.findByEmail(dto.getEmail());
            if (user == null) return responseUtil.notFound("User không tồn tại");
            if (user.isVerified()) return responseUtil.badRequest("User đã được xác thực");

            boolean otpValid = user.getOtp() != null
                    && user.getOtp().equals(dto.getOtp())
                    && user.getTimeExpired().isAfter(LocalDateTime.now());
            if (!otpValid) return responseUtil.badRequest("OTP sai hoặc đã hết hạn");
            user.setOtp(null);
            user.setVerified(true);
            user.setTimeExpired(null);
            userService.SaveUser(user);
            return responseUtil.ok("Xác nhận email thành công.");
        } catch (Exception e) {
            return responseUtil.error("Lỗi khi xác thực OTP", e);
        }
    }

    // ===================== LOGIN =====================
    @PostMapping("/login")
    public ResponseEntity<Apireponsi<String>> login(@RequestBody authRequesDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UsersEntity user = userService.FindByUsername(dto.getUsername());
            if (user == null) return responseUtil.notFound("User không tồn tại");
            if (!user.isVerified()) return responseUtil.badRequest("Tài khoản chưa xác thực email");

            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new Apireponsi<>(HttpStatus.OK,"Login thành công",token,null));

        } catch (BadCredentialsException e) {
            return responseUtil.unauthorized("Sai tài khoản hoặc mật khẩu");
        } catch (Exception e) {
            return responseUtil.error("Lỗi khi đăng nhập", e);
        }
    }
}
