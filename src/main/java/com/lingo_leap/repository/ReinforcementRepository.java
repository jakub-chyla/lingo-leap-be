package com.lingo_leap.repository;

import com.lingo_leap.model.Reinforcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReinforcementRepository extends JpaRepository<Reinforcement, Long> {

    Optional<Reinforcement> findByUserId(Long userId);
}
