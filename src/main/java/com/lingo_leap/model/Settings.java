package com.lingo_leap.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "settings")
public class Settings {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "most_wrong", nullable = false)
    private Integer mostWrong = 0;

    @Column(name = "least_answered", nullable = false)
    private Integer leastAnswered = 100;
}
