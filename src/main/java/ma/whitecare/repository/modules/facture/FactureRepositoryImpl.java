package ma.whitecare.repository.modules.facture;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.enums.StatutFacture;
import ma.whitecare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureRepositoryImpl implements FactureRepository {

    @Override
    public List<Facture> findBySituationFinanciereId(Long situationFinanciereId) {
        String sql = "SELECT * FROM facture WHERE situation_financiere_id = ?";
        return executeQuery(sql, situationFinanciereId);
    }

    @Override
    public List<Facture> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM facture WHERE consultation_id = ?";
        return executeQuery(sql, consultationId);
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        String sql = "SELECT * FROM facture WHERE statut = ?";
        return executeQuery(sql, statut.name());
    }

    @Override
    public List<Facture> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM facture WHERE date_facture BETWEEN ? AND ?";
        return executeQuery(sql, Timestamp.valueOf(startDate), Timestamp.valueOf(endDate));
    }

    @Override
    public List<Facture> findBySituationFinanciereIdAndStatut(Long situationFinanciereId, StatutFacture statut) {
        String sql = "SELECT * FROM facture WHERE situation_financiere_id = ? AND statut = ?";
        return executeQuery(sql, situationFinanciereId, statut.name());
    }

    @Override
    public boolean existsById(Long factureId) {
        String sql = "SELECT COUNT(*) FROM facture WHERE id_facture = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, factureId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getLong(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM facture";
        return executeCount(sql);
    }

    @Override
    public long countByStatut(StatutFacture statut) {
        String sql = "SELECT COUNT(*) FROM facture WHERE statut = ?";
        return executeCount(sql, statut.name());
    }

    @Override
    public long countBySituationFinanciereId(Long situationFinanciereId) {
        String sql = "SELECT COUNT(*) FROM facture WHERE situation_financiere_id = ?";
        return executeCount(sql, situationFinanciereId);
    }

    @Override
    public void updateStatut(Long id, StatutFacture statut) {
        String sql = "UPDATE facture SET statut = ?, last_modification_date = NOW() WHERE id_facture = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la facture ID: " + id, e);
        }
    }

    @Override
    public void create(Facture entity) {
        String sql = "INSERT INTO facture (totale_facture, totale_paye, reste, statut, date_facture, situation_financiere_id, consultation_id, creation_date, last_modification_date, created_by, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)";
        try (Connection conn = SessionFactory.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, entity.getTotaleFacture());
            stmt.setDouble(2, entity.getTotalePayé());
            stmt.setDouble(3, entity.getReste());
            stmt.setString(4, entity.getStatut().name());
            stmt.setTimestamp(5, entity.getDateFacture() != null ? Timestamp.valueOf(entity.getDateFacture()) : null);
            stmt.setObject(6,
                    entity.getSituationFinanciere() != null ? entity.getSituationFinanciere().getIdSF() : null);
            stmt.setObject(7, entity.getConsultation() != null ? entity.getConsultation().getIdConsultation() : null);
            stmt.setString(8, entity.getCreePar());
            stmt.setString(9, entity.getModifiePar());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next())
                    entity.setIdFature(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la facture", e);
        }
    }

    @Override
    public Facture findById(Long id) {
        String sql = "SELECT * FROM facture WHERE id_facture = ?";
        List<Facture> result = executeQuery(sql, id);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Facture> findAll() {
        return executeQuery("SELECT * FROM facture");
    }

    @Override
    public void update(Facture entity) {
        String sql = "UPDATE facture SET totale_facture = ?, totale_paye = ?, reste = ?, statut = ?, last_modification_date = NOW(), updated_by = ? WHERE id_facture = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, entity.getTotaleFacture());
            stmt.setDouble(2, entity.getTotalePayé());
            stmt.setDouble(3, entity.getReste());
            stmt.setString(4, entity.getStatut().name());
            stmt.setString(5, entity.getModifiePar());
            stmt.setLong(6, entity.getIdFature());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la facture ID: " + entity.getIdFature(), e);
        }
    }

    @Override
    public void delete(Facture oldElement) {
        if (oldElement != null && oldElement.getIdFature() != null) {
            deleteById(oldElement.getIdFature());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM facture WHERE id_facture = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Facture> executeQuery(String sql, Object... params) {
        List<Facture> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++)
                stmt.setObject(i + 1, params[i]);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(RowMappers.mapFacture(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private long executeCount(String sql, Object... params) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++)
                stmt.setObject(i + 1, params[i]);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
