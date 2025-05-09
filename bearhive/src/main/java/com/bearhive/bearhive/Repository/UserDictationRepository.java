package com.bearhive.bearhive.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.UserDictation;

@Repository
public interface UserDictationRepository extends JpaRepository<UserDictation, Long>{
    UserDictation findByUserIdAndDictationId(Long userId, Long dictationId); 
}
