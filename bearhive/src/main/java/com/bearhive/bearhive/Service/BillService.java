package com.bearhive.bearhive.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Model.Bill;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Repository.BillRepository;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepository;

    public Bill findByOrderId(String orderId) {
        return billRepository.findByOrderId(orderId);
    }

    public Bill createBill(User user, List<UserCourse> userCourses, String orderId, Long totalAmount) {
        Bill bill = new Bill();
        bill.setUser(user);
        bill.setUserCourses(userCourses);
        bill.setBillCode("HD" + System.currentTimeMillis());
        bill.setTotalAmount(totalAmount);
        bill.setCreatedAt(LocalDateTime.now());
        bill.setOrderId(orderId);
        bill = billRepository.save(bill);

        // for (UserCourse uc : userCourses) {
        //     uc.setBill(bill);
        // }
        return bill;
    }

    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }
    
    public Optional<Bill> findById(Long id) {
        return billRepository.findById(id);
    }
    
    public List<Bill> findByUserId(Long userId) {
        return billRepository.findByUserId(userId);
    }
}
