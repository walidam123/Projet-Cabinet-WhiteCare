package ma.whitecare.repository.modules.Ordonnance;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.Ordonnance.OrdonnanceRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

    private static final String BASE_SELECT =
            "SELECT o.*, " +
                    "c.id_consultation AS consul_id, c.date AS consul_date, c.statut AS consul_statut, c.observation_medecin AS consul_obs, " +
                    "d.idDM AS dossier_id, d.dateDecreation AS dossier_date, d.patient_id AS patient_id, d.medecin_id AS medecin_id " +
                    "FROM ordonnance o " +
                    "LEFT JOIN consultation c ON o.consultation_id = c.id_consultation " +
                    "LEFT JOIN dossierMedicale d ON o.dossierMedicale_id = d.idDM ";

    @Override
    public List<Ordonnance> findAll() {
        String sql = BASE_SELECT + " ORDER BY o.date DESC";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapResultSetToOrdonnance(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll", e);
        }
        return out;
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = BASE_SELECT + " WHERE o.idOrd = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapResultSetToOrdonnance(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById", e);
        }
        return null;
    }

    @Override
    public void create(Ordonnance newElement) {
        String sql = "INSERT INTO ordonnance (date, consultation_id, dossierMedicale_id, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(newElement.getDate()));
            ps.setLong(2, newElement.getConsultationid());
            ps.setLong(3, newElement.getDossierMedicaleid());
            ps.setString(4, newElement.getCreePar());
            ps.setString(5, newElement.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) newElement.setIdOrd(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create", e);
        }
    }

    @Override
    public void update(Ordonnance o) {
        String sql = "UPDATE ordonnance SET date=?, consultation_id=?, dossierMedicale_id=?, " +
                "updated_by=?, last_modification_date=CURRENT_TIMESTAMP WHERE idOrd=?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(o.getDate()));
            ps.setLong(2, o.getConsultationid());
            ps.setLong(3, o.getDossierMedicaleid());
            ps.setString(4, o.getModifiePar());
            ps.setLong(5, o.getIdOrd());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update", e);
        }
    }

    @Override
    public void delete(Ordonnance oldElement) {
        if (oldElement!=null)deleteById(oldElement.getIdOrd());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM ordonnance WHERE idOrd = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM ordonnance WHERE idOrd = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur existsById", e);
        }
    }

    @Override
    public List<Ordonnance> findByDossierMedicaleId(Long dossierId) {
        String sql = BASE_SELECT + " WHERE o.dossierMedicale_id = ? ORDER BY o.date DESC";

        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapResultSetToOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDossierMedicaleId", e);
        }

        return out;
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        String sql = BASE_SELECT + " WHERE o.consultation_id = ? ORDER BY o.date DESC";

        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapResultSetToOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByConsultationId", e);
        }

        return out;
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = BASE_SELECT + " WHERE o.date BETWEEN ? AND ? ORDER BY o.date DESC";

        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapResultSetToOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDateBetween", e);
        }

        return out;
    }
}
