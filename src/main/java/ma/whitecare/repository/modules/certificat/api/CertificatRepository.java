package ma.whitecare.repository.modules.certificat.api;

import ma.whitecare.entities.medical.Certificat;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CertificatRepository extends CrudRepository<Certificat,Long> {



    List<Certificat> findByDossierMedicaleId(Long dossierId);


    List<Certificat> findByConsultationId(Long consultationId);


    boolean existsById(Long id);
}
