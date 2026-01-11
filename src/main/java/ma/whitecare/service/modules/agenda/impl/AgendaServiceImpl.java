package ma.whitecare.service.modules.agenda.impl;

import ma.whitecare.common.exceptions.AgendaNotFoundException;
import ma.whitecare.common.exceptions.MedecinNotFoundException;
import ma.whitecare.common.exceptions.RelatedEntityNotFoundException;
import ma.whitecare.common.validators.AgendaValidator;
import ma.whitecare.entities.agenda.AgendaMensuel;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.entities.enums.JourSemaine;
import ma.whitecare.entities.enums.Mois;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.mvc.dto.AgendaDto.AgendaDTO;
import ma.whitecare.mvc.dto.AgendaDto.CreateAgendaDTO;
import ma.whitecare.mvc.dto.AgendaDto.UpdateAgendaDTO;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.agenda.api.AgendaRepository;
import ma.whitecare.service.modules.agenda.api.AgendaService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AgendaServiceImpl implements AgendaService {

    private final AgendaRepository agendaRepository;
    private final MedecinRepository medecinRepository;

    public AgendaServiceImpl(AgendaRepository agendaRepository,
                             MedecinRepository medecinRepository) {
        this.agendaRepository = agendaRepository;
        this.medecinRepository = medecinRepository;
    }

    // ========== CRUD AGENDAS ==========

    @Override
    public AgendaMensuel createAgenda(CreateAgendaDTO agendaDTO) {
        // Validation
        List<String> errors = AgendaValidator.validateCreateAgenda(agendaDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'existence du médecin
        validateMedecinExists(agendaDTO.getMedecinId());

        // Vérifier qu'il n'existe pas déjà un agenda pour ce médecin, mois et année
        Optional<AgendaMensuel> existing = agendaRepository.findByMoisAndAnneeAndMedecin(
                agendaDTO.getMois(), agendaDTO.getAnnee(), agendaDTO.getMedecinId());
        if (existing.isPresent()) {
            throw new ValidationException(
                    String.format("Un agenda existe déjà pour le médecin %d en %s %d",
                            agendaDTO.getMedecinId(), agendaDTO.getMois(), agendaDTO.getAnnee()));
        }

        // Convertir DTO en entité
        AgendaMensuel agenda = convertToAgenda(agendaDTO);

        // Définir les valeurs par défaut
        setDefaultValues(agenda);

        // Sauvegarder
        agendaRepository.create(agenda);

        return agenda;
    }

    @Override
    public AgendaMensuel getAgendaById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de l'agenda est obligatoire");
        }

        AgendaMensuel agenda = agendaRepository.findById(id);
        if (agenda == null) {
            throw new AgendaNotFoundException(id);
        }
        return agenda;
    }

    @Override
    public List<AgendaMensuel> findAll() {
        return agendaRepository.findAll();
    }

    @Override
    public AgendaMensuel updateAgenda(Long id, UpdateAgendaDTO updateDTO) {
        // Récupérer l'agenda existant
        AgendaMensuel agenda = getAgendaById(id);

        // Validation
        List<String> errors = AgendaValidator.validateUpdateAgenda(updateDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'existence du médecin si modifié
        if (updateDTO.getMedecinId() != null) {
            validateMedecinExists(updateDTO.getMedecinId());
        }

        // Vérifier l'unicité si mois/année/médecin sont modifiés
        if (updateDTO.getMois() != null || updateDTO.getAnnee() != null || updateDTO.getMedecinId() != null) {
            Mois mois = updateDTO.getMois() != null ? updateDTO.getMois() : agenda.getMois();
            int annee = updateDTO.getAnnee() != null ? updateDTO.getAnnee() : agenda.getAnnee();
            Long medecinId = updateDTO.getMedecinId() != null ? updateDTO.getMedecinId() :
                    (agenda.getMedecin() != null ? agenda.getMedecin().getIdUser() : null);

            Optional<AgendaMensuel> existing = agendaRepository.findByMoisAndAnneeAndMedecin(mois, annee, medecinId);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new ValidationException(
                        String.format("Un agenda existe déjà pour le médecin %d en %s %d",
                                medecinId, mois, annee));
            }
        }

        // Mettre à jour les champs
        updateAgendaFields(agenda, updateDTO);

        // Sauvegarder
        agendaRepository.update(agenda);

        return agenda;
    }

    @Override
    public void deleteAgenda(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de l'agenda est obligatoire");
        }

        // Vérifier que l'agenda existe
        getAgendaById(id);

        // Supprimer (les jours et créneaux seront supprimés en cascade)
        agendaRepository.deleteById(id);
    }

    // ========== RECHERCHES ==========

    @Override
    public List<AgendaMensuel> findByMedecinId(Long medecinId) {
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin est obligatoire");
        }
        return agendaRepository.findByMedecinId(medecinId);
    }

    @Override
    public Optional<AgendaMensuel> findByMoisAndAnneeAndMedecin(Mois mois, int annee, Long medecinId) {
        if (mois == null) {
            throw new IllegalArgumentException("Le mois est obligatoire");
        }
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin est obligatoire");
        }
        return agendaRepository.findByMoisAndAnneeAndMedecin(mois, annee, medecinId);
    }

    @Override
    public boolean existsById(Long id) {
        return agendaRepository.findById(id) != null;
    }

    // ========== GESTION DES JOURS ==========

    @Override
    public Jour ajouterJour(Long agendaId, LocalDate date, String raisonIndisponibilite) {
        // Vérifier que l'agenda existe
        AgendaMensuel agenda = getAgendaById(agendaId);

        // Vérifier que le jour n'existe pas déjà
        Optional<Jour> existing = agendaRepository.findJourByDate(agendaId, date);
        if (existing.isPresent()) {
            throw new ValidationException("Un jour existe déjà pour cette date dans cet agenda");
        }

        // Créer le jour
        Jour jour = new Jour();
        jour.setDate(date);
        jour.setJourSemaine(JourSemaine.valueOf(date.getDayOfWeek().name()));
        jour.setEstDisponible(raisonIndisponibilite == null || raisonIndisponibilite.trim().isEmpty());
        jour.setRaisonIndisponibilite(raisonIndisponibilite);

        // Ajouter le jour
        agendaRepository.ajouterJour(agendaId, jour);

        return jour;
    }

    @Override
    public void supprimerJour(Long jourId) {
        if (jourId == null) {
            throw new IllegalArgumentException("L'ID du jour est obligatoire");
        }
        agendaRepository.supprimerJour(jourId);
    }

    @Override
    public void mettreAJourJour(Jour jour) {
        if (jour == null || jour.getId() == null) {
            throw new IllegalArgumentException("Le jour et son ID sont obligatoires");
        }
        agendaRepository.mettreAJourJour(jour);
    }

    @Override
    public List<Jour> findJoursByAgendaId(Long agendaId) {
        if (agendaId == null) {
            throw new IllegalArgumentException("L'ID de l'agenda est obligatoire");
        }
        return agendaRepository.findJoursByAgendaId(agendaId);
    }

    @Override
    public List<Jour> findJoursDisponiblesByAgendaId(Long agendaId) {
        if (agendaId == null) {
            throw new IllegalArgumentException("L'ID de l'agenda est obligatoire");
        }
        return agendaRepository.findJoursDisponiblesByAgendaId(agendaId);
    }

    @Override
    public List<Jour> findJoursNonDisponiblesByAgendaId(Long agendaId) {
        if (agendaId == null) {
            throw new IllegalArgumentException("L'ID de l'agenda est obligatoire");
        }
        return agendaRepository.findJoursNonDisponiblesByAgendaId(agendaId);
    }

    @Override
    public Optional<Jour> findJourByDate(Long agendaId, LocalDate date) {
        if (agendaId == null) {
            throw new IllegalArgumentException("L'ID de l'agenda est obligatoire");
        }
        if (date == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }
        return agendaRepository.findJourByDate(agendaId, date);
    }

    @Override
    public boolean estJourDisponible(Long agendaId, LocalDate date) {
        if (agendaId == null) {
            throw new IllegalArgumentException("L'ID de l'agenda est obligatoire");
        }
        if (date == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }
        return agendaRepository.estJourDisponible(agendaId, date);
    }

    // ========== CONVERSIONS ==========

    @Override
    public AgendaDTO convertToDTO(AgendaMensuel agenda) {
        if (agenda == null) {
            return null;
        }

        return AgendaDTO.builder()
                .id(agenda.getId())
                .mois(agenda.getMois())
                .annee(agenda.getAnnee())
                .medecinId(agenda.getMedecin() != null ? agenda.getMedecin().getIdUser() : null)
                .dateCreation(agenda.getDateCreation())
                .dateDerniereModification(agenda.getDateDerniereModification())
                .creePar(agenda.getCreePar())
                .modifiePar(agenda.getModifiePar())
                .build();
    }

    @Override
    public List<AgendaDTO> convertToDTOList(List<AgendaMensuel> agendas) {
        if (agendas == null) {
            return List.of();
        }
        return agendas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private AgendaMensuel convertToAgenda(CreateAgendaDTO dto) {
        AgendaMensuel agenda = new AgendaMensuel();
        agenda.setMois(dto.getMois());
        agenda.setAnnee(dto.getAnnee());

        // Créer un objet Medecin avec juste l'ID
        if (dto.getMedecinId() != null) {
            Medecin medecin = new Medecin();
            medecin.setIdUser(dto.getMedecinId());
            agenda.setMedecin(medecin);
        }

        agenda.setCreePar(dto.getCreePar() != null ? dto.getCreePar() : "system");
        agenda.setModifiePar(dto.getModifiePar() != null ? dto.getModifiePar() : "system");
        return agenda;
    }

    private void setDefaultValues(AgendaMensuel agenda) {
        if (agenda.getCreePar() == null) {
            agenda.setCreePar("system");
        }
        if (agenda.getModifiePar() == null) {
            agenda.setModifiePar("system");
        }
    }

    private void updateAgendaFields(AgendaMensuel agenda, UpdateAgendaDTO updateDTO) {
        if (updateDTO.getMois() != null) {
            agenda.setMois(updateDTO.getMois());
        }
        if (updateDTO.getAnnee() != null) {
            agenda.setAnnee(updateDTO.getAnnee());
        }
        if (updateDTO.getMedecinId() != null) {
            Medecin medecin = new Medecin();
            medecin.setIdUser(updateDTO.getMedecinId());
            agenda.setMedecin(medecin);
        }
        if (updateDTO.getModifiePar() != null) {
            agenda.setModifiePar(updateDTO.getModifiePar());
        } else {
            agenda.setModifiePar("system");
        }
    }

    private void validateMedecinExists(Long medecinId) {
        if (medecinId == null) {
            throw new ValidationException("L'ID du médecin est obligatoire");
        }

        Medecin medecin = medecinRepository.findById(medecinId);
        if (medecin == null) {
            throw new MedecinNotFoundException(medecinId);
        }
    }
}
