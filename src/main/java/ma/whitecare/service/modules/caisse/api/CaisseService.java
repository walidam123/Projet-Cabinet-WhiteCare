package ma.whitecare.service.modules.caisse.api;

import ma.whitecare.mvc.dto.financial.CaisseStatsDTO;
import java.time.LocalDateTime;

public interface CaisseService {
    CaisseStatsDTO getGlobalStats();

    CaisseStatsDTO getStatsByPeriod(LocalDateTime start, LocalDateTime end);

    CaisseStatsDTO getStats();
}
