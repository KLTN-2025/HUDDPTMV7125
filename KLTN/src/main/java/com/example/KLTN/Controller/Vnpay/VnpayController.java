    package com.example.KLTN.Controller.Vnpay;




    import com.example.KLTN.Service.VnpayService;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;

    @RestController
    @RequestMapping("/api/auth")
    public class VnpayController {

        private final VnpayService vnpayService;

        public VnpayController(VnpayService vnpayService) {
            this.vnpayService = vnpayService;
        }
        @PostMapping("/create")
        public ResponseEntity<?> createPayment(@RequestParam("amount") long amount,
                                               @RequestParam("orderInfo") String orderInfo,
                                               HttpServletRequest request) {
            String paymentUrl = vnpayService.createPaymentUrl(amount, orderInfo, request);
            return ResponseEntity.ok(paymentUrl);
        }   

        // Callback khi thanh toán xong
        @GetMapping("/callback")
        public String vnpayCallback(@RequestParam Map<String, String> params) {
            return "Thanh toán thành công! Dữ liệu trả về từ VNPay: " + params.toString();
        }
    }