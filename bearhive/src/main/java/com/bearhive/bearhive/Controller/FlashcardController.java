package com.bearhive.bearhive.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bearhive.bearhive.Model.Flashcard;
import com.bearhive.bearhive.Service.FlashcardService;

@Controller
public class FlashcardController { 
    @Autowired
    private FlashcardService flashcardService; 
    
    @GetMapping("/flashcard")
    public String flashcard(Model model) {
        model.addAttribute("flashcards", flashcardService.getAllFlashcards());
        return "flashcard";
    }

    @GetMapping("/flashcard/{id}")
    public String getFlashcardInfo(@PathVariable Long id, Model model) {
        Flashcard flashcard = flashcardService.getFlashcardById(id);
        model.addAttribute("flashcard", flashcard);
        model.addAttribute("words", flashcard.getWords());
        return "flashcard-info";
    }

    @GetMapping("/flashcard/{id}/content")
    public String getFlashcardContent(@PathVariable Long id, Model model) {
        Flashcard flashcard = flashcardService.getFlashcardById(id);
        model.addAttribute("flashcard", flashcard);
        model.addAttribute("words", flashcard.getWords());
        return "flashcard-content";
    }
}
