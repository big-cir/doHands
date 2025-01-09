package com.be.dohands.level.controller;

import com.be.dohands.level.LevelExp;
import com.be.dohands.level.service.LevelExpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LevelExpController {

    private final LevelExpService levelExpService;

    @GetMapping("/level-exps/{levelExpId}")
    public ResponseEntity<LevelExp> getLevelExp(@PathVariable("levelExpId") Long levelExpId) {
        return ResponseEntity.ok(levelExpService.findLevelExp(levelExpId));
    }
}
