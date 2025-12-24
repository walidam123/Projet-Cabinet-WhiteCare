package ma.whitecare.service.modules.actes.api;

import ma.whitecare.entities.medical.Acte;
import ma.whitecare.mvc.dto.ActeDto.ActeDTO;
import ma.whitecare.mvc.dto.ActeDto.CreateActeDTO;
import ma.whitecare.mvc.dto.ActeDto.UpdateActeDTO;

import java.util.List;
import java.util.Optional;

public interface ActeService {

    // ========== CRUD ACTES ==========
    Acte createActe(CreateActeDTO acteDTO);
    Acte getActeById(Long id);
    List<Acte> getAll();
    Acte updateActe(Long id, UpdateActeDTO updateDTO);
    void deleteActe(Long id);

    // ========== RECHERCHES ==========
    Optional<Acte> getByExactLibelle(String libelle);
    List<Acte> getByLibelle(String libelle);
    List<Acte> getByCategorie(String categorie);
    Long countByCategorie(String categorie);

    // ========== MÉTHODES SPÉCIFIQUES ==========
    void updatePrix(Long acteId, Double nouveauPrix);
    void updateCategorie(Long acteId, String nouvelleCategorie);
    void updateLibelle(Long acteId, String nouveauLibelle);

    // ========== VALIDATION ==========
    boolean isLibelleAvailable(String libelle);

    // ========== CONVERSIONS ==========
    ActeDTO convertToDTO(Acte acte);
    List<ActeDTO> convertToDTOList(List<Acte> actes);
}
