package ma.whitecare.service.modules.certificat.impl;

import ma.whitecare.common.exceptions.CertificatNotFoundException;
import ma.whitecare.common.exceptions.ConsultationNotFoundException;
import ma.whitecare.common.exceptions.DossierMedicalNotFoundException;
import ma.whitecare.common.exceptions.InvalidDateRangeException;
import ma.whitecare.common.validators.CertificatValidator;
import ma.whitecare.entities.medical.Certificat;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.mvc.dto.CertificatDto.CertificatDTO;
import ma.whitecare.mvc.dto.CertificatDto.CreateCertificatDTO;
import ma.whitecare.mvc.dto.CertificatDto.UpdateCertificatDTO;
import ma.whitecare.common.util.PDFGenerator;
import ma.whitecare.repository.modules.certificat.api.CertificatRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.service.modules.certificat.api.CertificatService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CertificatServiceImpl implements CertificatService {

    private final CertificatRepository certificatRepository;
    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    public CertificatServiceImpl(CertificatRepository certificatRepository,
                                 ConsultationRepository consultationRepository,
                                 DossierMedicalRepository dossierMedicalRepository,
                                 PatientRepository patientRepository,
                                 MedecinRepository medecinRepository) {
        this.certificatRepository = certificatRepository;
        this.consultationRepository = consultationRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
    }

    // ========== CRUD CERTIFICATS ==========

    @Override
    public Certificat createCertificat(CreateCertificatDTO certificatDTO) {
        // Validation
        List<String> errors = CertificatValidator.validateCreateCertificat(certificatDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'existence des clés étrangères
        if (certificatDTO.getDossierMedicaleId() != null) {
            validateDossierMedicalExists(certificatDTO.getDossierMedicaleId());
        }
        if (certificatDTO.getConsultationId() != null) {
            validateConsultationExists(certificatDTO.getConsultationId());
        }

        // Convertir DTO en entité
        Certificat certificat = convertToCertificat(certificatDTO);

        // Calculer la durée si non fournie
        if (certificat.getDuree() == null && certificat.getDateDebut() != null && certificat.getDateFin() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    certificat.getDateDebut(), certificat.getDateFin()) + 1;
            certificat.setDuree((int) days);
        }

        // Définir les valeurs par défaut
        setDefaultValues(certificat);

        // Sauvegarder
        certificatRepository.create(certificat);

        return certificat;
    }

    @Override
    public Certificat getCertificatById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du certificat est obligatoire");
        }

        Certificat certificat = certificatRepository.findById(id);
        if (certificat == null) {
            throw new CertificatNotFoundException(id);
        }
        return certificat;
    }

    @Override
    public List<Certificat> findAll() {
        return certificatRepository.findAll();
    }

    @Override
    public Certificat updateCertificat(Long id, UpdateCertificatDTO updateDTO) {
        // Récupérer le certificat existant
        Certificat certificat = getCertificatById(id);

        // Validation
        List<String> errors = CertificatValidator.validateUpdateCertificat(updateDTO);
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
        updateCertificatFields(certificat, updateDTO);

        // Recalculer la durée si les dates ont changé
        if (certificat.getDateDebut() != null && certificat.getDateFin() != null) {
            if (updateDTO.getDuree() == null || 
                (updateDTO.getDateDebut() != null || updateDTO.getDateFin() != null)) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(
                        certificat.getDateDebut(), certificat.getDateFin()) + 1;
                certificat.setDuree((int) days);
            }
        }

        // Sauvegarder
        certificatRepository.update(certificat);

        return certificat;
    }

    @Override
    public void deleteCertificat(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du certificat est obligatoire");
        }

        // Vérifier que le certificat existe
        getCertificatById(id);

        // Supprimer
        certificatRepository.deleteById(id);
    }

    // ========== RECHERCHES ==========

    @Override
    public boolean existsById(Long id) {
        return certificatRepository.existsById(id);
    }

    @Override
    public List<Certificat> findByDossierMedicaleId(Long dossierId) {
        if (dossierId == null) {
            throw new IllegalArgumentException("L'ID du dossier médical est obligatoire");
        }
        return certificatRepository.findByDossierMedicaleId(dossierId);
    }

    @Override
    public List<Certificat> findByConsultationId(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est obligatoire");
        }
        return certificatRepository.findByConsultationId(consultationId);
    }

    @Override
    public List<Certificat> findByDateRange(LocalDate startDate, LocalDate endDate) {
        // Validation de la plage de dates
        List<String> errors = CertificatValidator.validateDateRange(startDate, endDate);
        if (!errors.isEmpty()) {
            throw new InvalidDateRangeException(String.join(", ", errors));
        }

        // Filtrer tous les certificats par plage de dates
        return certificatRepository.findAll().stream()
                .filter(c -> {
                    if (c.getDateDebut() == null || c.getDateFin() == null) {
                        return false;
                    }
                    // Vérifier si le certificat chevauche la plage de dates
                    return !c.getDateFin().isBefore(startDate) && !c.getDateDebut().isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    // ========== CONVERSIONS ==========

    @Override
    public CertificatDTO convertToDTO(Certificat certificat) {
        if (certificat == null) {
            return null;
        }

        return CertificatDTO.builder()
                .idCertif(certificat.getIdCertif())
                .dateDebut(certificat.getDateDebut())
                .dateFin(certificat.getDateFin())
                .duree(certificat.getDuree())
                .noteMedecin(certificat.getNoteMedecin())
                .dossierMedicaleId(certificat.getDossierMedicale() != null ?
                        certificat.getDossierMedicale().getIdDM() : null)
                .consultationId(certificat.getConsultation() != null ?
                        certificat.getConsultation().getIdConsultation() : null)
                .dateCreation(certificat.getDateCreation())
                .dateDerniereModification(certificat.getDateDerniereModification())
                .creePar(certificat.getCreePar())
                .modifiePar(certificat.getModifiePar())
                .build();
    }

    @Override
    public List<CertificatDTO> convertToDTOList(List<Certificat> certificats) {
        if (certificats == null) {
            return List.of();
        }
        return certificats.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== GÉNÉRATION PDF ==========

    @Override
    public byte[] generatePDF(Long certificatId) throws java.io.IOException {
        // Récupérer le certificat
        Certificat certificat = getCertificatById(certificatId);

        // Récupérer le dossier médical et le patient
        DossierMedicale dossier = null;
        if (certificat.getDossierMedicale() != null && certificat.getDossierMedicale().getIdDM() != null) {
            dossier = dossierMedicalRepository.findById(certificat.getDossierMedicale().getIdDM());
        }

        if (dossier == null || dossier.getPatient() == null) {
            throw new IllegalArgumentException("Impossible de générer le PDF : informations du patient manquantes");
        }

        // Récupérer le patient complet
        ma.whitecare.entities.patient.Patient patient = patientRepository.findById(
                dossier.getPatient().getId_Patient());
        if (patient == null) {
            throw new IllegalArgumentException("Patient non trouvé");
        }

        // Récupérer le médecin
        String medecinNom = "Non spécifié";
        String medecinPrenom = "";
        String specialite = null;
        if (dossier.getMedecin() != null && dossier.getMedecin().getIdUser() != null) {
            ma.whitecare.entities.user.Medecin medecin = medecinRepository.findById(
                    dossier.getMedecin().getIdUser());
            if (medecin != null) {
                medecinNom = medecin.getNom() != null ? medecin.getNom() : "Non spécifié";
                medecinPrenom = medecin.getPrenom() != null ? medecin.getPrenom() : "";
                specialite = medecin.getSpecialite();
            }
        }

        // Préparer les dates
        String dateDebut = certificat.getDateDebut() != null ?
                certificat.getDateDebut().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "Non spécifiée";
        String dateFin = certificat.getDateFin() != null ?
                certificat.getDateFin().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "Non spécifiée";

        // Générer le PDF
        return PDFGenerator.generateCertificatPDF(
                patient.getNom() != null ? patient.getNom() : "",
                patient.getPrenom() != null ? patient.getPrenom() : "",
                medecinNom,
                medecinPrenom,
                specialite,
                dateDebut,
                dateFin,
                certificat.getDuree(),
                certificat.getNoteMedecin()
        );
    }

    // ========== MÉTHODES PRIVÉES ==========

    private Certificat convertToCertificat(CreateCertificatDTO dto) {
        Certificat certificat = new Certificat();
        certificat.setDateDebut(dto.getDateDebut());
        certificat.setDateFin(dto.getDateFin());
        certificat.setDuree(dto.getDuree());
        certificat.setNoteMedecin(dto.getNoteMedecin());

        // Créer un objet DossierMedicale avec juste l'ID si fourni
        if (dto.getDossierMedicaleId() != null) {
            DossierMedicale dossierMedicale = new DossierMedicale();
            dossierMedicale.setIdDM(dto.getDossierMedicaleId());
            certificat.setDossierMedicale(dossierMedicale);
        }

        // Créer un objet Consultation avec juste l'ID si fourni
        if (dto.getConsultationId() != null) {
            Consultation consultation = new Consultation();
            consultation.setIdConsultation(dto.getConsultationId());
            certificat.setConsultation(consultation);
        }

        certificat.setCreePar(dto.getCreePar() != null ? dto.getCreePar() : "system");
        certificat.setModifiePar(dto.getModifiePar() != null ? dto.getModifiePar() : "system");
        return certificat;
    }

    private void setDefaultValues(Certificat certificat) {
        if (certificat.getDuree() == null && certificat.getDateDebut() != null && certificat.getDateFin() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    certificat.getDateDebut(), certificat.getDateFin()) + 1;
            certificat.setDuree((int) days);
        }
        if (certificat.getCreePar() == null) {
            certificat.setCreePar("system");
        }
        if (certificat.getModifiePar() == null) {
            certificat.setModifiePar("system");
        }
    }

    private void updateCertificatFields(Certificat certificat, UpdateCertificatDTO updateDTO) {
        if (updateDTO.getDateDebut() != null) {
            certificat.setDateDebut(updateDTO.getDateDebut());
        }
        if (updateDTO.getDateFin() != null) {
            certificat.setDateFin(updateDTO.getDateFin());
        }
        if (updateDTO.getDuree() != null) {
            certificat.setDuree(updateDTO.getDuree());
        }
        if (updateDTO.getNoteMedecin() != null) {
            certificat.setNoteMedecin(updateDTO.getNoteMedecin());
        }
        if (updateDTO.getDossierMedicaleId() != null) {
            DossierMedicale dossierMedicale = new DossierMedicale();
            dossierMedicale.setIdDM(updateDTO.getDossierMedicaleId());
            certificat.setDossierMedicale(dossierMedicale);
        }
        if (updateDTO.getConsultationId() != null) {
            Consultation consultation = new Consultation();
            consultation.setIdConsultation(updateDTO.getConsultationId());
            certificat.setConsultation(consultation);
        }
        if (updateDTO.getModifiePar() != null) {
            certificat.setModifiePar(updateDTO.getModifiePar());
        } else {
            certificat.setModifiePar("system");
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
