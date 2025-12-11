package ma.whitecare.repository.modules.medicament.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.entities.enums.FormeMedicament;
import ma.whitecare.repository.modules.medicament.api.MedicamentRepository;
import ma.whitecare.repository.common.RowMappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentRepositoryImpl implements MedicamentRepository {
    
    @Override
    public List<Medicament> findAll() {
        String sql = "SELECT * FROM medicament";
        List<Medicament> medicaments = new ArrayList<>();
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                medicaments.add(RowMappers.mapmedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les médicaments", e);
        }
        
        return medicaments;
    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM medicament WHERE idMct = ?";
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapmedicament(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du médicament ID: " + id, e);
        }
        
        return null;
    }

    @Override
    public void create(Medicament medicament) {
        String sql = """
            INSERT INTO medicament (nom, laboratoire, type, forme, remboursable, prixUnitaire, description,
                                  creation_date, last_modification_date, created_by, updated_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, medicament.getNom());
            ps.setString(2, medicament.getLaboratoire());
            ps.setString(3, medicament.getType());
            
            if (medicament.getForme() != null) {
                ps.setString(4, medicament.getForme().name());
            } else {
                ps.setString(4, FormeMedicament.COMPRIME.name());
            }
            
            ps.setBoolean(5, medicament.getRemboursable() != null ? medicament.getRemboursable() : false);
            ps.setDouble(6, medicament.getPrixUnitaire() != null ? medicament.getPrixUnitaire() : 0.0);
            ps.setString(7, medicament.getDescription());
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(8, now);
            ps.setTimestamp(9, now);
            ps.setString(10, medicament.getCreePar() != null ? medicament.getCreePar() : "system");
            ps.setString(11, medicament.getModifiePar() != null ? medicament.getModifiePar() : "system");
            
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    medicament.setIdMct(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du médicament: " + medicament.getNom(), e);
        }
    }

    @Override
    public void update(Medicament medicament) {
        String sql = """
            UPDATE medicament 
            SET nom = ?, laboratoire = ?, type = ?, forme = ?, remboursable = ?, 
                prixUnitaire = ?, description = ?, last_modification_date = ?, updated_by = ?
            WHERE idMct = ?
            """;
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setString(1, medicament.getNom());
            ps.setString(2, medicament.getLaboratoire());
            ps.setString(3, medicament.getType());
            
            if (medicament.getForme() != null) {
                ps.setString(4, medicament.getForme().name());
            } else {
                ps.setString(4, FormeMedicament.COMPRIME.name());
            }
            
            ps.setBoolean(5, medicament.getRemboursable() != null ? medicament.getRemboursable() : false);
            ps.setDouble(6, medicament.getPrixUnitaire() != null ? medicament.getPrixUnitaire() : 0.0);
            ps.setString(7, medicament.getDescription());
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(8, now);
            ps.setString(9, medicament.getModifiePar() != null ? medicament.getModifiePar() : "system");
            ps.setLong(10, medicament.getIdMct());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du médicament ID: " + medicament.getIdMct(), e);
        }
    }

    @Override
    public void delete(Medicament medicament) {
        deleteById(medicament.getIdMct());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM medicament WHERE idMct = ?";
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du médicament ID: " + id, e);
        }
    }
}

