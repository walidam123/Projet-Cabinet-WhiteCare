package ma.whitecare.repository.modules.cabinet.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.cabinet.api.ChargesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChargesRepositoryImpl implements ChargesRepository {


    @Override
    public List<Charges> findAll() {
        String sql = "SELECT * FROM charges ORDER BY date DESC";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapCharge(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Charges findById(Long id) {
        String sql = "SELECT * FROM charges WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCharge(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(Charges newElement) {
        String sql = "INSERT INTO charges (titre, description, montant, date, cabinet_medicale_id, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getTitre());
            ps.setString(2, newElement.getDescription());
            ps.setDouble(3, newElement.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(newElement.getDate()));
            ps.setLong(5, newElement.getCabinetMedicaleId());
            ps.setString(6, newElement.getCreePar());
            ps.setString(7, newElement.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    newElement.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Charges newValuesElement) {
        String sql = "UPDATE charges SET titre = ?, description = ?, montant = ?, date = ?, cabinet_medicale_id = ?, last_modification_date = ?, updated_by = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getTitre());
            ps.setString(2, newValuesElement.getDescription());
            ps.setDouble(3, newValuesElement.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(newValuesElement.getDate()));
            ps.setLong(5, newValuesElement.getCabinetMedicaleId());
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setString(7, newValuesElement.getModifiePar());
            ps.setLong(8, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }

    }

    @Override
    public void delete(Charges oldElement) {if (oldElement!=null) deleteById(oldElement.getId());

    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM charges WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }

    }


    @Override
    public List<Charges> findByCabinetMedicaleId(Long cabinetId) {
        String sql = "SELECT * FROM charges WHERE cabinet_medicale_id = ? ORDER BY date DESC";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCharge(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Charges> findByTitreContainingIgnoreCase(String titre) {
        String sql = "SELECT * FROM charges WHERE LOWER(titre) LIKE LOWER(?) ORDER BY date DESC";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + titre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCharge(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Charges> findByDescriptionContainingIgnoreCase(String description) {
        String sql = "SELECT * FROM charges WHERE LOWER(description) LIKE LOWER(?) ORDER BY date DESC";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + description + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCharge(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<Charges> findByTitreAndCabinetId(String titre, Long cabinetId) {
        String sql = "SELECT * FROM charges WHERE titre = ? AND cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titre);
            ps.setLong(2, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCharge(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Double calculateTotalCharges(Long cabinetId) {
        String sql = "SELECT SUM(montant) FROM charges WHERE cabinet_medicale_id = ?";
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
    public Double calculateTotalChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(montant) FROM charges WHERE cabinet_medicale_id = ? AND date BETWEEN ? AND ?";
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
    public Double calculateTotalChargesByTitre(Long cabinetId, String titre) {
        String sql = "SELECT SUM(montant) FROM charges WHERE cabinet_medicale_id = ? AND titre = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setString(2, titre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0.0;
    }


    @Override
    public Long countChargesByCabinet(Long cabinetId) {
        String sql = "SELECT COUNT(*) FROM charges WHERE cabinet_medicale_id = ?";
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
    public Long countChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT COUNT(*) FROM charges WHERE cabinet_medicale_id = ? AND date BETWEEN ? AND ?";
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
    public List<Charges> findChargesByCabinetAndPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM charges WHERE cabinet_medicale_id = ? AND date BETWEEN ? AND ? ORDER BY date DESC";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate));
            ps.setTimestamp(3, Timestamp.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCharge(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }




}
