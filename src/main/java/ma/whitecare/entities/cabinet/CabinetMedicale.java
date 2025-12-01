package ma.whitecare.entities.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.entities.user.Staff;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CabinetMedicale extends BaseEntity {

    private Long id;
    private String nom;
    private String email;
    private String logo;
    private String adresse;
    private String cin;
    private String tel1;
    private String tel2;
    private String siteWeb;
    private String instagram;
    private String facebook;
    private String description;


    private List<Charges> charges;
    private List<Revenues> revenues;
    private List<Statistiques> statistiques;
   private List<Staff> staffList;


    @Override
    public String toString() {
        return String.format(
                "CabinetMedicale{id=%d, nom='%s', email='%s', tel1='%s', adresse='%s', cin='%s'}",
                id != null ? id : 0,
                nom != null ? nom : "N/A",
                email != null ? email : "N/A",
                tel1 != null ? tel1 : "N/A",
                adresse != null ? adresse : "N/A",
                cin != null ? cin : "N/A"
        );
    }
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
