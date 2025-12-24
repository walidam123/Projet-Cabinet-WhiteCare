package ma.whitecare.service.modules.actes.impl;

import ma.whitecare.common.exceptions.BusinessRuleException;
import ma.whitecare.common.exceptions.DossierMedicalNotFoundException;
import ma.whitecare.common.exceptions.SimulationFinanciereNotFoundException;
import ma.whitecare.common.validators.SimulationFinanciereValidator;
import ma.whitecare.entities.enums.EnPromo;
import ma.whitecare.entities.enums.StatutSituationFinanciere;
import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.mvc.dto.SimulationFinanciereDto.CreateSimulationFinanciereDTO;
import ma.whitecare.mvc.dto.SimulationFinanciereDto.SimulationFinanciereDTO;
import ma.whitecare.mvc.dto.SimulationFinanciereDto.UpdateSimulationFinanciereDTO;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.dossierMedical.api.SituationFinanciereRepository;
import ma.whitecare.service.modules.actes.api.SimulationFinanciereService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

public class SimulationFinanciereServiceImpl implements SimulationFinanciereService {

    private final SituationFinanciereRepository situationFinanciereRepository;
    private final DossierMedicalRepository dossierMedicalRepository;

    public SimulationFinanciereServiceImpl(SituationFinanciereRepository situationFinanciereRepository,
                                           DossierMedicalRepository dossierMedicalRepository) {
        this.situationFinanciereRepository = situationFinanciereRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
    }

    // ========== CRUD SIMULATIONS FINANCIÈRES ==========

    @Override
    public SituationFinanciere createSimulationFinanciere(CreateSimulationFinanciereDTO simulationDTO) {
        // Validation
        List<String> errors = SimulationFinanciereValidator.validateCreateSimulationFinanciere(simulationDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'existence du dossier médical
        validateDossierMedicalExists(simulationDTO.getDossierMedicaleId());

        // Vérifier qu'il n'existe pas déjà une simulation pour ce dossier
        if (situationFinanciereRepository.existsByDossierMedicaleId(simulationDTO.getDossierMedicaleId())) {
            throw new BusinessRuleException("Une simulation financière existe déjà pour ce dossier médical");
        }

        // Convertir DTO en entité
        SituationFinanciere situationFinanciere = convertToSituationFinanciere(simulationDTO);

        // Définir les valeurs par défaut
        setDefaultValues(situationFinanciere);

        // Sauvegarder
        situationFinanciereRepository.create(situationFinanciere);

        return situationFinanciere;
    }

    @Override
    public SituationFinanciere getSimulationFinanciereById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la simulation financière est obligatoire");
        }

        SituationFinanciere situationFinanciere = situationFinanciereRepository.findById(id);
        if (situationFinanciere == null) {
            throw new SimulationFinanciereNotFoundException(id);
        }
        return situationFinanciere;
    }

    @Override
    public List<SituationFinanciere> findAll() {
        return situationFinanciereRepository.findAll();
    }

    @Override
    public SituationFinanciere updateSimulationFinanciere(Long id, UpdateSimulationFinanciereDTO updateDTO) {
        // Récupérer la simulation existante
        SituationFinanciere situationFinanciere = getSimulationFinanciereById(id);

        // Validation
        List<String> errors = SimulationFinanciereValidator.validateUpdateSimulationFinanciere(updateDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'existence du dossier médical si modifié
        if (updateDTO.getDossierMedicaleId() != null) {
            validateDossierMedicalExists(updateDTO.getDossierMedicaleId());
        }

        // Mettre à jour les champs
        updateSituationFinanciereFields(situationFinanciere, updateDTO);

        // Recalculer le crédit si nécessaire
        recalculateCredit(situationFinanciere);

        // Sauvegarder
        situationFinanciereRepository.update(situationFinanciere);

        return situationFinanciere;
    }

    @Override
    public void deleteSimulationFinanciere(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la simulation financière est obligatoire");
        }

        // Vérifier que la simulation existe
        getSimulationFinanciereById(id);

        // Supprimer
        situationFinanciereRepository.deleteById(id);
    }

    // ========== RECHERCHES ==========

    @Override
    public boolean existsById(Long id) {
        return situationFinanciereRepository.existsById(id);
    }

    @Override
    public SituationFinanciere findByDossierMedicaleId(Long dossierMedicaleId) {
        if (dossierMedicaleId == null) {
            throw new IllegalArgumentException("L'ID du dossier médical est obligatoire");
        }
        return situationFinanciereRepository.findByDossierMedicaleId(dossierMedicaleId);
    }

    @Override
    public List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return situationFinanciereRepository.findByStatut(statut);
    }

    @Override
    public List<SituationFinanciere> findByEnPromo(EnPromo enPromo) {
        if (enPromo == null) {
            throw new IllegalArgumentException("Le statut en promo est obligatoire");
        }
        return situationFinanciereRepository.findByEnPromo(enPromo);
    }

    @Override
    public List<SituationFinanciere> findByDossierMedicaleIdAndStatut(Long dossierMedicaleId, StatutSituationFinanciere statut) {
        if (dossierMedicaleId == null) {
            throw new IllegalArgumentException("L'ID du dossier médical est obligatoire");
        }
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return situationFinanciereRepository.findByDossierMedicaleIdAndStatut(dossierMedicaleId, statut);
    }

    @Override
    public boolean existsByDossierMedicaleId(Long dossierMedicaleId) {
        if (dossierMedicaleId == null) {
            throw new IllegalArgumentException("L'ID du dossier médical est obligatoire");
        }
        return situationFinanciereRepository.existsByDossierMedicaleId(dossierMedicaleId);
    }

    // ========== STATISTIQUES ==========

    @Override
    public long countAll() {
        return situationFinanciereRepository.countAll();
    }

    @Override
    public long countByStatut(StatutSituationFinanciere statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return situationFinanciereRepository.countByStatut(statut);
    }

    @Override
    public long countByDossierMedicaleId(Long dossierMedicaleId) {
        if (dossierMedicaleId == null) {
            throw new IllegalArgumentException("L'ID du dossier médical est obligatoire");
        }
        return situationFinanciereRepository.countByDossierMedicaleId(dossierMedicaleId);
    }

    // ========== MÉTHODES SPÉCIFIQUES ==========

    @Override
    public void updateStatut(Long id, StatutSituationFinanciere statut) {
        // Vérifier que la simulation existe
        getSimulationFinanciereById(id);

        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }

        // Mettre à jour
        situationFinanciereRepository.updateStatut(id, statut);
    }

    @Override
    public void updateTotaleDesActes(Long id, Double nouvelleTotaleDesActes) {
        SituationFinanciere situationFinanciere = getSimulationFinanciereById(id);

        // Validation
        List<String> errors = SimulationFinanciereValidator.validateMontant(nouvelleTotaleDesActes, "Le total des actes");
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier la cohérence avec totalePaye
        if (situationFinanciere.getTotalePaye() != null && nouvelleTotaleDesActes < situationFinanciere.getTotalePaye()) {
            throw new BusinessRuleException("Le total des actes ne peut pas être inférieur au total payé");
        }

        // Mettre à jour
        situationFinanciere.setTotaleDesActes(nouvelleTotaleDesActes);
        recalculateCredit(situationFinanciere);
        situationFinanciereRepository.update(situationFinanciere);
    }

    @Override
    public void updateTotalePaye(Long id, Double nouvelleTotalePaye) {
        SituationFinanciere situationFinanciere = getSimulationFinanciereById(id);

        // Validation
        List<String> errors = SimulationFinanciereValidator.validateMontant(nouvelleTotalePaye, "Le total payé");
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier la cohérence avec totaleDesActes
        if (situationFinanciere.getTotaleDesActes() != null && nouvelleTotalePaye > situationFinanciere.getTotaleDesActes()) {
            throw new BusinessRuleException("Le total payé ne peut pas dépasser le total des actes");
        }

        // Mettre à jour
        situationFinanciere.setTotalePaye(nouvelleTotalePaye);
        recalculateCredit(situationFinanciere);
        situationFinanciereRepository.update(situationFinanciere);
    }

    @Override
    public void updateCredit(Long id, Double nouveauCredit) {
        SituationFinanciere situationFinanciere = getSimulationFinanciereById(id);

        // Validation
        List<String> errors = SimulationFinanciereValidator.validateMontant(nouveauCredit, "Le crédit");
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier la cohérence
        if (situationFinanciere.getTotaleDesActes() != null && situationFinanciere.getTotalePaye() != null) {
            double expectedCredit = situationFinanciere.getTotaleDesActes() - situationFinanciere.getTotalePaye();
            if (Math.abs(nouveauCredit - expectedCredit) > 0.01) {
                throw new BusinessRuleException("Le crédit doit être égal à (total des actes - total payé)");
            }
        }

        // Mettre à jour
        situationFinanciere.setCredit(nouveauCredit);
        situationFinanciereRepository.update(situationFinanciere);
    }

    // ========== CONVERSIONS ==========

    @Override
    public SimulationFinanciereDTO convertToDTO(SituationFinanciere situationFinanciere) {
        if (situationFinanciere == null) {
            return null;
        }

        return SimulationFinanciereDTO.builder()
                .idSF(situationFinanciere.getIdSF())
                .totaleDesActes(situationFinanciere.getTotaleDesActes())
                .totalePaye(situationFinanciere.getTotalePaye())
                .credit(situationFinanciere.getCredit())
                .reste(situationFinanciere.getReste())
                .statut(situationFinanciere.getStatut())
                .enPromo(situationFinanciere.getEnPromo())
                .dossierMedicaleId(situationFinanciere.getDossierMedicale() != null ?
                        situationFinanciere.getDossierMedicale().getIdDM() : null)
                .dateCreation(situationFinanciere.getDateCreation())
                .dateDerniereModification(situationFinanciere.getDateDerniereModification())
                .creePar(situationFinanciere.getCreePar())
                .modifiePar(situationFinanciere.getModifiePar())
                .build();
    }

    @Override
    public List<SimulationFinanciereDTO> convertToDTOList(List<SituationFinanciere> situationsFinancieres) {
        if (situationsFinancieres == null) {
            return List.of();
        }
        return situationsFinancieres.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private SituationFinanciere convertToSituationFinanciere(CreateSimulationFinanciereDTO dto) {
        SituationFinanciere situationFinanciere = new SituationFinanciere();
        situationFinanciere.setTotaleDesActes(dto.getTotaleDesActes());
        situationFinanciere.setTotalePaye(dto.getTotalePaye());
        situationFinanciere.setCredit(dto.getCredit());
        situationFinanciere.setStatut(dto.getStatut());
        situationFinanciere.setEnPromo(dto.getEnPromo() != null ? dto.getEnPromo() : EnPromo.NON);
        
        // Créer un objet DossierMedicale avec juste l'ID
        if (dto.getDossierMedicaleId() != null) {
            DossierMedicale dossierMedicale = new DossierMedicale();
            dossierMedicale.setIdDM(dto.getDossierMedicaleId());
            situationFinanciere.setDossierMedicale(dossierMedicale);
        }
        
        situationFinanciere.setCreePar(dto.getCreePar() != null ? dto.getCreePar() : "system");
        situationFinanciere.setModifiePar(dto.getModifiePar() != null ? dto.getModifiePar() : "system");
        return situationFinanciere;
    }

    private void setDefaultValues(SituationFinanciere situationFinanciere) {
        if (situationFinanciere.getTotaleDesActes() == null) {
            situationFinanciere.setTotaleDesActes(0.0);
        }
        if (situationFinanciere.getTotalePaye() == null) {
            situationFinanciere.setTotalePaye(0.0);
        }
        if (situationFinanciere.getCredit() == null) {
            recalculateCredit(situationFinanciere);
        }
        if (situationFinanciere.getEnPromo() == null) {
            situationFinanciere.setEnPromo(EnPromo.NON);
        }
        if (situationFinanciere.getCreePar() == null) {
            situationFinanciere.setCreePar("system");
        }
        if (situationFinanciere.getModifiePar() == null) {
            situationFinanciere.setModifiePar("system");
        }
    }

    private void updateSituationFinanciereFields(SituationFinanciere situationFinanciere, UpdateSimulationFinanciereDTO updateDTO) {
        if (updateDTO.getTotaleDesActes() != null) {
            situationFinanciere.setTotaleDesActes(updateDTO.getTotaleDesActes());
        }
        if (updateDTO.getTotalePaye() != null) {
            situationFinanciere.setTotalePaye(updateDTO.getTotalePaye());
        }
        if (updateDTO.getCredit() != null) {
            situationFinanciere.setCredit(updateDTO.getCredit());
        }
        if (updateDTO.getStatut() != null) {
            situationFinanciere.setStatut(updateDTO.getStatut());
        }
        if (updateDTO.getEnPromo() != null) {
            situationFinanciere.setEnPromo(updateDTO.getEnPromo());
        }
        if (updateDTO.getDossierMedicaleId() != null) {
            DossierMedicale dossierMedicale = new DossierMedicale();
            dossierMedicale.setIdDM(updateDTO.getDossierMedicaleId());
            situationFinanciere.setDossierMedicale(dossierMedicale);
        }
        if (updateDTO.getModifiePar() != null) {
            situationFinanciere.setModifiePar(updateDTO.getModifiePar());
        } else {
            situationFinanciere.setModifiePar("system");
        }
    }

    private void recalculateCredit(SituationFinanciere situationFinanciere) {
        if (situationFinanciere.getTotaleDesActes() != null && situationFinanciere.getTotalePaye() != null) {
            double credit = situationFinanciere.getTotaleDesActes() - situationFinanciere.getTotalePaye();
            situationFinanciere.setCredit(Math.max(0.0, credit));
        }
    }

    private void validateDossierMedicalExists(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical est obligatoire");
        }

        if (dossierMedicalRepository.findById(dossierId) == null) {
            throw new DossierMedicalNotFoundException(dossierId);
        }
    }
}
