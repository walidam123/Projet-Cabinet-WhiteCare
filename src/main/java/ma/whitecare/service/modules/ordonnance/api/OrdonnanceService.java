package ma.whitecare.service.modules.ordonnance.api;


import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.mvc.dto.OrdonnanceDto.CreateOrdonnanceDTO;
import ma.whitecare.mvc.dto.OrdonnanceDto.OrdonnanceDTO;
import ma.whitecare.mvc.dto.OrdonnanceDto.UpdateOrdonnanceDTO;

import java.time.LocalDate;
import java.util.List;

public interface OrdonnanceService {

    // ========== CRUD ORDONNANCES ==========
    Ordonnance createOrdonnance(CreateOrdonnanceDTO ordonnanceDTO);
    Ordonnance getOrdonnanceById(Long id);
    List<Ordonnance> findAll();
    Ordonnance updateOrdonnance(Long id, UpdateOrdonnanceDTO updateDTO);
    void deleteOrdonnance(Long id);

    // ========== RECHERCHES ==========
    boolean existsById(Long id);
    List<Ordonnance> findByDossierMedicaleId(Long dossierId);
    List<Ordonnance> findByConsultationId(Long consultationId);
    List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end);

    // ========== CONVERSIONS ==========
    OrdonnanceDTO convertToDTO(Ordonnance ordonnance);
    List<OrdonnanceDTO> convertToDTOList(List<Ordonnance> ordonnances);
}

