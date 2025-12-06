package ma.whitecare.service.modules.dossierMedical;

import ma.whitecare.entities.medical.Prescription;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.whitecare.repository.modules.medicament.api.MedicamentRepository;
import ma.whitecare.repository.modules.ordonnance.api.OrdonnanceRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des prescriptions médicales.
 * Gère la création, modification et consultation des prescriptions
 * avec validation des quantités, fréquences et durées de traitement.
 */
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicamentRepository medicamentRepository;
    private final OrdonnanceRepository ordonnanceRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               MedicamentRepository medicamentRepository,
                               OrdonnanceRepository ordonnanceRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicamentRepository = medicamentRepository;
        this.ordonnanceRepository = ordonnanceRepository;
    }

    /**
     * Récupère toutes les prescriptions.
     */
    public List<Prescription> findAll() {
        return prescriptionRepository.findAll();
    }

    /**
     * Récupère une prescription par son ID avec vérification d'existence.
     */
    public Prescription findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la prescription doit être valide");
        }
        Prescription prescription = prescriptionRepository.findById(id);
        if (prescription == null) {
            throw new IllegalArgumentException("Prescription introuvable avec l'ID: " + id);
        }
        return prescription;
    }

    /**
     * Crée une nouvelle prescription avec validation complète.
     */
    public Prescription create(Prescription prescription) {
        validatePrescription(prescription);
        
        // Vérifier que l'ordonnance existe
        if (prescription.getOrdonnance() == null || prescription.getOrdonnance().getIdOrd() == null) {
            throw new IllegalArgumentException("L'ordonnance est obligatoire pour créer une prescription");
        }
        Ordonnance ordonnance = ordonnanceRepository.findById(prescription.getOrdonnance().getIdOrd());
        if (ordonnance == null) {
            throw new IllegalArgumentException("Ordonnance introuvable avec l'ID: " + prescription.getOrdonnance().getIdOrd());
        }

        // Vérifier que le médicament existe
        if (prescription.getMedicament() == null || prescription.getMedicament().getIdMct() == null) {
            throw new IllegalArgumentException("Le médicament est obligatoire pour créer une prescription");
        }
        Medicament medicament = medicamentRepository.findById(prescription.getMedicament().getIdMct());
        if (medicament == null) {
            throw new IllegalArgumentException("Médicament introuvable avec l'ID: " + prescription.getMedicament().getIdMct());
        }

        // Valider la quantité
        if (prescription.getQuantité() <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }

        // Valider la durée en jours
        if (prescription.getDuréeEnJours() <= 0) {
            throw new IllegalArgumentException("La durée en jours doit être positive");
        }

        // Valider la fréquence
        if (prescription.getFréquence() == null || prescription.getFréquence().trim().isEmpty()) {
            throw new IllegalArgumentException("La fréquence est obligatoire");
        }

        prescriptionRepository.create(prescription);
        return prescription;
    }

    /**
     * Met à jour une prescription existante avec validation.
     */
    public Prescription update(Prescription prescription) {
        if (prescription == null || prescription.getIdPr() == null) {
            throw new IllegalArgumentException("La prescription et son ID sont obligatoires");
        }

        // Vérifier que la prescription existe
        Prescription existing = prescriptionRepository.findById(prescription.getIdPr());
        if (existing == null) {
            throw new IllegalArgumentException("Prescription introuvable avec l'ID: " + prescription.getIdPr());
        }

        validatePrescription(prescription);
        
        // Vérifier l'ordonnance si modifiée
        if (prescription.getOrdonnance() != null && prescription.getOrdonnance().getIdOrd() != null) {
            Ordonnance ordonnance = ordonnanceRepository.findById(prescription.getOrdonnance().getIdOrd());
            if (ordonnance == null) {
                throw new IllegalArgumentException("Ordonnance introuvable avec l'ID: " + prescription.getOrdonnance().getIdOrd());
            }
        }

        // Vérifier le médicament si modifié
        if (prescription.getMedicament() != null && prescription.getMedicament().getIdMct() != null) {
            Medicament medicament = medicamentRepository.findById(prescription.getMedicament().getIdMct());
            if (medicament == null) {
                throw new IllegalArgumentException("Médicament introuvable avec l'ID: " + prescription.getMedicament().getIdMct());
            }
        }

        prescriptionRepository.update(prescription);
        return prescription;
    }

    /**
     * Supprime une prescription avec vérification d'existence.
     */
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la prescription doit être valide");
        }
        
        Prescription prescription = prescriptionRepository.findById(id);
        if (prescription == null) {
            throw new IllegalArgumentException("Prescription introuvable avec l'ID: " + id);
        }

        prescriptionRepository.deleteById(id);
    }

    /**
     * Supprime une prescription.
     */
    public void delete(Prescription prescription) {
        if (prescription == null || prescription.getIdPr() == null) {
            throw new IllegalArgumentException("La prescription est obligatoire");
        }
        delete(prescription.getIdPr());
    }

    /**
     * Trouve toutes les prescriptions d'une ordonnance.
     */
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null || ordonnanceId <= 0) {
            throw new IllegalArgumentException("L'ID de l'ordonnance doit être valide");
        }
        
        Ordonnance ordonnance = ordonnanceRepository.findById(ordonnanceId);
        if (ordonnance == null) {
            throw new IllegalArgumentException("Ordonnance introuvable avec l'ID: " + ordonnanceId);
        }

        return prescriptionRepository.findAll().stream()
                .filter(p -> p.getOrdonnance() != null && ordonnanceId.equals(p.getOrdonnance().getIdOrd()))
                .collect(Collectors.toList());
    }

    /**
     * Trouve toutes les prescriptions d'un médicament.
     */
    public List<Prescription> findByMedicamentId(Long medicamentId) {
        if (medicamentId == null || medicamentId <= 0) {
            throw new IllegalArgumentException("L'ID du médicament doit être valide");
        }
        
        Medicament medicament = medicamentRepository.findById(medicamentId);
        if (medicament == null) {
            throw new IllegalArgumentException("Médicament introuvable avec l'ID: " + medicamentId);
        }

        return prescriptionRepository.findAll().stream()
                .filter(p -> p.getMedicament() != null && medicamentId.equals(p.getMedicament().getIdMct()))
                .collect(Collectors.toList());
    }

    /**
     * Trouve toutes les prescriptions avec une durée supérieure à un nombre de jours.
     */
    public List<Prescription> findByDureeSuperieure(int jours) {
        if (jours < 0) {
            throw new IllegalArgumentException("Le nombre de jours doit être positif");
        }

        return prescriptionRepository.findAll().stream()
                .filter(p -> p.getDuréeEnJours() > jours)
                .collect(Collectors.toList());
    }

    /**
     * Calcule le coût total d'une ordonnance (somme des prix des médicaments × quantités).
     */
    public Double calculateCoutTotalOrdonnance(Long ordonnanceId) {
        List<Prescription> prescriptions = findByOrdonnanceId(ordonnanceId);
        
        return prescriptions.stream()
                .filter(p -> p.getMedicament() != null && p.getMedicament().getPrixUnitaire() != null)
                .mapToDouble(p -> {
                    double prixUnitaire = p.getMedicament().getPrixUnitaire();
                    int quantite = p.getQuantité();
                    return prixUnitaire * quantite;
                })
                .sum();
    }

    /**
     * Compte le nombre de prescriptions.
     */
    public long count() {
        return prescriptionRepository.findAll().size();
    }

    /**
     * Compte le nombre de prescriptions par médicament.
     */
    public long countByMedicamentId(Long medicamentId) {
        return findByMedicamentId(medicamentId).size();
    }

    /**
     * Valide les données d'une prescription.
     */
    private void validatePrescription(Prescription prescription) {
        if (prescription == null) {
            throw new IllegalArgumentException("La prescription ne peut pas être null");
        }
        
        if (prescription.getQuantité() <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        
        if (prescription.getDuréeEnJours() <= 0) {
            throw new IllegalArgumentException("La durée en jours doit être positive");
        }
        
        if (prescription.getFréquence() == null || prescription.getFréquence().trim().isEmpty()) {
            throw new IllegalArgumentException("La fréquence est obligatoire");
        }
    }
}

