package com.be.dohands.sheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sheets")
public class SheetController {

    private final SpreadSheetService sheetService;
    private final String spreadsheetId = "1nEA36Rft_qqzLjRaojQC1hCwxGuhBQz_ABoi5jRXdOk";
    @GetMapping("/test/success")
    public String testSuccess() {
        return "success";
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        String sheetName = (String) payload.get("sheetName");

        switch (sheetName) {
            case "참고. 구성원 정보":
                sheetService.readAndUpdateMemberSheet(payload);
                break;
            case "참고. 게시판":
                sheetService.readAndUpdateArticleSheet(payload);
                break;
            case "참고. 전사 프로젝트":
                sheetService.readAndUpdateTfExpSheet(payload);
                break;
            case "참고. 레벨별 경험치":
                sheetService.readAndUpdateLevelExpSheet(payload);
                break;
            default:
                throw new IllegalArgumentException("Unknown sheet name: " + sheetName);
        }

        return ResponseEntity.ok("Webhook received successfully");
    }

    @PostMapping("/many-webhook")
    public ResponseEntity<String> handleManyWebhook(@RequestBody Map<String, Object> payload) {
        String sheetName = (String) payload.get("sheetName");

        switch (sheetName) {
            case "참고. 직무별 퀘스트":
                sheetService.readAndUpdateJobRequestSheet(payload);
                break;
            case "참고. 인사평가":
                break;
            case "참고. 리더부여 퀘스트":
                sheetService.readAndUpdateLeaderRequestSheet(payload);
                break;
            default:
                throw new IllegalArgumentException("Unknown sheet name: " + sheetName);
        }

        return ResponseEntity.ok("Webhook received successfully");
    }

    @PostMapping("/test/change")
    public void changeSheet() throws GeneralSecurityException, IOException {
        sheetService.createMemberInfoToSheetForTest(spreadsheetId);
    }
}
