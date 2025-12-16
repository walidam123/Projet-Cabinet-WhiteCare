package ma.whitecare.service.modules.UserManager.api;

import ma.whitecare.entities.user.Staff;
import ma.whitecare.mvc.dto.UserDto.StaffDTO;
import ma.whitecare.mvc.dto.UserDto.StaffStatisticsDTO;
import ma.whitecare.mvc.dto.UserDto.UpdateStaffDTO;

import java.util.List;

public interface StaffService {



    // ========== CRUD GÉNÉRAL ==========
    Staff getStaffById(Long staffId);
    List<Staff> getAllStaff();
    void deleteStaff(Long staffId);

    // ========== GESTION CABINET ==========
    List<Staff> getStaffByCabinet(Long cabinetId);
    List<Staff> getActiveStaffByCabinet(Long cabinetId);
    void assignStaffToCabinet(Long staffId, Long cabinetId);
    Long countStaffByCabinet(Long cabinetId);

    // ========== GESTION RÉMUNÉRATION ==========
    void updateSalaire(Long staffId, Double nouveauSalaire);
    void updatePrime(Long staffId, Double nouvellePrime);
    void updateSoldeConge(Long staffId, Integer nouveauSolde);
    Integer getSoldeConge(Long staffId);

    // ========== MISE À JOUR GÉNÉRALE ==========
    Staff updateStaff(Long staffId, UpdateStaffDTO updateDTO);



    // ========== STATISTIQUES ==========
    StaffStatisticsDTO getStaffStatistics();
    StaffStatisticsDTO getStaffStatisticsByCabinet(Long cabinetId);

    // ========== RECHERCHE ==========
    List<Staff> findStaffByCabinetWithPagination(Long cabinetId, int page, int size);

    // ========== CONVERSIONS ==========
    StaffDTO convertToDTO(Staff staff);
    List<StaffDTO> convertToDTOList(List<Staff> staffList);
}
