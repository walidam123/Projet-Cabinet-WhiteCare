package ma.whitecare.repository.modules.patient.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.enums.NiveauDeRisque;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.patient.api.AntecedentRepository;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    @Override
    public List<Antecedents> findAll() {
        String sql = "SELECT * FROM antecedents ORDER BY id_antecedent";
        List<Antecedents> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Antecedents findById(Long id) {
        String sql = "SELECT * FROM antecedents WHERE id_antecedent = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapAntecedent(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Antecedents antecedent) {
        String sql = """
            INSERT INTO antecedents(nom, categorie, niveau_de_risque, 
                                   creation_date, last_modification_date, created_by, updated_by)
            VALUES(?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, antecedent.getNom());
            ps.setString(2, antecedent.getCategorie());
            ps.setString(3, antecedent.getNiveauDeRisque().name());

            // Gestion des dates et utilisateurs
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(4, now);
            ps.setTimestamp(5, now);
            ps.setString(6, antecedent.getCreePar());
            ps.setString(7, antecedent.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) antecedent.setId_Antecedent(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Antecedents antecedent) {
        String sql = """
            UPDATE antecedents SET nom=?, categorie=?, niveau_de_risque=?, 
                   last_modification_date=?, updated_by=? WHERE id_antecedent=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, antecedent.getNom());
            ps.setString(2, antecedent.getCategorie());
            ps.setString(3, antecedent.getNiveauDeRisque().name());
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setString(5, antecedent.getModifiePar());
            ps.setLong(6, antecedent.getId_Antecedent());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Antecedents antecedent) {
        if (antecedent != null) deleteById(antecedent.getId_Antecedent());
    }

    @Override
    public void deleteById(Long id) {
        // Supprimer d'abord les relations many-to-many
        removeAntecedentFromAllPatients(id);

        String sql = "DELETE FROM antecedents WHERE id_antecedent = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    // -------- Recherche et Filtrage --------
    @Override
    public Optional<Antecedents> findByNom(String nom) {
        String sql = "SELECT * FROM antecedents WHERE nom = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapAntecedent(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Antecedents> findByCategorie(String categorie) {
        String sql = "SELECT * FROM antecedents WHERE categorie = ? ORDER BY nom";
        List<Antecedents> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Antecedents> findByNiveauRisque(NiveauDeRisque niveau) {
        String sql = "SELECT * FROM antecedents WHERE niveau_de_risque = ? ORDER BY nom";
        List<Antecedents> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, niveau.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM antecedents WHERE id_antecedent = ?";
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
        String sql = "SELECT COUNT(*) FROM antecedents";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Antecedents> findPage(int limit, int offset) {
        String sql = "SELECT * FROM antecedents ORDER BY id_antecedent LIMIT ? OFFSET ?";
        List<Antecedents> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Patient> getPatientsHavingAntecedent(Long antecedentId) {
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


    @Override
    public void removeAntecedentFromAllPatients(Long antecedentId) {
        String sql = "DELETE FROM patient_antecedents WHERE antecedents_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, antecedentId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

}
