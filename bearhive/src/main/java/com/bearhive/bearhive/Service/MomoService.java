package com.bearhive.bearhive.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Config.MomoConfig;
import com.bearhive.bearhive.Model.Bill;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Repository.BillRepository;
import com.bearhive.bearhive.Repository.UserCourseRepository;
import com.bearhive.bearhive.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class MomoService {
    @Autowired
    private MomoConfig momoConfig;
    
    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserCourseRepository userCourseRepository;

    public String createPaymentRequest(String amount, Long userId, List<Long> courseIds) {
        try {
            // Generate requestId and orderId
            String requestId = momoConfig.PARTNER_CODE + new Date().getTime();
            String orderId = requestId;
            String orderInfo = "BearHive Course Payment";
            String extraData = "";

            // Generate raw signature
            String rawSignature = String.format(
                    "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                    momoConfig.ACCESS_KEY, amount, extraData, momoConfig.IPN_URL, orderId, orderInfo, momoConfig.PARTNER_CODE, momoConfig.REDIRECT_URL,
                    requestId, momoConfig.REQUEST_TYPE);

            // Sign with HMAC SHA256
            String signature = signHmacSHA256(rawSignature, momoConfig.SECRET_KEY);
            System.out.println("Generated Signature: " + signature);

            // Create a bill record in pending state
            createPendingBill(userId, courseIds, Long.parseLong(amount), orderId);

            JSONObject requestBody = new JSONObject();
            requestBody.put("partnerCode", momoConfig.PARTNER_CODE);
            requestBody.put("accessKey", momoConfig.ACCESS_KEY);
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", momoConfig.REDIRECT_URL);
            requestBody.put("ipnUrl", momoConfig.IPN_URL);
            requestBody.put("extraData", extraData);
            requestBody.put("requestType", momoConfig.REQUEST_TYPE);
            requestBody.put("signature", signature);
            requestBody.put("lang", "en");

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(momoConfig.ENDPOINT);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                System.out.println("Response from MoMo: " + result.toString());
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to create payment request: " + e.getMessage() + "\"}";
        }
    }

    @Transactional
    private void createPendingBill(Long userId, List<Long> courseIds, Long totalAmount, String orderId) {
        try {
            // Create a new bill
            Bill bill = new Bill();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            bill.setUser(user);
            bill.setBillCode("BH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            bill.setTotalAmount(totalAmount);
            bill.setCreatedAt(LocalDateTime.now());
            bill.setOrderId(orderId);
            bill.setPaymentStatus("PENDING");
            
            Bill savedBill = billRepository.save(bill);
            
            // Update UserCourse records with the bill reference
            for (Long courseId : courseIds) {
                UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId);
                if (userCourse != null) {
                    userCourse.setBill(savedBill);
                    userCourse.setStatus("PENDING");
                    userCourseRepository.save(userCourse);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create pending bill: " + e.getMessage());
        }
    }

    // HMAC SHA256 signing method
    private static String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Transactional
    public String checkPaymentStatus(String orderId) {
        try {
            // Generate requestId
            String requestId = momoConfig.PARTNER_CODE + new Date().getTime();

            // Generate raw signature for the status check
            String rawSignature = String.format(
                    "accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
                    momoConfig.ACCESS_KEY, orderId, momoConfig.PARTNER_CODE, requestId);

            // Sign with HMAC SHA256
            String signature = signHmacSHA256(rawSignature, momoConfig.SECRET_KEY);
            System.out.println("Generated Signature for Status Check: " + signature);

            // Prepare request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("partnerCode", momoConfig.PARTNER_CODE);
            requestBody.put("accessKey", momoConfig.ACCESS_KEY);
            requestBody.put("requestId", requestId);
            requestBody.put("orderId", orderId);
            requestBody.put("signature", signature);
            requestBody.put("lang", "en");

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://test-payment.momo.vn/v2/gateway/api/query");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                String responseString = result.toString();
                System.out.println("Response from MoMo (Status Check): " + responseString);
                
                // Update bill and userCourse status based on response
                JSONObject jsonResponse = new JSONObject(responseString);
                if (jsonResponse.has("resultCode") && jsonResponse.getInt("resultCode") == 0) {
                    updatePaymentStatus(orderId, "SUCCESS", String.valueOf(jsonResponse.get("transId")));
                }
                
                return responseString;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to check payment status: " + e.getMessage() + "\"}";
        }
    }
    
    @Transactional
    public void updatePaymentStatus(String orderId, String status, String transactionId) {
        Bill bill = billRepository.findByOrderId(orderId);
        if (bill != null) {
            bill.setPaymentStatus(status);
            bill.setMomoTransactionId(transactionId);
            billRepository.save(bill);
            
            // Update UserCourse linked to bill
            List<UserCourse> userCourses = userCourseRepository.findByBillId(bill.getId());
            for (UserCourse userCourse : userCourses) {
                if ("SUCCESS".equals(status)) {
                    userCourse.setStatus("PURCHASED");
                } else if ("CANCELLED".equals(status)) {
                    userCourse.setStatus("CART"); 
                } else {
                    userCourse.setStatus(status);
                }
                userCourseRepository.save(userCourse);
            }
        }
    }
    
    @Transactional
    public void processIpnCallback(String requestBody) {
        try {
            JSONObject jsonRequest = new JSONObject(requestBody);
            String orderId = jsonRequest.getString("orderId");
            int resultCode = jsonRequest.getInt("resultCode");
            
            String status;
            String transId = null;
            
            switch (resultCode) {
                case 0:
                    // Payment successful
                    status = "SUCCESS";
                    transId = jsonRequest.has("transId") ? String.valueOf(jsonRequest.get("transId")) : null;
                    break;
                case 99:
                    // Payment cancelled by user
                    status = "CANCELLED";
                    break;
                case 7:
                    // Insufficient balance
                    status = "INSUFFICIENT_BALANCE";
                    break;
                case 9:
                    // Transaction limit exceeded
                    status = "LIMIT_EXCEEDED";
                    break;
                case 1001:
                    // Fraud detection
                    status = "REJECTED";
                    break;
                default:
                    // Other failures
                    status = "FAILED";
                    break;
            }
            
            updatePaymentStatus(orderId, status, transId);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
