package com.lingo_leap.service;

import com.lingo_leap.dto.Answer;
import com.lingo_leap.kafka.producer.HistoryKafka;
import com.lingo_leap.model.History;
import com.lingo_leap.model.User;
import com.lingo_leap.repository.HistoryRepository;
import com.lingo_leap.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final ReinforcementService reinforcementService;
    private final UserDetailsServiceImp userDetailsService;
    private final WordRepository wordRepository;
    private final KafkaTemplate<String, HistoryKafka> kafkaTemplate;
    private static final String TOPIC = "NewTopic";

    public Integer saveAnswer(Answer answer) {
        Long userId = userDetailsService.getLoggedInUser().getId();

        var history = new History();
        history.setUserId(userId);
        history.setWordAskedId(answer.wordId());
        history.setIsCorrect(answer.isCorrect());
        boolean reinforcement = reinforcementService.isReinforcement(userId);
        boolean correct = answer.isCorrect();
        if (reinforcement && correct) {
            historyRepository.save(history);
        }

        if (!reinforcement) {
            historyRepository.save(history);
        }

        var word = wordRepository.findById(answer.wordId());
        if (word.isPresent()){
            User user = userDetailsService.getLoggedInUser();

            HistoryKafka dto = new HistoryKafka(user.getUsername(), word.get().getEnglish(), answer.isCorrect());
            kafkaTemplate.send(TOPIC, dto);
        }

        return findHistoryByUser(userId).size();
    }

    public Integer findCountOfIncorrect(Long userId) {
        return findHistoryByUser(userId).size();
    }

    public List<Long> findByUserIdAndWordIdLastInCorrect(Long userId, Integer limit) {
        return historyRepository.findByUserIdAndWordIdLastInCorrect(userId, limit);
    }

    public List<Long> findHistoryByUser(Long userId) {
        return findHistoryByUser(historyRepository.findTodayHistoryByUser(userId));
    }

    public List<Long> findHistoryByUser(List<History> histories) {
        if (histories.size() < 1) {
            return new ArrayList<>();
        }

        Map<Long, List<History>> map = new HashMap<>();

        for (History history : histories) {
            Long key = history.getWordAskedId();

            List<History> list = map.get(key);
            if (list == null) {
                list = new ArrayList<>();
                map.put(key, list);
            }

            list.add(history);
        }

        List<Long> ids = new ArrayList<>();

        for (Map.Entry<Long, List<History>> entry : map.entrySet()) {
            List<History> value = entry.getValue();
            History history = value.get(0);
            if (!history.getIsCorrect()) {
                ids.add(history.getWordAskedId());
            }
        }

        return ids;
    }

    public List<History> findAllExceptTodayHistoryByUser(Long userId) {
        return historyRepository.findAllExceptTodayHistoryByUser(userId);
    }

    public List<History> findAllOnlyCorrectTodayByUser(Long userId) {
        return historyRepository.findAllOnlyCorrectTodayByUser(userId);
    }

    public Long findRandomWordMostCommonWrongHistoryByUser(List<History> historyInput,  int count) {
        List<Long> keys = findMostCommonWrongHistoryByUser(historyInput, count);
        return keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
    }

    public List<Long> findMostCommonWrongHistoryByUser(List<History> historyInput, int count) {
        Map<Long, List<History>> map = new HashMap<>();

        for (History history : historyInput) {
            Long key = history.getWordAskedId();

            List<History> list = map.get(key);
            if (list == null) {
                list = new ArrayList<>();
                map.put(key, list);
            }

            list.add(history);
        }

        Map<Long, Double> correctRatio = new HashMap<>();

        for (Map.Entry<Long, List<History>> entry : map.entrySet()) {
            List<History> histories = entry.getValue();
            List<History> wrong = histories.stream().filter(h -> h.getIsCorrect().equals(false)).collect(Collectors.toList());
            Double ratio = wrong.size() / (double) histories.size();
            correctRatio.put(entry.getKey(), ratio);
        }

        Map<Long, Double> topWrongAnswers = correctRatio.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(count)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        return new ArrayList<>(topWrongAnswers.keySet());
    }

    public List<Long> findLatestDistinctWordAskedIds(Long userId) {
        return historyRepository.findLatestDistinctWordAskedIds(userId);
    }
}
