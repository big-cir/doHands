package com.be.dohands.member;

import com.be.dohands.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor
public class MemberExp extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userExpId;

    private Integer year;

    @Setter
    @Comment("현재 경험치")
    private Integer currentExp;

    @Setter
    @Comment("누적 경험치")
    private Integer cumulativeExp;

    private Long userId;

    public MemberExp(Integer year, Long userId) {
        this.year = year;
        this.currentExp = 0;
        this.cumulativeExp = 0;
        this.userId = userId;
    }

    public void addToCurrentExp(Integer givenExp) {
        this.currentExp += givenExp;
    }

}
