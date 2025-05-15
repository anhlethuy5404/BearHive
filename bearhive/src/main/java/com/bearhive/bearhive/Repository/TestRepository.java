package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long>{
    @Query("SELECT t FROM Test t WHERE t.user.id = :userId ORDER BY t.id DESC")
    List<Test> findByUserId(Long userId);
}
