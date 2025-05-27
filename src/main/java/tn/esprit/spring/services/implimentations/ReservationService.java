package tn.esprit.spring.services.implimentations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entities.Reservation;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.repositories.ReservationRepository;
import tn.esprit.spring.repositories.TerrainRepository;
import tn.esprit.spring.services.interfaces.IReservationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService implements IReservationService {


}
