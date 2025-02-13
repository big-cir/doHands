package com.be.dohands.level.service;

import com.be.dohands.level.LevelExp;
import com.be.dohands.level.repository.LevelExpRepository;
import java.util.List;
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

    @Transactional(readOnly = true)
    public List<LevelExp> findLevelExpByCategory(String jobCategory) {
        return levelExpRepository.findLevelExpsByJobGroupStartingWith(jobCategory);
    }

    @Transactional
    public LevelExp findNextExpByCategory(String jobCategory, int exp) {
        return levelExpRepository.findFirstByJobGroupStartingWithAndExpGreaterThan(jobCategory, exp).orElse(null);
    }
}
