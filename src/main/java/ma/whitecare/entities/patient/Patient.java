package ma.whitecare.entities.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.Assurance;

@Data @AllArgsConstructor @NoArgsConstructor @SuperBuilder

public class Patient extends BaseEntity {
    private Long id_Patient;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;

    private Sexe sexe;
    private Assurance assurance;

    private List<Antecedents> antecedents=null;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient that = (Patient) o;
        return id_Patient != null && id_Patient.equals(that.id_Patient);
    }

    @Override
    public int hashCode() {
        return id_Patient != null ? id_Patient.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Patient {
          id = %d,
          nom = '%s',
          prenom = '%s',
          adresse = '%s',
          telephone = '%s',
          email = '%s',
          dateNaissance = %s,
          dateCreation = %s,
          sexe = %s,
          assurance = %s,
          antecedentsCount = %d
        }
        """.formatted(
                id_Patient,
                nom,
                prenom,
                adresse,
                telephone,
                email,
                dateNaissance,
                dateCreation,
                sexe,
                assurance,
                antecedents == null ? 0 : antecedents.size()
        );
    }




}
