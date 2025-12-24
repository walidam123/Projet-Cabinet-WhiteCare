package ma.whitecare.repository.modules.dossierMedical.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.enums.EnPromo;
import ma.whitecare.entities.enums.StatutSituationFinanciere;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.dossierMedical.api.SituationFinanciereRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SituationFinanciereRepositoryImpl implements SituationFinanciereRepository {

    @Override
    public List<SituationFinanciere> findAll() {
        String sql = "SELECT * FROM situation_financiere ORDER BY idSf";
        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapSituationFinanciere(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de toutes les situations financières", e);
        }
        return out;
    }

    @Override
    public SituationFinanciere findById(Long id) {
        String sql = "SELECT * FROM situation_financiere WHERE idSf = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapSituationFinanciere(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la situation financière par ID: " + id, e);
        }
    }

    @Override
    public void create(SituationFinanciere situationFinanciere) {
        String sql = """
            INSERT INTO situation_financiere(totale_des_actes, totale_paye, credit, statut, en_promo,
                                            dossier_medicale_id,
                                            creation_date, last_modification_date, created_by, updated_by)
            VALUES(?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, situationFinanciere.getTotaleDesActes() != null ? situationFinanciere.getTotaleDesActes() : 0.0);
            ps.setDouble(2, situationFinanciere.getTotalePaye() != null ? situationFinanciere.getTotalePaye() : 0.0);
            ps.setDouble(3, situationFinanciere.getCredit() != null ? situationFinanciere.getCredit() : 0.0);

            if (situationFinanciere.getStatut() != null) {
                ps.setString(4, situationFinanciere.getStatut().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            if (situationFinanciere.getEnPromo() != null) {
                ps.setString(5, situationFinanciere.getEnPromo().name());
            } else {
                ps.setString(5, "NON");
            }

            if (situationFinanciere.getDossierMedicale() != null && situationFinanciere.getDossierMedicale().getIdDM() != null) {
                ps.setLong(6, situationFinanciere.getDossierMedicale().getIdDM());
            } else {
                throw new RuntimeException("Le dossier médical est requis pour créer une situation financière");
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(7, now);
            ps.setTimestamp(8, now);
            ps.setString(9, situationFinanciere.getCreePar() != null ? situationFinanciere.getCreePar() : "system");
            ps.setString(10, situationFinanciere.getModifiePar() != null ? situationFinanciere.getModifiePar() : "system");

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    situationFinanciere.setIdSF(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la situation financière", e);
        }
    }

    @Override
    public void update(SituationFinanciere situationFinanciere) {
        String sql = """
            UPDATE situation_financiere 
            SET totale_des_actes=?, totale_paye=?, credit=?, statut=?, en_promo=?,
                dossier_medicale_id=?,
                last_modification_date=?, updated_by=?
            WHERE idSf=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, situationFinanciere.getTotaleDesActes() != null ? situationFinanciere.getTotaleDesActes() : 0.0);
            ps.setDouble(2, situationFinanciere.getTotalePaye() != null ? situationFinanciere.getTotalePaye() : 0.0);
            ps.setDouble(3, situationFinanciere.getCredit() != null ? situationFinanciere.getCredit() : 0.0);

            if (situationFinanciere.getStatut() != null) {
                ps.setString(4, situationFinanciere.getStatut().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            if (situationFinanciere.getEnPromo() != null) {
                ps.setString(5, situationFinanciere.getEnPromo().name());
            } else {
                ps.setString(5, "NON");
            }

            if (situationFinanciere.getDossierMedicale() != null && situationFinanciere.getDossierMedicale().getIdDM() != null) {
                ps.setLong(6, situationFinanciere.getDossierMedicale().getIdDM());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            ps.setString(8, situationFinanciere.getModifiePar() != null ? situationFinanciere.getModifiePar() : "system");
            ps.setLong(9, situationFinanciere.getIdSF());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la situation financière ID: " + situationFinanciere.getIdSF(), e);
        }
    }

    @Override
    public void delete(SituationFinanciere situationFinanciere) {
        if (situationFinanciere != null) {
            deleteById(situationFinanciere.getIdSF());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM situation_financiere WHERE idSf = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la situation financière ID: " + id, e);
        }
    }

    @Override
    public SituationFinanciere findByDossierMedicaleId(Long dossierMedicaleId) {
        String sql = "SELECT * FROM situation_financiere WHERE dossier_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapSituationFinanciere(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par dossier médical ID: " + dossierMedicaleId, e);
        }
    }

    @Override
    public List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut) {
        String sql = "SELECT * FROM situation_financiere WHERE statut = ? ORDER BY idSf";
        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapSituationFinanciere(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par statut: " + statut, e);
        }
        return out;
    }

    @Override
    public List<SituationFinanciere> findByEnPromo(EnPromo enPromo) {
        String sql = "SELECT * FROM situation_financiere WHERE en_promo = ? ORDER BY idSf";
        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, enPromo.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapSituationFinanciere(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par en_promo: " + enPromo, e);
        }
        return out;
    }

    @Override
    public List<SituationFinanciere> findByDossierMedicaleIdAndStatut(Long dossierMedicaleId, StatutSituationFinanciere statut) {
        String sql = "SELECT * FROM situation_financiere WHERE dossier_medicale_id = ? AND statut = ? ORDER BY idSf";
        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicaleId);
            ps.setString(2, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapSituationFinanciere(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par dossier médical et statut", e);
        }
        return out;
    }

    @Override
    public boolean existsById(Long situationFinanciereId) {
        String sql = "SELECT 1 FROM situation_financiere WHERE idSf = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, situationFinanciereId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence par ID: " + situationFinanciereId, e);
        }
    }

    @Override
    public boolean existsByDossierMedicaleId(Long dossierMedicaleId) {
        String sql = "SELECT 1 FROM situation_financiere WHERE dossier_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence par dossier médical ID: " + dossierMedicaleId, e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM situation_financiere";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage de toutes les situations financières", e);
        }
    }

    @Override
    public long countByStatut(StatutSituationFinanciere statut) {
        String sql = "SELECT COUNT(*) FROM situation_financiere WHERE statut = ?";
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
    public long countByDossierMedicaleId(Long dossierMedicaleId) {
        String sql = "SELECT COUNT(*) FROM situation_financiere WHERE dossier_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par dossier médical ID: " + dossierMedicaleId, e);
        }
    }

    @Override
    public void updateStatut(Long id, StatutSituationFinanciere statut) {
        String sql = "UPDATE situation_financiere SET statut = ?, last_modification_date = ? WHERE idSf = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setLong(3, id);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Aucune situation financière trouvée avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la situation financière ID: " + id, e);
        }
    }
}
