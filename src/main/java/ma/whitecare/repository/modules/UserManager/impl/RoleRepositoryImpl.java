package ma.whitecare.repository.modules.UserManager.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.user.Role;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.UserManager.api.RoleRepository;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoleRepositoryImpl implements RoleRepository {
    @Override
    public Optional<Role> findByLibelle(LibelleRole libelle) {
        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, libelle.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapRole(rs));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<Role> findByLibelleString(String libelle) {
        try {
            LibelleRole roleLibelle = LibelleRole.valueOf(libelle.toUpperCase());
            return findByLibelle(roleLibelle);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }    }

    @Override
    public Long findIdByLibelle(LibelleRole role) {
        String sql = "SELECT id FROM role WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        String sql = "INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES (?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void assignRoleLibelleToUser(Long userId, LibelleRole libelle) {
        Optional<Role> roleOpt = findByLibelle(libelle);
        if (roleOpt.isPresent()) {
            assignRoleToUser(userId, roleOpt.get().getIdRole());
        } else {
            throw new RuntimeException("Rôle non trouvé: " + libelle);
        }
    }

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return;

        String sql = "INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES (?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (Long roleId : roleIds) {
                ps.setLong(1, userId);
                ps.setLong(2, roleId);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        String sql = "DELETE FROM utilisateur_role WHERE utilisateur_id = ? AND role_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeAllRolesFromUser(Long userId) {
        String sql = "DELETE FROM utilisateur_role WHERE utilisateur_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void updateUserRoles(Long userId, List<Long> newRoleIds) {
        removeAllRolesFromUser(userId);

        if (newRoleIds != null && !newRoleIds.isEmpty()) {
            assignRolesToUser(userId, newRoleIds);
        }
    }

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        String sql = "SELECT r.* FROM role r " +
                "JOIN utilisateur_role ur ON r.id = ur.role_id " +
                "WHERE ur.utilisateur_id = ? ORDER BY r.libelle";
        List<Role> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRole(rs));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Utilisateur> findUsersByRoleId(Long roleId) {
        String sql = "SELECT u.* FROM utilisateur u " +
                "JOIN utilisateur_role ur ON u.id = ur.utilisateur_id " +
                "WHERE ur.role_id = ? ORDER BY u.nom, u.prenom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapUtilisateur(rs));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Utilisateur> findUsersByRoleLibelle(LibelleRole libelle) {
        Optional<Role> roleOpt = findByLibelle(libelle);
        if (roleOpt.isPresent()) {
            return findUsersByRoleId(roleOpt.get().getIdRole());
        }
        return new ArrayList<>();
    }

    @Override
    public List<LibelleRole> findRoleLibellesByUserId(Long userId) {
        List<Role> roles = findRolesByUserId(userId);
        return roles.stream()
                .map(Role::getLibelle)
                .collect(Collectors.toList());
    }

    @Override
    public boolean userHasRole(Long userId, Long roleId) {
        String sql = "SELECT COUNT(*) FROM utilisateur_role WHERE utilisateur_id = ? AND role_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1) > 0;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return false;
    }

    @Override
    public boolean userHasRoleLibelle(Long userId, LibelleRole libelle) {
        List<LibelleRole> userRoles = findRoleLibellesByUserId(userId);
        return userRoles.contains(libelle);
    }

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM role ORDER BY libelle";
        List<Role> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapRole(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Role findById(Long id) {
        String sql = "SELECT * FROM role WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapRole(rs);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(Role newElement) {
        String sql = "INSERT INTO role (libelle, creation_date, last_modification_date, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getLibelle().name());
            ps.setTimestamp(2,new Timestamp(System.currentTimeMillis() ));
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, newElement.getCreePar());
            ps.setString(5, newElement.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    newElement.setIdRole(rs.getLong(1));
                }
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Role newValuesElement) {
        String sql = "UPDATE role SET libelle = ?, last_modification_date = ?, updated_by = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getLibelle().name());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis() ));
            ps.setString(3, newValuesElement.getModifiePar());
            ps.setLong(4, newValuesElement.getIdRole());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Role oldElement) {
if (oldElement!=null) deleteById(oldElement.getIdRole());
    }

    @Override
    public void deleteById(Long roleId) {
        String sql = "DELETE FROM role WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
