package ma.whitecare.service.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.Prescription;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.whitecare.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.whitecare.repository.modules.medicament.api.MedicamentRepository;
import ma.whitecare.service.modules.dossierMedical.api.PrescriptionService;

import javax.validation.ValidationException;
import java.util.List;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final MedicamentRepository medicamentRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                   OrdonnanceRepository ordonnanceRepository,
                                   MedicamentRepository medicamentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.ordonnanceRepository = ordonnanceRepository;
        this.medicamentRepository = medicamentRepository;
    }

    @Override
    public Prescription createPrescription(Prescription prescription) {
        // Validation
        validatePrescription(prescription);

        // Vérifier que l'ordonnance existe
        if (prescription.getOrdonnance() == null || prescription.getOrdonnance().getIdOrd() == null) {
            throw new ValidationException("L'ordonnance est requise pour créer une prescription");
        }
        Ordonnance ordonnance = ordonnanceRepository.findById(prescription.getOrdonnance().getIdOrd());
        if (ordonnance == null) {
            throw new ValidationException("L'ordonnance avec l'ID " + prescription.getOrdonnance().getIdOrd() + " n'existe pas");
        }

        // Vérifier que le médicament existe
        if (prescription.getMedicament() == null || prescription.getMedicament().getIdMct() == null) {
            throw new ValidationException("Le médicament est requis pour créer une prescription");
        }
        Medicament medicament = medicamentRepository.findById(prescription.getMedicament().getIdMct());
        if (medicament == null) {
            throw new ValidationException("Le médicament avec l'ID " + prescription.getMedicament().getIdMct() + " n'existe pas");
        }

        // Valider la quantité
        if (prescription.getQuantité() <= 0) {
            throw new ValidationException("La quantité doit être positive");
        }

        // Valider la durée
        if (prescription.getDuréeEnJours() <= 0) {
            throw new ValidationException("La durée en jours doit être positive");
        }

        // Valider la fréquence
        if (prescription.getFréquence() == null || prescription.getFréquence().trim().isEmpty()) {
            throw new ValidationException("La fréquence est requise");
        }

        // Set audit fields
        prescription.setCreePar("system"); // À remplacer par l'utilisateur connecté
        prescription.setModifiePar("system");

        // Créer la prescription
        prescriptionRepository.create(prescription);
        return prescription;
    }

    @Override
    public Prescription updatePrescription(Long prescriptionId, Prescription prescription) {
        Prescription existingPrescription = getPrescriptionById(prescriptionId);

        // Validation
        validatePrescription(prescription);

        // Vérifier que l'ordonnance existe si modifiée
        if (prescription.getOrdonnance() != null && prescription.getOrdonnance().getIdOrd() != null) {
            Ordonnance ordonnance = ordonnanceRepository.findById(prescription.getOrdonnance().getIdOrd());
            if (ordonnance == null) {
                throw new ValidationException("L'ordonnance avec l'ID " + prescription.getOrdonnance().getIdOrd() + " n'existe pas");
            }
        }

        // Vérifier que le médicament existe si modifié
        if (prescription.getMedicament() != null && prescription.getMedicament().getIdMct() != null) {
            Medicament medicament = medicamentRepository.findById(prescription.getMedicament().getIdMct());
            if (medicament == null) {
                throw new ValidationException("Le médicament avec l'ID " + prescription.getMedicament().getIdMct() + " n'existe pas");
            }
        }

        // Mettre à jour la quantité si fournie
        if (prescription.getQuantité() > 0) {
            existingPrescription.setQuantité(prescription.getQuantité());
        }

        // Mettre à jour la durée si fournie
        if (prescription.getDuréeEnJours() > 0) {
            existingPrescription.setDuréeEnJours(prescription.getDuréeEnJours());
        }

        // Valider la fréquence si modifiée
        if (prescription.getFréquence() != null && !prescription.getFréquence().trim().isEmpty()) {
            existingPrescription.setFréquence(prescription.getFréquence());
        }

        // Mettre à jour les champs
        if (prescription.getMedicament() != null) {
            existingPrescription.setMedicament(prescription.getMedicament());
        }
        if (prescription.getOrdonnance() != null) {
            existingPrescription.setOrdonnance(prescription.getOrdonnance());
        }

        // Mettre à jour les champs d'audit
        existingPrescription.setModifiePar("system"); // À remplacer par l'utilisateur connecté

        prescriptionRepository.update(existingPrescription);
        return existingPrescription;
    }

    @Override
    public void deletePrescription(Long prescriptionId) {
        if (!existsById(prescriptionId)) {
            throw new ValidationException("Prescription non trouvée avec l'ID: " + prescriptionId);
        }
        prescriptionRepository.deleteById(prescriptionId);
    }

    @Override
    public Prescription getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId);
        if (prescription == null) {
            throw new ValidationException("Prescription non trouvée avec l'ID: " + prescriptionId);
        }
        return prescription;
    }

    @Override
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) {
            throw new ValidationException("L'ID de l'ordonnance ne peut pas être null");
        }
        return prescriptionRepository.findByOrdonnanceId(ordonnanceId);
    }

    @Override
    public List<Prescription> findByMedicamentId(Long medicamentId) {
        if (medicamentId == null) {
            throw new ValidationException("L'ID du médicament ne peut pas être null");
        }
        return prescriptionRepository.findByMedicamentId(medicamentId);
    }

    @Override
    public List<Prescription> findByDureeSuperieure(Integer dureeMin) {
        if (dureeMin == null || dureeMin <= 0) {
            throw new ValidationException("La durée minimale doit être positive");
        }
        return prescriptionRepository.findByDureeSuperieure(dureeMin);
    }

    @Override
    public List<Prescription> findByOrdonnanceAndMedicament(Long ordonnanceId, Long medicamentId) {
        if (ordonnanceId == null || medicamentId == null) {
            throw new ValidationException("L'ID de l'ordonnance et l'ID du médicament sont requis");
        }
        return prescriptionRepository.findByOrdonnanceAndMedicament(ordonnanceId, medicamentId);
    }

    @Override
    public Double calculateCoutTotalOrdonnance(Long ordonnanceId) {
        if (ordonnanceId == null) {
            throw new ValidationException("L'ID de l'ordonnance ne peut pas être null");
        }
        return prescriptionRepository.calculateCoutTotalOrdonnance(ordonnanceId);
    }

    @Override
    public boolean existsById(Long prescriptionId) {
        if (prescriptionId == null) {
            return false;
        }
        return prescriptionRepository.existsById(prescriptionId);
    }

    @Override
    public long countAllPrescriptions() {
        return prescriptionRepository.countAll();
    }

    @Override
    public long countByOrdonnanceId(Long ordonnanceId) {
        return prescriptionRepository.countByOrdonnanceId(ordonnanceId);
    }

    @Override
    public long countByMedicamentId(Long medicamentId) {
        return prescriptionRepository.countByMedicamentId(medicamentId);
    }

    // ========== VALIDATION PRIVÉE ==========
    private void validatePrescription(Prescription prescription) {
        if (prescription == null) {
            throw new ValidationException("La prescription ne peut pas être null");
        }
    }
}

