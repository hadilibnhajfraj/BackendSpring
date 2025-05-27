    package tn.esprit.spring.services;

    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import tn.esprit.spring.entities.Equipe;
    import tn.esprit.spring.entities.MatchFo;
    import tn.esprit.spring.entities.Planning;
    import tn.esprit.spring.repositories.MatchFoRepository;
    import tn.esprit.spring.repositories.PlanningRepository;

    import java.util.*;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class MatchFoService {
        private final MatchFoRepository matchFoRepository;
        private final PlanningRepository planningRepository;


    }
