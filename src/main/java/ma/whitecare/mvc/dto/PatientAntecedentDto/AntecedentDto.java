package ma.whitecare.mvc.dto.PatientAntecedentDto;

import ma.whitecare.entities.enums.NiveauDeRisque;

public class AntecedentDto {
    private String nom;
    private String categorie;
    private NiveauDeRisque niveauDeRisque;
    private String creePar;
    private String modifiePar;

    // Getters et setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public NiveauDeRisque getNiveauDeRisque() {
        return niveauDeRisque;
    }

    public void setNiveauDeRisque(NiveauDeRisque niveauDeRisque) {
        this.niveauDeRisque = niveauDeRisque;
    }

    public String getCreePar() {
        return creePar;
    }

    public void setCreePar(String creePar) {
        this.creePar = creePar;
    }

    public String getModifiePar() {
        return modifiePar;
    }

    public void setModifiePar(String modifiePar) {
        this.modifiePar = modifiePar;
    }
}
