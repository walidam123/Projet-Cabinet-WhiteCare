package ma.whitecare.service.modules.UserManager.impl;

import ma.whitecare.common.exceptions.*;
import ma.whitecare.common.validators.SecretaireValidator;
import ma.whitecare.common.validators.UserValidator;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.user.Secretaire;

import ma.whitecare.mvc.dto.UserDto.CreateSecretaireDTO;
import ma.whitecare.mvc.dto.UserDto.SecretaireDTO;
import ma.whitecare.mvc.dto.UserDto.SecretaireStatisticsDTO;
import ma.whitecare.mvc.dto.UserDto.UpdateSecretaireDTO;
import ma.whitecare.repository.modules.UserManager.api.SecretaireRepository;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.service.modules.UserManager.api.SecretaireService;
import ma.whitecare.service.modules.UserManager.api.UserService;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

public class SecretaireServiceImpl implements SecretaireService {

    private final SecretaireRepository secretaireRepository;
    private final UserService userService;
    private final CabinetMedicaleRepository cabinetRepository;

    public SecretaireServiceImpl(SecretaireRepository secretaireRepository,
                                 UserService userService,
                                 CabinetMedicaleRepository cabinetRepository) {
        this.secretaireRepository = secretaireRepository;
        this.userService = userService;
        this.cabinetRepository = cabinetRepository;
    }

    @Override
    public Secretaire createSecretaire(CreateSecretaireDTO secretaireDTO) {
        // Validation
        validateCreateSecretaireData(secretaireDTO);

        // Vérifier unicité
        checkUniqueness(secretaireDTO);

        // Vérifier cabinet
        if (secretaireDTO.getCabinetMedicaleId() != null) {
            validateCabinetExists(secretaireDTO.getCabinetMedicaleId());
        }

        // Convertir DTO en entité
        Secretaire secretaire = convertToSecretaire(secretaireDTO);




        // Sauvegarder
        secretaireRepository.create(secretaire);

        return secretaire;
    }

    @Override
    public Secretaire getSecretaireById(Long secretaireId) {
        Secretaire secretaire = secretaireRepository.findById(secretaireId);
        if (secretaire == null) {
            throw new SecretaireNotFoundException(secretaireId);
        }
        return secretaire;
    }

    @Override
    public List<Secretaire> getAllSecretaires() {
        return secretaireRepository.findAll();
    }

    @Override
    public Secretaire updateSecretaire(Long secretaireId, UpdateSecretaireDTO updateDTO) {
        Secretaire secretaire = getSecretaireById(secretaireId);

        // Validation
        validateUpdateSecretaireData(updateDTO);

        // Vérifier cabinet
        if (updateDTO.getCabinetMedicaleId() != null) {
            validateCabinetExists(updateDTO.getCabinetMedicaleId());
        }

        // Mettre à jour les champs
        updateSecretaireFields(secretaire, updateDTO);

        // Sauvegarder
        secretaireRepository.update(secretaire);

        return secretaire;
    }

    @Override
    public void deleteSecretaire(Long secretaireId) {
        getSecretaireById(secretaireId);
        secretaireRepository.deleteById(secretaireId);
    }

    @Override
    public List<Secretaire> getSecretairesByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return secretaireRepository.findByCabinetId(cabinetId);
    }

    @Override
    public Long countSecretairesByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return (long) getSecretairesByCabinet(cabinetId).size();
    }

    @Override
    public void assignSecretaireToCabinet(Long secretaireId, Long cabinetId) {
        getSecretaireById(secretaireId);
        validateCabinetExists(cabinetId);
        secretaireRepository.affecterAuCabinet(secretaireId, cabinetId);
    }

    @Override
    public void removeSecretaireFromCabinet(Long secretaireId) {
        getSecretaireById(secretaireId);
        secretaireRepository.retirerDuCabinet(secretaireId);
    }

    @Override
    public void updateNumCNSS(Long secretaireId, String numCNSS) {
        getSecretaireById(secretaireId);
        // À implémenter si vous avez cette méthode dans le repository
    }

    @Override
    public void updateCommission(Long secretaireId, Double commission) {
        // Validation
        List<String> errors = SecretaireValidator.validateCommission(commission);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        getSecretaireById(secretaireId);
        secretaireRepository.updateCommission(secretaireId, commission);
    }

    @Override
    public void updateNiveauEtude(Long secretaireId, String niveauEtude) {
        // À implémenter
    }



    @Override
    public void updateStatutDisponibilite(Long secretaireId, boolean disponible) {
        getSecretaireById(secretaireId);
        secretaireRepository.updateStatutDisponibilite(secretaireId, disponible);
    }



    @Override
    public List<Secretaire> findSecretairesDisponiblesByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return secretaireRepository.findSecretairesDisponiblesByCabinet(cabinetId);
    }

    @Override
    public List<Secretaire> findByNumeroCNSS(String numeroCNSS) {
        return secretaireRepository.findByNumeroCRSS(numeroCNSS);
    }

    @Override
    public List<Secretaire> findByNomPrenom(String nom, String prenom) {
        return secretaireRepository.findByNomPrenom(nom, prenom);
    }



    @Override
    public List<Secretaire> findSecretairesWithPagination(int page, int size) {
        int offset = page * size;
        return getAllSecretaires().stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());
    }







    @Override
    public SecretaireStatisticsDTO getSecretaireStatisticsByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        List<Secretaire> secretaires = getSecretairesByCabinet(cabinetId);

        return SecretaireStatisticsDTO.builder()
                .totalSecretaires((long) secretaires.size())
                .totalActifs(secretaires.stream().filter(Secretaire::getActif).count())
                .totalInactifs(secretaires.stream().filter(s -> !s.getActif()).count())
                .build();
    }

    @Override
    public SecretaireDTO convertToDTO(Secretaire secretaire) {
        SecretaireDTO dto = (SecretaireDTO) SecretaireDTO.builder()
                .idUser(secretaire.getIdUser())
                .nom(secretaire.getNom())
                .prenom(secretaire.getPrenom())
                .login(secretaire.getLogin())
                .cin(secretaire.getCin())
                .email(secretaire.getEmail())
                .telephone(secretaire.getTel())
                .adresse(secretaire.getAdresse())
                .dateNaissance(secretaire.getDateNaissance())
                .sexe(secretaire.getSexe())
                .actif(secretaire.getActif())
                .salaire(secretaire.getSalaire())
                .prime(secretaire.getPrime())
                .dateRecrutement(secretaire.getDateRecrutement())
                .soldeConge(secretaire.getSoldeConge())
                .cabinetMedicaleId(secretaire.getCabinetMedicaleId())
                .numCNSS(secretaire.getNumCNSS())
                .commission(secretaire.getCommission())
                .typeStaff("SECRETAIRE")
                .build();

        // Rôles
        try {
            List<LibelleRole> roles = userService.getUserRoles(secretaire.getIdUser());
            dto.setRoles(roles);
        } catch (Exception e) {
            dto.setRoles(List.of(LibelleRole.SECRETAIRE));
        }

        return dto;
    }

    @Override
    public List<SecretaireDTO> convertToDTOList(List<Secretaire> secretaires) {
        return secretaires.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private void validateCreateSecretaireData(CreateSecretaireDTO dto) {
        List<String> errors = new ArrayList<>();
        errors.addAll(UserValidator.validateCreateUser(dto));
        errors.addAll(SecretaireValidator.validateCreateSecretaire(dto));

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    private void validateUpdateSecretaireData(UpdateSecretaireDTO dto) {
        List<String> errors = new ArrayList<>();
        errors.addAll(UserValidator.validateUpdateUser(dto));

        if (dto.getCommission() != null) {
            errors.addAll(SecretaireValidator.validateCommission(dto.getCommission()));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    private void checkUniqueness(CreateSecretaireDTO dto) {
        if (!userService.isLoginAvailable(dto.getLogin())) {
            throw new UserAlreadyExistsException("login", dto.getLogin());
        }

        if (!userService.isCinAvailable(dto.getCin())) {
            throw new UserAlreadyExistsException("CIN", dto.getCin());
        }

        // Vérifier numéro CNSS unique
        if (!secretaireRepository.findByNumeroCRSS(dto.getNumCNSS()).isEmpty()) {
            throw new ValidationException("Numéro CNSS déjà utilisé");
        }
    }

    private void validateCabinetExists(Long cabinetId) {
        if (cabinetId == null) {
            throw new ValidationException("L'ID du cabinet est obligatoire");
        }

        if (!cabinetRepository.existsById(cabinetId)) {
            throw new CabinetNotFoundException(cabinetId);
        }
    }

    private Secretaire convertToSecretaire(CreateSecretaireDTO dto) {
        Secretaire secretaire = new Secretaire();

        // Champs utilisateur
        secretaire.setNom(dto.getNom());
        secretaire.setPrenom(dto.getPrenom());
        secretaire.setLogin(dto.getLogin());
        secretaire.setCin(dto.getCin());
        secretaire.setEmail(dto.getEmail());
        secretaire.setTel(dto.getTelephone());
        secretaire.setAdresse(dto.getAdresse());
        secretaire.setDateNaissance(dto.getDateNaissance());
        secretaire.setSexe(dto.getSexe());
        secretaire.setActif(dto.isActif());
        secretaire.setMotDePass(dto.getPassword());

        // Champs staff ✅
        secretaire.setSalaire(dto.getSalaire());
        secretaire.setPrime(dto.getPrime());
        secretaire.setDateRecrutement(dto.getDateRecrutement());
        secretaire.setSoldeConge(dto.getSoldeConge());
        secretaire.setCabinetMedicaleId(dto.getCabinetMedicaleId());

        // Champs secrétaire
        secretaire.setNumCNSS(dto.getNumCNSS());
        secretaire.setCommission(dto.getCommission());

        return secretaire;
    }



    private void updateSecretaireFields(Secretaire secretaire, UpdateSecretaireDTO dto) {
        // Champs utilisateur
        if (dto.getNom() != null) secretaire.setNom(dto.getNom());
        if (dto.getPrenom() != null) secretaire.setPrenom(dto.getPrenom());
        if (dto.getEmail() != null) secretaire.setEmail(dto.getEmail());
        if (dto.getTelephone() != null) secretaire.setTel(dto.getTelephone());
        if (dto.getAdresse() != null) secretaire.setAdresse(dto.getAdresse());
        if (dto.getDateNaissance() != null) secretaire.setDateNaissance(dto.getDateNaissance());
        if (dto.getSexe() != null) secretaire.setSexe(dto.getSexe());
        if (dto.getActif()) secretaire.setActif(dto.getActif());

        // Champs staff
        if (dto.getSalaire() != null) secretaire.setSalaire(dto.getSalaire());
        if (dto.getPrime() != null) secretaire.setPrime(dto.getPrime());
        if (dto.getDateRecrutement() != null) secretaire.setDateRecrutement(dto.getDateRecrutement());
        if (dto.getSoldeConge() != null) secretaire.setSoldeConge(dto.getSoldeConge());
        if (dto.getCabinetMedicaleId() != null) secretaire.setCabinetMedicaleId(dto.getCabinetMedicaleId());

        // Champs secrétaire
        if (dto.getNumCNSS() != null) secretaire.setNumCNSS(dto.getNumCNSS());
        if (dto.getCommission() != null) secretaire.setCommission(dto.getCommission());
    }


}