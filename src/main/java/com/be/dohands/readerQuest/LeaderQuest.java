package com.be.dohands.readerQuest;

import com.be.dohands.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LeaderQuest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaderQuestId;

    private String department;

    private String questName;

    private String period;

    private Integer proportion;

    private Integer exp;

    private Integer maxScore;

    private Integer medianScore;

    private String maxStandard;

    private String medianStandard;

    private String notes;
}
