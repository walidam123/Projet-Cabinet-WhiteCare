package ma.whitecare.service.modules.cabinet.api;

import ma.whitecare.entities.cabinet.CabinetMedicale;
import java.util.List;

public interface CabinetService {
    List<CabinetMedicale> getAllCabinets();

    CabinetMedicale getCabinetById(Long id);

    void createCabinet(CabinetMedicale cabinet);

    void updateCabinet(CabinetMedicale cabinet);

    void deleteCabinet(Long id);
}
