package ma.whitecare.repository.common;

import ma.whitecare.entities.agenda.AgendaMensuel;
import ma.whitecare.entities.agenda.Creneau;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.entities.cabinet.CabinetMedicale;
import ma.whitecare.entities.cabinet.Statistiques;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.medical.*;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.enums.*;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.user.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
        if (dl != null) patientRow.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
        patientRow.setCreePar(rs.getString("created_by"));
        patientRow.setModifiePar(rs.getString("updated_by"));
        return patientRow;
    }

    public static Consultation mapConsultation(ResultSet rs) throws SQLException {
        Consultation consultation = new Consultation();
        consultation.setIdConsultation(rs.getLong("id_consultation"));
        var date = rs.getDate("date");
        if (date != null) {
            consultation.setDate(date.toLocalDate());
        }
        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            consultation.setStatut(StatutConsultation.valueOf(statutStr));
        }
        consultation.setObservationMedecin(rs.getString("observation_medecin"));

        // Mapping de l'ID du dossier médical
        Long dossierId = getLongSafe(rs, "dossier_medicale_id");
        if (dossierId != null) {
            DossierMedicale dossier = new DossierMedicale();
            dossier.setIdDM(dossierId);
            consultation.setDossierMedicale(dossier);
        }

        var dc = rs.getTimestamp("creation_date");
        if (dc != null) consultation.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) consultation.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
        consultation.setCreePar(rs.getString("created_by"));
        consultation.setModifiePar(rs.getString("updated_by"));
        return consultation;
    }

    public static InterventionMedecin mapInterventionMedecin(ResultSet rs) throws SQLException {
        InterventionMedecin intervention = new InterventionMedecin();
        intervention.setIdIM(rs.getLong("id_im"));
        intervention.setPrixDePatient(rs.getDouble("prix_de_patient"));
        int numDent = rs.getInt("num_dent");
        intervention.setNumDent(rs.wasNull() ? null : numDent);

        // Mapping des IDs des relations
        Long consultationId = getLongSafe(rs, "consultation_id");
        if (consultationId != null) {
            Consultation consultation = new Consultation();
            consultation.setIdConsultation(consultationId);
            intervention.setConsultation(consultation);
        }

        Long acteId = getLongSafe(rs, "acte_id");
        if (acteId != null) {
            Acte acte = new Acte();
            acte.setIdActe(acteId);
            intervention.setActe(acte);
        }

        var dc = rs.getTimestamp("creation_date");
        if (dc != null) intervention.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) intervention.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
        intervention.setCreePar(rs.getString("created_by"));
        intervention.setModifiePar(rs.getString("updated_by"));
        return intervention;
    }

    public static DossierMedicale mapDossierMedicale(ResultSet rs) throws SQLException {
        DossierMedicale dossier = new DossierMedicale();
        dossier.setIdDM(rs.getLong("idDM"));
        var dateCreation = rs.getDate("dateDecreation");
        if (dateCreation != null) {
            dossier.setDateDeCreation(dateCreation.toLocalDate());
        }

        // Mapping des IDs des relations pour éviter les nulls lors des updates
        Long patientId = getLongSafe(rs, "patient_id");
        if (patientId != null) {
            Patient patient = new Patient();
            patient.setId_Patient(patientId);
            dossier.setPatient(patient);
        }

        Long medecinId = getLongSafe(rs, "medecin_id");
        if (medecinId != null) {
            Medecin medecin = new Medecin();
            medecin.setIdUser(medecinId);
            dossier.setMedecin(medecin);
        }

        var dc = rs.getTimestamp("creation_date");
        if (dc != null) dossier.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) dossier.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
        dossier.setCreePar(rs.getString("created_by"));
        dossier.setModifiePar(rs.getString("updated_by"));
        return dossier;
    }

    public static Prescription mapPrescription(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription();
        prescription.setIdPr(rs.getLong("idPr"));
        prescription.setQuantite(rs.getInt("quantite"));
        prescription.setFrequence(rs.getString("frequence"));
        prescription.setDureeEnJours(rs.getInt("dureeEnjours"));

        // Mapping des IDs des relations
        Long medicamentId = getLongSafe(rs, "medicament_id");
        if (medicamentId != null) {
            Medicament medicament = new Medicament();
            medicament.setIdMct(medicamentId);
            prescription.setMedicament(medicament);
        }

        Long ordonnanceId = getLongSafe(rs, "ordonnance_id");
        if (ordonnanceId != null) {
            Ordonnance ordonnance = new Ordonnance();
            ordonnance.setIdOrd(ordonnanceId);
            prescription.setOrdonnance(ordonnance);
        }

        var dc = rs.getTimestamp("creation_date");
        if (dc != null) prescription.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) prescription.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
        prescription.setCreePar(rs.getString("created_by"));
        prescription.setModifiePar(rs.getString("updated_by"));
        return prescription;
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
        if (dl != null) antecedentRow.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
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

        // Mapping des IDs des relations
        Long CabinetMedicaleId = rs.getLong("cabinet_medicale_id");
        if (CabinetMedicaleId != null) {
            CabinetMedicale Cabinet = new CabinetMedicale();
            Cabinet.setId(CabinetMedicaleId);
            revenueRow.setCabinet(Cabinet);
        }

        var dc = rs.getTimestamp("creation_date");
        if (dc != null) revenueRow.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) revenueRow.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
        revenueRow.setCreePar(rs.getString("created_by"));
        revenueRow.setModifiePar(rs.getString("updated_by"));
        return revenueRow;
    }

    public static Acte mapActeComplet(ResultSet rs) throws SQLException {
        Acte acteRow = new Acte();

        acteRow.setIdActe(rs.getLong("idActe"));
        acteRow.setLibelle(rs.getString("libelle"));
        acteRow.setCategorie(rs.getString("categorie"));
        acteRow.setPrixDeBase(rs.getDouble("prixDeBase"));
        var dc = rs.getTimestamp("creation_date");
        if (dc != null) acteRow.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) acteRow.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
        acteRow.setCreePar(rs.getString("created_by"));
        acteRow.setModifiePar(rs.getString("updated_by"));

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
        if (dl != null) CabinetRow.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
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
        Long cabinetId = getLong(rs, "cabinet_medicale_id");
        if (cabinetId != null) {
            CabinetMedicale cabinet = new CabinetMedicale();
            cabinet.setId(cabinetId);
            ChargesRow.setCabinet(cabinet);
        }
        var dc = rs.getTimestamp("creation_date");
        if (dc != null) ChargesRow.setDateCreation(LocalDate.from(dc.toLocalDateTime()));
        var dl = rs.getTimestamp("last_modification_date");
        if (dl != null) ChargesRow.setDateDerniereModification(LocalDate.from(dl.toLocalDateTime()));
        ChargesRow.setCreePar(rs.getString("created_by"));
        ChargesRow.setModifiePar(rs.getString("updated_by"));
        return ChargesRow;
    }

    public static Statistiques mapStatistique(ResultSet rs) throws SQLException {

        Statistiques StatRow=new Statistiques();
        StatRow.setId(rs.getLong("id"));
        StatRow.setNom(rs.getString("nom"));
        StatRow.setCategorie(CategorieStatistique.valueOf(rs.getString("categorie")));
        StatRow.setChiffre(rs.getDouble("chiffre"));
        StatRow.setDateCalcul(rs.getDate("dateCalcul").toLocalDate());
        Long cabinetId = getLong(rs, "cabinet_medicale_id");
        if (cabinetId != null) {
            CabinetMedicale cabinet = new CabinetMedicale();
            cabinet.setId(cabinetId);
            StatRow.setCabinet(cabinet);
        }
        StatRow.setDateCreation(LocalDate.from(rs.getTimestamp("creation_date").toLocalDateTime()));
        StatRow.setDateDerniereModification(LocalDate.from(rs.getTimestamp("last_modification_date").toLocalDateTime()));
        StatRow.setCreePar(rs.getString("created_by"));
        StatRow.setModifiePar(rs.getString("updated_by"));

        return StatRow;
    }


    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        return AgendaMensuel.builder()
                .id(rs.getLong("id"))
                .mois(Mois.valueOf(rs.getString("mois")))
                .annee(rs.getInt("annee"))
                .medecinId(rs.getLong("medecin_id"))
                .dateCreation(LocalDate.from(rs.getTimestamp("creation_date").toLocalDateTime()))
                .dateDerniereModification(LocalDate.from(rs.getTimestamp("last_modification_date").toLocalDateTime()))
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
                .dateDerniereModification(LocalDate.from(rs.getTimestamp("last_modification_date").toLocalDateTime()))
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
                .dateCreation(rs.getTimestamp("creation_date").toLocalDateTime().toLocalDate())
                .dateDerniereModification(rs.getTimestamp("last_modification_date").toLocalDateTime().toLocalDate())
                .creePar(rs.getString("created_by"))
                .modifiePar(rs.getString("updated_by"))
                .build();
    }




    // === MÉTHODE POUR COPIER LES PROPRIÉTÉS UTILISATEUR ===
    private static void copierProprietesUtilisateur(Utilisateur dest, Utilisateur source) {
        dest.setIdUser(source.getIdUser());
        dest.setNom(source.getNom());
        dest.setPrenom(source.getPrenom());
        dest.setEmail(source.getEmail());
        dest.setAdresse(source.getAdresse());
        dest.setCin(source.getCin());
        dest.setTel(source.getTel());
        dest.setSexe(source.getSexe());
        dest.setLogin(source.getLogin());
        dest.setMotDePass(source.getMotDePass());
        dest.setLastLoginDate(source.getLastLoginDate());
        dest.setDateNaissance(source.getDateNaissance());
        dest.setDateCreation(source.getDateCreation());
        dest.setDateDerniereModification(source.getDateDerniereModification());
        dest.setCreePar(source.getCreePar());
        dest.setModifiePar(source.getModifiePar());
        dest.setActif(source.getActif());
    }

    // === MÉTHODE POUR COPIER LES PROPRIÉTÉS STAFF ===
    private static void copierProprietesStaff(Staff dest, Staff source) {
        copierProprietesUtilisateur(dest, source);
        dest.setSalaire(source.getSalaire());
        dest.setPrime(source.getPrime());
        dest.setDateRecrutement(source.getDateRecrutement());
        dest.setSoldeConge(source.getSoldeConge());
        dest.setCabinetMedicaleId(source.getCabinetMedicaleId());
    }

    // === MAPPER POUR MÉDECIN COMPLET ===
    public static Medecin mapMedecinComplet(ResultSet rs) throws SQLException {
        // Créer le médecin directement
        Medecin medecin = new Medecin();

        // Copier les propriétés Utilisateur
        Utilisateur utilisateur = mapUtilisateur(rs);
        copierProprietesUtilisateur(medecin, utilisateur);

        // Propriétés Staff
        medecin.setSalaire(getDoubleSafe(rs, "salaire"));
        medecin.setPrime(getDoubleSafe(rs, "prime"));
        medecin.setDateRecrutement(getLocalDateSafe(rs, "date_recrutement"));
        medecin.setSoldeConge(getIntegerSafe(rs, "solde_conge"));

        // Cabinet médical


            medecin.setCabinetMedicaleId(getLongSafe(rs, "cabinet_medicale_id"));


        // Propriétés spécifiques Médecin
        medecin.setSpecialite(rs.getString("specialite"));

        return medecin;
    }

    // === MAPPER POUR SECRÉTAIRE COMPLET ===
    public static Secretaire mapSecretaireComplet(ResultSet rs) throws SQLException {
        // Créer la secrétaire directement
        Secretaire secretaire = new Secretaire();

        // Copier les propriétés Utilisateur
        Utilisateur utilisateur = mapUtilisateur(rs);
        copierProprietesUtilisateur(secretaire, utilisateur);

        // Propriétés Staff
        secretaire.setSalaire(getDoubleSafe(rs, "salaire"));
        secretaire.setPrime(getDoubleSafe(rs, "prime"));
        secretaire.setDateRecrutement(getLocalDateSafe(rs, "date_recrutement"));
        secretaire.setSoldeConge(getIntegerSafe(rs, "solde_conge"));

        // Cabinet médical
        secretaire.setCabinetMedicaleId(getLongSafe(rs, "cabinet_medicale_id"));


        // Propriétés spécifiques Secrétaire
        secretaire.setNumCNSS(rs.getString("num_cnss"));
        secretaire.setCommission(getDoubleSafe(rs, "commission"));

        return secretaire;
    }

    // === MAPPER POUR MÉDECIN DIRECT (sans jointure) ===
    public static Medecin mapMedecinDirect(ResultSet rs) throws SQLException {
        Medecin medecin = new Medecin();
        medecin.setIdUser(getLong(rs, "id"));
        medecin.setSpecialite(rs.getString("specialite"));
        return medecin;
    }

    // === MAPPER POUR SECRÉTAIRE DIRECT (sans jointure) ===
    public static Secretaire mapSecretaireDirect(ResultSet rs) throws SQLException {
        Secretaire secretaire = new Secretaire();
        secretaire.setIdUser(getLong(rs, "id"));
        secretaire.setNumCNSS(rs.getString("num_cnss"));
        secretaire.setCommission(getDoubleSafe(rs, "commission"));
        return secretaire;
    }

    // === MAPPER GÉNÉRIQUE POUR DÉTECTER LE TYPE ===
    public static Utilisateur mapStaffGenerique(ResultSet rs) throws SQLException {
        boolean isMedecin = rs.getString("specialite") != null;
        boolean isSecretaire = rs.getString("num_cnss") != null;

        if (isMedecin) {
            return mapMedecinComplet(rs);
        } else if (isSecretaire) {
            return mapSecretaireComplet(rs);
        }

        // Si ni médecin ni secrétaire, retourner juste un Utilisateur
        return mapUtilisateur(rs);
    }


    public static Medicament mapResultSetToMedicament(ResultSet rs) throws SQLException {
        Medicament medicament = new Medicament();

        medicament.setIdMct(rs.getLong("idMct"));
        medicament.setNom(rs.getString("nom"));
        medicament.setLaboratoire(rs.getString("laboratoire"));
        medicament.setType(rs.getString("type"));

        String formeStr = rs.getString("forme");
        if (formeStr != null) {
            try {
                medicament.setForme(FormeMedicament.valueOf(formeStr));
            } catch (IllegalArgumentException e) {
                medicament.setForme(null);
            }
        }

        medicament.setRemboursable(rs.getBoolean("remboursable"));
        medicament.setPrixUnitaire(rs.getDouble("prixUnitaire"));
        medicament.setDescription(rs.getString("description"));

        // Champs d'audit
        medicament.setDateCreation(rs.getTimestamp("creation_date").toLocalDateTime().toLocalDate());
        Timestamp modifDate = rs.getTimestamp("last_modification_date");
        if (modifDate != null) {
            medicament.setDateDerniereModification(modifDate.toLocalDateTime().toLocalDate());
        }
        medicament.setCreePar(rs.getString("created_by"));
        medicament.setModifiePar(rs.getString("updated_by"));

        return medicament;
    }

    public static Certificat mapResultSetToCertificat(ResultSet rs) throws SQLException {
        Certificat certificat = new Certificat();

        // Champs primaires
        certificat.setIdCertif(rs.getLong("id_certif"));

        // Dates
        java.sql.Date dateDebutSql = rs.getDate("date_debut");
        if (dateDebutSql != null) {
            certificat.setDateDebut(dateDebutSql.toLocalDate());
        }

        java.sql.Date dateFinSql = rs.getDate("date_fin");
        if (dateFinSql != null) {
            certificat.setDateFin(dateFinSql.toLocalDate());
        }

        // Durée
        certificat.setDuree(rs.getInt("duree"));

        // Note médecin
        certificat.setNoteMedecin(rs.getString("note_medecin"));

        // Relations (créer des objets minimaux avec juste les IDs)
        Long dossierId = rs.getLong("dossier_medicale_id");
        if (!rs.wasNull()) {
            DossierMedicale dossier = new DossierMedicale();
            dossier.setIdDM(dossierId);
            certificat.setDossierMedicale(dossier);
        }

        Long consultationId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation consultation = new Consultation();
            consultation.setIdConsultation(consultationId);
            certificat.setConsultation(consultation);
        }

        // Champs d'audit
        Timestamp creationDate = rs.getTimestamp("creation_date");
        if (creationDate != null) {
            certificat.setDateCreation(creationDate.toLocalDateTime().toLocalDate());
        }

        Timestamp modifDate = rs.getTimestamp("last_modification_date");
        if (modifDate != null) {
            certificat.setDateDerniereModification(modifDate.toLocalDateTime().toLocalDate());
        }

        certificat.setCreePar(rs.getString("created_by"));
        certificat.setModifiePar(rs.getString("updated_by"));



        return certificat;
    }



    private static Long getLong(ResultSet rs, String column) throws SQLException {
        return rs.getLong(column);
    }

    private static Long getLongSafe(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private static Double getDoubleSafe(ResultSet rs, String column) throws SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }

    private static Integer getIntegerSafe(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private static LocalDate getLocalDateSafe(ResultSet rs, String column) throws SQLException {
        java.sql.Date date = rs.getDate(column);
        return date != null ? date.toLocalDate() : null;
    }

    private static LocalDateTime getLocalDateTimeSafe(ResultSet rs, String column) throws SQLException {
        java.sql.Timestamp timestamp = rs.getTimestamp(column);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    private static LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        java.sql.Timestamp timestamp = rs.getTimestamp(column);
        return timestamp.toLocalDateTime();
    }

    public static Facture mapFacture(ResultSet rs) throws SQLException {
        Facture facture = new Facture();
        
        facture.setIdFature(rs.getLong("id_facture"));
        facture.setTotaleFacture(getDoubleSafe(rs, "totale_facture"));
        facture.setTotalePayé(getDoubleSafe(rs, "totale_paye"));
        facture.setReste(getDoubleSafe(rs, "reste"));
        
        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            try {
                facture.setStatut(StatutFacture.valueOf(statutStr));
            } catch (IllegalArgumentException e) {
                facture.setStatut(null);
            }
        }
        
        Timestamp dateFacture = rs.getTimestamp("date_facture");
        if (dateFacture != null) {
            facture.setDateFacture(dateFacture.toLocalDateTime());
        }
        
        // Mapping des relations
        Long situationFinanciereId = getLongSafe(rs, "situation_financiere_id");
        if (situationFinanciereId != null) {
            SituationFinanciere situationFinanciere = new SituationFinanciere();
            situationFinanciere.setIdSF(situationFinanciereId);
            facture.setSituationFinanciere(situationFinanciere);
        }
        
        Long consultationId = getLongSafe(rs, "consultation_id");
        if (consultationId != null) {
            Consultation consultation = new Consultation();
            consultation.setIdConsultation(consultationId);
            facture.setConsultation(consultation);
        }
        
        // Mapping des champs BaseEntity
        Timestamp creationDate = rs.getTimestamp("creation_date");
        if (creationDate != null) {
            facture.setDateCreation(creationDate.toLocalDateTime().toLocalDate());
        }
        
        Timestamp modifDate = rs.getTimestamp("last_modification_date");
        if (modifDate != null) {
            facture.setDateDerniereModification(modifDate.toLocalDateTime().toLocalDate());
        }
        
        facture.setCreePar(rs.getString("created_by"));
        facture.setModifiePar(rs.getString("updated_by"));
        
        return facture;
    }

    public static SituationFinanciere mapSituationFinanciere(ResultSet rs) throws SQLException {
        SituationFinanciere situationFinanciere = new SituationFinanciere();
        
        situationFinanciere.setIdSF(rs.getLong("idSf"));
        situationFinanciere.setTotaleDesActes(getDoubleSafe(rs, "totale_des_actes"));
        situationFinanciere.setTotalePaye(getDoubleSafe(rs, "totale_paye"));
        situationFinanciere.setCrédit(getDoubleSafe(rs, "credit"));
        
        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            try {
                situationFinanciere.setStatut(StatutSituationFinanciere.valueOf(statutStr));
            } catch (IllegalArgumentException e) {
                situationFinanciere.setStatut(null);
            }
        }
        
        String enPromoStr = rs.getString("en_promo");
        if (enPromoStr != null) {
            try {
                situationFinanciere.setEnPromo(EnPromo.valueOf(enPromoStr));
            } catch (IllegalArgumentException e) {
                situationFinanciere.setEnPromo(null);
            }
        }
        
        // Mapping de la relation
        Long dossierMedicaleId = getLongSafe(rs, "dossier_medicale_id");
        if (dossierMedicaleId != null) {
            DossierMedicale dossierMedicale = new DossierMedicale();
            dossierMedicale.setIdDM(dossierMedicaleId);
            situationFinanciere.setDossierMedicale(dossierMedicale);
        }
        
        // Mapping des champs BaseEntity
        Timestamp creationDate = rs.getTimestamp("creation_date");
        if (creationDate != null) {
            situationFinanciere.setDateCreation(creationDate.toLocalDateTime().toLocalDate());
        }
        
        Timestamp modifDate = rs.getTimestamp("last_modification_date");
        if (modifDate != null) {
            situationFinanciere.setDateDerniereModification(modifDate.toLocalDateTime().toLocalDate());
        }
        
        situationFinanciere.setCreePar(rs.getString("created_by"));
        situationFinanciere.setModifiePar(rs.getString("updated_by"));
        
        return situationFinanciere;
    }
}
