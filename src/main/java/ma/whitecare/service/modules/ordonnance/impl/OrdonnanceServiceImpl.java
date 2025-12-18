package ma.whitecare.service.modules.ordonnance.impl;

import ma.whitecare.common.exceptions.ConsultationNotFoundException;
import ma.whitecare.common.exceptions.DossierMedicalNotFoundException;
import ma.whitecare.common.exceptions.OrdonnanceNotFoundException;
import ma.whitecare.common.validators.OrdonnanceValidator;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.mvc.dto.OrdonnanceDto.CreateOrdonnanceDTO;
import ma.whitecare.mvc.dto.OrdonnanceDto.OrdonnanceDTO;
import ma.whitecare.mvc.dto.OrdonnanceDto.UpdateOrdonnanceDTO;
import ma.whitecare.repository.modules.Ordonnance.OrdonnanceRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.service.modules.ordonnance.api.OrdonnanceService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierMedicalRepository;

    public OrdonnanceServiceImpl(OrdonnanceRepository ordonnanceRepository,
                                 ConsultationRepository consultationRepository,
                                 DossierMedicalRepository dossierMedicalRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
        this.consultationRepository = consultationRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
    }

    // ========== CRUD ORDONNANCES ==========

    @Override
    public Ordonnance createOrdonnance(CreateOrdonnanceDTO ordonnanceDTO) {
        // Validation
        List<String> errors = OrdonnanceValidator.validateCreateOrdonnance(ordonnanceDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'existence des clés étrangères
        if (ordonnanceDTO.getDossierMedicaleId() != null) {
            validateDossierMedicalExists(ordonnanceDTO.getDossierMedicaleId());
        }
        if (ordonnanceDTO.getConsultationId() != null) {
            validateConsultationExists(ordonnanceDTO.getConsultationId());
        }

        // Convertir DTO en entité
        Ordonnance ordonnance = convertToOrdonnance(ordonnanceDTO);

        // Définir les valeurs par défaut
        setDefaultValues(ordonnance);

        // Sauvegarder
        ordonnanceRepository.create(ordonnance);

        return ordonnance;
    }

    @Override
    public Ordonnance getOrdonnanceById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est obligatoire");
        }

        Ordonnance ordonnance = ordonnanceRepository.findById(id);
        if (ordonnance == null) {
            throw new OrdonnanceNotFoundException(id);
        }
        return ordonnance;
    }

    @Override
    public List<Ordonnance> findAll() {
        return ordonnanceRepository.findAll();
    }

    @Override
    public Ordonnance updateOrdonnance(Long id, UpdateOrdonnanceDTO updateDTO) {
        // Récupérer l'ordonnance existante
        Ordonnance ordonnance = getOrdonnanceById(id);

        // Validation
        List<String> errors = OrdonnanceValidator.validateUpdateOrdonnance(updateDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'existence des clés étrangères si elles sont modifiées
        if (updateDTO.getDossierMedicaleId() != null) {
            validateDossierMedicalExists(updateDTO.getDossierMedicaleId());
        }
        if (updateDTO.getConsultationId() != null) {
            validateConsultationExists(updateDTO.getConsultationId());
        }

        // Mettre à jour les champs
        updateOrdonnanceFields(ordonnance, updateDTO);

        // Sauvegarder
        ordonnanceRepository.update(ordonnance);

        return ordonnance;
    }

    @Override
    public void deleteOrdonnance(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de l'ordonnance est obligatoire");
        }

        // Vérifier que l'ordonnance existe
        getOrdonnanceById(id);

        // Supprimer
        ordonnanceRepository.deleteById(id);
    }

    // ========== RECHERCHES ==========

    @Override
    public boolean existsById(Long id) {
        return ordonnanceRepository.existsById(id);
    }

    @Override
    public List<Ordonnance> findByDossierMedicaleId(Long dossierId) {
        if (dossierId == null) {
            throw new IllegalArgumentException("L'ID du dossier médical est obligatoire");
        }
        return ordonnanceRepository.findByDossierMedicaleId(dossierId);
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est obligatoire");
        }
        return ordonnanceRepository.findByConsultationId(consultationId);
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        // Validation de la plage de dates
        List<String> errors = OrdonnanceValidator.validateDateRange(start, end);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
        return ordonnanceRepository.findByDateBetween(start, end);
    }

    // ========== CONVERSIONS ==========

    @Override
    public OrdonnanceDTO convertToDTO(Ordonnance ordonnance) {
        if (ordonnance == null) {
            return null;
        }

        return OrdonnanceDTO.builder()
                .idOrd(ordonnance.getIdOrd())
                .date(ordonnance.getDate())
                .dossierMedicaleId(ordonnance.getDossierMedicaleid())
                .consultationId(ordonnance.getConsultationid())
                .dateCreation(ordonnance.getDateCreation())
                .dateDerniereModification(ordonnance.getDateDerniereModification())
                .creePar(ordonnance.getCreePar())
                .modifiePar(ordonnance.getModifiePar())
                .build();
    }

    @Override
    public List<OrdonnanceDTO> convertToDTOList(List<Ordonnance> ordonnances) {
        if (ordonnances == null) {
            return List.of();
        }
        return ordonnances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private Ordonnance convertToOrdonnance(CreateOrdonnanceDTO dto) {
        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setDate(dto.getDate());
        ordonnance.setDossierMedicaleid(dto.getDossierMedicaleId());
        ordonnance.setConsultationid(dto.getConsultationId());
        ordonnance.setCreePar(dto.getCreePar() != null ? dto.getCreePar() : "system");
        ordonnance.setModifiePar(dto.getModifiePar() != null ? dto.getModifiePar() : "system");
        return ordonnance;
    }

    private void setDefaultValues(Ordonnance ordonnance) {
        if (ordonnance.getDate() == null) {
            ordonnance.setDate(LocalDate.now());
        }
        if (ordonnance.getCreePar() == null) {
            ordonnance.setCreePar("system");
        }
        if (ordonnance.getModifiePar() == null) {
            ordonnance.setModifiePar("system");
        }
    }

    private void updateOrdonnanceFields(Ordonnance ordonnance, UpdateOrdonnanceDTO updateDTO) {
        if (updateDTO.getDate() != null) {
            ordonnance.setDate(updateDTO.getDate());
        }
        if (updateDTO.getDossierMedicaleId() != null) {
            ordonnance.setDossierMedicaleid(updateDTO.getDossierMedicaleId());
        }
        if (updateDTO.getConsultationId() != null) {
            ordonnance.setConsultationid(updateDTO.getConsultationId());
        }
        if (updateDTO.getModifiePar() != null) {
            ordonnance.setModifiePar(updateDTO.getModifiePar());
        } else {
            ordonnance.setModifiePar("system");
        }
    }

    // ========== VALIDATION DES CLÉS ÉTRANGÈRES ==========

    private void validateDossierMedicalExists(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical est obligatoire");
        }

        if (dossierMedicalRepository.findById(dossierId) == null) {
            throw new DossierMedicalNotFoundException(dossierId);
        }
    }

    private void validateConsultationExists(Long consultationId) {
        if (consultationId == null) {
            throw new ValidationException("L'ID de la consultation est obligatoire");
        }

        if (consultationRepository.findById(consultationId) == null) {
            throw new ConsultationNotFoundException(consultationId);
        }
    }
}
