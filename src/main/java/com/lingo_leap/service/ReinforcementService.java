package com.lingo_leap.service;

import com.lingo_leap.model.Reinforcement;
import com.lingo_leap.record.UserReinforcement;
import com.lingo_leap.repository.ReinforcementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReinforcementService {

    private final ReinforcementRepository reinforcementRepository;

    @Transactional
    public boolean handleReinforcement(Long userId, int size, int reinforcementRepetitionCount) {
        if (size >= reinforcementRepetitionCount) {
            addReinforcement(userId);
        } else if (size <= 0) {
            removeIfExistReinforcement(userId);
        }
        return reinforcementRepository.findByUserId(userId).isPresent();
    }

    public Boolean addReinforcement(Long userId) {
        reinforcementRepository.findByUserId(userId)
                .orElseGet(() -> reinforcementRepository.save(new Reinforcement(null, userId)));
        return true;
    }

    public Boolean removeIfExistReinforcement(Long userId) {
        Optional<Reinforcement> userReinforcementOptional = reinforcementRepository.findByUserId(userId);
        if (userReinforcementOptional.isPresent()) {
            reinforcementRepository.delete(userReinforcementOptional.get());
        }
        return false;
    }

    public Boolean setReinforcement(UserReinforcement user) {
        if (user.isReinforcement()) {
            return addReinforcement(user.userId());
        } else {
            return removeIfExistReinforcement(user.userId());
        }
    }

    public Boolean getReinforcement(Long userId) {
        return reinforcementRepository.findByUserId(userId).isPresent();
    }

    public void resetAll() {
        reinforcementRepository.deleteAll();
    }

    public boolean isReinforcement(Long userId) {
        return reinforcementRepository.findByUserId(userId).isPresent();
    }
}
