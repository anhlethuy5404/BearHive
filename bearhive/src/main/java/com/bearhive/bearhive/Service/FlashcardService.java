package com.bearhive.bearhive.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Model.Flashcard;
import com.bearhive.bearhive.Repository.FlashcardRepository;

@Service
public class FlashcardService {
    @Autowired
    private FlashcardRepository flashcardRepository;

    public List<Flashcard> getAllFlashcards() {
        return flashcardRepository.findAll();
    }
    
    public Flashcard getFlashcardById(Long id) {
        return flashcardRepository.findById(id).orElseThrow(() -> new RuntimeException("Flashcard not found"));
    }
}
