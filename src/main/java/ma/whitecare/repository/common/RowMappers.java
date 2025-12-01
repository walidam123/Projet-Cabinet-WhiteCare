package ma.whitecare.repository.common;

import ma.whitecare.entities.agenda.AgendaMensuel;
import ma.whitecare.entities.agenda.Creneau;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.entities.cabinet.CabinetMedicale;
import ma.whitecare.entities.cabinet.Statistiques;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.entities.medical.Certificat;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.enums.*;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.user.Role;
import ma.whitecare.entities.user.Utilisateur;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public final class RowMappers {

    private RowMappers() {
    }

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
        patientRow.setSexe(Sexe.valueOf(rs.getString("sexe")));
        patientRow.setAssurance(Assurance.valueOf(rs.getString("assurance")));
        var dc = rs.getTimestamp("creation_date");
        if (dc != null) patientRow.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) patientRow.setDateDerniereModification(dl.toLocalDateTime());
        patientRow.setCreePar(rs.getString("created_by"));
        patientRow.setModifiePar(rs.getString("updated_by"));
        return patientRow;
    }


    public static Antecedents mapAntecedent(ResultSet rs) throws SQLException {
        Antecedents antecedentRow = new Antecedents();

        antecedentRow.setId_Antecedent(rs.getLong("id_antecedent"));
        antecedentRow.setNom(rs.getString("nom"));
        antecedentRow.setCategorie(rs.getString("categorie"));
        antecedentRow.setNiveauDeRisque(NiveauDeRisque.valueOf(rs.getString("niveau_de_risque")));
        var dc = rs.getTimestamp("creation_date");
        if (dc != null) antecedentRow.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) antecedentRow.setDateDerniereModification(dl.toLocalDateTime());
        antecedentRow.setCreePar(rs.getString("created_by"));
        antecedentRow.setModifiePar(rs.getString("updated_by"));
        return antecedentRow;
    }

    public static Revenues mapRevenue(ResultSet rs) throws SQLException {
        Revenues revenueRow = new Revenues();

        revenueRow.setId(rs.getLong("id"));
        revenueRow.setTitre(rs.getString("titre"));
        revenueRow.setDescription(rs.getString("description"));
        revenueRow.setMontant(rs.getDouble("montant"));
        revenueRow.setDate(rs.getTimestamp("date").toLocalDateTime());
        revenueRow.setCabinetMedicaleId(rs.getLong("cabinet_medicale_id"));
        var dc = rs.getTimestamp("creation_date");
        if (dc != null) revenueRow.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) revenueRow.setDateDerniereModification(dl.toLocalDateTime());
        revenueRow.setCreePar(rs.getString("created_by"));
        revenueRow.setModifiePar(rs.getString("updated_by"));
        return revenueRow;
    }

    public static Acte mapActe(ResultSet rs) throws SQLException {
        Acte acteRow = new Acte();

        acteRow.setIdActe(rs.getLong("id"));
        acteRow.setLibelle(rs.getString("nom"));
        acteRow.setCategorie(rs.getString("categorie"));
        acteRow.setPrixDeBase(rs.getDouble("prixDeBase"));

        return acteRow;
    }

    public static CabinetMedicale mapCabinetMedicale(ResultSet rs) throws SQLException {
        CabinetMedicale CabinetRow = new CabinetMedicale();

        CabinetRow.setId(rs.getLong("id"));
        CabinetRow.setNom(rs.getString("nom"));
        CabinetRow.setEmail(rs.getString("email"));
        CabinetRow.setAdresse(rs.getString("adresse"));
        CabinetRow.setCin(rs.getString("cin"));
        CabinetRow.setTel1(rs.getString("tel1"));
        CabinetRow.setTel2(rs.getString("tel2"));
        CabinetRow.setSiteWeb(rs.getString("siteweb"));
        CabinetRow.setInstagram(rs.getString("instagram"));
        CabinetRow.setDescription(rs.getString("description"));
        CabinetRow.setFacebook(rs.getString("facebook"));
        CabinetRow.setDescription(rs.getString("description"));
        var dc = rs.getTimestamp("creation_date");
        if (dc != null) CabinetRow.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) CabinetRow.setDateDerniereModification(dl.toLocalDateTime());
        CabinetRow.setCreePar(rs.getString("created_by"));
        CabinetRow.setModifiePar(rs.getString("updated_by"));

        return CabinetRow;


    }

    public static Certificat mapCertificat(ResultSet rs) throws SQLException {
        Certificat CertificatRow = new Certificat();

        CertificatRow.setIdCertif(rs.getLong("id"));
        var dn = rs.getDate("date_debut");

        if (dn != null) CertificatRow.setDateDebut(dn.toLocalDate());
        var df = rs.getDate("date_fin");

        if (df != null) CertificatRow.setDateFin(df.toLocalDate());
        CertificatRow.setDuree(rs.getInt("prixDeBase"));
        CertificatRow.setNoteMedecin(rs.getString("note_medecin"));
        return CertificatRow;
    }

    public static Medicament mapmedicament(ResultSet rs) throws SQLException {


        Medicament MedicamentRow = new Medicament();


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

    public static Charges mapCharge(ResultSet rs) throws SQLException {
        Charges ChargesRow = new Charges();

        ChargesRow.setId(rs.getLong("id"));
        ChargesRow.setTitre(rs.getString("titre"));
        ChargesRow.setDescription(rs.getString("description"));
        ChargesRow.setMontant(rs.getDouble("montant"));
        ChargesRow.setDate(rs.getTimestamp("date").toLocalDateTime());
        ChargesRow.setCabinetMedicaleId(rs.getLong("cabinet_medicale_id"));
        var dc = rs.getTimestamp("creation_date");
        if (dc != null) ChargesRow.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) ChargesRow.setDateDerniereModification(dl.toLocalDateTime());
        ChargesRow.setCreePar(rs.getString("created_by"));
        ChargesRow.setModifiePar(rs.getString("updated_by"));
        return ChargesRow;
    }

    public static Statistiques mapStatistique(ResultSet rs) throws SQLException {
        return Statistiques.builder()
                .id(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .categorie(CategorieStatistique.valueOf(rs.getString("categorie")))
                .chiffre(rs.getDouble("chiffre"))
                .dateCalcul(rs.getDate("dateCalcul").toLocalDate())
                .cabinetMedicaleId(rs.getLong("cabinet_medicale_id"))
                .dateCreation(LocalDate.from(rs.getTimestamp("creation_date").toLocalDateTime()))
                .dateDerniereModification(rs.getTimestamp("last_modification_date").toLocalDateTime())
                .creePar(rs.getString("created_by"))
                .modifiePar(rs.getString("updated_by"))
                .build();
    }


    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        return AgendaMensuel.builder()
                .id(rs.getLong("id"))
                .mois(Mois.valueOf(rs.getString("mois")))
                .annee(rs.getInt("annee"))
                .medecinId(rs.getLong("medecin_id"))
                .dateCreation(LocalDate.from(rs.getTimestamp("creation_date").toLocalDateTime()))
                .dateDerniereModification(rs.getTimestamp("last_modification_date").toLocalDateTime())
                .creePar(rs.getString("created_by"))
                .modifiePar(rs.getString("updated_by"))
                .build();
    }

    public static Jour mapJourAgenda(ResultSet rs) throws SQLException {
        return Jour.builder()
                .id(rs.getLong("id"))
                .date(rs.getDate("date_jour").toLocalDate())
                .jourSemaine(JourSemaine.valueOf(rs.getString("jour_semaine")))
                .estDisponible(rs.getBoolean("est_disponible"))
                .raisonIndisponibilite(rs.getString("raison_indisponibilite"))
                .build();
    }

    public static Creneau mapCreneauHoraire(ResultSet rs) throws SQLException {
        return Creneau.builder()
                .id(rs.getLong("id"))
                .heureDebut(rs.getTime("heure_debut").toLocalTime())
                .heureFin(rs.getTime("heure_fin").toLocalTime())
                .estDisponible(rs.getBoolean("est_disponible"))
                .motifIndisponibilite(rs.getString("motif_indisponibilite"))
                .rendezVousId(rs.getLong("rendez_vous_id"))
                .build();
    }

    public static Role mapRole(ResultSet rs) throws SQLException {
        return Role.builder()
                .idRole(rs.getLong("id"))
                .libelle(LibelleRole.valueOf(rs.getString("libelle")))
                .dateCreation(LocalDate.from(rs.getTimestamp("creation_date").toLocalDateTime()))
                .dateDerniereModification(rs.getTimestamp("last_modification_date").toLocalDateTime())
                .creePar(rs.getString("created_by"))
                .modifiePar(rs.getString("updated_by"))
                // Les privilèges seront chargés séparément
                .build();
    }

    public static Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
        return Utilisateur.builder()
                .idUser(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .email(rs.getString("email"))
                .adresse(rs.getString("adresse"))
                .cin(rs.getString("cin"))
                .tel(rs.getString("tel"))
                .sexe(Sexe.valueOf(rs.getString("sexe")))
                .login(rs.getString("login"))
                .motDePass(rs.getString("password_hash"))
                .LastLoginDate(rs.getTimestamp("last_login_date") != null ?
                        LocalDate.from(rs.getTimestamp("last_login_date").toLocalDateTime()) : null)
                .dateNaissance(rs.getDate("date_naissance") != null ?
                        rs.getDate("date_naissance").toLocalDate() : null)
                .actif(rs.getBoolean("actif"))
                .dateCreation(LocalDate.from(rs.getTimestamp("creation_date").toLocalDateTime()))
                .dateDerniereModification(rs.getTimestamp("last_modification_date").toLocalDateTime())
                .creePar(rs.getString("created_by"))
                .modifiePar(rs.getString("updated_by"))
                .build();
    }
}
