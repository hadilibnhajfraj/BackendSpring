package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.services.MetricsService;

import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@Slf4j
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverviewMetrics() {
        log.info("MetricsController - Received request for /api/metrics/overview");
        try {
            Map<String, Object> metrics = metricsService.getOverviewMetrics();
            log.info("MetricsController - Successfully returning metrics: {}", metrics);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("MetricsController - Error in controller: {}", e.getMessage(), e);
            throw e;
        }
    }
}
