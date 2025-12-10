package ma.whitecare.mvc.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.enums.Sexe;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDTO {

    // Informations héritées d'Utilisateur
    private Long idUser;
    private String nom;
    private String prenom;
    private String login;
    private String cin;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private Boolean actif;
    private List<LibelleRole> roles;

    // Informations spécifiques au Staff
    private Double salaire;
    private Double prime;
    private LocalDate dateRecrutement;
    private Integer soldeConge;
    private Long cabinetMedicaleId;

    // Informations spécifiques selon le type
    private String typeStaff;  // "MEDECIN" ou "SECRETAIRE"
    private String specialite; // Pour médecin
    private String numCNSS;    // Pour secrétaire
    private Double commission; // Pour secrétaire

    // Informations d'audit
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
}
