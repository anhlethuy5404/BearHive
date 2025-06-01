package com.bearhive.bearhive.Controller;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bearhive.bearhive.Model.Bill;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Model.VnpayRequest;
import com.bearhive.bearhive.Repository.UserCourseRepository;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Service.BillService;
import com.bearhive.bearhive.Service.VnpayService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/api/vnpay")
@AllArgsConstructor
public class VnpayController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BillService billService; 
    @Autowired
    private UserCourseRepository userCourseRepository; 

    private final VnpayService vnpayService;

    @PostMapping("/create")
    public ResponseEntity<String> createVnpayPayment(@RequestBody VnpayRequest paymentRequest) {
        try {
            User user = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Người dùng chưa đăng nhập!");
            }
            String email = authentication.getName();
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
            Bill bill = new Bill();
            bill.setUser(user);
            bill.setTotalAmount(Long.parseLong(paymentRequest.getAmount())); 
            bill.setCreatedAt(java.time.LocalDateTime.now());
            bill.setBillCode("BH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            bill.setPaymentStatus("PENDING");
            String orderId = "VNPAY" + System.currentTimeMillis();
            bill.setOrderId(orderId);
            Bill savedBill = billService.save(bill);

            paymentRequest.setOrderId(savedBill.getOrderId()); 
            String paymentUrl = vnpayService.createPayment(paymentRequest);
            return ResponseEntity.ok(paymentUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi tạo thanh toán!");
        }
    }

    @GetMapping("/return")
    public String returnPayment(
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("vnp_TxnRef") String txnRef,
            @RequestParam(value = "vnp_TransactionNo", required = false) String transactionNo,
            @RequestParam(value = "vnp_OrderInfo", required = false) String orderInfo,
            Model model) {
        
        try {
            if ("00".equals(responseCode)) {
                Bill bill = billService.findByOrderId(txnRef);
                if (bill != null) {
                    bill.setPaymentStatus("SUCCESS");
                    bill.setMomoTransactionId(transactionNo); 
                    billService.save(bill);
                    
                    // Cập nhật trạng thái các course trong giỏ hàng thành PURCHASED
                    updateUserCoursesToPurchased(bill);
                    
                    model.addAttribute("success", "Thanh toán thành công! Các khóa học của bạn đã sẵn sàng.");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                            model.addAttribute("createdAtFormatted", bill.getCreatedAt().format(formatter));
                            model.addAttribute("user", bill.getUser());
                    model.addAttribute("bill", bill);
                    return "confirmation"; 
                } else {
                    model.addAttribute("error", "Xác minh thanh toán thất bại. Vui lòng liên hệ hỗ trợ.");
                    return "redirect:/cart"; 
                }
            } else {
                // Thanh toán thất bại
                Bill bill = billService.findByOrderId(txnRef);
                if (bill != null) {
                    bill.setPaymentStatus("FAILED");
                    billService.save(bill);
                }
                
                model.addAttribute("error", "Thanh toán thất bại.");
                return "redirect:/cart"; 
            }
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi thanh toán");
            return "redirect:/cart"; 
        }
    }
    
    private void updateUserCoursesToPurchased(Bill bill) {
        try {
            // Lấy tất cả UserCourse trong bill này
            if (bill.getUserCourses() != null && !bill.getUserCourses().isEmpty()) {
                for (UserCourse userCourse : bill.getUserCourses()) {
                    if ("CART".equals(userCourse.getStatus())) {
                        userCourse.setStatus("PURCHASED");
                        userCourseRepository.save(userCourse);
                    }
                }
            } else {
                updateCartItemsToPurchased(bill.getUser(), bill);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật trạng thái course: " + e.getMessage());
        }
    }
    
    private void updateCartItemsToPurchased(User user, Bill bill) {
        try {
            var cartItems = userCourseRepository.findByUserIdAndStatus(user.getId(), "CART");
            
            for (UserCourse cartItem : cartItems) {
                cartItem.setStatus("PURCHASED");
                cartItem.setBill(bill);
                userCourseRepository.save(cartItem);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật giỏ hàng: " + e.getMessage());
        }
    }
}
