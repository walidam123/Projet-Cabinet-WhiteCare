package ma.whitecare.repository.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;

import java.util.List;

public class ConsultationRepositoryImpl implements ConsultationRepository {
    @Override
    public List<Consultation> findAll() {
        return List.of();
    }

    @Override
    public Consultation findById(Long aLong) {
        return null;
    }

    @Override
    public void create(Consultation newElement) {

    }

    @Override
    public void update(Consultation newValuesElement) {

    }

    @Override
    public void delete(Consultation oldElement) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
}
