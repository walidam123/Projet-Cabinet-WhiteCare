package ma.whitecare.service.modules.ordonnance.api;


import ma.whitecare.entities.medical.Ordonnance;

import java.time.LocalDate;
import java.util.List;

public interface OrdonnanceService {

    List<Ordonnance> findAll();

    Ordonnance findById(Long id);

    void create(Ordonnance ordonnance);

    void update(Ordonnance ordonnance);

    void delete(Long id);

    boolean existsById(Long id);

    List<Ordonnance> findByDossierMedicaleId(Long dossierId);

    List<Ordonnance> findByConsultationId(Long consultationId);

    List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end);
}

