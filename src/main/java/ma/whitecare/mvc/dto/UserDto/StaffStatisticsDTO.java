package ma.whitecare.mvc.dto.UserDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffStatisticsDTO {

    // Totaux
    private Long totalStaff;
    private Long totalMedecins;
    private Long totalSecretaires;
    private Long totalActifs;
    private Long totalInactifs;

    // Par cabinet
    private Map<Long, Long> staffParCabinet;  // cabinetId -> nombre staff


}
