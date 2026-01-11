package ma.whitecare.service.modules.certificat.api;

import ma.whitecare.entities.medical.Certificat;
import ma.whitecare.mvc.dto.CertificatDto.CertificatDTO;
import ma.whitecare.mvc.dto.CertificatDto.CreateCertificatDTO;
import ma.whitecare.mvc.dto.CertificatDto.UpdateCertificatDTO;

import java.time.LocalDate;
import java.util.List;

public interface CertificatService {

    // ========== CRUD CERTIFICATS ==========
    Certificat createCertificat(CreateCertificatDTO certificatDTO);
    Certificat getCertificatById(Long id);
    List<Certificat> findAll();
    Certificat updateCertificat(Long id, UpdateCertificatDTO updateDTO);
    void deleteCertificat(Long id);

    // ========== RECHERCHES ==========
    boolean existsById(Long id);
    List<Certificat> findByDossierMedicaleId(Long dossierId);
    List<Certificat> findByConsultationId(Long consultationId);
    List<Certificat> findByDateRange(LocalDate startDate, LocalDate endDate);

    // ========== CONVERSIONS ==========
    CertificatDTO convertToDTO(Certificat certificat);
    List<CertificatDTO> convertToDTOList(List<Certificat> certificats);

    // ========== GÉNÉRATION PDF ==========
    byte[] generatePDF(Long certificatId) throws java.io.IOException;
}
