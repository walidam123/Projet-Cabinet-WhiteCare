package ma.whitecare.repository.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.Prescription;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;

import java.util.List;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {
    @Override
    public List<Prescription> findAll() {
        return List.of();
    }

    @Override
    public Prescription findById(Long aLong) {
        return null;
    }

    @Override
    public void create(Prescription newElement) {

    }

    @Override
    public void update(Prescription newValuesElement) {

    }

    @Override
    public void delete(Prescription oldElement) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
}
