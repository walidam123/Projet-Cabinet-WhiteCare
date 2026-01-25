package ma.whitecare.service.modules.dossierMedical.impl;

import ma.whitecare.common.exceptions.BusinessRuleException;
import ma.whitecare.common.exceptions.ConsultationNotFoundException;
import ma.whitecare.common.exceptions.InvalidDateRangeException;
import ma.whitecare.common.exceptions.RelatedEntityNotFoundException;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.medical.InterventionMedecin;
import ma.whitecare.entities.medical.Prescription;
import ma.whitecare.entities.enums.StatutConsultation;

import ma.whitecare.mvc.dto.DossierMedicale.*;
import ma.whitecare.repository.modules.Ordonnance.OrdonnanceRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.whitecare.service.modules.dossierMedical.api.ConsultationService;
import ma.whitecare.mvc.dto.dossierMedical.ConsultationCompleteDTO;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final InterventionRepository interventionRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final PrescriptionRepository prescriptionRepository;

    public ConsultationServiceImpl(ConsultationRepository consultationRepository,
            DossierMedicalRepository dossierMedicalRepository,
            InterventionRepository interventionRepository,
            OrdonnanceRepository ordonnanceRepository,
            PrescriptionRepository prescriptionRepository) {
        this.consultationRepository = consultationRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.interventionRepository = interventionRepository;
        this.ordonnanceRepository = ordonnanceRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public ConsultationDTO createConsultation(CreateConsultationDTO dto) {
        // Convertir DTO en Entity
        Consultation consultation = convertToEntity(dto);

        // Validation
        validateConsultation(consultation);

        // Définir la date si non fournie
        if (consultation.getDate() == null) {
            consultation.setDate(LocalDate.now());
        }

        // Définir le statut par défaut si non fourni
        if (consultation.getStatut() == null) {
            consultation.setStatut(StatutConsultation.EN_ATTENTE);
        }

        // Set audit fields
        consultation.setCreePar("system"); // À remplacer par l'utilisateur connecté
        consultation.setModifiePar("system");

        // Créer la consultation
        consultationRepository.create(consultation);
        return convertToDTO(consultation);
    }

    @Override
    public ConsultationDTO updateConsultation(Long consultationId, UpdateConsultationDTO dto) {
        Consultation existingConsultation = consultationRepository.findById(consultationId);
        if (existingConsultation == null) {
            throw new ConsultationNotFoundException(consultationId);
        }

        // Mettre à jour l'entité depuis le DTO
        updateEntityFromDTO(existingConsultation, dto);

        // Validation
        validateConsultation(existingConsultation);

        // Mettre à jour les champs d'audit
        existingConsultation.setModifiePar("system"); // À remplacer par l'utilisateur connecté

        consultationRepository.update(existingConsultation);
        return convertToDTO(existingConsultation);
    }

    @Override
    public void deleteConsultation(Long consultationId) {
        if (!existsById(consultationId)) {
            throw new ConsultationNotFoundException(consultationId);
        }
        consultationRepository.deleteById(consultationId);
    }

    @Override
    public ConsultationDTO getConsultationById(Long consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId);
        if (consultation == null) {
            throw new ConsultationNotFoundException(consultationId);
        }
        return convertToDTO(consultation);
    }

    @Override
    public List<ConsultationDTO> getAllConsultations() {
        return consultationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationDTO> findByDossierMedicalId(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical ne peut pas être null");
        }
        return consultationRepository.findByDossierMedicalId(dossierId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ma.whitecare.entities.medical.Consultation> getConsultationsByDossierId(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical ne peut pas être null");
        }
        List<ma.whitecare.entities.medical.Consultation> consultations = consultationRepository
                .findByDossierMedicalId(dossierId);
        for (ma.whitecare.entities.medical.Consultation c : consultations) {
            c.setInterventionMedecinList(interventionRepository.findByConsultationId(c.getIdConsultation()));
        }
        return consultations;
    }

    @Override
    public List<ConsultationDTO> findByStatut(StatutConsultation statut) {
        if (statut == null) {
            throw new ValidationException("Le statut ne peut pas être null");
        }
        return consultationRepository.findByStatut(statut).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationDTO> findByDate(LocalDate date) {
        if (date == null) {
            throw new ValidationException("La date ne peut pas être null");
        }
        return consultationRepository.findByDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationDTO> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Les dates de début et de fin sont requises");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("La date de début doit être antérieure à la date de fin");
        }
        return consultationRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationDTO> findByDossierAndDate(Long dossierId, LocalDate date) {
        if (dossierId == null || date == null) {
            throw new ValidationException("L'ID du dossier médical et la date sont requis");
        }
        return consultationRepository.findByDossierAndDate(dossierId, date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void changeStatut(Long consultationId, StatutConsultation nouveauStatut) {
        if (nouveauStatut == null) {
            throw new ValidationException("Le nouveau statut ne peut pas être null");
        }
        Consultation consultation = consultationRepository.findById(consultationId);
        if (consultation == null) {
            throw new ConsultationNotFoundException(consultationId);
        }

        // Validation des transitions de statut
        StatutConsultation ancienStatut = consultation.getStatut();
        if (ancienStatut == StatutConsultation.TERMINEE && nouveauStatut != StatutConsultation.TERMINEE) {
            throw new BusinessRuleException("Une consultation terminée ne peut pas changer de statut");
        }
        if (ancienStatut == StatutConsultation.ANNULEE && nouveauStatut != StatutConsultation.ANNULEE) {
            throw new BusinessRuleException("Une consultation annulée ne peut pas changer de statut");
        }

        consultation.setStatut(nouveauStatut);
        consultation.setModifiePar("system");
        consultationRepository.update(consultation);
    }

    @Override
    public boolean existsById(Long consultationId) {
        if (consultationId == null) {
            return false;
        }
        return consultationRepository.existsById(consultationId);
    }

    @Override
    public long countAllConsultations() {
        return consultationRepository.countAll();
    }

    @Override
    public long countByStatut(StatutConsultation statut) {
        if (statut == null) {
            throw new ValidationException("Le statut ne peut pas être null");
        }
        return consultationRepository.countByStatut(statut);
    }

    @Override
    public long countByDossierMedicalId(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical ne peut pas être null");
        }
        return consultationRepository.countByDossierMedicalId(dossierId);
    }

    @Override
    public List<ConsultationDTO> getConsultationsDuJour() {
        LocalDate aujourdhui = LocalDate.now();
        return consultationRepository.findByDate(aujourdhui).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void addIntervention(Long consultationId, ma.whitecare.entities.medical.InterventionMedecin intervention) {
        ma.whitecare.entities.medical.Consultation consultation = consultationRepository.findById(consultationId);
        if (consultation == null) {
            throw new ConsultationNotFoundException(consultationId);
        }
        intervention.setConsultation(consultation);
        interventionRepository.create(intervention); // Assuming create/save exists
    }

    @Override
    public ConsultationCompleteDTO getConsultationComplete(Long consultationId) {
        if (consultationId == null) {
            throw new ValidationException("L'ID de la consultation ne peut pas être null");
        }

        Consultation consultation = consultationRepository.findById(consultationId);
        if (consultation == null) {
            throw new ConsultationNotFoundException(consultationId);
        }

        // Convertir la consultation de base en DTO
        ConsultationCompleteDTO dto = convertToConsultationCompleteDTO(consultation);

        // Charger les interventions associées
        List<InterventionMedecin> interventions = interventionRepository.findByConsultationId(consultationId);
        dto.setInterventions(interventions.stream()
                .map(this::convertInterventionToDTO)
                .collect(Collectors.toList()));

        // Charger les prescriptions associées via ordonnances
        List<ma.whitecare.entities.medical.Ordonnance> ordonnances = ordonnanceRepository.findAll().stream()
                .filter(ord -> ord.getConsultation() != null &&
                        ord.getConsultation().getIdConsultation() != null &&
                        ord.getConsultation().getIdConsultation().equals(consultationId))
                .collect(Collectors.toList());

        // Récupérer toutes les prescriptions de ces ordonnances
        List<PrescriptionDTO> prescriptions = ordonnances.stream()
                .flatMap(ord -> prescriptionRepository.findByOrdonnanceId(ord.getIdOrd()).stream())
                .map(this::convertPrescriptionToDTO)
                .collect(Collectors.toList());

        dto.setPrescriptions(prescriptions);

        return dto;
    }

    // ========== CONVERSION METHODS ==========
    private Consultation convertToEntity(CreateConsultationDTO dto) {
        // Charger le dossier médical
        DossierMedicale dossier = dossierMedicalRepository.findById(dto.getDossierMedicalId());
        if (dossier == null) {
            throw new RelatedEntityNotFoundException("dossier médical", dto.getDossierMedicalId());
        }

        // Construire l'entité
        Consultation consultation = Consultation.builder()
                .dossierMedicale(dossier)
                .Date(dto.getDate() != null ? dto.getDate() : LocalDate.now())
                .statut(dto.getStatut() != null ? dto.getStatut() : StatutConsultation.EN_ATTENTE)
                .observationMedecin(dto.getObservationMedecin())
                .build();
        return consultation;
    }

    private void updateEntityFromDTO(Consultation entity, UpdateConsultationDTO dto) {
        // Mettre à jour la date si fournie
        if (dto.getDate() != null) {
            entity.setDate(dto.getDate());
        }

        // Mettre à jour le statut si fourni
        if (dto.getStatut() != null) {
            entity.setStatut(dto.getStatut());
        }

        // Mettre à jour l'observation si fournie
        if (dto.getObservationMedecin() != null) {
            entity.setObservationMedecin(dto.getObservationMedecin());
        }

        // Mettre à jour le dossier médical si fourni
        if (dto.getDossierMedicalId() != null) {
            DossierMedicale dossier = dossierMedicalRepository.findById(dto.getDossierMedicalId());
            if (dossier == null) {
                throw new RelatedEntityNotFoundException("dossier médical", dto.getDossierMedicalId());
            }
            entity.setDossierMedicale(dossier);
        }
    }

    private ConsultationDTO convertToDTO(Consultation entity) {
        return ConsultationDTO.builder()
                .idConsultation(entity.getIdConsultation())
                .date(entity.getDate())
                .statut(entity.getStatut())
                .observationMedecin(entity.getObservationMedecin())
                .dossierMedicalId(entity.getDossierMedicale() != null ? entity.getDossierMedicale().getIdDM() : null)
                .dateCreation(entity.getDateCreation())
                .dateDerniereModification(entity.getDateDerniereModification())
                .createdBy(entity.getCreePar())
                .updatedBy(entity.getModifiePar())
                .build();
    }

    private ConsultationCompleteDTO convertToConsultationCompleteDTO(Consultation entity) {
        return ConsultationCompleteDTO.builder()
                .idConsultation(entity.getIdConsultation())
                .date(entity.getDate())
                .statut(entity.getStatut())
                .observationMedecin(entity.getObservationMedecin())
                .dossierMedicalId(entity.getDossierMedicale() != null ? entity.getDossierMedicale().getIdDM() : null)
                .dateCreation(entity.getDateCreation())
                .dateDerniereModification(entity.getDateDerniereModification())
                .createdBy(entity.getCreePar())
                .updatedBy(entity.getModifiePar())
                .build();
    }

    private InterventionDTO convertInterventionToDTO(InterventionMedecin entity) {
        return InterventionDTO.builder()
                .idIM(entity.getIdIM())
                .prixDePatient(entity.getPrixDePatient())
                .numDent(entity.getNumDent())
                .acteId(entity.getActe() != null ? entity.getActe().getIdActe() : null)
                .consultationId(entity.getConsultation() != null ? entity.getConsultation().getIdConsultation() : null)
                .dateCreation(entity.getDateCreation())
                .dateDerniereModification(entity.getDateDerniereModification())
                .createdBy(entity.getCreePar())
                .updatedBy(entity.getModifiePar())
                .build();
    }

    private PrescriptionDTO convertPrescriptionToDTO(Prescription entity) {
        return PrescriptionDTO.builder()
                .idPr(entity.getIdPr())
                .quantite(entity.getQuantite())
                .frequence(entity.getFrequence())
                .dureeEnJours(entity.getDureeEnJours())
                .medicamentId(entity.getMedicament() != null ? entity.getMedicament().getIdMct() : null)
                .ordonnanceId(entity.getOrdonnance() != null ? entity.getOrdonnance().getIdOrd() : null)
                .dateCreation(entity.getDateCreation())
                .dateDerniereModification(entity.getDateDerniereModification())
                .createdBy(entity.getCreePar())
                .updatedBy(entity.getModifiePar())
                .build();
    }

    // ========== VALIDATION PRIVÉE ==========
    private void validateConsultation(Consultation consultation) {
        if (consultation == null) {
            throw new ValidationException("La consultation ne peut pas être null");
        }
    }
}