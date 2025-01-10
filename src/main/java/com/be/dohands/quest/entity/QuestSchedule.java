package com.be.dohands.quest.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.be.dohands.base.BaseTimeEntity;
import com.be.dohands.common.data.QuestType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestSchedule extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long questScheduleId;

    private Integer month;

    private Integer week;

    private String department;

    @Enumerated(value = EnumType.STRING)
    private QuestType questType;

    private Long questId;
}
