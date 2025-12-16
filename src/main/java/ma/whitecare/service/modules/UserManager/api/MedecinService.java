package ma.whitecare.service.modules.UserManager.api;

import ma.whitecare.entities.user.Medecin;
import ma.whitecare.mvc.dto.UserDto.CreateMedecinDTO;
import ma.whitecare.mvc.dto.UserDto.MedecinDTO;
import ma.whitecare.mvc.dto.UserDto.MedecinStatisticsDTO;
import ma.whitecare.mvc.dto.UserDto.UpdateMedecinDTO;

import java.util.List;

public interface MedecinService {

    // ========== CRUD MÉDECINS ==========
    Medecin createMedecin(CreateMedecinDTO medecinDTO);
    Medecin getMedecinById(Long medecinId);
    List<Medecin> getAllMedecins();
    Medecin updateMedecin(Long medecinId, UpdateMedecinDTO updateDTO);
    void deleteMedecin(Long medecinId);

    // ========== GESTION CABINET ==========
    List<Medecin> getMedecinsByCabinet(Long cabinetId);
    Long countMedecinsByCabinet(Long cabinetId);
    void assignMedecinToCabinet(Long medecinId, Long cabinetId);
    void removeMedecinFromCabinet(Long medecinId);

    // ========== GESTION SPÉCIALITÉS ==========
    void updateSpecialite(Long medecinId, String nouvelleSpecialite);
    List<Medecin> findMedecinsBySpecialite(String specialite);




    // ========== GESTION DISPONIBILITÉ ==========
    void updateDisponibilite(Long medecinId, boolean disponible);

    List<Medecin> findMedecinsDisponiblesByCabinet(Long cabinetId);

    // ========== RECHERCHE & FILTRES ==========



    List<Medecin> findMedecinsByNomPrenom(String nom, String prenom);

    List<Medecin> findMedecinsWithPagination(int page, int size);

    // ========== STATISTIQUES MÉDECINS ==========

    MedecinStatisticsDTO getMedecinStatisticsByCabinet(Long cabinetId);

    // ========== CONVERSIONS ==========
    MedecinDTO convertToDTO(Medecin medecin);
    List<MedecinDTO> convertToDTOList(List<Medecin> medecins);
}
