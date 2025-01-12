package com.be.dohands.quest.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
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
public class UserQuestEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long userQuestId;

    @Enumerated(value = EnumType.STRING)
    private QuestType questType;

    private Long questId;

    private Long questExpId;

    private Long userId;

    private Long questScheduleId;

    @Enumerated(value = EnumType.STRING)
    private StatusType statusType;

}
