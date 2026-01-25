package ma.whitecare.service.modules.rdv.impl;

import ma.whitecare.common.exceptions.ConsultationNotFoundException;
import ma.whitecare.common.exceptions.DossierMedicalNotFoundException;
import ma.whitecare.common.exceptions.RDVConflictException;
import ma.whitecare.common.exceptions.RDVInvalidStatusException;
import ma.whitecare.common.exceptions.RDVNotFoundException;
import ma.whitecare.common.validators.RDVValidator;
import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.mvc.dto.RDVDto.CreateRDVDTO;
import ma.whitecare.mvc.dto.RDVDto.RDVDTO;
import ma.whitecare.mvc.dto.RDVDto.UpdateRDVDTO;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.rdv.api.RDVRepository;
import ma.whitecare.service.modules.rdv.api.RDVService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class RDVServiceImpl implements RDVService {

    private final RDVRepository rdvRepository;
    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierMedicalRepository;

    public RDVServiceImpl(RDVRepository rdvRepository,
            ConsultationRepository consultationRepository,
            DossierMedicalRepository dossierMedicalRepository) {
        this.rdvRepository = rdvRepository;
        this.consultationRepository = consultationRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
    }

    // ========== CRUD RDV ==========

    @Override
    public RDV createRDV(CreateRDVDTO rdvDTO) {
        // Récupérer la consultation
        Consultation consultation = validateAndGetConsultation(rdvDTO.getConsultationId());

        // Validation
        List<String> errors = RDVValidator.validateCreateRDV(rdvDTO, consultation);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'existence du dossier médical si fourni
        if (rdvDTO.getDossierMedicaleId() != null) {
            validateDossierMedicalExists(rdvDTO.getDossierMedicaleId());
        }

        // Vérifier qu'il n'y a pas de conflit (intervalle de 30 min)
        validateRDVInterval(rdvDTO.getDate(), rdvDTO.getHeure(), null);

        // Convertir DTO en entité
        RDV rdv = convertToRDV(rdvDTO);

        // Définir le statut par défaut si non fourni
        if (rdv.getStatut() == null) {
            rdv.setStatut(StatutRendezVous.PLANIFIE);
        }

        // Définir les valeurs par défaut
        setDefaultValues(rdv);

        // Sauvegarder
        rdvRepository.create(rdv);

        return rdv;
    }

    @Override
    public RDV getRDVById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du rendez-vous est obligatoire");
        }

        RDV rdv = rdvRepository.findById(id);
        if (rdv == null) {
            throw new RDVNotFoundException(id);
        }
        return rdv;
    }

    @Override
    public List<RDV> findAll() {
        return rdvRepository.findAll();
    }

    @Override
    public RDV updateRDV(Long id, UpdateRDVDTO updateDTO) {
        // Récupérer le RDV existant
        RDV rdv = getRDVById(id);

        // Vérifier que le RDV peut être modifié
        if (!RDVValidator.canModifyRDV(rdv.getStatut())) {
            throw new RDVInvalidStatusException(
                    String.format("Impossible de modifier un rendez-vous avec le statut %s", rdv.getStatut()));
        }

        // Récupérer la consultation (actuelle ou nouvelle)
        Consultation consultation = rdv.getConsultation();
        if (updateDTO.getConsultationId() != null) {
            consultation = validateAndGetConsultation(updateDTO.getConsultationId());
        }

        // Validation
        List<String> errors = RDVValidator.validateUpdateRDV(updateDTO, consultation);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'existence du dossier médical si modifié
        if (updateDTO.getDossierMedicaleId() != null) {
            validateDossierMedicalExists(updateDTO.getDossierMedicaleId());
        }

        // Validation de la transition de statut si le statut est modifié
        if (updateDTO.getStatut() != null && !updateDTO.getStatut().equals(rdv.getStatut())) {
            List<String> statusErrors = RDVValidator.validateStatusTransition(rdv.getStatut(), updateDTO.getStatut());
            if (!statusErrors.isEmpty()) {
                throw new RDVInvalidStatusException(rdv.getStatut(), updateDTO.getStatut());
            }
        }

        // Vérifier qu'il n'y a pas de conflit si la date/heure est modifiée
        LocalDate newDate = updateDTO.getDate() != null ? updateDTO.getDate() : rdv.getDate();
        LocalTime newHeure = updateDTO.getHeure() != null ? updateDTO.getHeure() : rdv.getHeure();

        validateRDVInterval(newDate, newHeure, id);

        // Mettre à jour les champs
        updateRDVFields(rdv, updateDTO, consultation);

        // Sauvegarder
        rdvRepository.update(rdv);

        return rdv;
    }

    @Override
    public void deleteRDV(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du rendez-vous est obligatoire");
        }

        // Vérifier que le RDV existe
        RDV rdv = getRDVById(id);

        // Vérifier que le RDV peut être supprimé (pas de statut TERMINE)
        if (rdv.getStatut() == StatutRendezVous.TERMINE) {
            throw new RDVInvalidStatusException("Impossible de supprimer un rendez-vous terminé");
        }

        // Supprimer
        rdvRepository.deleteById(id);
    }

    // ========== RECHERCHES ==========

    @Override
    public boolean existsById(Long id) {
        return rdvRepository.existsById(id);
    }

    @Override
    public List<RDV> findByConsultationId(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est obligatoire");
        }
        return rdvRepository.findByConsultationId(consultationId);
    }

    @Override
    public List<RDV> findByDossierMedicaleId(Long dossierMedicaleId) {
        if (dossierMedicaleId == null) {
            throw new IllegalArgumentException("L'ID du dossier médical est obligatoire");
        }
        return rdvRepository.findByDossierMedicaleId(dossierMedicaleId);
    }

    @Override
    public List<RDV> findByStatut(StatutRendezVous statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return rdvRepository.findByStatut(statut);
    }

    @Override
    public List<RDV> findByStatutAndDate(StatutRendezVous statut, LocalDate date) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        if (date == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }
        return rdvRepository.findByStatutAndDate(statut, date);
    }

    @Override
    public List<RDV> findByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }
        return rdvRepository.findByDate(date);
    }

    @Override
    public List<RDV> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires");
        }
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("La date de début ne peut pas être après la date de fin");
        }
        return rdvRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<RDV> findByDossierMedicaleIdAndStatut(Long dossierMedicaleId, StatutRendezVous statut) {
        if (dossierMedicaleId == null) {
            throw new IllegalArgumentException("L'ID du dossier médical est obligatoire");
        }
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return rdvRepository.findByDossierMedicaleIdAndStatut(dossierMedicaleId, statut);
    }

    @Override
    public List<RDV> findByConsultationIdAndStatut(Long consultationId, StatutRendezVous statut) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est obligatoire");
        }
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return rdvRepository.findByConsultationIdAndStatut(consultationId, statut);
    }

    // ========== GESTION DES STATUTS ==========

    @Override
    public RDV updateStatut(Long id, StatutRendezVous nouveauStatut) {
        if (nouveauStatut == null) {
            throw new IllegalArgumentException("Le nouveau statut est obligatoire");
        }

        // Récupérer le RDV existant
        RDV rdv = getRDVById(id);

        // Vérifier que le RDV peut être modifié
        if (!RDVValidator.canModifyRDV(rdv.getStatut())) {
            throw new RDVInvalidStatusException(
                    String.format("Impossible de modifier un rendez-vous avec le statut %s", rdv.getStatut()));
        }

        // Validation de la transition de statut
        List<String> errors = RDVValidator.validateStatusTransition(rdv.getStatut(), nouveauStatut);
        if (!errors.isEmpty()) {
            throw new RDVInvalidStatusException(rdv.getStatut(), nouveauStatut);
        }

        // Mettre à jour le statut
        rdv.setStatut(nouveauStatut);
        rdv.setModifiePar("system");

        // Sauvegarder
        rdvRepository.update(rdv);

        return rdv;
    }

    @Override
    public boolean canModifyRDV(Long id) {
        RDV rdv = getRDVById(id);
        return RDVValidator.canModifyRDV(rdv.getStatut());
    }

    // ========== STATISTIQUES ==========

    @Override
    public long countByStatut(StatutRendezVous statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return rdvRepository.countByStatut(statut);
    }

    @Override
    public long countByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }
        return rdvRepository.countByDate(date);
    }

    // ========== VALIDATION ==========

    @Override
    public boolean existsByDateAndHeure(LocalDate date, LocalTime heure) {
        if (date == null || heure == null) {
            return false;
        }
        return rdvRepository.existsByDateAndHeure(date, heure);
    }

    @Override
    public boolean existsByConsultationId(Long consultationId) {
        if (consultationId == null) {
            return false;
        }
        return rdvRepository.existsByConsultationId(consultationId);
    }

    // ========== CONVERSIONS ==========

    @Override
    public RDVDTO convertToDTO(RDV rdv) {
        if (rdv == null) {
            return null;
        }

        return RDVDTO.builder()
                .idRDV(rdv.getIdRDV())
                .date(rdv.getDate())
                .heure(rdv.getHeure())
                .motif(rdv.getMotif())
                .statut(rdv.getStatut())
                .noteMedecin(rdv.getNoteMedecin())
                .consultationId(rdv.getConsultation() != null ? rdv.getConsultation().getIdConsultation() : null)
                .dossierMedicaleId(rdv.getDossierMedicale() != null ? rdv.getDossierMedicale().getIdDM() : null)
                .dateCreation(rdv.getDateCreation())
                .dateDerniereModification(rdv.getDateDerniereModification())
                .creePar(rdv.getCreePar())
                .modifiePar(rdv.getModifiePar())
                .build();
    }

    @Override
    public List<RDVDTO> convertToDTOList(List<RDV> rdvs) {
        if (rdvs == null) {
            return List.of();
        }
        return rdvs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private RDV convertToRDV(CreateRDVDTO dto) {
        RDV rdv = new RDV();
        rdv.setDate(dto.getDate());
        rdv.setHeure(dto.getHeure());
        rdv.setMotif(dto.getMotif());
        rdv.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutRendezVous.PLANIFIE);
        rdv.setNoteMedecin(dto.getNoteMedecin());

        // Créer un objet Consultation avec juste l'ID
        if (dto.getConsultationId() != null) {
            Consultation consultation = new Consultation();
            consultation.setIdConsultation(dto.getConsultationId());
            rdv.setConsultation(consultation);
        }

        // Créer un objet DossierMedicale avec juste l'ID si fourni
        if (dto.getDossierMedicaleId() != null) {
            DossierMedicale dossierMedicale = new DossierMedicale();
            dossierMedicale.setIdDM(dto.getDossierMedicaleId());
            rdv.setDossierMedicale(dossierMedicale);
        }

        rdv.setCreePar(dto.getCreePar() != null ? dto.getCreePar() : "system");
        rdv.setModifiePar(dto.getModifiePar() != null ? dto.getModifiePar() : "system");
        return rdv;
    }

    private void setDefaultValues(RDV rdv) {
        if (rdv.getStatut() == null) {
            rdv.setStatut(StatutRendezVous.PLANIFIE);
        }
        if (rdv.getCreePar() == null) {
            rdv.setCreePar("system");
        }
        if (rdv.getModifiePar() == null) {
            rdv.setModifiePar("system");
        }
    }

    private void updateRDVFields(RDV rdv, UpdateRDVDTO updateDTO, Consultation consultation) {
        if (updateDTO.getDate() != null) {
            rdv.setDate(updateDTO.getDate());
        }
        if (updateDTO.getHeure() != null) {
            rdv.setHeure(updateDTO.getHeure());
        }
        if (updateDTO.getMotif() != null) {
            rdv.setMotif(updateDTO.getMotif());
        }
        if (updateDTO.getStatut() != null) {
            rdv.setStatut(updateDTO.getStatut());
        }
        if (updateDTO.getNoteMedecin() != null) {
            rdv.setNoteMedecin(updateDTO.getNoteMedecin());
        }
        if (updateDTO.getConsultationId() != null && consultation != null) {
            rdv.setConsultation(consultation);
        }
        if (updateDTO.getDossierMedicaleId() != null) {
            DossierMedicale dossierMedicale = new DossierMedicale();
            dossierMedicale.setIdDM(updateDTO.getDossierMedicaleId());
            rdv.setDossierMedicale(dossierMedicale);
        } else if (updateDTO.getDossierMedicaleId() == null && updateDTO.getConsultationId() != null) {
            // Si consultation est modifiée mais dossierMedicaleId n'est pas fourni,
            // utiliser le dossier de la consultation
            if (consultation != null && consultation.getDossierMedicale() != null) {
                rdv.setDossierMedicale(consultation.getDossierMedicale());
            }
        }
        if (updateDTO.getModifiePar() != null) {
            rdv.setModifiePar(updateDTO.getModifiePar());
        } else {
            rdv.setModifiePar("system");
        }
    }

    private Consultation validateAndGetConsultation(Long consultationId) {
        if (consultationId == null) {
            throw new ValidationException("L'ID de la consultation est obligatoire");
        }

        Consultation consultation = consultationRepository.findById(consultationId);
        if (consultation == null) {
            throw new ConsultationNotFoundException(consultationId);
        }
        return consultation;
    }

    private void validateDossierMedicalExists(Long dossierId) {
        if (dossierId == null) {
            throw new ValidationException("L'ID du dossier médical est obligatoire");
        }

        if (dossierMedicalRepository.findById(dossierId) == null) {
            throw new DossierMedicalNotFoundException(dossierId);
        }
    }

    private void validateRDVInterval(LocalDate date, LocalTime time, Long excludeId) {
        List<RDV> dayRDVs = rdvRepository.findByDate(date);
        for (RDV existing : dayRDVs) {
            if (excludeId != null && existing.getIdRDV().equals(excludeId)) {
                continue;
            }

            // On ne vérifie pas les RDV annulés
            if (existing.getStatut() == StatutRendezVous.ANNULE) {
                continue;
            }

            LocalTime existingTime = existing.getHeure();
            long diffMinutes = java.time.Duration.between(time, existingTime).abs().toMinutes();

            if (diffMinutes < 30) {
                throw new RDVConflictException(
                        String.format(
                                "Conflit de temps: Un rendez-vous existe déjà à %s. L'intervalle minimum est de 30 minutes.",
                                existingTime.toString()));
            }
        }
    }
}
