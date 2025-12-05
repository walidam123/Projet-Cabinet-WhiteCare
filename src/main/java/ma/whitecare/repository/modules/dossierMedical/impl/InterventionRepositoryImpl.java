package ma.whitecare.repository.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.InterventionMedecin;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;

import java.util.List;

public class InterventionRepositoryImpl implements InterventionRepository
{
    @Override
    public List<InterventionMedecin> findAll() {
        return List.of();
    }

    @Override
    public InterventionMedecin findById(Long aLong) {
        return null;
    }

    @Override
    public void create(InterventionMedecin newElement) {

    }

    @Override
    public void update(InterventionMedecin newValuesElement) {

    }

    @Override
    public void delete(InterventionMedecin oldElement) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
}
