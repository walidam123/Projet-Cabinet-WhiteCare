package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Prescription extends BaseEntity {




private Long idPr;
private int quantité ;
private String fréquence ;
private int duréeEnJours ;


private Medicament medicament;
private Ordonnance ordonnance;

    @Override
    public int hashCode() {
        return idPr != null ? idPr.hashCode() : 0;
    }
}
