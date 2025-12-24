package ma.whitecare.service.modules.actes.impl;

import ma.whitecare.common.exceptions.ActeAlreadyExistsException;
import ma.whitecare.common.exceptions.ActeNotFoundException;
import ma.whitecare.common.exceptions.BusinessRuleException;
import ma.whitecare.common.validators.ActeValidator;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.mvc.dto.ActeDto.ActeDTO;
import ma.whitecare.mvc.dto.ActeDto.CreateActeDTO;
import ma.whitecare.mvc.dto.ActeDto.UpdateActeDTO;
import ma.whitecare.repository.modules.actes.api.ActeRepository;
import ma.whitecare.service.modules.actes.api.ActeService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActeServiceImpl implements ActeService {

    private final ActeRepository acteRepository;

    public ActeServiceImpl(ActeRepository acteRepository) {
        this.acteRepository = acteRepository;
    }

    // ========== CRUD ACTES ==========

    @Override
    public Acte createActe(CreateActeDTO acteDTO) {
        // Validation
        List<String> errors = ActeValidator.validateCreateActe(acteDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'unicité du libellé
        if (!isLibelleAvailable(acteDTO.getLibelle())) {
            throw new ActeAlreadyExistsException("libellé", acteDTO.getLibelle());
        }

        // Convertir DTO en entité
        Acte acte = convertToActe(acteDTO);

        // Définir les valeurs par défaut
        setDefaultValues(acte);

        // Sauvegarder
        acteRepository.create(acte);

        return acte;
    }

    @Override
    public Acte getActeById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de l'acte est obligatoire");
        }

        Acte acte = acteRepository.findById(id);
        if (acte == null) {
            throw new ActeNotFoundException(id);
        }
        return acte;
    }

    @Override
    public List<Acte> getAll() {
        return acteRepository.findAll();
    }

    @Override
    public Acte updateActe(Long id, UpdateActeDTO updateDTO) {
        // Récupérer l'acte existant
        Acte acte = getActeById(id);

        // Validation
        List<String> errors = ActeValidator.validateUpdateActe(updateDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'unicité du libellé si modifié
        if (updateDTO.getLibelle() != null && !updateDTO.getLibelle().equals(acte.getLibelle())) {
            if (!isLibelleAvailable(updateDTO.getLibelle())) {
                throw new ActeAlreadyExistsException("libellé", updateDTO.getLibelle());
            }
        }

        // Mettre à jour les champs
        updateActeFields(acte, updateDTO);

        // Sauvegarder
        acteRepository.update(acte);

        return acte;
    }

    @Override
    public void deleteActe(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de l'acte est obligatoire");
        }

        // Vérifier que l'acte existe
        getActeById(id);

        // Supprimer
        acteRepository.deleteById(id);
    }

    // ========== RECHERCHES ==========

    @Override
    public Optional<Acte> getByExactLibelle(String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé est obligatoire");
        }

        List<Acte> actes = acteRepository.findByLibelle(libelle);
        return actes.stream()
                .filter(a -> a.getLibelle().equalsIgnoreCase(libelle))
                .findFirst();
    }

    @Override
    public List<Acte> getByLibelle(String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé est obligatoire");
        }
        return acteRepository.findByLibelle(libelle);
    }

    @Override
    public List<Acte> getByCategorie(String categorie) {
        if (categorie == null || categorie.trim().isEmpty()) {
            throw new IllegalArgumentException("La catégorie est obligatoire");
        }
        return acteRepository.findByCategorie(categorie);
    }

    @Override
    public Long countByCategorie(String categorie) {
        if (categorie == null || categorie.trim().isEmpty()) {
            throw new IllegalArgumentException("La catégorie est obligatoire");
        }
        return acteRepository.countByCategorie(categorie);
    }

    // ========== MÉTHODES SPÉCIFIQUES ==========

    @Override
    public void updatePrix(Long acteId, Double nouveauPrix) {
        // Vérifier que l'acte existe
        getActeById(acteId);

        // Validation du prix
        List<String> errors = ActeValidator.validatePrixDeBase(nouveauPrix);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Mettre à jour
        acteRepository.updatePrix(acteId, nouveauPrix);
    }

    @Override
    public void updateCategorie(Long acteId, String nouvelleCategorie) {
        // Vérifier que l'acte existe
        getActeById(acteId);

        // Validation de la catégorie
        if (nouvelleCategorie == null || nouvelleCategorie.trim().isEmpty()) {
            throw new BusinessRuleException("La catégorie ne peut pas être vide");
        }
        if (nouvelleCategorie.length() < 2 || nouvelleCategorie.length() > 50) {
            throw new BusinessRuleException("La catégorie doit contenir entre 2 et 50 caractères");
        }

        // Mettre à jour
        acteRepository.updateCategorie(acteId, nouvelleCategorie);
    }

    @Override
    public void updateLibelle(Long acteId, String nouveauLibelle) {
        // Vérifier que l'acte existe
        Acte acte = getActeById(acteId);

        // Validation du libellé
        List<String> errors = ActeValidator.validateLibelle(nouveauLibelle);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'unicité si le libellé change
        if (!nouveauLibelle.equalsIgnoreCase(acte.getLibelle())) {
            if (!isLibelleAvailable(nouveauLibelle)) {
                throw new ActeAlreadyExistsException("libellé", nouveauLibelle);
            }
        }

        // Mettre à jour
        acteRepository.updateLibelle(acteId, nouveauLibelle);
    }

    // ========== VALIDATION ==========

    @Override
    public boolean isLibelleAvailable(String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            return false;
        }

        Optional<Acte> existing = getByExactLibelle(libelle);
        return existing.isEmpty();
    }

    // ========== CONVERSIONS ==========

    @Override
    public ActeDTO convertToDTO(Acte acte) {
        if (acte == null) {
            return null;
        }

        return ActeDTO.builder()
                .idActe(acte.getIdActe())
                .libelle(acte.getLibelle())
                .categorie(acte.getCategorie())
                .prixDeBase(acte.getPrixDeBase())
                .dateCreation(acte.getDateCreation())
                .dateDerniereModification(acte.getDateDerniereModification())
                .creePar(acte.getCreePar())
                .modifiePar(acte.getModifiePar())
                .build();
    }

    @Override
    public List<ActeDTO> convertToDTOList(List<Acte> actes) {
        if (actes == null) {
            return List.of();
        }
        return actes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private Acte convertToActe(CreateActeDTO dto) {
        Acte acte = new Acte();
        acte.setLibelle(dto.getLibelle());
        acte.setCategorie(dto.getCategorie());
        acte.setPrixDeBase(dto.getPrixDeBase());
        acte.setCreePar(dto.getCreePar() != null ? dto.getCreePar() : "system");
        acte.setModifiePar(dto.getModifiePar() != null ? dto.getModifiePar() : "system");
        return acte;
    }

    private void setDefaultValues(Acte acte) {
        if (acte.getPrixDeBase() == null) {
            acte.setPrixDeBase(0.0);
        }
        if (acte.getCreePar() == null) {
            acte.setCreePar("system");
        }
        if (acte.getModifiePar() == null) {
            acte.setModifiePar("system");
        }
    }

    private void updateActeFields(Acte acte, UpdateActeDTO updateDTO) {
        if (updateDTO.getLibelle() != null) {
            acte.setLibelle(updateDTO.getLibelle());
        }
        if (updateDTO.getCategorie() != null) {
            acte.setCategorie(updateDTO.getCategorie());
        }
        if (updateDTO.getPrixDeBase() != null) {
            acte.setPrixDeBase(updateDTO.getPrixDeBase());
        }
        if (updateDTO.getModifiePar() != null) {
            acte.setModifiePar(updateDTO.getModifiePar());
        } else {
            acte.setModifiePar("system");
        }
    }
}
