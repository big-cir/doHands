package com.be.dohands.quest.controller;

import com.be.dohands.quest.dto.JobQuestDetailResponseDTO;
import com.be.dohands.quest.dto.LeaderQuestDetailResponseDTO;
import com.be.dohands.quest.dto.QuestListResponseDTO;
import com.be.dohands.quest.service.QuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/quest")
@RequiredArgsConstructor
public class QuestController {
    private final QuestService questService;

    @GetMapping("/{userId}")
    public ResponseEntity<QuestListResponseDTO> getQuests(@PathVariable("userId") String userId){
        QuestListResponseDTO response = questService.getQuestList(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/leader/{userQuestId}")
    public ResponseEntity<LeaderQuestDetailResponseDTO> getLeaderQuestDetail(@PathVariable Long userQuestId){
        LeaderQuestDetailResponseDTO response = questService.getLeaderQuestDetail(userQuestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/job/{userQuestId}")
    public ResponseEntity<JobQuestDetailResponseDTO> getJobQuestDetail(@PathVariable Long userQuestId){
        JobQuestDetailResponseDTO response = questService.getJobQuestDetail(userQuestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
