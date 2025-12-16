package ma.whitecare.service.modules.UserManager.impl;

import ma.whitecare.common.exceptions.CabinetNotFoundException;
import ma.whitecare.common.exceptions.MedecinNotFoundException;
import ma.whitecare.common.exceptions.UserAlreadyExistsException;
import ma.whitecare.common.validators.MedecinValidator;
import ma.whitecare.common.validators.UserValidator;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.mvc.dto.UserDto.CreateMedecinDTO;
import ma.whitecare.mvc.dto.UserDto.MedecinDTO;
import ma.whitecare.mvc.dto.UserDto.MedecinStatisticsDTO;
import ma.whitecare.mvc.dto.UserDto.UpdateMedecinDTO;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.UserManager.api.StaffRepository;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.service.modules.UserManager.api.MedecinService;
import ma.whitecare.service.modules.UserManager.api.UserService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MedecinServiceImpl implements MedecinService {



    private final MedecinRepository medecinRepository;
    private final UserService userService;
    private final CabinetMedicaleRepository cabinetRepository;
    private final StaffRepository staffRepository;

    public MedecinServiceImpl(MedecinRepository medecinRepository,
                              UserService userService,
                              CabinetMedicaleRepository cabinetRepository,
                              StaffRepository staffRepository) {
        this.medecinRepository = medecinRepository;
        this.userService = userService;
        this.cabinetRepository = cabinetRepository;
        this.staffRepository = staffRepository;
    }
    @Override
    public Medecin createMedecin(CreateMedecinDTO medecinDTO) {
        // Validation des données
        validateCreateMedecinData(medecinDTO);

        // Vérifier l'unicité
        checkUniqueness(medecinDTO);

        // Vérifier le cabinet s'il est fourni
        if (medecinDTO.getCabinetMedicaleId() != null) {
            validateCabinetExists(medecinDTO.getCabinetMedicaleId());
        }

        // Convertir DTO en entité
        Medecin medecin = convertToMedecin(medecinDTO);

        // Définir les valeurs par défaut
        setDefaultValues(medecin);



        // Sauvegarder le médecin
        medecinRepository.create(medecin);

        // Assigner le rôle MEDECIN (déjà fait dans UserService via DTO)

        return medecin;
    }

    @Override
    public Medecin getMedecinById(Long medecinId) {
        Medecin medecin = medecinRepository.findById(medecinId);
        if (medecin == null) {
            throw new MedecinNotFoundException(medecinId);
        }
        return medecin;
    }

    @Override
    public List<Medecin> getAllMedecins() {
        return medecinRepository.findAll();

    }

    @Override
    public Medecin updateMedecin(Long medecinId, UpdateMedecinDTO updateDTO) {
        // Récupérer le médecin existant
        Medecin medecin = getMedecinById(medecinId);

        // Validation
        validateUpdateMedecinData(updateDTO);

        // Vérifier le cabinet s'il est fourni
        if (updateDTO.getCabinetMedicaleId() != null) {
            validateCabinetExists(updateDTO.getCabinetMedicaleId());
        }

        // Mettre à jour les champs généraux de l'utilisateur
        updateUserFields(medecin, updateDTO);

        // Mettre à jour les champs spécifiques au médecin
        updateMedecinSpecificFields(medecin, updateDTO);

        // Mettre à jour les champs staff
        updateStaffFields(medecin, updateDTO);

        // Sauvegarder
        medecinRepository.update(medecin);

        return medecin;
    }

    @Override
    public void deleteMedecin(Long medecinId) {
        // Vérifier que le médecin existe
        getMedecinById(medecinId);

        // Supprimer
        medecinRepository.deleteById(medecinId);
    }

    // ========== GESTION CABINET ==========

    @Override
    public List<Medecin> getMedecinsByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return medecinRepository.findByCabinetId(cabinetId);
    }

    @Override
    public Long countMedecinsByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return (long) getMedecinsByCabinet(cabinetId).size();
    }

    @Override
    public void assignMedecinToCabinet(Long medecinId, Long cabinetId) {
        // Vérifier que le médecin existe
        getMedecinById(medecinId);

        // Vérifier que le cabinet existe
        validateCabinetExists(cabinetId);

        // Assigner
        medecinRepository.affecterAuCabinet(medecinId, cabinetId);
    }

    @Override
    public void removeMedecinFromCabinet(Long medecinId) {
        // Vérifier que le médecin existe
        getMedecinById(medecinId);

        // Retirer du cabinet
        medecinRepository.retirerDuCabinet(medecinId);
    }

    // ========== GESTION SPÉCIALITÉS ==========

    @Override
    public void updateSpecialite(Long medecinId, String nouvelleSpecialite) {
        // Validation
        List<String> errors = MedecinValidator.validateSpecialite(nouvelleSpecialite);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier que le médecin existe
        getMedecinById(medecinId);

        // Mettre à jour
        medecinRepository.updateSpecialite(medecinId, nouvelleSpecialite);
    }

    @Override
    public List<Medecin> findMedecinsBySpecialite(String specialite) {
        return medecinRepository.findBySpecialite(specialite);
    }








    @Override
    public void updateDisponibilite(Long medecinId, boolean disponible) {
        getMedecinById(medecinId);
        medecinRepository.updateDisponibilite(medecinId, disponible);
    }

    @Override
    public List<Medecin> findMedecinsDisponiblesByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return getMedecinsByCabinet(cabinetId).stream()
                .filter(Medecin::getActif)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medecin> findMedecinsByNomPrenom(String nom, String prenom) {
        return medecinRepository.findByNomPrenom(nom, prenom);
    }

    @Override
    public List<Medecin> findMedecinsWithPagination(int page, int size) {
        int offset = page * size;
        return getAllMedecins().stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public MedecinStatisticsDTO getMedecinStatisticsByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        List<Medecin> medecinsCabinet = getMedecinsByCabinet(cabinetId);

        return MedecinStatisticsDTO.builder()
                .totalMedecins((long) medecinsCabinet.size())
                .totalActifs(medecinsCabinet.stream().filter(Medecin::getActif).count())
                .totalInactifs(medecinsCabinet.stream().filter(m -> !m.getActif()).count())
                .build();
    }

    @Override
    public MedecinDTO convertToDTO(Medecin medecin) {
        MedecinDTO dto = (MedecinDTO) MedecinDTO.builder()
                .idUser(medecin.getIdUser())
                .nom(medecin.getNom())
                .prenom(medecin.getPrenom())
                .login(medecin.getLogin())
                .cin(medecin.getCin())
                .email(medecin.getEmail())
                .telephone(medecin.getTel())
                .adresse(medecin.getAdresse())
                .dateNaissance(medecin.getDateNaissance())
                .sexe(medecin.getSexe())
                .actif(medecin.getActif())
                .salaire(medecin.getSalaire())
                .prime(medecin.getPrime())
                .dateRecrutement(medecin.getDateRecrutement())
                .soldeConge(medecin.getSoldeConge())
                .cabinetMedicaleId(medecin.getCabinetMedicaleId())
                .specialite(medecin.getSpecialite())
                .typeStaff("MEDECIN")
                .build();

        // Ajouter les rôles
        try {
            List<LibelleRole> roles = userService.getUserRoles(medecin.getIdUser());
            dto.setRoles(roles);
        } catch (Exception e) {
            dto.setRoles(List.of(LibelleRole.MEDECIN));
        }

        return dto;
    }

    @Override
    public List<MedecinDTO> convertToDTOList(List<Medecin> medecins) {
        return medecins.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private void validateCreateMedecinData(CreateMedecinDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation utilisateur
        errors.addAll(UserValidator.validateCreateUser(dto));

        // Validation médecin spécifique
        errors.addAll(MedecinValidator.validateCreateMedecin(dto));

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    private void validateUpdateMedecinData(UpdateMedecinDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation utilisateur
        errors.addAll(UserValidator.validateUpdateUser(dto));

        // Validation spécifique médecin
        if (dto.getSpecialite() != null) {
            errors.addAll(MedecinValidator.validateSpecialite(dto.getSpecialite()));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    private void checkUniqueness(CreateMedecinDTO dto) {
        // Vérification via UserService (login, CIN, email)
        if (!userService.isLoginAvailable(dto.getLogin())) {
            throw new UserAlreadyExistsException("login", dto.getLogin());
        }

        if (!userService.isCinAvailable(dto.getCin())) {
            throw new UserAlreadyExistsException("CIN", dto.getCin());
        }

        // Pas besoin de vérifier l'email car UserService le fait
    }

    private void validateCabinetExists(Long cabinetId) {
        if (cabinetId == null) {
            throw new ValidationException("L'ID du cabinet est obligatoire");
        }

        if (!cabinetRepository.existsById(cabinetId)) {
            throw new CabinetNotFoundException(cabinetId);
        }
    }

    // Dans la méthode createMedecin() :
    private Medecin convertToMedecin(CreateMedecinDTO dto) {
        Medecin medecin = new Medecin();

        // Champs utilisateur (hérités de CreateUserDTO)
        medecin.setNom(dto.getNom());
        medecin.setPrenom(dto.getPrenom());
        medecin.setLogin(dto.getLogin());
        medecin.setCin(dto.getCin());
        medecin.setEmail(dto.getEmail());
        medecin.setTel(dto.getTelephone());
        medecin.setAdresse(dto.getAdresse());
        medecin.setDateNaissance(dto.getDateNaissance());
        medecin.setSexe(dto.getSexe());
        medecin.setActif(dto.isActif());
        medecin.setMotDePass(dto.getPassword());

        // Champs staff (hérités de CreateStaffDTO) ✅
        medecin.setSalaire(dto.getSalaire());
        medecin.setPrime(dto.getPrime());
        medecin.setDateRecrutement(dto.getDateRecrutement());
        medecin.setSoldeConge(dto.getSoldeConge());
        medecin.setCabinetMedicaleId(dto.getCabinetMedicaleId());

        // Champs médecin
        medecin.setSpecialite(dto.getSpecialite());

        return medecin;
    }


    private void setDefaultValues(Medecin medecin) {
        if (medecin.getDateRecrutement() == null) {
            medecin.setDateRecrutement(LocalDate.now());
        }

        if (medecin.getSoldeConge() == null) {
            medecin.setSoldeConge(30); // 30 jours par défaut
        }

        if (medecin.getSalaire() == null) {
            medecin.setSalaire(8000.0); // Salaire par défaut pour médecin
        }
    }

    private void updateUserFields(Medecin medecin, UpdateMedecinDTO dto) {
        if (dto.getNom() != null) medecin.setNom(dto.getNom());
        if (dto.getPrenom() != null) medecin.setPrenom(dto.getPrenom());
        if (dto.getEmail() != null) medecin.setEmail(dto.getEmail());
        if (dto.getTelephone() != null) medecin.setTel(dto.getTelephone());
        if (dto.getAdresse() != null) medecin.setAdresse(dto.getAdresse());
        if (dto.getDateNaissance() != null) medecin.setDateNaissance(dto.getDateNaissance());
        if (dto.getSexe() != null) medecin.setSexe(dto.getSexe());
        if (dto.getActif()) medecin.setActif(dto.getActif());
    }

    private void updateMedecinSpecificFields(Medecin medecin, UpdateMedecinDTO dto) {
        // Mettre à jour la spécialité
        if (dto.getSpecialite() != null && !dto.getSpecialite().trim().isEmpty()) {
            medecin.setSpecialite(dto.getSpecialite());
        }
    }

    private void updateStaffFields(Medecin medecin, UpdateMedecinDTO dto) {
        if (dto.getSalaire() != null) medecin.setSalaire(dto.getSalaire());
        if (dto.getPrime() != null) medecin.setPrime(dto.getPrime());
        if (dto.getDateRecrutement() != null) medecin.setDateRecrutement(dto.getDateRecrutement());
        if (dto.getSoldeConge() != null) medecin.setSoldeConge(dto.getSoldeConge());
        if (dto.getCabinetMedicaleId() != null) medecin.setCabinetMedicaleId(dto.getCabinetMedicaleId());
    }
}
