package ma.whitecare.service.modules.medicament.impl;

import ma.whitecare.common.exceptions.MedicamentAlreadyExistsException;
import ma.whitecare.common.exceptions.MedicamentNotFoundException;
import ma.whitecare.common.validators.MedicamentValidator;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.mvc.dto.MedicamentDto.CreateMedicamentDTO;
import ma.whitecare.mvc.dto.MedicamentDto.MedicamentDTO;
import ma.whitecare.mvc.dto.MedicamentDto.UpdateMedicamentDTO;
import ma.whitecare.repository.modules.Medicament.MedicamentRepository;
import ma.whitecare.service.modules.medicament.api.MedicamentService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository repo;

    public MedicamentServiceImpl(MedicamentRepository repo) {
        this.repo = repo;
    }

    // ========== CRUD MÉDICAMENTS ==========

    @Override
    public Medicament createMedicament(CreateMedicamentDTO medicamentDTO) {
        // Validation
        List<String> errors = MedicamentValidator.validateCreateMedicament(medicamentDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'unicité du nom
        if (!isNomAvailable(medicamentDTO.getNom())) {
            throw new MedicamentAlreadyExistsException("nom", medicamentDTO.getNom());
        }

        // Convertir DTO en entité
        Medicament medicament = convertToMedicament(medicamentDTO);

        // Définir les valeurs par défaut
        setDefaultValues(medicament);

        // Sauvegarder
        repo.create(medicament);

        return medicament;
    }

    @Override
    public Medicament getMedicamentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du médicament est obligatoire");
        }

        Medicament medicament = repo.findById(id);
        if (medicament == null) {
            throw new MedicamentNotFoundException(id);
        }
        return medicament;
    }

    @Override
    public List<Medicament> getAll() {
        return repo.findAll();
    }

    @Override
    public Medicament updateMedicament(Long id, UpdateMedicamentDTO updateDTO) {
        // Récupérer le médicament existant
        Medicament medicament = getMedicamentById(id);

        // Validation
        List<String> errors = MedicamentValidator.validateUpdateMedicament(updateDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier l'unicité du nom si le nom est modifié
        if (updateDTO.getNom() != null && !updateDTO.getNom().equals(medicament.getNom())) {
            if (!isNomAvailable(updateDTO.getNom())) {
                throw new MedicamentAlreadyExistsException("nom", updateDTO.getNom());
            }
        }

        // Mettre à jour les champs
        updateMedicamentFields(medicament, updateDTO);

        // Sauvegarder
        repo.update(medicament);

        return medicament;
    }

    @Override
    public void deleteMedicament(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du médicament est obligatoire");
        }

        // Vérifier que le médicament existe
        getMedicamentById(id);

        // Supprimer
        repo.deleteById(id);
    }

    // ========== RECHERCHES ==========

    @Override
    public Optional<Medicament> getByExactName(String nom) {
        if (nom == null || nom.isBlank()) {
            return Optional.empty();
        }
        return repo.findByNomExact(nom);
    }

    @Override
    public List<Medicament> getByLaboratoire(String laboratoire) {
        if (laboratoire == null || laboratoire.isBlank()) {
            return List.of();
        }
        return repo.findByLaboratoire(laboratoire);
    }

    @Override
    public List<Medicament> getByType(String type) {
        if (type == null || type.isBlank()) {
            return List.of();
        }
        return repo.findByType(type);
    }

    // ========== VALIDATION ==========

    @Override
    public boolean isNomAvailable(String nom) {
        if (nom == null || nom.isBlank()) {
            return false;
        }
        Optional<Medicament> existing = repo.findByNomExact(nom);
        return existing.isEmpty();
    }

    // ========== CONVERSIONS ==========

    @Override
    public MedicamentDTO convertToDTO(Medicament medicament) {
        if (medicament == null) {
            return null;
        }

        return MedicamentDTO.builder()
                .idMct(medicament.getIdMct())
                .nom(medicament.getNom())
                .laboratoire(medicament.getLaboratoire())
                .type(medicament.getType())
                .forme(medicament.getForme())
                .remboursable(medicament.getRemboursable())
                .prixUnitaire(medicament.getPrixUnitaire())
                .description(medicament.getDescription())
                .dateCreation(medicament.getDateCreation())
                .dateDerniereModification(medicament.getDateDerniereModification())
                .creePar(medicament.getCreePar())
                .modifiePar(medicament.getModifiePar())
                .build();
    }

    @Override
    public List<MedicamentDTO> convertToDTOList(List<Medicament> medicaments) {
        if (medicaments == null) {
            return List.of();
        }
        return medicaments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private Medicament convertToMedicament(CreateMedicamentDTO dto) {
        Medicament medicament = new Medicament();
        medicament.setNom(dto.getNom());
        medicament.setLaboratoire(dto.getLaboratoire());
        medicament.setType(dto.getType());
        medicament.setForme(dto.getForme());
        medicament.setRemboursable(dto.getRemboursable());
        medicament.setPrixUnitaire(dto.getPrixUnitaire());
        medicament.setDescription(dto.getDescription());
        medicament.setCreePar(dto.getCreePar() != null ? dto.getCreePar() : "system");
        medicament.setModifiePar(dto.getModifiePar() != null ? dto.getModifiePar() : "system");
        return medicament;
    }

    private void setDefaultValues(Medicament medicament) {
        if (medicament.getRemboursable() == null) {
            medicament.setRemboursable(false);
        }
        if (medicament.getCreePar() == null) {
            medicament.setCreePar("system");
        }
        if (medicament.getModifiePar() == null) {
            medicament.setModifiePar("system");
        }
    }

    private void updateMedicamentFields(Medicament medicament, UpdateMedicamentDTO updateDTO) {
        if (updateDTO.getNom() != null) {
            medicament.setNom(updateDTO.getNom());
        }
        if (updateDTO.getLaboratoire() != null) {
            medicament.setLaboratoire(updateDTO.getLaboratoire());
        }
        if (updateDTO.getType() != null) {
            medicament.setType(updateDTO.getType());
        }
        if (updateDTO.getForme() != null) {
            medicament.setForme(updateDTO.getForme());
        }
        if (updateDTO.getRemboursable() != null) {
            medicament.setRemboursable(updateDTO.getRemboursable());
        }
        if (updateDTO.getPrixUnitaire() != null) {
            medicament.setPrixUnitaire(updateDTO.getPrixUnitaire());
        }
        if (updateDTO.getDescription() != null) {
            medicament.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getModifiePar() != null) {
            medicament.setModifiePar(updateDTO.getModifiePar());
        } else {
            medicament.setModifiePar("system");
        }
    }
}