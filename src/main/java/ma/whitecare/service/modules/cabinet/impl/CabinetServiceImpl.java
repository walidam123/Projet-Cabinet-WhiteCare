package ma.whitecare.service.modules.cabinet.impl;

import ma.whitecare.entities.cabinet.CabinetMedicale;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.service.modules.cabinet.api.CabinetService;

import java.util.List;

public class CabinetServiceImpl implements CabinetService {
    private final CabinetMedicaleRepository cabinetRepository;

    public CabinetServiceImpl(CabinetMedicaleRepository cabinetRepository) {
        this.cabinetRepository = cabinetRepository;
    }

    @Override
    public List<CabinetMedicale> getAllCabinets() {
        return cabinetRepository.findAll();
    }

    @Override
    public CabinetMedicale getCabinetById(Long id) {
        return cabinetRepository.findById(id);
    }

    @Override
    public void createCabinet(CabinetMedicale cabinet) {
        cabinetRepository.create(cabinet);
    }

    @Override
    public void updateCabinet(CabinetMedicale cabinet) {
        cabinetRepository.update(cabinet);
    }

    @Override
    public void deleteCabinet(Long id) {
        cabinetRepository.deleteById(id);
    }
}
