package ma.whitecare.service.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.service.modules.dossierMedical.api.ConsultationService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierMedicalRepository;

    public ConsultationServiceImpl(ConsultationRepository consultationRepository,
                                    DossierMedicalRepository dossierMedicalRepository) {
        this.consultationRepository = consultationRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
    }

    @Override
    public Consultation createConsultation(Consultation consultation) {
        // Validation
        validateConsultation(consultation);

        // Vérifier que le dossier médical existe
        if (consultation.getDossierMedicale() == null || consultation.getDossierMedicale().getIdDM() == null) {
            throw new ValidationException("Le dossier médical est requis pour créer une consultation");
        }
        DossierMedicale dossier = dossierMedicalRepository.findById(consultation.getDossierMedicale().getIdDM());
        if (dossier == null) {
            throw new ValidationException("Le dossier médical avec l'ID " + consultation.getDossierMedicale().getIdDM() + " n'existe pas");
        }

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
        return consultation;
    }

    @Override
    public Consultation updateConsultation(Long consultationId, Consultation consultation) {
        Consultation existingConsultation = getConsultationById(consultationId);

        // Validation
        validateConsultation(consultation);

        // Vérifier que le dossier médical existe si modifié
        if (consultation.getDossierMedicale() != null && consultation.getDossierMedicale().getIdDM() != null) {
            DossierMedicale dossier = dossierMedicalRepository.findById(consultation.getDossierMedicale().getIdDM());
            if (dossier == null) {
                throw new ValidationException("Le dossier médical avec l'ID " + consultation.getDossierMedicale().getIdDM() + " n'existe pas");
            }
        }

        // Mettre à jour les champs
        if (consultation.getDate() != null) {
            existingConsultation.setDate(consultation.getDate());
        }
        if (consultation.getStatut() != null) {
            existingConsultation.setStatut(consultation.getStatut());
        }
        if (consultation.getObservationMedecin() != null) {
            existingConsultation.setObservationMedecin(consultation.getObservationMedecin());
        }
        if (consultation.getDossierMedicale() != null) {
            existingConsultation.setDossierMedicale(consultation.getDossierMedicale());
        }

        // Mettre à jour les champs d'audit
        existingConsultation.setModifiePar("system"); // À remplacer par l'utilisateur connecté

        consultationRepository.update(existingConsultation);
        return existingConsultation;
    }

    @Override
    public void deleteConsultation(Long consultationId) {
        if (!existsById(consultationId)) {
            throw new ValidationException("Consultation non trouvée avec l'ID: " + consultationId);
        }
        consultationRepository.deleteById(consultationId);
    }

    @Override
    public Consultation getConsultationById(Long consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId);
        if (consultation == null) {
            throw new ValidationException("Consultation non trouvée avec l'ID: " + consultationId);
        }
        return consultation;
    }

    @Override
    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAll();
    }

    @Override
    public List<Consultation> findByDossierMedicalId(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical ne peut pas être null");
        }
        List<Consultation> allConsultations = getAllConsultations();
        return allConsultations.stream()
                .filter(c -> c.getDossierMedicale() != null && dossierId.equals(c.getDossierMedicale().getIdDM()))
                .toList();
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        if (statut == null) {
            throw new ValidationException("Le statut ne peut pas être null");
        }
        List<Consultation> allConsultations = getAllConsultations();
        return allConsultations.stream()
                .filter(c -> statut.equals(c.getStatut()))
                .toList();
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        if (date == null) {
            throw new ValidationException("La date ne peut pas être null");
        }
        List<Consultation> allConsultations = getAllConsultations();
        return allConsultations.stream()
                .filter(c -> date.equals(c.getDate()))
                .toList();
    }

    @Override
    public List<Consultation> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Les dates de début et de fin sont requises");
        }
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("La date de début doit être antérieure à la date de fin");
        }
        List<Consultation> allConsultations = getAllConsultations();
        return allConsultations.stream()
                .filter(c -> c.getDate() != null &&
                        !c.getDate().isBefore(startDate) &&
                        !c.getDate().isAfter(endDate))
                .toList();
    }

    @Override
    public List<Consultation> findByDossierAndDate(Long dossierId, LocalDate date) {
        if (dossierId == null || date == null) {
            throw new ValidationException("L'ID du dossier médical et la date sont requis");
        }
        List<Consultation> allConsultations = getAllConsultations();
        return allConsultations.stream()
                .filter(c -> c.getDossierMedicale() != null &&
                        dossierId.equals(c.getDossierMedicale().getIdDM()) &&
                        date.equals(c.getDate()))
                .toList();
    }

    @Override
    public void changeStatut(Long consultationId, StatutConsultation nouveauStatut) {
        if (nouveauStatut == null) {
            throw new ValidationException("Le nouveau statut ne peut pas être null");
        }
        Consultation consultation = getConsultationById(consultationId);

        // Validation des transitions de statut
        StatutConsultation ancienStatut = consultation.getStatut();
        if (ancienStatut == StatutConsultation.TERMINEE && nouveauStatut != StatutConsultation.TERMINEE) {
            throw new ValidationException("Une consultation terminée ne peut pas changer de statut");
        }
        if (ancienStatut == StatutConsultation.ANNULEE && nouveauStatut != StatutConsultation.ANNULEE) {
            throw new ValidationException("Une consultation annulée ne peut pas changer de statut");
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
        return consultationRepository.findById(consultationId) != null;
    }

    @Override
    public long countAllConsultations() {
        return getAllConsultations().size();
    }

    @Override
    public long countByStatut(StatutConsultation statut) {
        return findByStatut(statut).size();
    }

    @Override
    public long countByDossierMedicalId(Long dossierId) {
        return findByDossierMedicalId(dossierId).size();
    }

    // ========== VALIDATION PRIVÉE ==========
    private void validateConsultation(Consultation consultation) {
        if (consultation == null) {
            throw new ValidationException("La consultation ne peut pas être null");
        }
    }
}

