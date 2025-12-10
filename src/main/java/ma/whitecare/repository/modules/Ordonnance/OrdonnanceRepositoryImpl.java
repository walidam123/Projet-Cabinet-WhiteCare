package ma.whitecare.repository.modules.Ordonnance;

import ma.whitecare.entities.medical.Ordonnance;

import java.time.LocalDate;
import java.util.List;

public class OrdonnanceRepositoryImpl implements  OrdonnanceRepository {
    @Override
    public List<Ordonnance> findAll() {
        return List.of();
    }

    @Override
    public Ordonnance findById(Long aLong) {
        return null;
    }

    @Override
    public void create(Ordonnance newElement) {

    }

    @Override
    public void update(Ordonnance newValuesElement) {

    }

    @Override
    public void delete(Ordonnance oldElement) {

    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public List<Ordonnance> findByDossierMedicaleId(Long dossierId) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }
}
