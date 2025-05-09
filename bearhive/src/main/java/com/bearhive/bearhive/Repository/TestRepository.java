package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long>{
    List<Test> findByUserId(Long userId);
}
