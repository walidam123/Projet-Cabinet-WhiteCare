package ma.whitecare.repository.modules.dossierMedical.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Prescription;

import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static ma.whitecare.repository.common.RowMappers.mapPrescription;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    @Override
    public List<Prescription> findAll() {
        String sql = "SELECT * FROM prescription ORDER BY idPr";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(mapPrescription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de toutes les prescriptions", e);
        }
        return out;
    }

    @Override
    public Prescription findById(Long id) {
        String sql = "SELECT * FROM prescription WHERE idPr = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapPrescription(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la prescription par ID: " + id, e);
        }
    }

    @Override
    public void create(Prescription prescription) {
        String sql = """
                INSERT INTO prescription(quantite, frequence, dureeEnjours, medicament_id, ordonnance_id,
                                         creation_date, last_modification_date, created_by, updated_by)
                VALUES(?,?,?,?,?,?,?,?,?)
                """;
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, prescription.getQuantite());
            ps.setString(2, prescription.getFrequence());
            ps.setInt(3, prescription.getDureeEnJours());
            ps.setLong(4, prescription.getMedicament().getIdMct());
            ps.setLong(5, prescription.getOrdonnance().getIdOrd());

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(6, now);
            ps.setTimestamp(7, now);
            ps.setString(8, prescription.getCreePar() != null ? prescription.getCreePar() : "system");
            ps.setString(9, prescription.getModifiePar() != null ? prescription.getModifiePar() : "system");

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    prescription.setIdPr(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la prescription", e);
        }
    }

    @Override
    public void update(Prescription prescription) {
        String sql = """
                UPDATE prescription
                SET quantite=?, frequence=?, dureeEnjours=?, medicament_id=?, ordonnance_id=?,
                    last_modification_date=?, updated_by=?
                WHERE idPr=?
                """;
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, prescription.getQuantite());
            ps.setString(2, prescription.getFrequence());
            ps.setInt(3, prescription.getDureeEnJours());

            if (prescription.getMedicament() != null) {
                ps.setLong(4, prescription.getMedicament().getIdMct());
            } else {
                ps.setNull(4, Types.BIGINT);
            }

            if (prescription.getOrdonnance() != null) {
                ps.setLong(5, prescription.getOrdonnance().getIdOrd());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setString(7, prescription.getModifiePar() != null ? prescription.getModifiePar() : "system");
            ps.setLong(8, prescription.getIdPr());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la prescription ID: " + prescription.getIdPr(),
                    e);
        }
    }

    @Override
    public void delete(Prescription prescription) {
        if (prescription != null) {
            deleteById(prescription.getIdPr());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM prescription WHERE idPr = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la prescription ID: " + id, e);
        }
    }

    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        String sql = """
                SELECT p.*,
                       m.nom as medicament_nom,
                       m.prixUnitaire as medicament_prix,
                       m.description as medicament_description
                FROM prescription p
                LEFT JOIN medicament m ON p.medicament_id = m.idMct
                WHERE p.ordonnance_id = ?
                ORDER BY p.idPr
                """;
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, ordonnanceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Custom mapping to include medication details
                    Prescription prescription = new Prescription();
                    prescription.setIdPr(rs.getLong("idPr"));
                    prescription.setQuantite(rs.getInt("quantite"));
                    prescription.setFrequence(rs.getString("frequence"));
                    prescription.setDureeEnJours(rs.getInt("dureeEnjours"));

                    // Map medication with full details
                    Long medicamentId = rs.getObject("medicament_id", Long.class);
                    if (medicamentId != null) {
                        ma.whitecare.entities.medical.Medicament medicament = new ma.whitecare.entities.medical.Medicament();
                        medicament.setIdMct(medicamentId);
                        medicament.setNom(rs.getString("medicament_nom"));
                        medicament.setPrixUnitaire(rs.getDouble("medicament_prix"));
                        medicament.setDescription(rs.getString("medicament_description"));
                        prescription.setMedicament(medicament);
                    }

                    // Map ordonnance (just ID)
                    Long ordId = rs.getObject("ordonnance_id", Long.class);
                    if (ordId != null) {
                        ma.whitecare.entities.medical.Ordonnance ordonnance = new ma.whitecare.entities.medical.Ordonnance();
                        ordonnance.setIdOrd(ordId);
                        prescription.setOrdonnance(ordonnance);
                    }

                    // Map audit fields
                    var dc = rs.getTimestamp("creation_date");
                    if (dc != null)
                        prescription.setDateCreation(java.time.LocalDate.from(dc.toLocalDateTime()));
                    var dl = rs.getTimestamp("last_modification_date");
                    if (dl != null)
                        prescription.setDateDerniereModification(java.time.LocalDate.from(dl.toLocalDateTime()));
                    prescription.setCreePar(rs.getString("created_by"));
                    prescription.setModifiePar(rs.getString("updated_by"));

                    out.add(prescription);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par ordonnance ID: " + ordonnanceId, e);
        }
        return out;
    }

    @Override
    public List<Prescription> findByMedicamentId(Long medicamentId) {
        String sql = "SELECT * FROM prescription WHERE medicament_id = ? ORDER BY idPr";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medicamentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapPrescription(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par médicament ID: " + medicamentId, e);
        }
        return out;
    }

    @Override
    public List<Prescription> findByDureeSuperieure(Integer dureeMin) {
        String sql = "SELECT * FROM prescription WHERE dureeEnjours >= ? ORDER BY dureeEnjours";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, dureeMin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapPrescription(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par durée minimale: " + dureeMin, e);
        }
        return out;
    }

    @Override
    public List<Prescription> findByOrdonnanceAndMedicament(Long ordonnanceId, Long medicamentId) {
        String sql = "SELECT * FROM prescription WHERE ordonnance_id = ? AND medicament_id = ? ORDER BY idPr";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, ordonnanceId);
            ps.setLong(2, medicamentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapPrescription(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par ordonnance et médicament", e);
        }
        return out;
    }

    @Override
    public Double calculateCoutTotalOrdonnance(Long ordonnanceId) {
        String sql = """
                SELECT SUM(p.quantite * m.prixUnitaire)
                FROM prescription p
                JOIN medicament m ON p.medicament_id = m.idMct
                WHERE p.ordonnance_id = ?
                """;
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, ordonnanceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : total;
                }
                return 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du coût total par ordonnance ID: " + ordonnanceId, e);
        }
    }

    @Override
    public boolean existsById(Long prescriptionId) {
        String sql = "SELECT 1 FROM prescription WHERE idPr = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, prescriptionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence par ID: " + prescriptionId, e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM prescription";
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage de toutes les prescriptions", e);
        }
    }

    @Override
    public long countByOrdonnanceId(Long ordonnanceId) {
        String sql = "SELECT COUNT(*) FROM prescription WHERE ordonnance_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, ordonnanceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par ordonnance ID: " + ordonnanceId, e);
        }
    }

    @Override
    public long countByMedicamentId(Long medicamentId) {
        String sql = "SELECT COUNT(*) FROM prescription WHERE medicament_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medicamentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par médicament ID: " + medicamentId, e);
        }
    }
}
