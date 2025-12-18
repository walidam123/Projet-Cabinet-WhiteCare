package ma.whitecare.mvc.dto.UserDto;

import lombok.*;
import ma.whitecare.entities.enums.LibelleRole;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long hommes;
    private long femmes;
    private Map<LibelleRole, Long> usersByRole;
    private Map<Integer, Long> usersByAgeGroup;
    private Map<Integer, Long> registrationsByMonth;
    private Map<Integer, Long> registrationsByYear;
}