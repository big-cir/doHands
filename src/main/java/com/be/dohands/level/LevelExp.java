package com.be.dohands.level;

import com.be.dohands.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"jobGroup", "name", "exp"}, callSuper = false)
public class LevelExp extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long levelExpId;

    private String jobGroup;

    private String name;

    private Integer exp;

    public boolean isUpdated(LevelExp newEntity) {
        return !this.equals(newEntity);
    }

}
