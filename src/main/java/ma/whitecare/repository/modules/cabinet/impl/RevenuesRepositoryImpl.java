package ma.whitecare.repository.modules.cabinet.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.cabinet.api.RevenuesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RevenuesRepositoryImpl implements RevenuesRepository {


    @Override
    public List<Revenues> findAll() {
        String sql = "SELECT * FROM revenues ORDER BY date DESC";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapRevenue(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }


    @Override
    public Revenues findById(Long id) {
        String sql = "SELECT * FROM revenues WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapRevenue(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }
    @Override
    public void create(Revenues revenue) {
        String sql = "INSERT INTO revenues (titre, description, montant, date, cabinet_medicale_id, creation_date, last_modification_date, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, revenue.getTitre());
            ps.setString(2, revenue.getDescription());
            ps.setDouble(3, revenue.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(revenue.getDate()));
            ps.setLong(5, revenue.getCabinet().getId());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(6, now);
            ps.setTimestamp(7, now);
            ps.setString(8, revenue.getCreePar());
            ps.setString(9, revenue.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    revenue.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Revenues revenue) {
        String sql = "UPDATE revenues SET titre = ?, description = ?, montant = ?, date = ?, cabinet_medicale_id = ?, last_modification_date = ?, updated_by = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, revenue.getTitre());
            ps.setString(2, revenue.getDescription());
            ps.setDouble(3, revenue.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(revenue.getDate()));
            ps.setLong(5, revenue.getCabinet().getId());
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setString(7, revenue.getModifiePar());
            ps.setLong(8, revenue.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Revenues revenue) {
        if (revenue!=null)deleteById(revenue.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM revenues WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Revenues> findByCabinetMedicaleId(Long cabinetId) {
        String sql = "SELECT * FROM revenues WHERE cabinet_medicale_id = ? ORDER BY date DESC";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenue(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Revenues> findByTitleContainingIgnoreCase(String title) {
        String sql = "SELECT * FROM revenues WHERE LOWER(titre) LIKE LOWER(?) ORDER BY date DESC";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + title + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenue(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Revenues> findByDescriptionContainingIgnoreCase(String description) {
        String sql = "SELECT * FROM revenues WHERE LOWER(description) LIKE LOWER(?) ORDER BY date DESC";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + description + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenue(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<Revenues> findByTitleAndCabinetId(String title, Long cabinetId) {
        String sql = "SELECT * FROM revenues WHERE titre = ? AND cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setLong(2, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapRevenue(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Double calculateTotalRevenues(Long cabinetId) {
        String sql = "SELECT SUM(montant) FROM revenues WHERE cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0.0;
    }

    @Override
    public Double calculateTotalRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(montant) FROM revenues WHERE cabinet_medicale_id = ? AND date BETWEEN ? AND ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate));
            ps.setTimestamp(3, Timestamp.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0.0;
    }

    @Override
    public Double calculateTotalRevenuesByTitle(Long cabinetId, String title) {
        String sql = "SELECT SUM(montant) FROM revenues WHERE cabinet_medicale_id = ? AND titre = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setString(2, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0.0;
    }

    @Override
    public Double calculateAverageRevenue(Long cabinetId) {
        String sql = "SELECT AVG(montant) FROM revenues WHERE cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0.0;
    }

    @Override
    public Long countRevenuesByCabinet(Long cabinetId) {
        String sql = "SELECT COUNT(*) FROM revenues WHERE cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0L;
    }

    @Override
    public Long countRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT COUNT(*) FROM revenues WHERE cabinet_medicale_id = ? AND date BETWEEN ? AND ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate));
            ps.setTimestamp(3, Timestamp.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0L;
    }

    @Override
    public List<Revenues> findRevenuesByCabinetAndPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM revenues WHERE cabinet_medicale_id = ? AND date BETWEEN ? AND ? ORDER BY date DESC";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate));
            ps.setTimestamp(3, Timestamp.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenue(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }















}
