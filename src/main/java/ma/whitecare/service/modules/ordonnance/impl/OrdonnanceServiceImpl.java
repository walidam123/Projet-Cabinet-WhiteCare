package ma.whitecare.service.modules.ordonnance.impl;



import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.repository.modules.Ordonnance.OrdonnanceRepository;
import ma.whitecare.service.modules.ordonnance.api.OrdonnanceService;

import java.time.LocalDate;
import java.util.List;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;

    public OrdonnanceServiceImpl(OrdonnanceRepository ordonnanceRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
    }

    @Override
    public List<Ordonnance> findAll() {
        return ordonnanceRepository.findAll();
    }

    @Override
    public Ordonnance findById(Long id) {
        return ordonnanceRepository.findById(id);
    }

    @Override
    public void create(Ordonnance ordonnance) {
        if (ordonnance == null) {
            throw new IllegalArgumentException("Ordonnance ne peut pas être null");
        }
        ordonnanceRepository.create(ordonnance);
    }

    @Override
    public void update(Ordonnance ordonnance) {
        if (ordonnance == null || ordonnance.getIdOrd() == null) {
            throw new IllegalArgumentException("Ordonnance invalide pour update");
        }
        ordonnanceRepository.update(ordonnance);
    }

    @Override
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("ID est null");
        ordonnanceRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return ordonnanceRepository.existsById(id);
    }

    @Override
    public List<Ordonnance> findByDossierMedicaleId(Long dossierId) {
        return ordonnanceRepository.findByDossierMedicaleId(dossierId);
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        return ordonnanceRepository.findByConsultationId(consultationId);
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        return ordonnanceRepository.findByDateBetween(start, end);
    }
}
