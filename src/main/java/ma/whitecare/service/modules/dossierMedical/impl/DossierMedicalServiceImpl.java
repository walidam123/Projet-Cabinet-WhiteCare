package ma.whitecare.service.modules.dossierMedical.impl;

import ma.whitecare.common.exceptions.DossierMedicalNotFoundException;
import ma.whitecare.common.exceptions.DuplicateDossierMedicalException;
import ma.whitecare.common.exceptions.RelatedEntityNotFoundException;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;

import ma.whitecare.mvc.dto.DossierMedicale.ConsultationDTO;
import ma.whitecare.mvc.dto.DossierMedicale.CreateDossierMedicalDTO;
import ma.whitecare.mvc.dto.DossierMedicale.DossierMedicalDTO;
import ma.whitecare.mvc.dto.DossierMedicale.UpdateDossierMedicalDTO;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.service.modules.dossierMedical.api.DossierMedicalService;
import ma.whitecare.common.exceptions.InvalidDateRangeException;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final ConsultationRepository consultationRepository;

    public DossierMedicalServiceImpl(DossierMedicalRepository dossierMedicalRepository,
            PatientRepository patientRepository,
            MedecinRepository medecinRepository,
            ConsultationRepository consultationRepository) {
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.consultationRepository = consultationRepository;
    }

    @Override
    public DossierMedicalDTO createDossierMedical(CreateDossierMedicalDTO dto) {
        // Convertir DTO en Entity
        DossierMedicale dossierMedical = convertToEntity(dto);

        // Validation
        validateDossierMedical(dossierMedical);

        // Vérifier l'unicité : un patient ne peut avoir qu'un seul dossier médical
        if (existsByPatientId(dto.getPatientId())) {
            throw new DuplicateDossierMedicalException(dto.getPatientId());
        }

        // Définir la date de création si non fournie
        if (dossierMedical.getDateDeCreation() == null) {
            dossierMedical.setDateDeCreation(LocalDate.now());
        }

        // Set audit fields
        dossierMedical.setCreePar("system"); // À remplacer par l'utilisateur connecté
        dossierMedical.setModifiePar("system");

        // Créer le dossier médical
        dossierMedicalRepository.create(dossierMedical);
        return convertToDTO(dossierMedical);
    }

    @Override
    public DossierMedicalDTO updateDossierMedical(Long dossierId, UpdateDossierMedicalDTO dto) {
        DossierMedicale existingDossier = dossierMedicalRepository.findById(dossierId);
        if (existingDossier == null) {
            throw new DossierMedicalNotFoundException(dossierId);
        }

        // Mettre à jour l'entité depuis le DTO
        updateEntityFromDTO(existingDossier, dto);

        // Validation
        validateDossierMedical(existingDossier);

        // Mettre à jour les champs d'audit
        existingDossier.setModifiePar("system"); // À remplacer par l'utilisateur connecté

        dossierMedicalRepository.update(existingDossier);
        return convertToDTO(existingDossier);
    }

    @Override
    public void deleteDossierMedical(Long dossierId) {
        if (!existsById(dossierId)) {
            throw new DossierMedicalNotFoundException(dossierId);
        }
        dossierMedicalRepository.deleteById(dossierId);
    }

    @Override
    public DossierMedicalDTO getDossierMedicalById(Long dossierId) {
        DossierMedicale dossier = dossierMedicalRepository.findById(dossierId);
        if (dossier == null) {
            throw new DossierMedicalNotFoundException(dossierId);
        }
        return convertToDTO(dossier);
    }

    @Override
    public List<DossierMedicalDTO> getAllDossiersMedicaux() {
        return dossierMedicalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DossierMedicalDTO> findByPatientId(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("L'ID du patient ne peut pas être null");
        }
        return dossierMedicalRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DossierMedicalDTO> findByMedecinId(Long medecinId) {
        if (medecinId == null) {
            throw new ValidationException("L'ID du médecin ne peut pas être null");
        }
        return dossierMedicalRepository.findByMedecinId(medecinId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DossierMedicalDTO> findByDateCreation(LocalDate date) {
        if (date == null) {
            throw new ValidationException("La date ne peut pas être null");
        }
        return dossierMedicalRepository.findByDateCreation(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DossierMedicalDTO> findByDateCreationBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Les dates de début et de fin sont requises");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("La date de début doit être antérieure à la date de fin");
        }
        return dossierMedicalRepository.findByDateCreationBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPatientId(Long patientId) {
        if (patientId == null) {
            return false;
        }
        return dossierMedicalRepository.existsByPatientId(patientId);
    }

    @Override
    public boolean existsById(Long dossierId) {
        if (dossierId == null) {
            return false;
        }
        return dossierMedicalRepository.existsById(dossierId);
    }

    @Override
    public long countAllDossiers() {
        return dossierMedicalRepository.countAll();
    }

    @Override
    public long countByPatientId(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("L'ID du patient ne peut pas être null");
        }
        return dossierMedicalRepository.countByPatientId(patientId);
    }

    @Override
    public long countByMedecinId(Long medecinId) {
        if (medecinId == null) {
            throw new ValidationException("L'ID du médecin ne peut pas être null");
        }
        return dossierMedicalRepository.countByMedecinId(medecinId);
    }

    @Override
    public ConsultationDTO getDerniereConsultation(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical ne peut pas être null");
        }

        // Vérifier que le dossier existe
        if (!existsById(dossierId)) {
            throw new DossierMedicalNotFoundException(dossierId);
        }

        // Récupérer toutes les consultations du dossier, triées par date décroissante
        List<Consultation> consultations = consultationRepository.findByDossierMedicalId(dossierId);

        if (consultations == null || consultations.isEmpty()) {
            return null;
        }

        // Retourner la consultation la plus récente
        Consultation consultation = consultations.stream()
                .max(Comparator.comparing(Consultation::getDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        return consultation != null ? convertConsultationToDTO(consultation) : null;
    }

    @Override
    public List<ConsultationDTO> getHistoriqueCompletPatient(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("L'ID du patient ne peut pas être null");
        }

        // Récupérer le dossier médical du patient
        List<DossierMedicale> dossiers = dossierMedicalRepository.findByPatientId(patientId);

        if (dossiers == null || dossiers.isEmpty()) {
            return List.of();
        }

        // Récupérer toutes les consultations de tous les dossiers du patient
        return dossiers.stream()
                .flatMap(dossier -> consultationRepository.findByDossierMedicalId(dossier.getIdDM()).stream())
                .sorted(Comparator.comparing(Consultation::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::convertConsultationToDTO)
                .collect(Collectors.toList());
    }

    // ========== CONVERSION METHODS ==========
    private DossierMedicale convertToEntity(CreateDossierMedicalDTO dto) {
        // Charger le patient
        Patient patient = patientRepository.findById(dto.getPatientId());
        if (patient == null) {
            throw new RelatedEntityNotFoundException("patient", dto.getPatientId());
        }

        // Charger le médecin
        Medecin medecin = medecinRepository.findById(dto.getMedecinId());
        if (medecin == null) {
            throw new RelatedEntityNotFoundException("médecin", dto.getMedecinId());
        }

        // Construire l'entité
        return DossierMedicale.builder()
                .dateDeCreation(dto.getDateDeCreation() != null ? dto.getDateDeCreation() : LocalDate.now())
                .patient(patient)
                .medecin(medecin)
                .build();
    }

    private void updateEntityFromDTO(DossierMedicale entity, UpdateDossierMedicalDTO dto) {
        // Mettre à jour la date de création si fournie
        if (dto.getDateDeCreation() != null) {
            entity.setDateDeCreation(dto.getDateDeCreation());
        }

        // Mettre à jour le patient si fourni
        if (dto.getPatientId() != null) {
            Patient patient = patientRepository.findById(dto.getPatientId());
            if (patient == null) {
                throw new RelatedEntityNotFoundException("patient", dto.getPatientId());
            }
            entity.setPatient(patient);
        }

        // Mettre à jour le médecin si fourni
        if (dto.getMedecinId() != null) {
            Medecin medecin = medecinRepository.findById(dto.getMedecinId());
            if (medecin == null) {
                throw new RelatedEntityNotFoundException("médecin", dto.getMedecinId());
            }
            entity.setMedecin(medecin);
        }
    }

    private DossierMedicalDTO convertToDTO(DossierMedicale entity) {
        return DossierMedicalDTO.builder()
                .idDM(entity.getIdDM())
                .dateDeCreation(entity.getDateDeCreation())
                .patientId(entity.getPatient() != null ? entity.getPatient().getId_Patient() : null)
                .medecinId(entity.getMedecin() != null ? entity.getMedecin().getIdUser() : null)
                .dateCreation(entity.getDateCreation())
                .dateDerniereModification(entity.getDateDerniereModification())
                .createdBy(entity.getCreePar())
                .updatedBy(entity.getModifiePar())
                .build();
    }

    private ConsultationDTO convertConsultationToDTO(Consultation consultation) {
        return ConsultationDTO.builder()
                .idConsultation(consultation.getIdConsultation())
                .date(consultation.getDate())
                .statut(consultation.getStatut())
                .observationMedecin(consultation.getObservationMedecin())
                .dossierMedicalId(
                        consultation.getDossierMedicale() != null ? consultation.getDossierMedicale().getIdDM() : null)
                .dateCreation(consultation.getDateCreation())
                .dateDerniereModification(consultation.getDateDerniereModification())
                .createdBy(consultation.getCreePar())
                .updatedBy(consultation.getModifiePar())
                .build();
    }

    // ========== VALIDATION PRIVÉE ==========
    private void validateDossierMedical(DossierMedicale dossierMedical) {
        if (dossierMedical == null) {
            throw new ValidationException("Le dossier médical ne peut pas être null");
        }
        // La validation des dates est gérée dans les méthodes spécifiques
    }

    @Override
    public DossierMedicale saveDossier(DossierMedicale dossierMedical) {
        if (dossierMedical == null) {
            throw new ValidationException("Le dossier médical ne peut pas être null");
        }
        dossierMedicalRepository.create(dossierMedical);
        return dossierMedical;
    }

    @Override
    public DossierMedicale getDossierByPatientId(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("L'ID du patient ne peut pas être null");
        }
        return dossierMedicalRepository.findByPatientId(patientId).stream()
                .findFirst()
                .orElse(null);
    }
}