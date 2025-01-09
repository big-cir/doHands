package com.be.dohands.jobQuest;

import com.be.dohands.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class JobQuestDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobQuestDetailId;

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
