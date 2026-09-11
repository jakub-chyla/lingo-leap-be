package com.lingo_leap.repository;

import com.lingo_leap.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findAllByOrderByEnglishAsc();

    @Query(value = "SELECT * FROM words ORDER BY RANDOM() LIMIT 9", nativeQuery = true)
    List<Word> findRandomWords();

    List<Word> findByIdIn(List<Long> ids);

    @Query("SELECT w FROM Word w WHERE NOT EXISTS (SELECT 1 FROM Attachment a WHERE a.wordId = w.id)")
    List<Word> findWordsWithOutAttachments();
}
