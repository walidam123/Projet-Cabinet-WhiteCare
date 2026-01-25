package ma.whitecare.service.modules.situationFinanciere.impl;

import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.enums.StatutSituationFinanciere;
import ma.whitecare.repository.modules.dossierMedical.api.SituationFinanciereRepository;
import ma.whitecare.service.modules.situationFinanciere.api.SituationFinanciereService;

import java.util.List;

public class SituationFinanciereServiceImpl implements SituationFinanciereService {

    private final SituationFinanciereRepository repository;

    public SituationFinanciereServiceImpl(SituationFinanciereRepository repository) {
        this.repository = repository;
    }

    @Override
    public SituationFinanciere create(SituationFinanciere sf) {
        repository.create(sf);
        return sf;
    }

    @Override
    public SituationFinanciere update(Long id, SituationFinanciere sf) {
        if (!repository.existsById(id))
            return null;
        sf.setIdSF(id);
        repository.update(sf);
        return sf;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public SituationFinanciere getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<SituationFinanciere> getAll() {
        return repository.findAll();
    }

    @Override
    public SituationFinanciere getByDossierId(Long dossierId) {
        return repository.findByDossierMedicaleId(dossierId);
    }

    @Override
    public void resetSituation(Long id) {
        SituationFinanciere sf = repository.findById(id);
        if (sf != null) {
            sf.setTotaleDesActes(0.0);
            sf.setTotalePaye(0.0);
            sf.setCredit(0.0);
            sf.setStatut(StatutSituationFinanciere.SOLDE);
            repository.update(sf);
        }
    }
}
