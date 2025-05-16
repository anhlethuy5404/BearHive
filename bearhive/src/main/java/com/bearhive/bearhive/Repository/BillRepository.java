package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Bill findByOrderId(String orderId);
    List<Bill> findByUserId(Long userId);
}
