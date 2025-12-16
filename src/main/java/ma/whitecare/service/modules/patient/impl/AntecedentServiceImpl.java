package ma.whitecare.service.modules.patient.impl;

import ma.whitecare.common.exceptions.AntecedentNotFoundException;
import ma.whitecare.common.validators.AntecedentValidator;
import ma.whitecare.entities.enums.NiveauDeRisque;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.mvc.dto.PatientAntecedentDto.AntecedentDto;
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
        Antecedents antecedent = antecedentRepository.findById(id);
        if (antecedent == null) {
            throw new AntecedentNotFoundException(id);
        }
        return antecedent;
    }

    @Override
    public void createAntecedent(Antecedents antecedent) {
        AntecedentDto dto = convertToDto(antecedent);
        AntecedentValidator.validate(dto);
        antecedentRepository.create(antecedent);
    }

    @Override
    public void updateAntecedent(Antecedents antecedent) {
        AntecedentDto dto = convertToDto(antecedent);
        AntecedentValidator.validate(dto);
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

    // ✅ Méthode utilitaire pour convertir un Antecedents en DTO
    private AntecedentDto convertToDto(Antecedents a) {
        AntecedentDto dto = new AntecedentDto();
        dto.setNom(a.getNom());
        dto.setCategorie(a.getCategorie());
        dto.setNiveauDeRisque(a.getNiveauDeRisque());
        dto.setCreePar(a.getCreePar());
        dto.setModifiePar(a.getModifiePar());
        return dto;
    }
}
