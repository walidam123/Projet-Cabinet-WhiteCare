package ma.whitecare.service.modules.patient.impl;

import ma.whitecare.entities.enums.NiveauDeRisque;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.repository.modules.patient.api.AntecedentRepository;
import ma.whitecare.service.modules.patient.api.AntecedentService;

import java.util.List;
import java.util.Optional;

public class AntecedentServiceImpl implements AntecedentService {

    private final AntecedentRepository antecedentRepository;

    public AntecedentServiceImpl(AntecedentRepository antecedentRepository) {
        this.antecedentRepository = antecedentRepository;
    }

    @Override
    public List<Antecedents> getAllAntecedents() {
        return antecedentRepository.findAll();
    }

    @Override
    public Antecedents getAntecedentById(Long id) {
        if (!antecedentRepository.existsById(id)) {
            throw new RuntimeException("Antécédent non trouvé");
        }
        return antecedentRepository.findById(id);
    }

    @Override
    public void createAntecedent(Antecedents antecedent) {
        antecedentRepository.create(antecedent);
    }

    @Override
    public void updateAntecedent(Antecedents antecedent) {
        antecedentRepository.update(antecedent);
    }

    @Override
    public void deleteAntecedent(Antecedents antecedent) {
        antecedentRepository.delete(antecedent);
    }

    @Override
    public void deleteAntecedentById(Long id) {
        antecedentRepository.deleteById(id);
    }

    @Override
    public Optional<Antecedents> findByNom(String nom) {
        return antecedentRepository.findByNom(nom);
    }

    @Override
    public List<Antecedents> findByCategorie(String categorie) {
        return antecedentRepository.findByCategorie(categorie);
    }

    @Override
    public List<Antecedents> findByNiveauRisque(NiveauDeRisque niveau) {
        return antecedentRepository.findByNiveauRisque(niveau);
    }

    @Override
    public long count() {
        return antecedentRepository.count();
    }

    @Override
    public List<Antecedents> findPage(int limit, int offset) {
        return antecedentRepository.findPage(limit, offset);
    }
}
