package ma.whitecare.service.modules.actes.api;

import ma.whitecare.entities.enums.EnPromo;
import ma.whitecare.entities.enums.StatutSituationFinanciere;
import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.mvc.dto.SimulationFinanciereDto.CreateSimulationFinanciereDTO;
import ma.whitecare.mvc.dto.SimulationFinanciereDto.SimulationFinanciereDTO;
import ma.whitecare.mvc.dto.SimulationFinanciereDto.UpdateSimulationFinanciereDTO;

import java.util.List;

public interface SimulationFinanciereService {

    // ========== CRUD SIMULATIONS FINANCIÈRES ==========
    SituationFinanciere createSimulationFinanciere(CreateSimulationFinanciereDTO simulationDTO);
    SituationFinanciere getSimulationFinanciereById(Long id);
    List<SituationFinanciere> findAll();
    SituationFinanciere updateSimulationFinanciere(Long id, UpdateSimulationFinanciereDTO updateDTO);
    void deleteSimulationFinanciere(Long id);

    // ========== RECHERCHES ==========
    boolean existsById(Long id);
    SituationFinanciere findByDossierMedicaleId(Long dossierMedicaleId);
    List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut);
    List<SituationFinanciere> findByEnPromo(EnPromo enPromo);
    List<SituationFinanciere> findByDossierMedicaleIdAndStatut(Long dossierMedicaleId, StatutSituationFinanciere statut);
    boolean existsByDossierMedicaleId(Long dossierMedicaleId);

    // ========== STATISTIQUES ==========
    long countAll();
    long countByStatut(StatutSituationFinanciere statut);
    long countByDossierMedicaleId(Long dossierMedicaleId);

    // ========== MÉTHODES SPÉCIFIQUES ==========
    void updateStatut(Long id, StatutSituationFinanciere statut);
    void updateTotaleDesActes(Long id, Double nouvelleTotaleDesActes);
    void updateTotalePaye(Long id, Double nouvelleTotalePaye);
    void updateCredit(Long id, Double nouveauCredit);

    // ========== CONVERSIONS ==========
    SimulationFinanciereDTO convertToDTO(SituationFinanciere situationFinanciere);
    List<SimulationFinanciereDTO> convertToDTOList(List<SituationFinanciere> situationsFinancieres);
}
