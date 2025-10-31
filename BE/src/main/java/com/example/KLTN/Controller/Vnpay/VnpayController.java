        package com.example.KLTN.Controller.Vnpay;




        import com.example.KLTN.Service.VnpayService;
        import jakarta.servlet.http.HttpServletRequest;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

        import java.util.Map;
        import java.util.stream.Collectors;

        @RestController
        @RequestMapping("/api/auth")
        public class VnpayController {
            @Autowired
            private VnpayService vnPayService;

            @PostMapping("/create")
            public ResponseEntity<?> createPayment(HttpServletRequest request,
                                                   @RequestParam long amount,
                                                   @RequestParam String orderInfo,
                                                   @RequestParam String orderType) {
                String redirectUrl = vnPayService.createRedirectUrl(request, amount, orderInfo, orderType);
                return ResponseEntity.ok(Map.of("url", redirectUrl));
            }

            @GetMapping("/vnpay-return")
            public ResponseEntity<?> vnpayReturn(HttpServletRequest request) {
                Map<String, String> params = request.getParameterMap().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));

                boolean isValid = vnPayService.validateReturn(params);
                if (!isValid) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
                }

                String txnRef = params.get("vnp_TxnRef");
                String responseCode = params.get("vnp_ResponseCode");
                String amount = params.get("vnp_Amount");
                // Xử lý cập nhật đơn hàng trong DB theo txnRef, amount, responseCode

                if ("00".equals(responseCode)) {
                    // thành công
                    return ResponseEntity.ok("Payment successful: " + txnRef);
                } else {
                    // thất bại
                    return ResponseEntity.ok("Payment failed: " + txnRef);
                }
            }
        }