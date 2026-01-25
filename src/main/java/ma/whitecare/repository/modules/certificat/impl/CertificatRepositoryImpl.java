package ma.whitecare.repository.modules.certificat.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Certificat;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.certificat.api.CertificatRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificatRepositoryImpl implements CertificatRepository {
    @Override
    public List<Certificat> findAll() {
        String sql = "SELECT * FROM certificat ORDER BY date_debut DESC";
        List<Certificat> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapResultSetToCertificat(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des certificats", e);
        }
        return out;
    }

    @Override
    public Certificat findById(Long id) {
        String sql = "SELECT * FROM certificat WHERE id_certif = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapResultSetToCertificat(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du certificat par ID", e);
        }

    }

    @Override
    public void create(Certificat newElement) {
        String sql = "INSERT INTO certificat (date_debut, date_fin, duree, note_medecin, " +
                "dossier_medicale_id, consulation_id, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(newElement.getDateDebut()));
            ps.setDate(2, Date.valueOf(newElement.getDateFin()));
            ps.setInt(3,
                    newElement.getDuree() != null ? newElement.getDuree()
                            : (int) java.time.temporal.ChronoUnit.DAYS.between(
                                    newElement.getDateDebut(), newElement.getDateFin()) + 1);
            ps.setString(4, newElement.getNoteMedecin());
            ps.setObject(5, newElement.getDossierMedicale() != null ? newElement.getDossierMedicale().getIdDM() : null);
            ps.setObject(6,
                    newElement.getConsultation() != null ? newElement.getConsultation().getIdConsultation() : null);
            ps.setString(7, newElement.getCreePar() != null ? newElement.getCreePar() : "system");
            ps.setString(8, newElement.getModifiePar() != null ? newElement.getModifiePar() : "system");

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setIdCertif(generatedKeys.getLong(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);

        }
    }

    @Override
    public void update(Certificat newValuesElement) {
        String sql = "UPDATE certificat SET date_debut = ?, date_fin = ?, duree = ?, " +
                "note_medecin = ?, dossier_medicale_id = ?, consulation_id = ?, " +
                "updated_by = ?, last_modification_date = CURRENT_TIMESTAMP " +
                "WHERE id_certif = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(newValuesElement.getDateDebut()));
            ps.setDate(2, Date.valueOf(newValuesElement.getDateFin()));
            ps.setInt(3, newValuesElement.getDuree());
            ps.setString(4, newValuesElement.getNoteMedecin());
            ps.setObject(5,
                    newValuesElement.getDossierMedicale() != null ? newValuesElement.getDossierMedicale().getIdDM()
                            : null);
            ps.setObject(6,
                    newValuesElement.getConsultation() != null ? newValuesElement.getConsultation().getIdConsultation()
                            : null);
            ps.setString(7, newValuesElement.getModifiePar() != null ? newValuesElement.getModifiePar() : "system");
            ps.setLong(8, newValuesElement.getIdCertif());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du certificat", e);
        }
    }

    @Override
    public void delete(Certificat oldElement) {
        if (oldElement != null && oldElement.getIdCertif() != null) {
            deleteById(oldElement.getIdCertif());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM certificat WHERE id_certif = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du certificat", e);
        }
    }

    @Override
    public List<Certificat> findByDossierMedicaleId(Long dossierId) {
        String sql = "SELECT * FROM certificat WHERE dossier_medicale_id = ? ORDER BY date_debut DESC";
        List<Certificat> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapResultSetToCertificat(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par dossier médical", e);
        }
        return out;

    }

    @Override
    public List<Certificat> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM certificat WHERE consulation_id = ? ORDER BY date_debut DESC";
        List<Certificat> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapResultSetToCertificat(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par consultation", e);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM certificat WHERE id_certif = ?";

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
}
