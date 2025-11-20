package ma.whitecare.entities.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.entities.user.Staff;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CabinetMedicale extends BaseEntity {

    private Long idUser;
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
}
