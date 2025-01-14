package com.be.dohands.quest.entity;

import com.be.dohands.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderQuestExpEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaderQuestExpId;

    private String employeeNumber;

    private String questName;

    private String content;

    private Integer exp;

    private Long leaderQuestId;

    private Integer month;

    private Integer week;

    private String notes;

    private Integer sheetRow;

}
