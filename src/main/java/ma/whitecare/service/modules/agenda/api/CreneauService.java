package ma.whitecare.service.modules.agenda.api;

import ma.whitecare.entities.agenda.Creneau;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.mvc.dto.CreneauDto.CreateCreneauDTO;
import ma.whitecare.mvc.dto.CreneauDto.CreneauDTO;
import ma.whitecare.mvc.dto.CreneauDto.UpdateCreneauDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CreneauService {

    // ========== CRUD CRENEAUX ==========
    Creneau createCreneau(CreateCreneauDTO creneauDTO);
    Creneau getCreneauById(Long id);
    List<Creneau> findAll();
    Creneau updateCreneau(Long id, UpdateCreneauDTO updateDTO);
    void deleteCreneau(Long id);

    // ========== RECHERCHES ==========
    List<Creneau> findByJourId(Long jourId);
    List<Creneau> findCreneauxDisponiblesByJourId(Long jourId);
    List<Creneau> findCreneauxByAgendaAndDate(Long agendaId, LocalDate date);
    List<Creneau> findCreneauxDisponiblesByDate(Long agendaId, LocalDate date);
    boolean existsById(Long id);

    // ========== VALIDATION ==========
    boolean estCreneauDisponible(Long jourId, LocalTime heureDebut, LocalTime heureFin);
    boolean creneauxSeChevauchent(Long jourId, LocalTime heureDebut, LocalTime heureFin, Long creneauExcluId);

    // ========== CONVERSIONS ==========
    CreneauDTO convertToDTO(Creneau creneau);
    List<CreneauDTO> convertToDTOList(List<Creneau> creneaux);
}
