package ma.whitecare.repository.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;

import java.util.List;

public class DossierMedicalRepositoryImpl implements DossierMedicalRepository {


    @Override
    public List<DossierMedicale> findAll() {
        return List.of();
    }

    @Override
    public DossierMedicale findById(Long aLong) {
        return null;
    }

    @Override
    public void create(DossierMedicale newElement) {

    }

    @Override
    public void update(DossierMedicale newValuesElement) {

    }

    @Override
    public void delete(DossierMedicale oldElement) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
}
