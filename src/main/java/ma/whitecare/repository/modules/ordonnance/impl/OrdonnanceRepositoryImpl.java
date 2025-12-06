package ma.whitecare.repository.modules.ordonnance.impl;

import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.repository.modules.ordonnance.api.OrdonnanceRepository;

import java.time.LocalDate;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

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
        // TODO: Implement JDBC logic
    }

    @Override
    public void update(Ordonnance newValuesElement) {
        // TODO: Implement JDBC logic
    }

    @Override
    public void delete(Ordonnance oldElement) {
        // TODO: Implement JDBC logic
    }

    @Override
    public void deleteById(Long aLong) {
        // TODO: Implement JDBC logic
    }

    @Override
    public List<Ordonnance> findByDossierMedicalId(Long dossierId) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public long count() {
        return 0;
    }
}

