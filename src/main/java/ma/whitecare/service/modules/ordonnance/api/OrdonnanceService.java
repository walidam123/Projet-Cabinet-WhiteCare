package ma.whitecare.service.modules.ordonnance.api;

import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.mvc.dto.OrdannanceDto.CreateOrdonnanceDTO;
import ma.whitecare.mvc.dto.OrdannanceDto.OrdonnanceDTO;
import ma.whitecare.mvc.dto.OrdannanceDto.UpdateOrdonnanceDTO;

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

    List<ma.whitecare.entities.medical.Prescription> getPrescriptionsByOrdonnanceId(Long ordonnanceId);

    // ========== CONVERSIONS ==========
    OrdonnanceDTO convertToDTO(Ordonnance ordonnance);

    List<OrdonnanceDTO> convertToDTOList(List<Ordonnance> ordonnances);

    // ========== GÉNÉRATION PDF ==========
    byte[] generatePDF(Long ordonnanceId) throws java.io.IOException;
}