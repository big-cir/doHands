package com.be.dohands.badge.controller;

import com.be.dohands.badge.Badge;
import com.be.dohands.badge.BadgeResponseDTO;
import com.be.dohands.badge.service.BadgeService;
import com.be.dohands.common.security.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping("/badges")
    public ResponseEntity<BadgeResponseDTO> getBadges(@AuthenticationPrincipal CustomUserDetails user) {
        BadgeResponseDTO response = badgeService.findMemberBadges(user.getUsername());
        return ResponseEntity.ok(response);
    }
}
