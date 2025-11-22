package ma.whitecare.repository.common;

import ma.whitecare.entities.cabinet.CabinetMedicale;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.entities.medical.Certificat;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.enums.*;
import ma.whitecare.entities.patient.Antecedents;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class RowMappers {

    private RowMappers(){}

    public static Patient mapPatient(ResultSet rs) throws SQLException {

        Patient patientRow = new Patient();

        patientRow.setId_Patient(rs.getLong("idPatient"));
        patientRow.setNom(rs.getString("nom"));
        patientRow.setPrenom(rs.getString("prenom"));
        patientRow.setAdresse(rs.getString("adresse"));
        patientRow.setTelephone(rs.getString("telephone"));
        patientRow.setEmail(rs.getString("email"));
        var dn = rs.getDate("dateDeNaissance");
        if (dn != null) patientRow.setDateNaissance(dn.toLocalDate());
        var dc = rs.getTimestamp("creation_date");
        if (dc != null) patientRow.setDateCreation(dc.toLocalDateTime());
        patientRow.setSexe(Sexe.valueOf(rs.getString("sexe")));
        patientRow.setAssurance(Assurance.valueOf(rs.getString("assurance")));

        return patientRow;
    }


    public static Antecedents mapAntecedent(ResultSet rs) throws SQLException {
        Antecedents antecedentRow = new Antecedents();

        antecedentRow.setId_Antecedent            (rs.getLong("id"));
        antecedentRow.setNom            (rs.getString("nom"));
        antecedentRow.setCategorie      (rs.getString("categorie"));
        antecedentRow.setNiveauDeRisque  (NiveauDeRisque.valueOf(rs.getString("niveauRisque")));

        return antecedentRow;
    }


    public static Acte mapActe(ResultSet rs) throws SQLException {
        Acte acteRow = new Acte();

        acteRow.setIdActe            (rs.getLong("id"));
        acteRow.setLibelle            (rs.getString("nom"));
        acteRow.setCategorie      (rs.getString("categorie"));
        acteRow.setPrixDeBase  (rs.getDouble("prixDeBase"));

        return acteRow;
    }
    public static CabinetMedicale mapCabinet(ResultSet rs) throws SQLException {
        CabinetMedicale CabinetRow = new CabinetMedicale();

        CabinetRow.setIdUser            (rs.getLong("id"));
        CabinetRow.setNom          (rs.getString("nom"));
        CabinetRow.setEmail     (rs.getString("email"));
        CabinetRow.setAdresse  (rs.getString("adresse"));
        CabinetRow.setCin  (rs.getString("cin"));
        CabinetRow.setTel1  (rs.getString("tel1"));
        CabinetRow.setSiteWeb  (rs.getString("siteweb"));
        CabinetRow.setInstagram (rs.getString("instagram"));
        CabinetRow.setDescription (rs.getString("description"));

        return CabinetRow;




    }

    public static Certificat mapCertificat(ResultSet rs) throws SQLException {
        Certificat CertificatRow = new Certificat();

        CertificatRow.setIdCertif         (rs.getLong("id"));
        var dn=rs.getDate("date_debut");

        if (dn != null) CertificatRow.setDateDebut(dn.toLocalDate());
        var df=rs.getDate("date_fin");

        if (df != null) CertificatRow.setDateFin(df.toLocalDate());
        CertificatRow.setDuree  (rs.getInt("prixDeBase"));
        CertificatRow.setNoteMedecin  (rs.getString("note_medecin"));
        return CertificatRow;
    }


    public static Medicament mapmedicament(ResultSet rs) throws SQLException {


        Medicament MedicamentRow=new Medicament();




        // Mapping des champs spécifiques à Medicament
        MedicamentRow.setIdMct(rs.getLong("idMct"));
        MedicamentRow.setNom(rs.getString("nom"));
        MedicamentRow.setLaboratoire(rs.getString("laboratoire"));
        MedicamentRow.setType(rs.getString("type"));
        MedicamentRow.setForme(FormeMedicament.valueOf(rs.getString("forme")));
        MedicamentRow.setRemboursable(rs.getBoolean("remboursable"));
        MedicamentRow.setPrixUnitaire(rs.getDouble("prixUnitaire"));
        MedicamentRow.setDescription(rs.getString("Descritpion"));
        var da = rs.getTimestamp("creation_date");
        if (da != null) MedicamentRow.setDateCreation(da.toLocalDateTime().toLocalDate());
        return MedicamentRow;
    }


}
