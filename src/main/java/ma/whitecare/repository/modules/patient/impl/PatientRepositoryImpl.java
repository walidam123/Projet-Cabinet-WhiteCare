package ma.whitecare.repository.modules.patient.impl;

import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.conf.SessionFactory;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {

    // -------- CRUD --------
    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patient ORDER BY idPatient";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Patient findById(Long id) {
        String sql = "SELECT * FROM patient WHERE idPatient = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapPatient(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Patient p) {
        String sql = """
    INSERT INTO patient(nom, prenom, dateDeNaissance, sexe, adresse, telephone, email, assurance, 
                       creation_date, last_modification_date, created_by, updated_by)
    VALUES(?,?,?,?,?,?,?,?,?,?,?,?)
    """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());

            if (p.getDateNaissance() != null) {
                ps.setDate(3, Date.valueOf(p.getDateNaissance()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setString(4, p.getSexe() != null ? p.getSexe().name() : Sexe.HOMME.name());
            ps.setString(5, p.getAdresse());
            ps.setString(6, p.getTelephone());
            ps.setString(7, p.getEmail());
            ps.setString(8, p.getAssurance() != null ? p.getAssurance().name() : Assurance.AUCUNE.name());

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(9, now);
            ps.setTimestamp(10, now);
            ps.setString(11, p.getCreePar() != null ? p.getCreePar() : "system");
            ps.setString(12, p.getModifiePar() != null ? p.getModifiePar() : "system");

            ps.executeUpdate();

            // ✅ Récupérer l’ID généré et le mettre dans l’objet Patient
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId_Patient(keys.getLong(1)); // CORRECTION ICI
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du patient: " + e.getMessage(), e);
        }
    }


    @Override
    public void update(Patient p) {
        String sql = """
        UPDATE patient SET nom=?, prenom=?, dateDeNaissance=?, sexe=?, adresse=?, telephone=?, email=?, 
               assurance=?, last_modification_date=?, updated_by=? WHERE idPatient=?
        """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());  // NOUVEAU
            if (p.getDateNaissance() != null) ps.setDate(3, Date.valueOf(p.getDateNaissance()));
            else ps.setNull(3, Types.DATE);
            ps.setString(4, p.getSexe().name());
            ps.setString(5, p.getAdresse());
            ps.setString(6, p.getTelephone());
            ps.setString(7, p.getEmail());
            ps.setString(8, p.getAssurance().name());
            ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            ps.setString(10, p.getModifiePar());
            ps.setLong(11, p.getId_Patient());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Patient p) {
        if (p != null) deleteById(p.getId_Patient());
    }

    @Override
    public void deleteById(Long id) {
        // Supprimer d'abord les relations many-to-many
        removeAllAntecedentsFromPatient(id);

        String sql = "DELETE FROM patient WHERE idPatient = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- Recherche et Filtrage --------
    @Override
    public List<Patient> findByEmail(String email) {
        String sql = "SELECT * FROM patient WHERE email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return List.of(RowMappers.mapPatient(rs));
                return List.of();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Patient> findByTelephone(String telephone) {

        String sql = "SELECT * FROM patient WHERE telephone = ?";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, telephone);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapPatient(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par assurance: " + telephone, e);
        }
        return out;
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        String sql = "SELECT * FROM patient WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";  // MODIFIE
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);  // NOUVEAU
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Patient> findByAssurance(String assurance) {
        String sql = "SELECT * FROM patient WHERE assurance = ? ORDER BY nom, prenom";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, assurance);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapPatient(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par assurance: " + assurance, e);
        }
        return out;
    }

    @Override
    public List<Patient> findByDateNaissanceBetween(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM patient WHERE dateDeNaissance BETWEEN ? AND ? ORDER BY dateDeNaissance";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM patient WHERE idPatient = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM patient";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        String sql = "SELECT * FROM patient ORDER BY idPatient LIMIT ? OFFSET ?";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    // -------- Méthodes de Comptage --------


    @Override
    public Long countByAssurance(String assurance) {
        String sql = "SELECT COUNT(*) FROM patient WHERE assurance = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, assurance);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par assurance: " + assurance, e);
        }
    }



    // -------- Gestion des Antécédents (Many-to-Many) --------
    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        String sql = "INSERT INTO patient_antecedents(patient_id, antecedents_id) VALUES (?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        String sql = "DELETE FROM patient_antecedents WHERE patient_id = ? AND antecedents_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        String sql = "DELETE FROM patient_antecedents WHERE patient_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Antecedents> getAntecedentsOfPatient(Long patientId) {
        String sql = """
            SELECT a.* 
            FROM antecedents a 
            JOIN patient_antecedents pa ON pa.antecedents_id = a.id_antecedent
            WHERE pa.patient_id = ?
            ORDER BY a.categorie, a.niveau_de_risque, a.nom
            """;
        List<Antecedents> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        String sql = """
            SELECT p.* 
            FROM patient p 
            JOIN patient_antecedents pa ON pa.patient_id = p.idPatient
            WHERE pa.antecedents_id = ?
            ORDER BY p.nom
            """;
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, antecedentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    // -------- Méthodes supplémentaires utiles --------
    public List<Patient> findBySexe(String sexe) {
        String sql = "SELECT * FROM patient WHERE sexe = ? ORDER BY nom";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sexe);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    public Long countBySexe(String sexe) {
        String sql = "SELECT COUNT(*) FROM patient WHERE sexe = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sexe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par sexe: " + sexe, e);
        }
    }
}