package com.bearhive.bearhive.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bearhive.bearhive.Model.Dictation;
import com.bearhive.bearhive.Model.Sentence;
import com.bearhive.bearhive.Service.DictationService;

@Controller
@RequestMapping("/dictation")
public class DictationController {
    @Autowired
    private DictationService dictationService; 

    @GetMapping
    public String dictationHome(Model model,
            @RequestParam(value="tab", defaultValue="daily") String tab,
            @RequestParam(value="filter", defaultValue="learners") String filter) {
        //tab chinh
        List<Dictation> popularDictations = dictationService.getMostPopularDictations(filter);
        model.addAttribute("popularDictations", popularDictations);
        model.addAttribute("activeTab", tab);

        //xu ly cac tab
        if (tab.equals("daily")){
            List<Dictation> dailyDictations = dictationService.getDailyDictations();
            model.addAttribute("dailyDictations", dailyDictations);
        } else if (tab.equals("topic")) {
            model.addAttribute("dictationsByTopics", dictationService.getDictationsByTopics());
        } else if (tab.equals("difficulty")) {
            model.addAttribute("dictationsByDifficulty", dictationService.getDictationsByDifficulty());
        } 
        return "dictation";
    }

    @GetMapping("/content/{id}")
    public String dictationContent(@PathVariable Long id, @RequestParam(value="sentenceIndex", defaultValue="0") int sentenceIndex, Model model) {
        Dictation dictation = dictationService.getDictationById(id);
        if (dictation == null) {
            return "redirect:/dictation"; 
        }
        List<Sentence> sentences = dictationService.getSentencesByDictationId(id);
        model.addAttribute("dictation", dictation);
        model.addAttribute("sentences", sentences);
        if (sentenceIndex < 0) {
            sentenceIndex = 0;
        } else if (sentenceIndex >= sentences.size()) {
            sentenceIndex = sentences.size() - 1;
        }
        model.addAttribute("sentenceIndex", sentenceIndex);
        return "dictation-content";
    }
    
}
