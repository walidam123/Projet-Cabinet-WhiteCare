package ma.whitecare.service.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.InterventionMedecin;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.mvc.dto.dossierMedical.InterventionDTO;
import ma.whitecare.mvc.dto.dossierMedical.CreateInterventionDTO;
import ma.whitecare.mvc.dto.dossierMedical.UpdateInterventionDTO;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.actes.api.ActeRepository;
import ma.whitecare.service.modules.dossierMedical.api.InterventionService;
import ma.whitecare.common.exceptions.dossierMedicalExceptions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InterventionServiceImpl implements InterventionService {

    private final InterventionRepository interventionRepository;
    private final ConsultationRepository consultationRepository;
    private final ActeRepository acteRepository;
    private final DossierMedicalRepository dossierMedicalRepository;

    public InterventionServiceImpl(InterventionRepository interventionRepository,
                                    ConsultationRepository consultationRepository,
                                    ActeRepository acteRepository,
                                    DossierMedicalRepository dossierMedicalRepository) {
        this.interventionRepository = interventionRepository;
        this.consultationRepository = consultationRepository;
        this.acteRepository = acteRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
    }

    @Override
    public InterventionDTO createIntervention(CreateInterventionDTO dto) {
        // Convertir DTO en Entity
        InterventionMedecin intervention = convertToEntity(dto);
        // Validation
        validateIntervention(intervention);

        // Vérifier que la consultation existe
        if (intervention.getConsultation() == null || intervention.getConsultation().getIdConsultation() == null) {
            throw new ValidationException("La consultation est requise pour créer une intervention");
        }
        Consultation consultation = consultationRepository.findById(intervention.getConsultation().getIdConsultation());
        if (consultation == null) {
            throw new RelatedEntityNotFoundException("consultation", intervention.getConsultation().getIdConsultation());
        }

        // Vérifier que l'acte existe
        if (intervention.getActe() == null || intervention.getActe().getIdActe() == null) {
            throw new ValidationException("L'acte est requis pour créer une intervention");
        }
        Acte acte = acteRepository.findById(intervention.getActe().getIdActe());
        if (acte == null) {
            throw new RelatedEntityNotFoundException("acte", intervention.getActe().getIdActe());
        }

        // Valider le prix
        if (intervention.getPrixDePatient() == null || intervention.getPrixDePatient() < 0) {
            throw new BusinessRuleException("Le prix de l'intervention doit être positif ou nul");
        }

        // Valider le numéro de dent si fourni
        if (intervention.getNumDent() != null && (intervention.getNumDent() < 1 || intervention.getNumDent() > 32)) {
            throw new BusinessRuleException("Le numéro de dent doit être entre 1 et 32");
        }

        // Set audit fields
        intervention.setCreePar("system"); // À remplacer par l'utilisateur connecté
        intervention.setModifiePar("system");

        // Créer l'intervention
        interventionRepository.create(intervention);
        return convertToDTO(intervention);
    }

    @Override
    public InterventionDTO updateIntervention(Long interventionId, UpdateInterventionDTO dto) {
        InterventionMedecin existingIntervention = interventionRepository.findById(interventionId);
        if (existingIntervention == null) {
            throw new InterventionNotFoundException(interventionId);
        }

        // Mettre à jour l'entité depuis le DTO
        updateEntityFromDTO(existingIntervention, dto);

        // Validation
        validateIntervention(existingIntervention);

        // Mettre à jour les champs d'audit
        existingIntervention.setModifiePar("system"); // À remplacer par l'utilisateur connecté

        interventionRepository.update(existingIntervention);
        return convertToDTO(existingIntervention);
    }

    @Override
    public void deleteIntervention(Long interventionId) {
        if (!existsById(interventionId)) {
            throw new InterventionNotFoundException(interventionId);
        }
        interventionRepository.deleteById(interventionId);
    }

    @Override
    public InterventionDTO getInterventionById(Long interventionId) {
        InterventionMedecin intervention = interventionRepository.findById(interventionId);
        if (intervention == null) {
            throw new InterventionNotFoundException(interventionId);
        }
        return convertToDTO(intervention);
    }

    @Override
    public List<InterventionDTO> getAllInterventions() {
        return interventionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionDTO> findByConsultationId(Long consultationId) {
        if (consultationId == null) {
            throw new ValidationException("L'ID de la consultation ne peut pas être null");
        }
        return interventionRepository.findByConsultationId(consultationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionDTO> findByActeId(Long acteId) {
        if (acteId == null) {
            throw new ValidationException("L'ID de l'acte ne peut pas être null");
        }
        return interventionRepository.findByActeId(acteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionDTO> findByNumDent(Integer numDent) {
        if (numDent == null) {
            throw new ValidationException("Le numéro de dent ne peut pas être null");
        }
        if (numDent < 1 || numDent > 32) {
            throw new BusinessRuleException("Le numéro de dent doit être entre 1 et 32");
        }
        return interventionRepository.findByNumDent(numDent).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionDTO> findByConsultationAndActe(Long consultationId, Long acteId) {
        if (consultationId == null || acteId == null) {
            throw new ValidationException("L'ID de la consultation et l'ID de l'acte sont requis");
        }
        return interventionRepository.findByConsultationAndActe(consultationId, acteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateTotalByConsultation(Long consultationId) {
        if (consultationId == null) {
            throw new ValidationException("L'ID de la consultation ne peut pas être null");
        }
        return interventionRepository.calculateTotalByConsultation(consultationId);
    }

    @Override
    public boolean existsById(Long interventionId) {
        if (interventionId == null) {
            return false;
        }
        return interventionRepository.existsById(interventionId);
    }

    @Override
    public long countAllInterventions() {
        return interventionRepository.countAll();
    }

    @Override
    public long countByConsultationId(Long consultationId) {
        if (consultationId == null) {
            throw new ValidationException("L'ID de la consultation ne peut pas être null");
        }
        return interventionRepository.countByConsultationId(consultationId);
    }

    @Override
    public long countByActeId(Long acteId) {
        if (acteId == null) {
            throw new ValidationException("L'ID de l'acte ne peut pas être null");
        }
        return interventionRepository.countByActeId(acteId);
    }

    @Override
    public Map<Integer, List<InterventionDTO>> getHistoriqueDentaire(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical ne peut pas être null");
        }
        
        // Vérifier que le dossier existe
        if (dossierMedicalRepository.findById(dossierId) == null) {
            throw new DossierMedicalNotFoundException(dossierId);
        }
        
        // Récupérer toutes les consultations du dossier
        List<Consultation> consultations = consultationRepository.findByDossierMedicalId(dossierId);
        
        // Récupérer toutes les interventions de toutes les consultations
        List<InterventionMedecin> toutesInterventions = consultations.stream()
                .flatMap(consultation -> interventionRepository.findByConsultationId(consultation.getIdConsultation()).stream())
                .collect(Collectors.toList());
        
        // Grouper par numéro de dent et convertir en DTOs
        Map<Integer, List<InterventionDTO>> historiqueParDent = new HashMap<>();
        for (InterventionMedecin intervention : toutesInterventions) {
            if (intervention.getNumDent() != null) {
                historiqueParDent.computeIfAbsent(intervention.getNumDent(), k -> new java.util.ArrayList<>())
                        .add(convertToDTO(intervention));
            }
        }
        
        return historiqueParDent;
    }

    @Override
    public Double getCoutTotalPatient(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical ne peut pas être null");
        }
        
        // Vérifier que le dossier existe
        if (dossierMedicalRepository.findById(dossierId) == null) {
            throw new DossierMedicalNotFoundException(dossierId);
        }
        
        // Récupérer toutes les consultations du dossier
        List<Consultation> consultations = consultationRepository.findByDossierMedicalId(dossierId);
        
        // Calculer le coût total de toutes les interventions
        double total = 0.0;
        for (Consultation consultation : consultations) {
            Double coutConsultation = calculateTotalByConsultation(consultation.getIdConsultation());
            if (coutConsultation != null) {
                total += coutConsultation;
            }
        }
        
        return total;
    }

    // ========== CONVERSION METHODS ==========
    private InterventionMedecin convertToEntity(CreateInterventionDTO dto) {
        // Charger la consultation
        Consultation consultation = consultationRepository.findById(dto.getConsultationId());
        if (consultation == null) {
            throw new RelatedEntityNotFoundException("consultation", dto.getConsultationId());
        }

        // Charger l'acte
        Acte acte = acteRepository.findById(dto.getActeId());
        if (acte == null) {
            throw new RelatedEntityNotFoundException("acte", dto.getActeId());
        }

        // Construire l'entité
        return InterventionMedecin.builder()
                .consultation(consultation)
                .acte(acte)
                .prixDePatient(dto.getPrixDePatient())
                .numDent(dto.getNumDent())
                .build();
    }

    private void updateEntityFromDTO(InterventionMedecin entity, UpdateInterventionDTO dto) {
        // Mettre à jour le prix si fourni
        if (dto.getPrixDePatient() != null) {
            entity.setPrixDePatient(dto.getPrixDePatient());
        }

        // Mettre à jour le numéro de dent si fourni
        if (dto.getNumDent() != null) {
            entity.setNumDent(dto.getNumDent());
        }

        // Mettre à jour l'acte si fourni
        if (dto.getActeId() != null) {
            Acte acte = acteRepository.findById(dto.getActeId());
            if (acte == null) {
                throw new RelatedEntityNotFoundException("acte", dto.getActeId());
            }
            entity.setActe(acte);
        }

        // Mettre à jour la consultation si fournie
        if (dto.getConsultationId() != null) {
            Consultation consultation = consultationRepository.findById(dto.getConsultationId());
            if (consultation == null) {
                throw new RelatedEntityNotFoundException("consultation", dto.getConsultationId());
            }
            entity.setConsultation(consultation);
        }
    }

    private InterventionDTO convertToDTO(InterventionMedecin entity) {
        return InterventionDTO.builder()
                .idIM(entity.getIdIM())
                .prixDePatient(entity.getPrixDePatient())
                .numDent(entity.getNumDent())
                .acteId(entity.getActe() != null ? entity.getActe().getIdActe() : null)
                .consultationId(entity.getConsultation() != null ? 
                    entity.getConsultation().getIdConsultation() : null)
                .dateCreation(entity.getDateCreation())
                .dateDerniereModification(entity.getDateDerniereModification())
                .createdBy(entity.getCreePar())
                .updatedBy(entity.getModifiePar())
                .build();
    }

    // ========== VALIDATION PRIVÉE ==========
    private void validateIntervention(InterventionMedecin intervention) {
        if (intervention == null) {
            throw new ValidationException("L'intervention ne peut pas être null");
        }
    }
}

