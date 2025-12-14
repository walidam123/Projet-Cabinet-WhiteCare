package ma.whitecare.repository.modules.statistiques.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.cabinet.Statistiques;
import ma.whitecare.entities.enums.CategorieStatistique;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.statistiques.api.StatistiqueRepository;

import java.sql.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatistiqueRepositoryImpl implements StatistiqueRepository {
    @Override
    public List<Statistiques> findAll() {
        String sql = "SELECT * FROM statistiques ORDER BY dateCalcul DESC, nom";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapStatistique(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Statistiques findById(Long id) {
        String sql = "SELECT * FROM statistiques WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapStatistique(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(Statistiques newElement) {

        String sql = "INSERT INTO statistiques (nom, categorie, chiffre, dateCalcul, cabinet_medicale_id, creation_date, last_modification_date, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getCategorie().name());
            ps.setDouble(3, newElement.getChiffre());
            ps.setDate(4, Date.valueOf(newElement.getDateCalcul()));
            ps.setLong(5, newElement.getCabinet().getId());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(6,now  );
            ps.setTimestamp(7,now );
            ps.setString(8, newElement.getCreePar());
            ps.setString(9, newElement.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    newElement.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Statistiques newValuesElement) {
        String sql = "UPDATE statistiques SET nom = ?, categorie = ?, valeur = ?, dateCalcul = ?, cabinet_medicale_id = ?, last_modification_date = ?, updated_by = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getCategorie().name());
            ps.setDouble(3, newValuesElement.getChiffre());
            ps.setDate(4, Date.valueOf(newValuesElement.getDateCalcul()));
            ps.setLong(5, newValuesElement.getCabinet().getId());
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setString(7, newValuesElement.getModifiePar());
            ps.setLong(8, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Statistiques oldElement) {
        if (oldElement!=null) deleteById(oldElement.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM statistiques WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Statistiques> findByCabinetMedicaleId(Long cabinetId) {
        String sql = "SELECT * FROM statistiques WHERE cabinet_medicale_id = ? ORDER BY dateCalcul DESC, categorie";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Statistiques> findByNomContainingIgnoreCase(String nom) {
        String sql = "SELECT * FROM statistiques WHERE LOWER(nom) LIKE LOWER(?) ORDER BY dateCalcul DESC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Statistiques> findByCategorie(CategorieStatistique categorie) {
        String sql = "SELECT * FROM statistiques WHERE categorie = ? ORDER BY dateCalcul DESC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Statistiques> findByCategorieAndCabinet(CategorieStatistique categorie, Long cabinetId) {
        String sql = "SELECT * FROM statistiques WHERE categorie = ? AND cabinet_medicale_id = ? ORDER BY dateCalcul DESC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            ps.setLong(2, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<Statistiques> findByNomAndCabinetId(String nom, Long cabinetId) {
        String sql = "SELECT * FROM statistiques WHERE nom = ? AND cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setLong(2, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Statistiques> findByDateCalcul(LocalDate dateCalcul) {
        String sql = "SELECT * FROM statistiques WHERE dateCalcul = ? ORDER BY categorie";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dateCalcul));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Statistiques> findByDateCalculBetween(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM statistiques WHERE dateCalcul BETWEEN ? AND ? ORDER BY dateCalcul DESC, categorie";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Statistiques> findByCabinetAndDateCalculBetween(Long cabinetId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM statistiques WHERE cabinet_medicale_id = ? AND dateCalcul BETWEEN ? AND ? ORDER BY dateCalcul DESC, categorie";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Statistiques> findByCategorieAndDateCalculBetween(CategorieStatistique categorie, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM statistiques WHERE categorie = ? AND dateCalcul BETWEEN ? AND ? ORDER BY dateCalcul DESC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Double calculateMoyenneByCategorie(CategorieStatistique categorie, Long cabinetId) {
        String sql = "SELECT AVG(chiffre) FROM statistiques WHERE categorie = ? AND cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            ps.setLong(2, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0.0;
    }

    @Override
    public Double calculateSommeByCategorie(CategorieStatistique categorie, Long cabinetId) {
        String sql = "SELECT SUM(chiffre) FROM statistiques WHERE categorie = ? AND cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            ps.setLong(2, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0.0;
    }
}
