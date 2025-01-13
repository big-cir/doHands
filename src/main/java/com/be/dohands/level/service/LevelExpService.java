package com.be.dohands.level.service;

import com.be.dohands.level.LevelExp;
import com.be.dohands.level.repository.LevelExpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LevelExpService {

    private final LevelExpRepository levelExpRepository;

    @Transactional
    public LevelExp findLevelExp(Long levelExpId) {
        return levelExpRepository.findById(levelExpId).orElse(null);
    }

    @Transactional(readOnly = true)
    public LevelExp findByName(String name) {
        return levelExpRepository.findLevelExpByName(name).orElse(null);
    }
}
