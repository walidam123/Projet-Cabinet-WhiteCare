package ma.whitecare.service.modules.agenda.impl;

import ma.whitecare.common.exceptions.CreneauConflictException;
import ma.whitecare.common.exceptions.CreneauNotFoundException;
import ma.whitecare.common.exceptions.JourNotFoundException;
import ma.whitecare.common.validators.CreneauValidator;
import ma.whitecare.entities.agenda.AgendaMensuel;
import ma.whitecare.entities.agenda.Creneau;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.mvc.dto.CreneauDto.CreateCreneauDTO;
import ma.whitecare.mvc.dto.CreneauDto.CreneauDTO;
import ma.whitecare.mvc.dto.CreneauDto.UpdateCreneauDTO;
import ma.whitecare.repository.modules.agenda.api.AgendaRepository;
import ma.whitecare.service.modules.agenda.api.CreneauService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class CreneauServiceImpl implements CreneauService {

    private final AgendaRepository agendaRepository;

    public CreneauServiceImpl(AgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    // ========== CRUD CRENEAUX ==========

    @Override
    public Creneau createCreneau(CreateCreneauDTO creneauDTO) {
        // Récupérer le jour
        Jour jour = findJourById(creneauDTO.getJourId());

        // Validation
        List<String> errors = CreneauValidator.validateCreateCreneau(creneauDTO, jour);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier que le jour est disponible
        if (!jour.isEstDisponible()) {
            throw new ValidationException("Impossible de créer un créneau pour un jour non disponible");
        }

        // Convertir DTO en entité
        Creneau creneau = convertToCreneau(creneauDTO);

        // Vérifier qu'il n'y a pas de chevauchement avec d'autres créneaux
        List<Creneau> creneauxExistants = agendaRepository.findCreneauxByJourId(creneauDTO.getJourId());
        List<String> chevauchementErrors = CreneauValidator.validatePasDeChevauchement(creneau, creneauxExistants);
        if (!chevauchementErrors.isEmpty()) {
            throw new CreneauConflictException(String.join(", ", chevauchementErrors));
        }

        // Définir les valeurs par défaut
        setDefaultValues(creneau);

        // Sauvegarder
        agendaRepository.ajouterCreneau(creneauDTO.getJourId(), creneau);

        return creneau;
    }

    @Override
    public Creneau getCreneauById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du créneau est obligatoire");
        }

        // Chercher dans tous les jours de tous les agendas
        // Note: Cette méthode nécessite une recherche dans la base de données
        // Pour l'instant, on va chercher dans tous les agendas
        List<AgendaMensuel> agendas = agendaRepository.findAll();
        for (AgendaMensuel agenda : agendas) {
            List<Jour> jours = agendaRepository.findJoursByAgendaId(agenda.getId());
            for (Jour jour : jours) {
                List<Creneau> creneaux = agendaRepository.findCreneauxByJourId(jour.getId());
                for (Creneau creneau : creneaux) {
                    if (creneau.getId() != null && creneau.getId().equals(id)) {
                        return creneau;
                    }
                }
            }
        }

        throw new CreneauNotFoundException(id);
    }

    @Override
    public List<Creneau> findAll() {
        // Récupérer tous les créneaux de tous les agendas
        List<AgendaMensuel> agendas = agendaRepository.findAll();
        return agendas.stream()
                .flatMap(agenda -> agendaRepository.findJoursByAgendaId(agenda.getId()).stream())
                .flatMap(jour -> agendaRepository.findCreneauxByJourId(jour.getId()).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Creneau updateCreneau(Long id, UpdateCreneauDTO updateDTO) {
        // Récupérer le créneau existant
        Creneau creneau = getCreneauById(id);

        // Récupérer le jour associé
        Jour jour = findJourByCreneauId(id);

        // Validation
        List<String> errors = CreneauValidator.validateUpdateCreneau(updateDTO, jour);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Mettre à jour les champs
        updateCreneauFields(creneau, updateDTO);

        // Vérifier qu'il n'y a pas de chevauchement avec d'autres créneaux (sauf lui-même)
        List<Creneau> creneauxExistants = agendaRepository.findCreneauxByJourId(jour.getId());
        List<String> chevauchementErrors = CreneauValidator.validatePasDeChevauchement(creneau, creneauxExistants);
        if (!chevauchementErrors.isEmpty()) {
            throw new CreneauConflictException(String.join(", ", chevauchementErrors));
        }

        // Sauvegarder
        agendaRepository.mettreAJourCreneau(creneau);

        return creneau;
    }

    @Override
    public void deleteCreneau(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du créneau est obligatoire");
        }

        // Vérifier que le créneau existe
        getCreneauById(id);

        // Supprimer
        agendaRepository.supprimerCreneau(id);
    }

    // ========== RECHERCHES ==========

    @Override
    public List<Creneau> findByJourId(Long jourId) {
        if (jourId == null) {
            throw new IllegalArgumentException("L'ID du jour est obligatoire");
        }
        return agendaRepository.findCreneauxByJourId(jourId);
    }

    @Override
    public List<Creneau> findCreneauxDisponiblesByJourId(Long jourId) {
        if (jourId == null) {
            throw new IllegalArgumentException("L'ID du jour est obligatoire");
        }
        return agendaRepository.findCreneauxDisponiblesByJourId(jourId);
    }

    @Override
    public List<Creneau> findCreneauxByAgendaAndDate(Long agendaId, LocalDate date) {
        if (agendaId == null) {
            throw new IllegalArgumentException("L'ID de l'agenda est obligatoire");
        }
        if (date == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }
        return agendaRepository.findCreneauxByAgendaAndDate(agendaId, date);
    }

    @Override
    public List<Creneau> findCreneauxDisponiblesByDate(Long agendaId, LocalDate date) {
        if (agendaId == null) {
            throw new IllegalArgumentException("L'ID de l'agenda est obligatoire");
        }
        if (date == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }
        return agendaRepository.findCreneauxDisponiblesByDate(agendaId, date);
    }

    @Override
    public boolean existsById(Long id) {
        try {
            getCreneauById(id);
            return true;
        } catch (CreneauNotFoundException e) {
            return false;
        }
    }

    // ========== VALIDATION ==========

    @Override
    public boolean estCreneauDisponible(Long jourId, LocalTime heureDebut, LocalTime heureFin) {
        if (jourId == null || heureDebut == null || heureFin == null) {
            return false;
        }

        // Vérifier qu'il n'y a pas de chevauchement avec d'autres créneaux
        return !creneauxSeChevauchent(jourId, heureDebut, heureFin, null);
    }

    @Override
    public boolean creneauxSeChevauchent(Long jourId, LocalTime heureDebut, LocalTime heureFin, Long creneauExcluId) {
        if (jourId == null || heureDebut == null || heureFin == null) {
            return false;
        }

        Creneau creneauTest = new Creneau();
        creneauTest.setHeureDebut(heureDebut);
        creneauTest.setHeureFin(heureFin);
        creneauTest.setId(creneauExcluId);

        List<Creneau> creneauxExistants = agendaRepository.findCreneauxByJourId(jourId);
        for (Creneau creneauExistant : creneauxExistants) {
            // Ignorer le créneau exclu
            if (creneauExcluId != null && creneauExistant.getId() != null &&
                    creneauExcluId.equals(creneauExistant.getId())) {
                continue;
            }

            // Vérifier le chevauchement seulement si le créneau existant est disponible ou a un rendez-vous
            if (creneauExistant.isEstDisponible() || creneauExistant.getRendezVousId() != null) {
                if (CreneauValidator.creneauxSeChevauchent(creneauTest, creneauExistant)) {
                    return true;
                }
            }
        }

        return false;
    }

    // ========== CONVERSIONS ==========

    @Override
    public CreneauDTO convertToDTO(Creneau creneau) {
        if (creneau == null) {
            return null;
        }

        // Trouver le jour associé
        Long jourId = findJourIdByCreneauId(creneau.getId());

        return CreneauDTO.builder()
                .id(creneau.getId())
                .jourId(jourId)
                .heureDebut(creneau.getHeureDebut())
                .heureFin(creneau.getHeureFin())
                .estDisponible(creneau.isEstDisponible())
                .motifIndisponibilite(creneau.getMotifIndisponibilite())
                .rendezVousId(creneau.getRendezVousId())
                .dateCreation(creneau.getDateCreation())
                .dateDerniereModification(creneau.getDateDerniereModification())
                .creePar(creneau.getCreePar())
                .modifiePar(creneau.getModifiePar())
                .build();
    }

    @Override
    public List<CreneauDTO> convertToDTOList(List<Creneau> creneaux) {
        if (creneaux == null) {
            return List.of();
        }
        return creneaux.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private Creneau convertToCreneau(CreateCreneauDTO dto) {
        Creneau creneau = new Creneau();
        creneau.setHeureDebut(dto.getHeureDebut());
        creneau.setHeureFin(dto.getHeureFin());
        creneau.setEstDisponible(dto.getEstDisponible() != null ? dto.getEstDisponible() : true);
        creneau.setMotifIndisponibilite(dto.getMotifIndisponibilite());
        creneau.setRendezVousId(dto.getRendezVousId());
        creneau.setCreePar(dto.getCreePar() != null ? dto.getCreePar() : "system");
        creneau.setModifiePar(dto.getModifiePar() != null ? dto.getModifiePar() : "system");
        return creneau;
    }

    private void setDefaultValues(Creneau creneau) {
        if (!creneau.isEstDisponible() && creneau.getMotifIndisponibilite() == null) {
            creneau.setMotifIndisponibilite("Non spécifié");
        }
        if (creneau.getCreePar() == null) {
            creneau.setCreePar("system");
        }
        if (creneau.getModifiePar() == null) {
            creneau.setModifiePar("system");
        }
    }

    private void updateCreneauFields(Creneau creneau, UpdateCreneauDTO updateDTO) {
        if (updateDTO.getHeureDebut() != null) {
            creneau.setHeureDebut(updateDTO.getHeureDebut());
        }
        if (updateDTO.getHeureFin() != null) {
            creneau.setHeureFin(updateDTO.getHeureFin());
        }
        if (updateDTO.getEstDisponible() != null) {
            creneau.setEstDisponible(updateDTO.getEstDisponible());
        }
        if (updateDTO.getMotifIndisponibilite() != null) {
            creneau.setMotifIndisponibilite(updateDTO.getMotifIndisponibilite());
        }
        if (updateDTO.getRendezVousId() != null) {
            creneau.setRendezVousId(updateDTO.getRendezVousId());
        }
        if (updateDTO.getModifiePar() != null) {
            creneau.setModifiePar(updateDTO.getModifiePar());
        } else {
            creneau.setModifiePar("system");
        }
    }

    private Jour findJourById(Long jourId) {
        if (jourId == null) {
            throw new IllegalArgumentException("L'ID du jour est obligatoire");
        }

        // Chercher dans tous les agendas
        List<AgendaMensuel> agendas = agendaRepository.findAll();
        for (AgendaMensuel agenda : agendas) {
            List<Jour> jours = agendaRepository.findJoursByAgendaId(agenda.getId());
            for (Jour jour : jours) {
                if (jour.getId() != null && jour.getId().equals(jourId)) {
                    return jour;
                }
            }
        }

        throw new JourNotFoundException(jourId);
    }

    private Jour findJourByCreneauId(Long creneauId) {
        // Chercher dans tous les agendas
        List<AgendaMensuel> agendas = agendaRepository.findAll();
        for (AgendaMensuel agenda : agendas) {
            List<Jour> jours = agendaRepository.findJoursByAgendaId(agenda.getId());
            for (Jour jour : jours) {
                List<Creneau> creneaux = agendaRepository.findCreneauxByJourId(jour.getId());
                for (Creneau creneau : creneaux) {
                    if (creneau.getId() != null && creneau.getId().equals(creneauId)) {
                        return jour;
                    }
                }
            }
        }

        throw new CreneauNotFoundException(creneauId);
    }

    private Long findJourIdByCreneauId(Long creneauId) {
        Jour jour = findJourByCreneauId(creneauId);
        return jour.getId();
    }
}
