package ma.whitecare.service.modules.UserManager.impl;

import ma.whitecare.common.exceptions.CabinetNotFoundException;
import ma.whitecare.common.exceptions.StaffNotFoundException;
import ma.whitecare.common.validators.StaffValidator;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.entities.user.Staff;
import ma.whitecare.mvc.dto.*;
import ma.whitecare.repository.modules.UserManager.api.StaffRepository;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.service.modules.UserManager.api.StaffService;
import ma.whitecare.service.modules.UserManager.api.UserService;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserService userService;
    private final CabinetMedicaleRepository cabinetRepository;
    public StaffServiceImpl(StaffRepository staffRepository, UserService userService,CabinetMedicaleRepository cabinetRepository) {
        this.staffRepository = staffRepository;
        this.userService = userService;
        this.cabinetRepository=cabinetRepository;
    }


    // ========== CRUD GÉNÉRAL ==========


    @Override
    public Staff getStaffById(Long staffId) {
        Staff staff = staffRepository.findById(staffId);
        if (staff == null) {
            throw new StaffNotFoundException(staffId);
        }
        return staff;
    }

    @Override
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @Override
    public void deleteStaff(Long staffId) {
        // Vérifier que le staff existe
        if (staffRepository.findById(staffId) == null) {
            throw new StaffNotFoundException(staffId);
        }

        // Supprimer le staff (la suppression cascade gère l'utilisateur)
        staffRepository.deleteById(staffId);
    }

    // ========== GESTION CABINET ==========

    @Override
    public List<Staff> getStaffByCabinet(Long cabinetId) {

        validateCabinetExists(cabinetId);

        return staffRepository.findByCabinetMedicaleId(cabinetId);
    }

    @Override
    public List<Staff> getActiveStaffByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);

        return staffRepository.findByCabinetAndActif(cabinetId, true);
    }

    @Override
    public void assignStaffToCabinet(Long staffId, Long cabinetId) {
        // Vérifier que le staff existe
        Staff staff = getStaffById(staffId);

        validateCabinetExists(cabinetId);

        // Assigner au cabinet
        staffRepository.assignToCabinet(staffId, cabinetId);
    }

    @Override
    public Long countStaffByCabinet(Long cabinetId) {
        return staffRepository.countStaffByCabinet(cabinetId);
    }

    // ========== GESTION RÉMUNÉRATION ==========

    @Override
    public void updateSalaire(Long staffId, Double nouveauSalaire) {
        // Validation
        List<String> errors = StaffValidator.validateSalaire(nouveauSalaire);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier que le staff existe
        getStaffById(staffId);

        // Mettre à jour le salaire
        staffRepository.updateSalaire(staffId, nouveauSalaire);
    }

    @Override
    public void updatePrime(Long staffId, Double nouvellePrime) {
        // Validation
        List<String> errors = StaffValidator.validatePrime(nouvellePrime);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier que le staff existe
        getStaffById(staffId);

        // Mettre à jour la prime
        staffRepository.updatePrime(staffId, nouvellePrime);
    }

    @Override
    public void updateSoldeConge(Long staffId, Integer nouveauSolde) {
        // Validation
        List<String> errors = StaffValidator.validateSoldeConge(nouveauSolde);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Vérifier que le staff existe
        getStaffById(staffId);

        // Mettre à jour le solde de congé
        staffRepository.updateSoldeConge(staffId, nouveauSolde);
    }

    @Override
    public Integer getSoldeConge(Long staffId) {
        Staff staff = getStaffById(staffId);
        return staff.getSoldeConge();
    }

    @Override
    public Staff updateStaff(Long staffId, UpdateStaffDTO updateDTO) {
        // Récupérer le staff existant
        Staff staff = getStaffById(staffId);

        // Validation des données de mise à jour
        validateUpdateStaffData(updateDTO);

        // Mettre à jour les champs staff
        if (updateDTO.getSalaire() != null) {
            staff.setSalaire(updateDTO.getSalaire());
        }

        if (updateDTO.getPrime() != null) {
            staff.setPrime(updateDTO.getPrime());
        }

        if (updateDTO.getDateRecrutement() != null) {
            staff.setDateRecrutement(updateDTO.getDateRecrutement());
        }

        if (updateDTO.getSoldeConge() != null) {
            staff.setSoldeConge(updateDTO.getSoldeConge());
        }

        if (updateDTO.getCabinetMedicaleId() != null) {
            staff.setCabinetMedicaleId(updateDTO.getCabinetMedicaleId());
        }

        // Mettre à jour les champs spécifiques selon le type
        updateSpecificStaffFields(staff,  updateDTO);

        // Sauvegarder les modifications
        staffRepository.update(staff);

        return staff;
    }

    @Override
    public StaffStatisticsDTO getStaffStatistics() {
        return null;
    }

    @Override
    public StaffStatisticsDTO getStaffStatisticsByCabinet(Long cabinetId) {
        List<Staff> cabinetStaff = getStaffByCabinet(cabinetId);
        List<Medecin> medecins = staffRepository.findMedecinsByCabinet(cabinetId);
        List<Secretaire> secretaires = staffRepository.findSecretairesByCabinet(cabinetId);

        return StaffStatisticsDTO.builder()
                .totalStaff((long) cabinetStaff.size())
                .totalMedecins((long) medecins.size())
                .totalSecretaires((long) secretaires.size())
                .totalActifs((long) cabinetStaff.stream().filter(Staff::getActif).count())
                .totalInactifs((long) cabinetStaff.stream().filter(staff -> !staff.getActif()).count())

                .build();
    }

    // ========== RECHERCHE ==========

    @Override
    public List<Staff> findStaffByCabinetWithPagination(Long cabinetId, int page, int size) {
        int offset = page * size;
        return staffRepository.findPageByCabinet(cabinetId, size, offset);
    }

    // ========== CONVERSIONS ==========

    @Override
    public StaffDTO convertToDTO(Staff staff) {
        StaffDTO dto = StaffDTO.builder()
                .idUser(staff.getIdUser())
                .nom(staff.getNom())
                .prenom(staff.getPrenom())
                .login(staff.getLogin())
                .cin(staff.getCin())
                .email(staff.getEmail())
                .telephone(staff.getTel())
                .adresse(staff.getAdresse())
                .dateNaissance(staff.getDateNaissance())
                .sexe(staff.getSexe())
                .actif(staff.getActif())
                .salaire(staff.getSalaire())
                .prime(staff.getPrime())
                .dateRecrutement(staff.getDateRecrutement())
                .soldeConge(staff.getSoldeConge())
                .cabinetMedicaleId(staff.getCabinetMedicaleId())
                .build();

        // Récupérer les rôles
        try {
            List<LibelleRole> roles = userService.getUserRoles(staff.getIdUser());
            dto.setRoles(roles);
        } catch (Exception e) {
            dto.setRoles(Collections.emptyList());
        }

        // Déterminer le type et ajouter les champs spécifiques
        if (staff instanceof Medecin) {
            Medecin medecin = (Medecin) staff;
            dto.setTypeStaff("MEDECIN");
            dto.setSpecialite(medecin.getSpecialite());
        } else if (staff instanceof Secretaire) {
            Secretaire secretaire = (Secretaire) staff;
            dto.setTypeStaff("SECRETAIRE");
            dto.setNumCNSS(secretaire.getNumCNSS());
            dto.setCommission(secretaire.getCommission());
        }

        return dto;
    }

    @Override
    public List<StaffDTO> convertToDTOList(List<Staff> staffList) {
        return staffList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES PRIVÉES ==========

    private void validateUpdateStaffData(UpdateStaffDTO updateDTO) {
        List<String> errors = StaffValidator.validateUpdateStaff(updateDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    private void updateSpecificStaffFields(Staff staff, UpdateStaffDTO updateDTO) {
        if (staff instanceof Medecin && updateDTO instanceof UpdateMedecinDTO) {
            Medecin medecin = (Medecin) staff;
            UpdateMedecinDTO medecinDTO = (UpdateMedecinDTO) updateDTO;

            if (medecinDTO.getSpecialite() != null) {
                medecin.setSpecialite(medecinDTO.getSpecialite());
            }
        } else if (staff instanceof Secretaire && updateDTO instanceof UpdateSecretaireDTO) {
            Secretaire secretaire = (Secretaire) staff;
            UpdateSecretaireDTO secretaireDTO = (UpdateSecretaireDTO) updateDTO;

            if (secretaireDTO.getNumCNSS() != null) {
                secretaire.setNumCNSS(secretaireDTO.getNumCNSS());
            }
            if (secretaireDTO.getCommission() != null) {
                secretaire.setCommission(secretaireDTO.getCommission());
            }
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
}
