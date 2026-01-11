package ma.whitecare.service.modules.agenda.api;

import ma.whitecare.entities.agenda.AgendaMensuel;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.entities.enums.Mois;
import ma.whitecare.mvc.dto.AgendaDto.AgendaDTO;
import ma.whitecare.mvc.dto.AgendaDto.CreateAgendaDTO;
import ma.whitecare.mvc.dto.AgendaDto.UpdateAgendaDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendaService {

    // ========== CRUD AGENDAS ==========
    AgendaMensuel createAgenda(CreateAgendaDTO agendaDTO);
    AgendaMensuel getAgendaById(Long id);
    List<AgendaMensuel> findAll();
    AgendaMensuel updateAgenda(Long id, UpdateAgendaDTO updateDTO);
    void deleteAgenda(Long id);

    // ========== RECHERCHES ==========
    List<AgendaMensuel> findByMedecinId(Long medecinId);
    Optional<AgendaMensuel> findByMoisAndAnneeAndMedecin(Mois mois, int annee, Long medecinId);
    boolean existsById(Long id);

    // ========== GESTION DES JOURS ==========
    Jour ajouterJour(Long agendaId, LocalDate date, String raisonIndisponibilite);
    void supprimerJour(Long jourId);
    void mettreAJourJour(Jour jour);
    List<Jour> findJoursByAgendaId(Long agendaId);
    List<Jour> findJoursDisponiblesByAgendaId(Long agendaId);
    List<Jour> findJoursNonDisponiblesByAgendaId(Long agendaId);
    Optional<Jour> findJourByDate(Long agendaId, LocalDate date);
    boolean estJourDisponible(Long agendaId, LocalDate date);

    // ========== CONVERSIONS ==========
    AgendaDTO convertToDTO(AgendaMensuel agenda);
    List<AgendaDTO> convertToDTOList(List<AgendaMensuel> agendas);
}
