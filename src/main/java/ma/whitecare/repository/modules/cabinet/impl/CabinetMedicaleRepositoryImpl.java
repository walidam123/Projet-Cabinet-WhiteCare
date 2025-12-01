package ma.whitecare.repository.modules.cabinet.impl;

import ma.whitecare.entities.cabinet.CabinetMedicale;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.conf.SessionFactory;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;


public class CabinetMedicaleRepositoryImpl implements CabinetMedicaleRepository {
    @Override
    public List<CabinetMedicale> findAll() {
        String sql = "SELECT * FROM cabinet_medicale ORDER BY nom";
        List<CabinetMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapCabinetMedicale(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public CabinetMedicale findById(Long id) {
        String sql = "SELECT * FROM cabinet_medicale WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCabinetMedicale(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(CabinetMedicale newElement) {
        String sql = "INSERT INTO cabinet_medicale (nom, email, logo, adresse, cin, tel1, tel2, siteweb, instagram, facebook, description, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getEmail());
            ps.setString(3, newElement.getLogo());
            ps.setString(4, newElement.getAdresse());
            ps.setString(5, newElement.getCin());
            ps.setString(6, newElement.getTel1());
            ps.setString(7, newElement.getTel2());
            ps.setString(8, newElement.getSiteWeb());
            ps.setString(9, newElement.getInstagram());
            ps.setString(10, newElement.getFacebook());
            ps.setString(11, newElement.getDescription());
            ps.setString(12, newElement.getCreePar());
            ps.setString(13, newElement.getModifiePar());

            ps.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    newElement.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(CabinetMedicale newValuesElement) {
        String sql = "UPDATE cabinet_medicale SET nom = ?, email = ?, logo = ?, adresse = ?, cin = ?, tel1 = ?, tel2 = ?, siteweb = ?, instagram = ?, facebook = ?, description = ?, last_modification_date = ?, updated_by = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getEmail());
            ps.setString(3, newValuesElement.getLogo());
            ps.setString(4, newValuesElement.getAdresse());
            ps.setString(5, newValuesElement.getCin());
            ps.setString(6, newValuesElement.getTel1());
            ps.setString(7, newValuesElement.getTel2());
            ps.setString(8, newValuesElement.getSiteWeb());
            ps.setString(9, newValuesElement.getInstagram());
            ps.setString(10, newValuesElement.getFacebook());
            ps.setString(11, newValuesElement.getDescription());
            ps.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
            ps.setString(13, newValuesElement.getModifiePar());
            ps.setLong(14, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(CabinetMedicale oldElement) {if (oldElement!=null) deleteById(oldElement.getId());

    }

    @Override
    public void deleteById(Long aLong) {
        String sql = "DELETE FROM cabinet_medicale WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, aLong);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<CabinetMedicale> findByNom(String nom) {
        String sql = "SELECT * FROM cabinet_medicale WHERE nom = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedicale(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<CabinetMedicale> findByEmail(String email) {
        String sql = "SELECT * FROM cabinet_medicale WHERE email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedicale(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<CabinetMedicale> findByCin(String cin) {
        String sql = "SELECT * FROM cabinet_medicale WHERE cin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedicale(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }


//TEST/TEST/TEST
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
    public Double calculateChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
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
    public Double calculateRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
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
    public Double calculateProfit(Long cabinetId) {
        String sql = "SELECT (COALESCE(SUM(r.montant), 0) - COALESCE(SUM(c.montant), 0)) as profit " +
                "FROM cabinet_medicale cab " +
                "LEFT JOIN revenues r ON r.cabinet_medicale_id = cab.id " +
                "LEFT JOIN charges c ON c.cabinet_medicale_id = cab.id " +
                "WHERE cab.id = ?";
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
    public Double calculateProfitByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT (COALESCE(SUM(r.montant), 0) - COALESCE(SUM(c.montant), 0)) as profit " +
                "FROM cabinet_medicale cab " +
                "LEFT JOIN revenues r ON r.cabinet_medicale_id = cab.id AND r.date BETWEEN ? AND ? " +
                "LEFT JOIN charges c ON c.cabinet_medicale_id = cab.id AND c.date BETWEEN ? AND ? " +
                "WHERE cab.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ps.setTimestamp(3, Timestamp.valueOf(startDate));
            ps.setTimestamp(4, Timestamp.valueOf(endDate));
            ps.setLong(5, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0.0;
    }

    @Override
    public Long countStaffByCabinet(Long cabinetId) {
        String sql = "SELECT COUNT(*) FROM staff WHERE cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0L;
    }



}


