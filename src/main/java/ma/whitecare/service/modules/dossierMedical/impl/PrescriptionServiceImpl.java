package ma.whitecare.service.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.Prescription;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.mvc.dto.dossierMedical.PrescriptionDTO;
import ma.whitecare.mvc.dto.dossierMedical.CreatePrescriptionDTO;
import ma.whitecare.mvc.dto.dossierMedical.UpdatePrescriptionDTO;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.whitecare.repository.modules.medicament.api.MedicamentRepository;
import ma.whitecare.service.modules.dossierMedical.api.PrescriptionService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final MedicamentRepository medicamentRepository;
    private final DossierMedicalRepository dossierMedicalRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                   OrdonnanceRepository ordonnanceRepository,
                                   MedicamentRepository medicamentRepository,
                                   DossierMedicalRepository dossierMedicalRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.ordonnanceRepository = ordonnanceRepository;
        this.medicamentRepository = medicamentRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
    }

    @Override
    public PrescriptionDTO createPrescription(CreatePrescriptionDTO dto) {
        // Convertir DTO en Entity
        Prescription prescription = convertToEntity(dto);
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
        return convertToDTO(prescription);
    }

    @Override
    public PrescriptionDTO updatePrescription(Long prescriptionId, UpdatePrescriptionDTO dto) {
        Prescription existingPrescription = prescriptionRepository.findById(prescriptionId);
        if (existingPrescription == null) {
            throw new ValidationException("Prescription non trouvée avec l'ID: " + prescriptionId);
        }

        // Mettre à jour l'entité depuis le DTO
        updateEntityFromDTO(existingPrescription, dto);

        // Validation
        validatePrescription(existingPrescription);

        // Mettre à jour les champs d'audit
        existingPrescription.setModifiePar("system"); // À remplacer par l'utilisateur connecté

        prescriptionRepository.update(existingPrescription);
        return convertToDTO(existingPrescription);
    }

    @Override
    public void deletePrescription(Long prescriptionId) {
        if (!existsById(prescriptionId)) {
            throw new ValidationException("Prescription non trouvée avec l'ID: " + prescriptionId);
        }
        prescriptionRepository.deleteById(prescriptionId);
    }

    @Override
    public PrescriptionDTO getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId);
        if (prescription == null) {
            throw new ValidationException("Prescription non trouvée avec l'ID: " + prescriptionId);
        }
        return convertToDTO(prescription);
    }

    @Override
    public List<PrescriptionDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDTO> findByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) {
            throw new ValidationException("L'ID de l'ordonnance ne peut pas être null");
        }
        return prescriptionRepository.findByOrdonnanceId(ordonnanceId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDTO> findByMedicamentId(Long medicamentId) {
        if (medicamentId == null) {
            throw new ValidationException("L'ID du médicament ne peut pas être null");
        }
        return prescriptionRepository.findByMedicamentId(medicamentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDTO> findByDureeSuperieure(Integer dureeMin) {
        if (dureeMin == null || dureeMin <= 0) {
            throw new ValidationException("La durée minimale doit être positive");
        }
        return prescriptionRepository.findByDureeSuperieure(dureeMin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDTO> findByOrdonnanceAndMedicament(Long ordonnanceId, Long medicamentId) {
        if (ordonnanceId == null || medicamentId == null) {
            throw new ValidationException("L'ID de l'ordonnance et l'ID du médicament sont requis");
        }
        return prescriptionRepository.findByOrdonnanceAndMedicament(ordonnanceId, medicamentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
        if (ordonnanceId == null) {
            throw new ValidationException("L'ID de l'ordonnance ne peut pas être null");
        }
        return prescriptionRepository.countByOrdonnanceId(ordonnanceId);
    }

    @Override
    public long countByMedicamentId(Long medicamentId) {
        if (medicamentId == null) {
            throw new ValidationException("L'ID du médicament ne peut pas être null");
        }
        return prescriptionRepository.countByMedicamentId(medicamentId);
    }

    @Override
    public List<PrescriptionDTO> getPrescriptionsActives(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("L'ID du patient ne peut pas être null");
        }
        
        // Récupérer le dossier médical du patient
        List<ma.whitecare.entities.medical.DossierMedicale> dossiers = dossierMedicalRepository.findByPatientId(patientId);
        
        if (dossiers == null || dossiers.isEmpty()) {
            return List.of();
        }
        
        // Récupérer toutes les ordonnances de tous les dossiers du patient
        List<Ordonnance> toutesOrdonnances = new ArrayList<>();
        for (ma.whitecare.entities.medical.DossierMedicale dossier : dossiers) {
            // Récupérer toutes les ordonnances (on doit filtrer par dossier)
            List<Ordonnance> ordonnances = ordonnanceRepository.findAll().stream()
                    .filter(ord -> ord.getDossierMedicale() != null && 
                            ord.getDossierMedicale().getIdDM().equals(dossier.getIdDM()))
                    .collect(Collectors.toList());
            toutesOrdonnances.addAll(ordonnances);
        }
        
        // Récupérer toutes les prescriptions de ces ordonnances
        List<Prescription> toutesPrescriptions = new ArrayList<>();
        for (Ordonnance ordonnance : toutesOrdonnances) {
            toutesPrescriptions.addAll(prescriptionRepository.findByOrdonnanceId(ordonnance.getIdOrd()));
        }
        
        // Filtrer les prescriptions actives (date de fin dans le futur)
        LocalDate aujourdhui = LocalDate.now();
        return toutesPrescriptions.stream()
                .filter(prescription -> {
                    if (prescription.getOrdonnance() == null || prescription.getOrdonnance().getDate() == null) {
                        return false;
                    }
                    LocalDate dateDebut = prescription.getOrdonnance().getDate();
                    LocalDate dateFin = dateDebut.plusDays(prescription.getDuréeEnJours());
                    return dateFin.isAfter(aujourdhui) || dateFin.isEqual(aujourdhui);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDTO> getHistoriquePrescriptions(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("L'ID du patient ne peut pas être null");
        }
        
        // Récupérer le dossier médical du patient
        List<ma.whitecare.entities.medical.DossierMedicale> dossiers = dossierMedicalRepository.findByPatientId(patientId);
        
        if (dossiers == null || dossiers.isEmpty()) {
            return List.of();
        }
        
        // Récupérer toutes les ordonnances de tous les dossiers du patient
        List<Ordonnance> toutesOrdonnances = new ArrayList<>();
        for (ma.whitecare.entities.medical.DossierMedicale dossier : dossiers) {
            // Récupérer toutes les ordonnances (on doit filtrer par dossier)
            List<Ordonnance> ordonnances = ordonnanceRepository.findAll().stream()
                    .filter(ord -> ord.getDossierMedicale() != null && 
                            ord.getDossierMedicale().getIdDM().equals(dossier.getIdDM()))
                    .collect(Collectors.toList());
            toutesOrdonnances.addAll(ordonnances);
        }
        
        // Récupérer toutes les prescriptions de ces ordonnances, triées par date décroissante
        List<Prescription> toutesPrescriptions = new ArrayList<>();
        for (Ordonnance ordonnance : toutesOrdonnances) {
            toutesPrescriptions.addAll(prescriptionRepository.findByOrdonnanceId(ordonnance.getIdOrd()));
        }
        
        // Trier par date d'ordonnance décroissante
        return toutesPrescriptions.stream()
                .sorted((p1, p2) -> {
                    LocalDate date1 = p1.getOrdonnance() != null && p1.getOrdonnance().getDate() != null 
                            ? p1.getOrdonnance().getDate() : LocalDate.MIN;
                    LocalDate date2 = p2.getOrdonnance() != null && p2.getOrdonnance().getDate() != null 
                            ? p2.getOrdonnance().getDate() : LocalDate.MIN;
                    return date2.compareTo(date1);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== CONVERSION METHODS ==========
    private Prescription convertToEntity(CreatePrescriptionDTO dto) {
        // Charger l'ordonnance
        Ordonnance ordonnance = ordonnanceRepository.findById(dto.getOrdonnanceId());
        if (ordonnance == null) {
            throw new ValidationException("L'ordonnance avec l'ID " + dto.getOrdonnanceId() + " n'existe pas");
        }

        // Charger le médicament
        Medicament medicament = medicamentRepository.findById(dto.getMedicamentId());
        if (medicament == null) {
            throw new ValidationException("Le médicament avec l'ID " + dto.getMedicamentId() + " n'existe pas");
        }

        // Construire l'entité
        return Prescription.builder()
                .ordonnance(ordonnance)
                .medicament(medicament)
                .quantité(dto.getQuantité())
                .fréquence(dto.getFréquence())
                .duréeEnJours(dto.getDuréeEnJours())
                .build();
    }

    private void updateEntityFromDTO(Prescription entity, UpdatePrescriptionDTO dto) {
        // Mettre à jour la quantité si fournie
        if (dto.getQuantité() != null && dto.getQuantité() > 0) {
            entity.setQuantité(dto.getQuantité());
        }

        // Mettre à jour la durée si fournie
        if (dto.getDuréeEnJours() != null && dto.getDuréeEnJours() > 0) {
            entity.setDuréeEnJours(dto.getDuréeEnJours());
        }

        // Mettre à jour la fréquence si fournie
        if (dto.getFréquence() != null && !dto.getFréquence().trim().isEmpty()) {
            entity.setFréquence(dto.getFréquence());
        }

        // Mettre à jour le médicament si fourni
        if (dto.getMedicamentId() != null) {
            Medicament medicament = medicamentRepository.findById(dto.getMedicamentId());
            if (medicament == null) {
                throw new ValidationException("Le médicament avec l'ID " + dto.getMedicamentId() + " n'existe pas");
            }
            entity.setMedicament(medicament);
        }

        // Mettre à jour l'ordonnance si fournie
        if (dto.getOrdonnanceId() != null) {
            Ordonnance ordonnance = ordonnanceRepository.findById(dto.getOrdonnanceId());
            if (ordonnance == null) {
                throw new ValidationException("L'ordonnance avec l'ID " + dto.getOrdonnanceId() + " n'existe pas");
            }
            entity.setOrdonnance(ordonnance);
        }
    }

    private PrescriptionDTO convertToDTO(Prescription entity) {
        return PrescriptionDTO.builder()
                .idPr(entity.getIdPr())
                .quantité(entity.getQuantité())
                .fréquence(entity.getFréquence())
                .duréeEnJours(entity.getDuréeEnJours())
                .medicamentId(entity.getMedicament() != null ? entity.getMedicament().getIdMct() : null)
                .ordonnanceId(entity.getOrdonnance() != null ? entity.getOrdonnance().getIdOrd() : null)
                .dateCreation(entity.getDateCreation())
                .dateDerniereModification(entity.getDateDerniereModification())
                .createdBy(entity.getCreePar())
                .updatedBy(entity.getModifiePar())
                .build();
    }

    // ========== VALIDATION PRIVÉE ==========
    private void validatePrescription(Prescription prescription) {
        if (prescription == null) {
            throw new ValidationException("La prescription ne peut pas être null");
        }
    }
}

