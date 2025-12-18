package ma.whitecare.service.modules.UserManager.api;

import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.mvc.dto.UserDto.CreateSecretaireDTO;
import ma.whitecare.mvc.dto.UserDto.SecretaireDTO;
import ma.whitecare.mvc.dto.UserDto.SecretaireStatisticsDTO;
import ma.whitecare.mvc.dto.UserDto.UpdateSecretaireDTO;

import java.util.List;

public interface SecretaireService {

    // ========== CRUD SECRÉTAIRES ==========
    Secretaire createSecretaire(CreateSecretaireDTO secretaireDTO);
    Secretaire getSecretaireById(Long secretaireId);
    List<Secretaire> getAllSecretaires();
    Secretaire updateSecretaire(Long secretaireId, UpdateSecretaireDTO updateDTO);
    void deleteSecretaire(Long secretaireId);

    // ========== GESTION CABINET ==========
    List<Secretaire> getSecretairesByCabinet(Long cabinetId);
    Long countSecretairesByCabinet(Long cabinetId);
    void assignSecretaireToCabinet(Long secretaireId, Long cabinetId);
    void removeSecretaireFromCabinet(Long secretaireId);

    // ========== GESTION PROFESSIONNELLE ==========
    void updateNumCNSS(Long secretaireId, String numCNSS);
    void updateCommission(Long secretaireId, Double commission);
    void updateNiveauEtude(Long secretaireId, String niveauEtude);


    // ========== GESTION DISPONIBILITÉ ==========
    void updateStatutDisponibilite(Long secretaireId, boolean disponible);

    List<Secretaire> findSecretairesDisponiblesByCabinet(Long cabinetId);

    // ========== RECHERCHE & FILTRES ==========
    List<Secretaire> findByNumeroCNSS(String numeroCNSS);
    List<Secretaire> findByNomPrenom(String nom, String prenom);

    List<Secretaire> findSecretairesWithPagination(int page, int size);



    // ========== STATISTIQUES SECRÉTAIRES ==========

    SecretaireStatisticsDTO getSecretaireStatisticsByCabinet(Long cabinetId);

    // ========== CONVERSIONS ==========
    SecretaireDTO convertToDTO(Secretaire secretaire);
    List<SecretaireDTO> convertToDTOList(List<Secretaire> secretaires);
}
