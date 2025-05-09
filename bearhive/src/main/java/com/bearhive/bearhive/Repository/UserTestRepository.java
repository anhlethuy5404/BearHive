package com.bearhive.bearhive.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.UserTest;

@Repository
public interface UserTestRepository extends JpaRepository<UserTest, Long>{

}
