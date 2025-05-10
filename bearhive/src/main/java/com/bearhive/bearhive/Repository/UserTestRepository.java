package com.bearhive.bearhive.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.UserTest;

@Repository
public interface UserTestRepository extends JpaRepository<UserTest, Long>{
    @Query("SELECT ut FROM UserTest ut WHERE ut.test.id = :testId AND ut.user.id = :userId " +
           "ORDER BY ut.completedDate DESC LIMIT 1")
    Optional<UserTest> findLatestByTestIdAndUserId(@Param("testId") Long testId, @Param("userId") Long userId);
}
