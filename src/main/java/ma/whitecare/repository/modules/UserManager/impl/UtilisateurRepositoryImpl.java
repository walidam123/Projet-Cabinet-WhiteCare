package ma.whitecare.repository.modules.UserManager.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {


    @Override
    public Optional<Utilisateur> findByLogin(String login) {
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<Utilisateur> findByCin(String cin) {
        String sql = "SELECT * FROM utilisateur WHERE cin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Utilisateur> findByNomAndPrenom(String nom, String prenom) {
        String sql = "SELECT * FROM utilisateur WHERE nom = ? AND prenom = ?";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;    }

    @Override
    public List<Utilisateur> findBySexe(Sexe sexe) {
        String sql = "SELECT * FROM utilisateur WHERE sexe = ? ORDER BY nom, prenom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sexe.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;    }

    @Override
    public List<Utilisateur> findHommes() {
        return findBySexe(Sexe.HOMME);    }

    @Override
    public List<Utilisateur> findFemmes() {
        return findBySexe(Sexe.FEMME);    }

    @Override
    public List<Utilisateur> findByActif(boolean actif) {
        String sql = "SELECT * FROM utilisateur WHERE actif = ? ORDER BY nom, prenom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, actif);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Utilisateur> findActifs() {
        return findByActif(true);
    }

    @Override
    public List<Utilisateur> findInactifs() {
        return findByActif(false);
    }

    @Override
    public List<Utilisateur> findAdmins() {
        // Supposant que ADMIN est un rôle
        String sql = "SELECT DISTINCT u.* FROM utilisateur u " +
                "JOIN utilisateur_role ur ON u.id = ur.utilisateur_id " +
                "JOIN role r ON ur.role_id = r.id " +
                "WHERE r.libelle IN ('ADMIN', 'SUPER_ADMIN')  " +
                "ORDER BY u.nom, u.prenom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Utilisateur> findMedecins() {
        String sql = "SELECT DISTINCT u.* FROM utilisateur u " +
                "JOIN utilisateur_role ur ON u.id = ur.utilisateur_id " +
                "JOIN role r ON ur.role_id = r.id " +
                "WHERE r.libelle = 'MEDECIN'" +
                "ORDER BY u.nom, u.prenom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Utilisateur> findSecretaires() {
        String sql = "SELECT DISTINCT u.* FROM utilisateur u " +
                "JOIN utilisateur_role ur ON u.id = ur.utilisateur_id " +
                "JOIN role r ON ur.role_id = r.id " +
                "WHERE r.libelle = 'SECRETAIRE'" +
                "ORDER BY u.nom, u.prenom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM utilisateur";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    @Override
    public List<Utilisateur> findWithPagination(int offset, int limit) {
        String sql = "SELECT * FROM utilisateur ORDER BY nom, prenom LIMIT ? OFFSET ?";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
    @Override
    public void updatePassword(Long userId, String newPasswordHash) {
        String sql = "UPDATE utilisateur SET password_hash = ?, last_modification_date = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void activateUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return;

        String sql = "UPDATE utilisateur SET actif = 1, last_modification_date = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (Long userId : userIds) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.setLong(2, userId);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    @Override
    public void deactivateUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return;

        String sql = "UPDATE utilisateur SET actif = 0, last_modification_date = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (Long userId : userIds) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.setLong(2, userId);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM utilisateur ORDER BY nom, prenom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Utilisateur findById(Long id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapUtilisateur(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(Utilisateur newElement) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, adresse, cin, tel, sexe, login, password_hash, " +
                "last_login_date, date_naissance, actif, creation_date, last_modification_date, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getPrenom());
            ps.setString(3, newElement.getEmail());
            ps.setString(4, newElement.getAdresse());
            ps.setString(5, newElement.getCin());
            ps.setString(6, newElement.getTel());
            ps.setString(7, newElement.getSexe() != null ? newElement.getSexe().name() : Sexe.HOMME.name());
            ps.setString(8, newElement.getLogin());
            ps.setString(9, newElement.getMotDePass());
            ps.setTimestamp(10, newElement.getLastLoginDate() != null ?
                    Timestamp.valueOf(newElement.getLastLoginDate().atStartOfDay()) : null);
            ps.setDate(11, newElement.getDateNaissance() != null ?
                    Date.valueOf(newElement.getDateNaissance()) : null);
            ps.setBoolean(12, newElement.getActif());
            ps.setTimestamp(13, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(14, new Timestamp(System.currentTimeMillis() ));
            ps.setString(15, newElement.getCreePar());
            ps.setString(16, newElement.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    newElement.setIdUser(rs.getLong(1));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Utilisateur newValuesElement) {
        String sql = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, adresse = ?, cin = ?, tel = ?, sexe = ?, " +
                "login = ?, last_login_date = ?, date_naissance = ?, actif = ?, last_modification_date = ?, updated_by = ? " +
                "WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getPrenom());
            ps.setString(3, newValuesElement.getEmail());
            ps.setString(4, newValuesElement.getAdresse());
            ps.setString(5, newValuesElement.getCin());
            ps.setString(6, newValuesElement.getTel());
            ps.setString(7, newValuesElement.getSexe() != null ? newValuesElement.getSexe().name() : null);
            ps.setString(8, newValuesElement.getLogin());
            ps.setTimestamp(9, newValuesElement.getLastLoginDate() != null ?
                    Timestamp.valueOf(newValuesElement.getLastLoginDate().atStartOfDay()) : null);
            ps.setDate(10, newValuesElement.getDateNaissance() != null ?
                    Date.valueOf(newValuesElement.getDateNaissance()) : null);
            ps.setBoolean(11, newValuesElement.getActif());
            ps.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
            ps.setString(13, newValuesElement.getModifiePar());
            ps.setLong(14, newValuesElement.getIdUser());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Utilisateur oldElement) {
        deleteById(oldElement.getIdUser());

    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
