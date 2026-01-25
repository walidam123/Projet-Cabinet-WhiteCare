package ma.whitecare.mvc.dto.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaisseStatsDTO {
    private Double totalRevenue;
    private Double totalCharges;
    private Double totalInvoiced;
    private Double balance;
    private Long transactionCount;
}
