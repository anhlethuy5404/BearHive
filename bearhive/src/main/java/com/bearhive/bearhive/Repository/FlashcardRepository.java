package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Flashcard;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    @Query("SELECT f FROM Flashcard f WHERE f.user.id = :userId ORDER BY f.id DESC")
    List<Flashcard> findByUserId(Long userId);

}
