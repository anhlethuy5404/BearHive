package com.bearhive.bearhive.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bearhive.bearhive.Model.Dictation;


@Repository
public interface DictationRepository extends JpaRepository<Dictation, Long> {
    List<Dictation> findByType(String type);
    List<Dictation> findByDifficulty(String difficulty);
    List<Dictation> findTop10ByOrderByRatingDesc(); //xep theo rating
    List<Dictation> findTop10ByOrderByLearnersDesc(); //xep theo so luong nguoi lam
    @Query (value = "select * from dictation order by rand() limit 5", nativeQuery = true)
    List<Dictation> findRandomDictations(); //lay ngau nhien 5 cai dictation
    @Query ("select distinct d.type from Dictation d")
    List<String> findDistinctTypes(); //lay ds cac type
}
