package ma.whitecare.service.modules.dossierMedical;

import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des dossiers médicaux.
 * Gère la création, modification, consultation et suppression des dossiers médicaux
 * avec validation des données et vérification des dépendances.
 */
public class DossierMedicalService {

    private final DossierMedicalRepository dossierRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    public DossierMedicalService(DossierMedicalRepository dossierRepository,
                                 PatientRepository patientRepository,
                                 MedecinRepository medecinRepository) {
        this.dossierRepository = dossierRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
    }

    /**
     * Récupère tous les dossiers médicaux.
     */
    public List<DossierMedicale> findAll() {
        return dossierRepository.findAll();
    }

    /**
     * Récupère un dossier médical par son ID avec vérification d'existence.
     */
    public DossierMedicale findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID du dossier médical doit être valide");
        }
        DossierMedicale dossier = dossierRepository.findById(id);
        if (dossier == null) {
            throw new IllegalArgumentException("Dossier médical introuvable avec l'ID: " + id);
        }
        return dossier;
    }

    /**
     * Crée un nouveau dossier médical avec validation complète.
     */
    public DossierMedicale create(DossierMedicale dossier) {
        validateDossier(dossier);
        
        // Vérifier que le patient existe
        if (dossier.getPatient() == null || dossier.getPatient().getId_Patient() == null) {
            throw new IllegalArgumentException("Le patient est obligatoire pour créer un dossier médical");
        }
        Patient patient = patientRepository.findById(dossier.getPatient().getId_Patient());
        if (patient == null) {
            throw new IllegalArgumentException("Patient introuvable avec l'ID: " + dossier.getPatient().getId_Patient());
        }

        // Vérifier que le médecin existe
        if (dossier.getMedecin() == null || dossier.getMedecin().getIdUser() == null) {
            throw new IllegalArgumentException("Le médecin est obligatoire pour créer un dossier médical");
        }
        Medecin medecin = medecinRepository.findById(dossier.getMedecin().getIdUser());
        if (medecin == null) {
            throw new IllegalArgumentException("Médecin introuvable avec l'ID: " + dossier.getMedecin().getIdUser());
        }

        // Vérifier qu'un dossier n'existe pas déjà pour ce patient
        List<DossierMedicale> existingDossiers = findByPatientId(dossier.getPatient().getId_Patient());
        if (!existingDossiers.isEmpty()) {
            throw new IllegalStateException("Un dossier médical existe déjà pour ce patient");
        }

        // Définir la date de création si non fournie
        if (dossier.getDateDeCreation() == null) {
            dossier.setDateDeCreation(LocalDate.now());
        }

        dossierRepository.create(dossier);
        return dossier;
    }

    /**
     * Met à jour un dossier médical existant avec validation.
     */
    public DossierMedicale update(DossierMedicale dossier) {
        if (dossier == null || dossier.getIdDM() == null) {
            throw new IllegalArgumentException("Le dossier médical et son ID sont obligatoires");
        }

        // Vérifier que le dossier existe
        DossierMedicale existing = dossierRepository.findById(dossier.getIdDM());
        if (existing == null) {
            throw new IllegalArgumentException("Dossier médical introuvable avec l'ID: " + dossier.getIdDM());
        }

        validateDossier(dossier);
        
        // Vérifier le patient si modifié
        if (dossier.getPatient() != null && dossier.getPatient().getId_Patient() != null) {
            Patient patient = patientRepository.findById(dossier.getPatient().getId_Patient());
            if (patient == null) {
                throw new IllegalArgumentException("Patient introuvable avec l'ID: " + dossier.getPatient().getId_Patient());
            }
        }

        // Vérifier le médecin si modifié
        if (dossier.getMedecin() != null && dossier.getMedecin().getIdUser() != null) {
            Medecin medecin = medecinRepository.findById(dossier.getMedecin().getIdUser());
            if (medecin == null) {
                throw new IllegalArgumentException("Médecin introuvable avec l'ID: " + dossier.getMedecin().getIdUser());
            }
        }

        dossierRepository.update(dossier);
        return dossier;
    }

    /**
     * Supprime un dossier médical avec vérification d'existence.
     */
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID du dossier médical doit être valide");
        }
        
        DossierMedicale dossier = dossierRepository.findById(id);
        if (dossier == null) {
            throw new IllegalArgumentException("Dossier médical introuvable avec l'ID: " + id);
        }

        dossierRepository.deleteById(id);
    }

    /**
     * Supprime un dossier médical.
     */
    public void delete(DossierMedicale dossier) {
        if (dossier == null || dossier.getIdDM() == null) {
            throw new IllegalArgumentException("Le dossier médical est obligatoire");
        }
        delete(dossier.getIdDM());
    }

    /**
     * Trouve tous les dossiers médicaux d'un patient.
     */
    public List<DossierMedicale> findByPatientId(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new IllegalArgumentException("L'ID du patient doit être valide");
        }
        
        if (!patientRepository.existsById(patientId)) {
            throw new IllegalArgumentException("Patient introuvable avec l'ID: " + patientId);
        }

        return dossierRepository.findAll().stream()
                .filter(d -> d.getPatient() != null && patientId.equals(d.getPatient().getId_Patient()))
                .collect(Collectors.toList());
    }

    /**
     * Trouve tous les dossiers médicaux d'un médecin.
     */
    public List<DossierMedicale> findByMedecinId(Long medecinId) {
        if (medecinId == null || medecinId <= 0) {
            throw new IllegalArgumentException("L'ID du médecin doit être valide");
        }
        
        Medecin medecin = medecinRepository.findById(medecinId);
        if (medecin == null) {
            throw new IllegalArgumentException("Médecin introuvable avec l'ID: " + medecinId);
        }

        return dossierRepository.findAll().stream()
                .filter(d -> d.getMedecin() != null && medecinId.equals(d.getMedecin().getIdUser()))
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un dossier médical existe pour un patient.
     */
    public boolean existsByPatientId(Long patientId) {
        if (patientId == null || patientId <= 0) {
            return false;
        }
        return !findByPatientId(patientId).isEmpty();
    }

    /**
     * Compte le nombre de dossiers médicaux.
     */
    public long count() {
        return dossierRepository.findAll().size();
    }

    /**
     * Valide les données d'un dossier médical.
     */
    private void validateDossier(DossierMedicale dossier) {
        if (dossier == null) {
            throw new IllegalArgumentException("Le dossier médical ne peut pas être null");
        }
        
        if (dossier.getDateDeCreation() != null && dossier.getDateDeCreation().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de création ne peut pas être dans le futur");
        }
    }
}

