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
    public List<Facture> findAll() {
        String sql = "SELECT * FROM facture ORDER BY id_facture";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapFacture(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de toutes les factures", e);
        }
        return out;
    }

    @Override
    public Facture findById(Long id) {
        String sql = "SELECT * FROM facture WHERE id_facture = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapFacture(rs);
                }
        return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la facture par ID: " + id, e);
        }
    }

    @Override
    public void create(Facture facture) {
        String sql = """
            INSERT INTO facture(totale_facture, totale_paye, reste, statut, date_facture,
                              situation_financiere_id, consultation_id,
                              creation_date, last_modification_date, created_by, updated_by)
            VALUES(?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, facture.getTotaleFacture() != null ? facture.getTotaleFacture() : 0.0);
            ps.setDouble(2, facture.getTotalePayé() != null ? facture.getTotalePayé() : 0.0);
            ps.setDouble(3, facture.getReste() != null ? facture.getReste() : 0.0);

            if (facture.getStatut() != null) {
                ps.setString(4, facture.getStatut().name());
            } else {
                ps.setString(4, StatutFacture.BROUILLON.name());
            }

            if (facture.getDateFacture() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(facture.getDateFacture()));
            } else {
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            }

            if (facture.getSituationFinanciere() != null && facture.getSituationFinanciere().getIdSF() != null) {
                ps.setLong(6, facture.getSituationFinanciere().getIdSF());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            if (facture.getConsultation() != null && facture.getConsultation().getIdConsultation() != null) {
                ps.setLong(7, facture.getConsultation().getIdConsultation());
            } else {
                ps.setNull(7, Types.BIGINT);
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(8, now);
            ps.setTimestamp(9, now);
            ps.setString(10, facture.getCreePar() != null ? facture.getCreePar() : "system");
            ps.setString(11, facture.getModifiePar() != null ? facture.getModifiePar() : "system");

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    facture.setIdFature(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la facture", e);
        }
    }

    @Override
    public void update(Facture facture) {
        String sql = """
            UPDATE facture 
            SET totale_facture=?, totale_paye=?, reste=?, statut=?, date_facture=?,
                situation_financiere_id=?, consultation_id=?,
                last_modification_date=?, updated_by=?
            WHERE id_facture=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, facture.getTotaleFacture() != null ? facture.getTotaleFacture() : 0.0);
            ps.setDouble(2, facture.getTotalePayé() != null ? facture.getTotalePayé() : 0.0);
            ps.setDouble(3, facture.getReste() != null ? facture.getReste() : 0.0);

            if (facture.getStatut() != null) {
                ps.setString(4, facture.getStatut().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            if (facture.getDateFacture() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(facture.getDateFacture()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            if (facture.getSituationFinanciere() != null && facture.getSituationFinanciere().getIdSF() != null) {
                ps.setLong(6, facture.getSituationFinanciere().getIdSF());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            if (facture.getConsultation() != null && facture.getConsultation().getIdConsultation() != null) {
                ps.setLong(7, facture.getConsultation().getIdConsultation());
            } else {
                ps.setNull(7, Types.BIGINT);
            }

            ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            ps.setString(9, facture.getModifiePar() != null ? facture.getModifiePar() : "system");
            ps.setLong(10, facture.getIdFature());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la facture ID: " + facture.getIdFature(), e);
        }
    }

    @Override
    public void delete(Facture facture) {
        if (facture != null) {
            deleteById(facture.getIdFature());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM facture WHERE id_facture = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la facture ID: " + id, e);
        }
    }

    @Override
    public List<Facture> findBySituationFinanciereId(Long situationFinanciereId) {
        String sql = "SELECT * FROM facture WHERE situation_financiere_id = ? ORDER BY date_facture DESC, id_facture";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, situationFinanciereId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par situation financière ID: " + situationFinanciereId, e);
        }
        return out;
    }

    @Override
    public List<Facture> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM facture WHERE consultation_id = ? ORDER BY date_facture DESC, id_facture";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par consultation ID: " + consultationId, e);
        }
        return out;
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        String sql = "SELECT * FROM facture WHERE statut = ? ORDER BY date_facture DESC, id_facture";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par statut: " + statut, e);
        }
        return out;
    }

    @Override
    public List<Facture> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM facture WHERE date_facture >= ? AND date_facture <= ? ORDER BY date_facture DESC, id_facture";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche entre les dates: " + startDate + " et " + endDate, e);
        }
        return out;
    }

    @Override
    public List<Facture> findBySituationFinanciereIdAndStatut(Long situationFinanciereId, StatutFacture statut) {
        String sql = "SELECT * FROM facture WHERE situation_financiere_id = ? AND statut = ? ORDER BY date_facture DESC, id_facture";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, situationFinanciereId);
            ps.setString(2, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par situation financière et statut", e);
        }
        return out;
    }

    @Override
    public boolean existsById(Long factureId) {
        String sql = "SELECT 1 FROM facture WHERE id_facture = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, factureId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence par ID: " + factureId, e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM facture";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage de toutes les factures", e);
        }
    }

    @Override
    public long countByStatut(StatutFacture statut) {
        String sql = "SELECT COUNT(*) FROM facture WHERE statut = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par statut: " + statut, e);
        }
    }

    @Override
    public long countBySituationFinanciereId(Long situationFinanciereId) {
        String sql = "SELECT COUNT(*) FROM facture WHERE situation_financiere_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, situationFinanciereId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par situation financière ID: " + situationFinanciereId, e);
        }
    }

    @Override
    public void updateStatut(Long id, StatutFacture statut) {
        String sql = "UPDATE facture SET statut = ?, last_modification_date = ? WHERE id_facture = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setLong(3, id);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Aucune facture trouvée avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la facture ID: " + id, e);
        }
    }
}
