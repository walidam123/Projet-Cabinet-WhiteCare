package ma.whitecare.entities.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.Assurance;

@Data @AllArgsConstructor @NoArgsConstructor @Builder

public class Patient extends BaseEntity {
    private Long id_Patient;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private LocalDateTime dateCreation;
    private Sexe sexe;
    private Assurance assurance;

    //private List<Antecedents> antecedents;
}
