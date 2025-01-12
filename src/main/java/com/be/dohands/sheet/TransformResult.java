package com.be.dohands.sheet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransformResult<T> {

    private T entity;
    private boolean notificationYn;

    public static <T> TransformResult<T> of(T entity, boolean notificationYn) {
        return TransformResult.<T>builder()
            .entity(entity)
            .notificationYn(notificationYn)
            .build();
    }
}
