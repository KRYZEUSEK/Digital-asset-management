package com.uep.wap.controller;

import com.uep.wap.dto.AnalyticsDashboardDTO;
import com.uep.wap.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public AnalyticsDashboardDTO getDashboard() {
        return analyticsService.getDashboard();
    }
}
