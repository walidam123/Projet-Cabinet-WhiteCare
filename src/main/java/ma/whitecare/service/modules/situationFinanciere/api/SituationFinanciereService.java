package ma.whitecare.service.modules.situationFinanciere.api;

import ma.whitecare.entities.financial.SituationFinanciere;
import java.util.List;

public interface SituationFinanciereService {
    SituationFinanciere create(SituationFinanciere sf);

    SituationFinanciere update(Long id, SituationFinanciere sf);

    void delete(Long id);

    SituationFinanciere getById(Long id);

    List<SituationFinanciere> getAll();

    SituationFinanciere getByDossierId(Long dossierId);

    void resetSituation(Long id);
}
