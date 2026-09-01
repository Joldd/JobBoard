package com.jobboard.controller;

import com.jobboard.dto.stats.DashboardStatsResponse;
import com.jobboard.security.SecurityUser;
import com.jobboard.service.StatsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Statistiques")
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/dashboard")
    public DashboardStatsResponse dashboard(@AuthenticationPrincipal SecurityUser user) {
        return statsService.getDashboardStats(user.getId());
    }
}
