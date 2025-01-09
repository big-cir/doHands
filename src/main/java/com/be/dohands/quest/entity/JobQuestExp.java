package com.be.dohands.quest.entity;

import com.be.dohands.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "JobQuestExp")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobQuestExp extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobQuestExpId;

    private Float maxStandard;

    private Float medianStandard;

    private Integer period;

    private Integer productivity;

    private Integer month;

    private Integer week;

    private LocalDateTime endDate;

    private Integer exp;

    private Long jobQuestId;
}
