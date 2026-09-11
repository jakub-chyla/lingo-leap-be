package com.lingo_leap.controller;

import com.lingo_leap.dto.WordDto;
import com.lingo_leap.model.Word;
import com.lingo_leap.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/word") //TODO: refactor name
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @PostMapping()
    public ResponseEntity<Word> saveWord(@RequestBody Word word) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(wordService.saveWord(word));
    }

    @GetMapping
    public ResponseEntity<List<WordDto>> findAll() {
        var words = wordService.findAll();
        return words.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(words);
    }

    @GetMapping("/most-wrong/{userId}")
    public ResponseEntity<List<WordDto>> getMostCommonWrongHistoryByUser(@PathVariable Long userId){
        return ResponseEntity.ok(wordService.findMostCommonWrongHistoryByUser(userId));
    }

    @GetMapping("/random")
    public ResponseEntity<List<WordDto>> getRandom(Long userId, Integer reinforcementRepetitionCount) {
        var randomWords = wordService.getRandomWordsForUser(userId, reinforcementRepetitionCount);
        return randomWords.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(randomWords);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteById(@PathVariable String id) {
        return ResponseEntity.ok(wordService.deleteById(Long.parseLong(id)));
    }

    @GetMapping("/replace-word/{wordIdStart}/{wordIdEnd}")
    public void replaceWords(@PathVariable int wordIdStart, @PathVariable int wordIdEnd){
        wordService.replaceWord(wordIdStart, wordIdEnd);
    }

}
