package ma.whitecare.repository.modules.medicament.impl;

import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.modules.medicament.api.MedicamentRepository;

import java.util.List;

public class MedicamentRepositoryImpl implements MedicamentRepository {

    @Override
    public List<Medicament> findAll() {
        return List.of();
    }

    @Override
    public Medicament findById(Long aLong) {
        return null;
    }

    @Override
    public void create(Medicament newElement) {
        // TODO: Implement JDBC logic
    }

    @Override
    public void update(Medicament newValuesElement) {
        // TODO: Implement JDBC logic
    }

    @Override
    public void delete(Medicament oldElement) {
        // TODO: Implement JDBC logic
    }

    @Override
    public void deleteById(Long aLong) {
        // TODO: Implement JDBC logic
    }

    @Override
    public List<Medicament> findByNom(String nom) {
        return List.of();
    }

    @Override
    public List<Medicament> findByLaboratoire(String laboratoire) {
        return List.of();
    }

    @Override
    public List<Medicament> findByType(String type) {
        return List.of();
    }

    @Override
    public List<Medicament> findByRemboursable(boolean remboursable) {
        return List.of();
    }

    @Override
    public void updatePrix(Long medicamentId, Double nouveauPrix) {
        // TODO: Implement JDBC logic
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

