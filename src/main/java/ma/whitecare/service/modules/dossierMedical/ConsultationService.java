package ma.whitecare.service.modules.dossierMedical;

import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des consultations médicales.
 * Gère la création, modification, consultation et suivi des consultations
 * avec validation des statuts et vérification des dépendances.
 */
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierRepository;

    public ConsultationService(ConsultationRepository consultationRepository,
                               DossierMedicalRepository dossierRepository) {
        this.consultationRepository = consultationRepository;
        this.dossierRepository = dossierRepository;
    }

    /**
     * Récupère toutes les consultations.
     */
    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }

    /**
     * Récupère une consultation par son ID avec vérification d'existence.
     */
    public Consultation findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la consultation doit être valide");
        }
        Consultation consultation = consultationRepository.findById(id);
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation introuvable avec l'ID: " + id);
        }
        return consultation;
    }

    /**
     * Crée une nouvelle consultation avec validation complète.
     */
    public Consultation create(Consultation consultation) {
        validateConsultation(consultation);
        
        // Vérifier que le dossier médical existe
        if (consultation.getDossierMedicale() == null || consultation.getDossierMedicale().getIdDM() == null) {
            throw new IllegalArgumentException("Le dossier médical est obligatoire pour créer une consultation");
        }
        DossierMedicale dossier = dossierRepository.findById(consultation.getDossierMedicale().getIdDM());
        if (dossier == null) {
            throw new IllegalArgumentException("Dossier médical introuvable avec l'ID: " + consultation.getDossierMedicale().getIdDM());
        }

        // Définir la date si non fournie
        if (consultation.getDate() == null) {
            consultation.setDate(LocalDate.now());
        }

        // Définir le statut par défaut si non fourni
        if (consultation.getStatut() == null) {
            consultation.setStatut(StatutConsultation.EN_ATTENTE);
        }

        consultationRepository.create(consultation);
        return consultation;
    }

    /**
     * Met à jour une consultation existante avec validation.
     */
    public Consultation update(Consultation consultation) {
        if (consultation == null || consultation.getIdConsultation() == null) {
            throw new IllegalArgumentException("La consultation et son ID sont obligatoires");
        }

        // Vérifier que la consultation existe
        Consultation existing = consultationRepository.findById(consultation.getIdConsultation());
        if (existing == null) {
            throw new IllegalArgumentException("Consultation introuvable avec l'ID: " + consultation.getIdConsultation());
        }

        validateConsultation(consultation);
        
        // Vérifier le dossier médical si modifié
        if (consultation.getDossierMedicale() != null && consultation.getDossierMedicale().getIdDM() != null) {
            DossierMedicale dossier = dossierRepository.findById(consultation.getDossierMedicale().getIdDM());
            if (dossier == null) {
                throw new IllegalArgumentException("Dossier médical introuvable avec l'ID: " + consultation.getDossierMedicale().getIdDM());
            }
        }

        consultationRepository.update(consultation);
        return consultation;
    }

    /**
     * Supprime une consultation avec vérification d'existence.
     */
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la consultation doit être valide");
        }
        
        Consultation consultation = consultationRepository.findById(id);
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation introuvable avec l'ID: " + id);
        }

        consultationRepository.deleteById(id);
    }

    /**
     * Supprime une consultation.
     */
    public void delete(Consultation consultation) {
        if (consultation == null || consultation.getIdConsultation() == null) {
            throw new IllegalArgumentException("La consultation est obligatoire");
        }
        delete(consultation.getIdConsultation());
    }

    /**
     * Trouve toutes les consultations d'un dossier médical.
     */
    public List<Consultation> findByDossierMedicalId(Long dossierId) {
        if (dossierId == null || dossierId <= 0) {
            throw new IllegalArgumentException("L'ID du dossier médical doit être valide");
        }
        
        DossierMedicale dossier = dossierRepository.findById(dossierId);
        if (dossier == null) {
            throw new IllegalArgumentException("Dossier médical introuvable avec l'ID: " + dossierId);
        }

        return consultationRepository.findAll().stream()
                .filter(c -> c.getDossierMedicale() != null && dossierId.equals(c.getDossierMedicale().getIdDM()))
                .collect(Collectors.toList());
    }

    /**
     * Trouve toutes les consultations par statut.
     */
    public List<Consultation> findByStatut(StatutConsultation statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être null");
        }

        return consultationRepository.findAll().stream()
                .filter(c -> statut.equals(c.getStatut()))
                .collect(Collectors.toList());
    }

    /**
     * Trouve toutes les consultations entre deux dates.
     */
    public List<Consultation> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        return consultationRepository.findAll().stream()
                .filter(c -> c.getDate() != null 
                        && !c.getDate().isBefore(startDate) 
                        && !c.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    /**
     * Trouve toutes les consultations d'urgence.
     */
    public List<Consultation> findUrgentes() {
        return findByStatut(StatutConsultation.URGENCE);
    }

    /**
     * Trouve toutes les consultations en cours.
     */
    public List<Consultation> findEnCours() {
        return findByStatut(StatutConsultation.EN_COURS);
    }

    /**
     * Change le statut d'une consultation.
     */
    public Consultation changeStatut(Long consultationId, StatutConsultation nouveauStatut) {
        if (nouveauStatut == null) {
            throw new IllegalArgumentException("Le nouveau statut ne peut pas être null");
        }

        Consultation consultation = findById(consultationId);
        
        // Validation des transitions de statut
        StatutConsultation ancienStatut = consultation.getStatut();
        if (ancienStatut == StatutConsultation.TERMINEE && nouveauStatut != StatutConsultation.TERMINEE) {
            throw new IllegalStateException("Une consultation terminée ne peut pas changer de statut");
        }
        if (ancienStatut == StatutConsultation.ANNULEE && nouveauStatut != StatutConsultation.ANNULEE) {
            throw new IllegalStateException("Une consultation annulée ne peut pas changer de statut");
        }

        consultation.setStatut(nouveauStatut);
        return update(consultation);
    }

    /**
     * Marque une consultation comme terminée.
     */
    public Consultation terminer(Long consultationId, String observationMedecin) {
        Consultation consultation = findById(consultationId);
        
        if (consultation.getStatut() == StatutConsultation.ANNULEE) {
            throw new IllegalStateException("Une consultation annulée ne peut pas être terminée");
        }

        consultation.setStatut(StatutConsultation.TERMINEE);
        if (observationMedecin != null && !observationMedecin.trim().isEmpty()) {
            consultation.setObservationMedecin(observationMedecin);
        }
        
        return update(consultation);
    }

    /**
     * Compte le nombre de consultations.
     */
    public long count() {
        return consultationRepository.findAll().size();
    }

    /**
     * Compte le nombre de consultations par statut.
     */
    public long countByStatut(StatutConsultation statut) {
        return findByStatut(statut).size();
    }

    /**
     * Valide les données d'une consultation.
     */
    private void validateConsultation(Consultation consultation) {
        if (consultation == null) {
            throw new IllegalArgumentException("La consultation ne peut pas être null");
        }
        
        if (consultation.getDate() != null && consultation.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de consultation ne peut pas être dans le futur");
        }
    }
}

