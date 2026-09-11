package com.lingo_leap.service;

import com.lingo_leap.dto.AttachmentDTO;
import com.lingo_leap.dto.WordDto;
import com.lingo_leap.enums.Language;
import com.lingo_leap.model.History;
import com.lingo_leap.model.Word;
import com.lingo_leap.repository.AttachmentRepository;
import com.lingo_leap.repository.WordRepository;
import com.lingo_leap.utils.Mapper;
import com.lingo_leap.utils.RandomUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;

    private final HistoryService historyService;

    private final AttachmentService attachmentService;

    private final AttachmentRepository attachmentRepository;

    private final ReinforcementService reinforcementService;

    private final TtsService ttsService;

    public List<WordDto> getRandomWordsForUser(Long userId, Integer reinforcementRepetitionCount) {
        var findTodayHistoryByUser = historyService.findHistoryByUser(userId);
        var isReinforcement = reinforcementService.handleReinforcement(userId, findTodayHistoryByUser.size(), reinforcementRepetitionCount);

        var englishAttachment = new AttachmentDTO();
        var polishAttachment = new AttachmentDTO();

        List<Word> words;
        List<WordDto> wordDtos;

        words = wordRepository.findRandomWords();

        if (isReinforcement) {
            Integer randIndex = RandomUtil.getRandomFromRange(0, findTodayHistoryByUser.size());
            var inCorrectWord = wordRepository.findById(findTodayHistoryByUser.get(randIndex));
            words.set(0, inCorrectWord.get());
        } else {
            Boolean getFromHistory = RandomUtil.percentChance(40);
            List<History> todayCorrect = historyService.findAllOnlyCorrectTodayByUser(userId);
            List<History> allHistory = historyService.findAllExceptTodayHistoryByUser(userId);
            allHistory.addAll(todayCorrect);

            if (userId != 0 && getFromHistory && allHistory.size() > 50) {
                Long inCorrectWordId = historyService.findRandomWordMostCommonWrongHistoryByUser(allHistory, 20);
                var inCorrectWord = wordRepository.findById(inCorrectWordId);
                words.set(0, inCorrectWord.get());
            }
        }

        englishAttachment = attachmentService.findByWordIdAndLanguageWithOutData(words.get(0).getId(), Language.ENGLISH);
        polishAttachment = attachmentService.findByWordIdAndLanguageWithOutData(words.get(0).getId(), Language.POLISH);

        wordDtos = words.stream().map(word -> Mapper.mapWordsNoAttachments(word)).collect(Collectors.toList());
        wordDtos.get(0).setEnglishAttachment(englishAttachment);
        wordDtos.get(0).setPolishAttachment(polishAttachment);

        return wordDtos;
    }

    public Word saveWord(Word word) {
        return wordRepository.save(word);
    }

    public List<WordDto> findAll() {
        var words = wordRepository.findAllByOrderByEnglishAsc();
        var attachments = attachmentService.findAll();

        return words.stream()
                .map(word -> Mapper.mapWordWithAttachmentsName(word, attachments))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long deleteById(Long id) {
        wordRepository.deleteById(id);
        attachmentService.deleteAttachmentsByWordId(id);
        return id;
    }

    public List<WordDto> findAllByWordIds(List<Long> ids) {
        var words = wordRepository.findByIdIn(ids);

        return words.stream()
                .map(word -> Mapper.mapWordsNoAttachments(word))
                .collect(Collectors.toList());
    }

    public List<WordDto> findMostCommonWrongHistoryByUser(Long userId) {
        List<History> allHistory = historyService.findAllExceptTodayHistoryByUser(userId);
        List<Long> wordIds = historyService.findMostCommonWrongHistoryByUser(allHistory, 20);
        return findAllByWordIds(wordIds);
    }


    public void replaceWord(int wordIdStart, int wordIdEnd) {
        List<Long> ids = getIdsFromRange(wordIdStart, wordIdEnd);
        List<Word> words = wordRepository.findByIdIn(ids);

        for (Word word : words) {
            ttsService.getSoundExistenceWord(word);
        }
    }


    List<Long> getIdsFromRange(int wordIdStart, int wordIdEnd) {
        List<Long> ids = new ArrayList<>();
        for (int i = wordIdStart; i <= wordIdEnd; i++) {
            ids.add((long) i);
        }
        return ids;
    }




}
