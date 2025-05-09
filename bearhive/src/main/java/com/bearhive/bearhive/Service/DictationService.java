package com.bearhive.bearhive.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bearhive.bearhive.Model.Dictation;
import com.bearhive.bearhive.Model.Sentence;
import com.bearhive.bearhive.Repository.DictationRepository;
import com.bearhive.bearhive.Repository.SentenceRepository;

@Service
public class DictationService {
    @Autowired
    private DictationRepository dictationRepository;

    @Autowired
    private SentenceRepository sentenceRepository;

    public List<Dictation> getAllDictations() {
        return dictationRepository.findAll();
    }

    //lay random 5 cai cho daily
    public List<Dictation> getDailyDictations() {
        return dictationRepository.findRandomDictations();
    }

    //filter theo top
    public List<Dictation> getMostPopularDictations(String filter) {
        if (filter.equals("rating")) {
            return dictationRepository.findTop10ByOrderByRatingDesc();
        } else if (filter.equals("learners")) {
            return dictationRepository.findTop10ByOrderByLearnersDesc();
        } else {
            return dictationRepository.findTop10ByOrderByRatingDesc(); 
        }
    }

    //lay ds theo topics
    public Map<String, List<Dictation>> getDictationsByTopics() {
        List<String> types = dictationRepository.findDistinctTypes();
        return types.stream()
                .collect(Collectors.toMap(
                    type -> type,
                    type -> dictationRepository.findByType(type)
                ));
    }

    //lay ds theo difficulty
    public Map<String, List<Dictation>> getDictationsByDifficulty() {
        return Map.of(
            "Easy", dictationRepository.findByDifficulty("Easy"),
            "Medium", dictationRepository.findByDifficulty("Medium"),
            "Hard", dictationRepository.findByDifficulty("Hard")
        );
    }

    public Dictation getDictationById(Long id) {
        return dictationRepository.findById(id).orElse(null);
    }

    public List<Sentence> getSentencesByDictationId(Long dictationId) {
        return sentenceRepository.findByDictationIdOrderByOrderNumber(dictationId);
    }
}
