package ma.whitecare.service.modules.medicament.impl;


import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.modules.Medicament.MedicamentRepository;
import ma.whitecare.service.modules.medicament.api.MedicamentService;

import java.util.List;
import java.util.Optional;

public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository repo;

    public MedicamentServiceImpl(MedicamentRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Medicament> getAll() {
        return repo.findAll();
    }

    @Override
    public Medicament getById(Long id) {
        return repo.findById(id);
    }

    @Override
    public void create(Medicament medicament) {
        if (medicament == null)
            throw new IllegalArgumentException("Le médicament ne peut pas être null");

        repo.create(medicament);
    }

    @Override
    public void update(Medicament medicament) {
        if (medicament == null || medicament.getIdMct() == null)
            throw new IllegalArgumentException("Médicament invalide pour update");

        repo.update(medicament);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Optional<Medicament> getByExactName(String nom) {
        if (nom == null || nom.isBlank())
            return Optional.empty();

        return repo.findByNomExact(nom);
    }

    @Override
    public List<Medicament> getByLaboratoire(String laboratoire) {
        return repo.findByLaboratoire(laboratoire);
    }

    @Override
    public List<Medicament> getByType(String type) {
        return repo.findByType(type);
    }
}

