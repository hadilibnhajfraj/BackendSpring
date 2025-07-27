package tn.esprit.spring.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.repositories.EquipeRepository;
import tn.esprit.spring.repositories.MatchFoRepository;
import tn.esprit.spring.repositories.ReservationRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final UserRepository userRepository;
    private final EquipeRepository equipeRepository;
    private final MatchFoRepository matchFoRepository;
    private final ReservationRepository reservationRepository;

    public Map<String, Object> getOverviewMetrics() {
        log.info("MetricsService - Starting to fetch overview metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            long totalUsers = userRepository.count();
            log.info("MetricsService - Total users count: {}", totalUsers);
            metrics.put("totalUsers", totalUsers);
            
            long totalTeams = equipeRepository.count();
            log.info("MetricsService - Total teams count: {}", totalTeams);
            metrics.put("totalTeams", totalTeams);
            
            long totalMatches = matchFoRepository.count();
            log.info("MetricsService - Total matches count: {}", totalMatches);
            metrics.put("totalMatches", totalMatches);
            
            long totalReservations = reservationRepository.count();
            log.info("MetricsService - Total reservations count: {}", totalReservations);
            metrics.put("totalReservations", totalReservations);
            
            log.info("MetricsService - Successfully fetched all metrics: {}", metrics);
            
        } catch (Exception e) {
            log.error("MetricsService - Error fetching metrics: {}", e.getMessage(), e);
            throw e;
        }
        
        return metrics;
    }
}

