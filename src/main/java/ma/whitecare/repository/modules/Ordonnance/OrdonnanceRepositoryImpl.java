package ma.whitecare.repository.modules.Ordonnance;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdonnanceRepositoryImpl implements  OrdonnanceRepository {
    @Override
    public List<Ordonnance> findAll() {
        String sql = "SELECT o.*, " +
                "c.idConsultation as consul_id, c.date as consul_date, c.motif as consul_motif, " +
                "d.idDossier as dossier_id, d.dateCreation as dossier_dateCreation " +
                "FROM ordonnance o " +
                "LEFT JOIN consultation c ON o.consultation_id = c.idConsultation " +
                "LEFT JOIN dossierMedicale d ON o.dossierMedicale_id = d.idDossier " +
                "ORDER BY o.date DESC";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapResultSetToOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les ordonnances", e);
        }
        return out;
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT o.*, " +
                "c.idConsultation as consul_id, c.date as consul_date, c.motif as consul_motif, " +
                "d.idDossier as dossier_id, d.dateCreation as dossier_dateCreation " +
                "FROM ordonnance o " +
                "LEFT JOIN consultation c ON o.consultation_id = c.idConsultation " +
                "LEFT JOIN dossierMedicale d ON o.dossierMedicale_id = d.idDossier " +
                "WHERE o.idOrd = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) RowMappers.mapResultSetToOrdonnance(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
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
                if (rs.next()) {
                    newElement.setIdOrd(rs.getLong(1));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }



    }


    @Override
    public void update(Ordonnance newValuesElement) {
        String sql = "UPDATE ordonnance SET date = ?, consultation_id = ?, dossierMedicale_id = ?, " +
                "updated_by = ?, last_modification_date = CURRENT_TIMESTAMP WHERE idOrd = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(newValuesElement.getDate()));
            ps.setLong(2,newValuesElement.getConsultationid());
            ps.setLong(3, newValuesElement.getDossierMedicaleid());
            ps.setString(4, newValuesElement.getModifiePar());
            ps.setLong(5, newValuesElement.getIdOrd());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ordonnance trouvée avec l'ID: " + newValuesElement.getIdOrd());
            }


        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'ordonnance", e);
        }
    }

    @Override
    public void delete(Ordonnance oldElement) {
if(oldElement!=null) deleteById(oldElement.getIdOrd());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM ordonnance WHERE idOrd = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean existsById(Long id) {

        String sql = "SELECT COUNT(*) FROM ordonnance WHERE idOrd = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence", e);
        }
    }

    @Override
    public List<Ordonnance> findByDossierMedicaleId(Long dossierId) {

        String sql = "SELECT o.*, " +
                "c.idConsultation as consul_id, c.date as consul_date, c.motif as consul_motif, " +
                "d.idDossier as dossier_id, d.dateCreation as dossier_dateCreation " +
                "FROM ordonnance o " +
                "LEFT JOIN consultation c ON o.consultation_id = c.idConsultation " +
                "LEFT JOIN dossierMedicale d ON o.dossierMedicale_id = d.idDossier " +
                "WHERE o.dossierMedicale_id = ? " +
                "ORDER BY o.date DESC";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapResultSetToOrdonnance(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par dossier médical", e);
        }
        return out;
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        String sql = "SELECT o.*, " +
                "c.idConsultation as consul_id, c.date as consul_date, c.motif as consul_motif, " +
                "d.idDossier as dossier_id, d.dateCreation as dossier_dateCreation " +
                "FROM ordonnance o " +
                "LEFT JOIN consultation c ON o.consultation_id = c.idConsultation " +
                "LEFT JOIN dossierMedicale d ON o.dossierMedicale_id = d.idDossier " +
                "WHERE o.consultation_id = ? " +
                "ORDER BY o.date DESC";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapResultSetToOrdonnance(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par consultation", e);
        }
        return out;
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT o.*, " +
                "c.idConsultation as consul_id, c.date as consul_date, c.motif as consul_motif, " +
                "d.idDossier as dossier_id, d.dateCreation as dossier_dateCreation " +
                "FROM ordonnance o " +
                "LEFT JOIN consultation c ON o.consultation_id = c.idConsultation " +
                "LEFT JOIN dossierMedicale d ON o.dossierMedicale_id = d.idDossier " +
                "WHERE o.date BETWEEN ? AND ? " +
                "ORDER BY o.date DESC";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapResultSetToOrdonnance(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par période", e);
        }
        return out;
    }
}
