package com.bearhive.bearhive.Controller;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bearhive.bearhive.Model.Bill;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Service.BillService;
import com.bearhive.bearhive.Service.MomoService;
import com.bearhive.bearhive.Service.UserCourseService;

@Controller
public class CartController {
    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillService billService;

    @Autowired
    private MomoService momoService;

    @GetMapping("/cart")
    public String showCart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<UserCourse> cartItems = new ArrayList<>();

        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
            cartItems = userCourseService.findByUserIdAndStatus(user.getId(), "CART");
        }

        model.addAttribute("cartItems", cartItems);
        return "cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
            UserCourse userCourse = userCourseService.findById(id)
                    .filter(uc -> uc.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid cart item ID"));
            userCourseService.delete(userCourse);
            redirectAttributes.addFlashAttribute("message", "Course removed from cart!");
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout(@RequestParam("selectedCourseIds") String selectedCourseIds,
                           @RequestParam("totalAmount") String totalAmount,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            List<Long> courseIdsList = Arrays.stream(selectedCourseIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            
            Long userId = userCourseService.getUserIdFromPrincipal(principal);
            
            // Call MomoService 
            String response = momoService.createPaymentRequest(totalAmount, userId, courseIdsList);
            
            // Parse payment URL from MoMo response
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("payUrl")) {
                String payUrl = jsonResponse.getString("payUrl");
                return "redirect:" + payUrl;
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to create payment URL");
                return "redirect:/cart";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Payment processing error: " + e.getMessage());
            return "redirect:/cart";
        }
    }
    
    // MoMo redirect after payment
    @GetMapping("/confirmation")
    public String confirmPayment(@RequestParam(value = "orderId", required = false) String orderId,
                                 @RequestParam(value = "resultCode", required = false) String resultCode,
                                 RedirectAttributes redirectAttributes, Model model) {
        try {
            if (orderId != null && resultCode != null) {
                switch (resultCode) {
                    case "0":
                        // Payment successful
                        String statusResponse = momoService.checkPaymentStatus(orderId);
                        try{
                        JSONObject jsonResponse = new JSONObject(statusResponse);
                        
                        if (jsonResponse.has("resultCode") && jsonResponse.getInt("resultCode") == 0) {
                            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công! Các khóa học của bạn đã sẵn sàng.");
                            // Update status SUCCESS
                            String transId = jsonResponse.has("transId") ? String.valueOf(jsonResponse.get("transId")) : "";
                            momoService.updatePaymentStatus(orderId, "SUCCESS", transId);
                            model.addAttribute("success", "Thanh toán thành công! Các khóa học của bạn đã sẵn sàng.");
                            Bill bill = billService.findByOrderId(orderId);
                            model.addAttribute("bill", bill);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                            model.addAttribute("createdAtFormatted", bill.getCreatedAt().format(formatter));
                            model.addAttribute("user", bill.getUser());
                            return "confirmation"; 
                        } else {
                            redirectAttributes.addFlashAttribute("error", "Xác minh thanh toán thất bại. Vui lòng liên hệ hỗ trợ.");
                            momoService.updatePaymentStatus(orderId, "FAILED", null);
                            model.addAttribute("error", "Xác minh thanh toán thất bại. Vui lòng liên hệ hỗ trợ.");
                            return "redirect:/cart"; 
                        }
                    }
                    catch (Exception ex) {
                            ex.printStackTrace();
                            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý xác nhận thanh toán (JSON): " + ex.getMessage() + ". Phản hồi: " + statusResponse);
                            return "redirect:/cart";
                        }
                    case "1001":
                        redirectAttributes.addFlashAttribute("error", "Giao dịch bị từ chối bởi hệ thống chống gian lận.");
                        momoService.updatePaymentStatus(orderId, "REJECTED", null);
                        break;
                    case "99":
                        redirectAttributes.addFlashAttribute("warning", "Thanh toán đã bị hủy. Bạn có thể thử lại sau.");
                        momoService.updatePaymentStatus(orderId, "CANCELLED", null);
                        break;
                    case "7":
                        redirectAttributes.addFlashAttribute("error", "Giao dịch bị từ chối do số dư không đủ.");
                        momoService.updatePaymentStatus(orderId, "INSUFFICIENT_BALANCE", null);
                        break;
                    case "9":
                        redirectAttributes.addFlashAttribute("error", "Giao dịch thất bại do vượt quá hạn mức giao dịch.");
                        momoService.updatePaymentStatus(orderId, "LIMIT_EXCEEDED", null);
                        break;
                    default:
                        redirectAttributes.addFlashAttribute("error", "Thanh toán không thành công. Mã lỗi: " + resultCode);
                        momoService.updatePaymentStatus(orderId, "FAILED", null);
                        break;
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Phản hồi thanh toán không hợp lệ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý xác nhận thanh toán: " + e.getMessage());
        }
        
        return "redirect:/cart"; 
    }
    
    // MoMo IPN 
    @PostMapping("/momo-notify")
    public ResponseEntity<String> momoNotify(@RequestBody String requestBody) {
        try {
            momoService.processIpnCallback(requestBody);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
