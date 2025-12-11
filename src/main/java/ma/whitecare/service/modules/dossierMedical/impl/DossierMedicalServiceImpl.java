package ma.whitecare.service.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.service.modules.dossierMedical.api.DossierMedicalService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    public DossierMedicalServiceImpl(DossierMedicalRepository dossierMedicalRepository,
                                     PatientRepository patientRepository,
                                     MedecinRepository medecinRepository) {
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
    }

    @Override
    public DossierMedicale createDossierMedical(DossierMedicale dossierMedical) {
        // Validation
        validateDossierMedical(dossierMedical);

        // Vérifier que le patient existe
        if (dossierMedical.getPatient() == null || dossierMedical.getPatient().getId_Patient() == null) {
            throw new ValidationException("Le patient est requis pour créer un dossier médical");
        }
        if (patientRepository.findById(dossierMedical.getPatient().getId_Patient()) == null) {
            throw new ValidationException("Le patient avec l'ID " + dossierMedical.getPatient().getId_Patient() + " n'existe pas");
        }

        // Vérifier que le médecin existe
        if (dossierMedical.getMedecin() == null || dossierMedical.getMedecin().getIdUser() == null) {
            throw new ValidationException("Le médecin est requis pour créer un dossier médical");
        }
        if (medecinRepository.findById(dossierMedical.getMedecin().getIdUser()) == null) {
            throw new ValidationException("Le médecin avec l'ID " + dossierMedical.getMedecin().getIdUser() + " n'existe pas");
        }

        // Vérifier l'unicité : un patient ne peut avoir qu'un seul dossier médical
        if (existsByPatientId(dossierMedical.getPatient().getId_Patient())) {
            throw new ValidationException("Un dossier médical existe déjà pour ce patient");
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
        return dossierMedical;
    }

    @Override
    public DossierMedicale updateDossierMedical(Long dossierId, DossierMedicale dossierMedical) {
        DossierMedicale existingDossier = getDossierMedicalById(dossierId);

        // Validation
        validateDossierMedical(dossierMedical);

        // Vérifier que le patient existe si modifié
        if (dossierMedical.getPatient() != null && dossierMedical.getPatient().getId_Patient() != null) {
            if (patientRepository.findById(dossierMedical.getPatient().getId_Patient()) == null) {
                throw new ValidationException("Le patient avec l'ID " + dossierMedical.getPatient().getId_Patient() + " n'existe pas");
            }
        }

        // Vérifier que le médecin existe si modifié
        if (dossierMedical.getMedecin() != null && dossierMedical.getMedecin().getIdUser() != null) {
            if (medecinRepository.findById(dossierMedical.getMedecin().getIdUser()) == null) {
                throw new ValidationException("Le médecin avec l'ID " + dossierMedical.getMedecin().getIdUser() + " n'existe pas");
            }
        }

        // Mettre à jour les champs
        if (dossierMedical.getDateDeCreation() != null) {
            existingDossier.setDateDeCreation(dossierMedical.getDateDeCreation());
        }
        if (dossierMedical.getPatient() != null) {
            existingDossier.setPatient(dossierMedical.getPatient());
        }
        if (dossierMedical.getMedecin() != null) {
            existingDossier.setMedecin(dossierMedical.getMedecin());
        }

        // Mettre à jour les champs d'audit
        existingDossier.setModifiePar("system"); // À remplacer par l'utilisateur connecté

        dossierMedicalRepository.update(existingDossier);
        return existingDossier;
    }

    @Override
    public void deleteDossierMedical(Long dossierId) {
        if (!existsById(dossierId)) {
            throw new ValidationException("Dossier médical non trouvé avec l'ID: " + dossierId);
        }
        dossierMedicalRepository.deleteById(dossierId);
    }

    @Override
    public DossierMedicale getDossierMedicalById(Long dossierId) {
        DossierMedicale dossier = dossierMedicalRepository.findById(dossierId);
        if (dossier == null) {
            throw new ValidationException("Dossier médical non trouvé avec l'ID: " + dossierId);
        }
        return dossier;
    }

    @Override
    public List<DossierMedicale> getAllDossiersMedicaux() {
        return dossierMedicalRepository.findAll();
    }

    @Override
    public List<DossierMedicale> findByPatientId(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("L'ID du patient ne peut pas être null");
        }
        List<DossierMedicale> allDossiers = getAllDossiersMedicaux();
        return allDossiers.stream()
                .filter(d -> d.getPatient() != null && patientId.equals(d.getPatient().getId_Patient()))
                .toList();
    }

    @Override
    public List<DossierMedicale> findByMedecinId(Long medecinId) {
        if (medecinId == null) {
            throw new ValidationException("L'ID du médecin ne peut pas être null");
        }
        List<DossierMedicale> allDossiers = getAllDossiersMedicaux();
        return allDossiers.stream()
                .filter(d -> d.getMedecin() != null && medecinId.equals(d.getMedecin().getIdUser()))
                .toList();
    }

    @Override
    public List<DossierMedicale> findByDateCreation(LocalDate date) {
        if (date == null) {
            throw new ValidationException("La date ne peut pas être null");
        }
        List<DossierMedicale> allDossiers = getAllDossiersMedicaux();
        return allDossiers.stream()
                .filter(d -> date.equals(d.getDateDeCreation()))
                .toList();
    }

    @Override
    public List<DossierMedicale> findByDateCreationBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Les dates de début et de fin sont requises");
        }
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("La date de début doit être antérieure à la date de fin");
        }
        List<DossierMedicale> allDossiers = getAllDossiersMedicaux();
        return allDossiers.stream()
                .filter(d -> d.getDateDeCreation() != null &&
                        !d.getDateDeCreation().isBefore(startDate) &&
                        !d.getDateDeCreation().isAfter(endDate))
                .toList();
    }

    @Override
    public boolean existsByPatientId(Long patientId) {
        if (patientId == null) {
            return false;
        }
        return !findByPatientId(patientId).isEmpty();
    }

    @Override
    public boolean existsById(Long dossierId) {
        if (dossierId == null) {
            return false;
        }
        return dossierMedicalRepository.findById(dossierId) != null;
    }

    @Override
    public long countAllDossiers() {
        return getAllDossiersMedicaux().size();
    }

    @Override
    public long countByPatientId(Long patientId) {
        return findByPatientId(patientId).size();
    }

    @Override
    public long countByMedecinId(Long medecinId) {
        return findByMedecinId(medecinId).size();
    }

    // ========== VALIDATION PRIVÉE ==========
    private void validateDossierMedical(DossierMedicale dossierMedical) {
        if (dossierMedical == null) {
            throw new ValidationException("Le dossier médical ne peut pas être null");
        }
        // La validation des dates est gérée dans les méthodes spécifiques
    }
}

