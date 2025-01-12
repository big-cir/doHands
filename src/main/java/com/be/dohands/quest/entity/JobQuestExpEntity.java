package com.be.dohands.quest.entity;

import com.be.dohands.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobQuestExpEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobQuestExpId;

    private Float maxStandard;

    private Float medianStandard;

    private Integer period;

    private Float productivity;

    private Integer month;

    private Integer week;

    private LocalDateTime endDate;

    private Integer exp;

    private Long jobQuestId;

    private String notes;

    private Integer sheetRow;
}
