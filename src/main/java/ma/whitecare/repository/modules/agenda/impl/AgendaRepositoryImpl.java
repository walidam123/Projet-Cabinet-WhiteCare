package ma.whitecare.repository.modules.agenda.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.agenda.AgendaMensuel;
import ma.whitecare.entities.agenda.Creneau;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.entities.enums.Mois;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.agenda.api.AgendaRepository;

import java.sql.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgendaRepositoryImpl implements AgendaRepository {


    @Override
    public List<AgendaMensuel> findAll() {
        String sql = "SELECT * FROM agenda_mensuel ORDER BY annee DESC, mois DESC";
        List<AgendaMensuel> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapAgendaMensuel(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public AgendaMensuel findById(Long id) {
        String sql = "SELECT * FROM agenda_mensuel WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapAgendaMensuel(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(AgendaMensuel newElement) {
        String sql = "INSERT INTO agenda_mensuel (mois, annee,medecin_id, creation_date, last_modification_date, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getMois().name());
            ps.setInt(2, newElement.getAnnee());
            ps.setObject(3, newElement.getMedecin().getIdUser(), Types.BIGINT);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
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
    public void update(AgendaMensuel newValuesElement) {
        String sql = "UPDATE agenda_mensuel SET mois = ?, annee = ?, medecin_id = ?, last_modification_date = ?, updated_by = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getMois().name());
            ps.setInt(2, newValuesElement.getAnnee());
            ps.setObject(3, newValuesElement.getMedecin().getIdUser(), Types.BIGINT);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setString(5, newValuesElement.getModifiePar());
            ps.setLong(6, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(AgendaMensuel oldElement) {if (oldElement!=null)     deleteById(oldElement.getId());


    }

    @Override
    public void deleteById(Long id) {
// Supprimer d'abord les créneaux, puis les jours, puis l'agenda
        String deleteCreneauxSql = "DELETE FROM creneau_horaire WHERE jour_agenda_id IN (SELECT id FROM jour_agenda WHERE agenda_mensuel_id = ?)";
        String deleteJoursSql = "DELETE FROM jour_agenda WHERE agenda_mensuel_id = ?";
        String deleteAgendaSql = "DELETE FROM agenda_mensuel WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps1 = c.prepareStatement(deleteCreneauxSql);
                 PreparedStatement ps2 = c.prepareStatement(deleteJoursSql);
                 PreparedStatement ps3 = c.prepareStatement(deleteAgendaSql)) {

                ps1.setLong(1, id);
                ps1.executeUpdate();

                ps2.setLong(1, id);
                ps2.executeUpdate();

                ps3.setLong(1, id);
                ps3.executeUpdate();

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    @Override
    public List<AgendaMensuel> findByMedecinId(Long medecinId) {
        String sql = "SELECT * FROM agenda_mensuel WHERE medecin_id = ? ORDER BY annee DESC, mois DESC";
        List<AgendaMensuel> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAgendaMensuel(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;    }

    @Override
    public Optional<AgendaMensuel> findByMoisAndAnneeAndMedecin(Mois mois, int annee, Long medecinId) {
        String sql = "SELECT * FROM agenda_mensuel WHERE mois = ? AND annee = ? AND medecin_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mois.name());
            ps.setInt(2, annee);
            ps.setLong(3, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapAgendaMensuel(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();    }

    @Override
    public void ajouterJour(Long agendaId, Jour jour) {
        String sql = "INSERT INTO jour_agenda (agenda_mensuel_id, date_jour, jour_semaine, est_disponible, raison_indisponibilite) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(jour.getDate()));
            ps.setString(3, jour.getJourSemaine().name());
            ps.setBoolean(4, jour.isEstDisponible());
            ps.setString(5, jour.getRaisonIndisponibilite());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    jour.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void supprimerJour(Long jourId) {
// Supprimer d'abord les créneaux, puis le jour
        String deleteCreneauxSql = "DELETE FROM creneau_horaire WHERE jour_agenda_id = ?";
        String deleteJourSql = "DELETE FROM jour_agenda WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps1 = c.prepareStatement(deleteCreneauxSql);
                 PreparedStatement ps2 = c.prepareStatement(deleteJourSql)) {

                ps1.setLong(1, jourId);
                ps1.executeUpdate();

                ps2.setLong(1, jourId);
                ps2.executeUpdate();

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void mettreAJourJour(Jour jour) {
        String sql = "UPDATE jour_agenda SET date_jour = ?, jour_semaine = ?, est_disponible = ?, raison_indisponibilite = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(jour.getDate()));
            ps.setString(2, jour.getJourSemaine().name());
            ps.setBoolean(3, jour.isEstDisponible());
            ps.setString(4, jour.getRaisonIndisponibilite());
            ps.setLong(5, jour.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Jour> findJoursByAgendaId(Long agendaId) {
        String sql = "SELECT * FROM jour_agenda WHERE agenda_mensuel_id = ? ORDER BY date_jour";
        List<Jour> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapJourAgenda(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;    }

    @Override
    public List<Jour> findJoursNonDisponiblesByAgendaId(Long agendaId) {
        String sql = "SELECT * FROM jour_agenda WHERE agenda_mensuel_id = ? AND est_disponible = FALSE ORDER BY date_jour";
        List<Jour> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapJourAgenda(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Jour> findJoursDisponiblesByAgendaId(Long agendaId) {
        String sql = "SELECT * FROM jour_agenda WHERE agenda_mensuel_id = ? AND est_disponible = TRUE ORDER BY date_jour";
        List<Jour> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapJourAgenda(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<Jour> findJourByDate(Long agendaId, LocalDate date) {
        String sql = "SELECT * FROM jour_agenda WHERE agenda_mensuel_id = ? AND date_jour = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapJourAgenda(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();    }

    @Override
    public void ajouterCreneau(Long jourId, Creneau creneau) {
        String sql = "INSERT INTO creneau_horaire (jour_agenda_id, heure_debut, heure_fin, est_disponible, motif_indisponibilite, rendez_vous_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, jourId);
            ps.setTime(2, Time.valueOf(creneau.getHeureDebut()));
            ps.setTime(3, Time.valueOf(creneau.getHeureFin()));
            ps.setBoolean(4, creneau.isEstDisponible());
            ps.setString(5, creneau.getMotifIndisponibilite());
            ps.setObject(6, creneau.getRendezVousId(), Types.BIGINT);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    creneau.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void supprimerCreneau(Long creneauId) {
        String sql = "DELETE FROM creneau_horaire WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, creneauId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void mettreAJourCreneau(Creneau creneau) {
        String sql = "UPDATE creneau_horaire SET heure_debut = ?, heure_fin = ?, est_disponible = ?, motif_indisponibilite = ?, rendez_vous_id = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setTime(1, Time.valueOf(creneau.getHeureDebut()));
            ps.setTime(2, Time.valueOf(creneau.getHeureFin()));
            ps.setBoolean(3, creneau.isEstDisponible());
            ps.setString(4, creneau.getMotifIndisponibilite());
            ps.setObject(5, creneau.getRendezVousId(), Types.BIGINT);
            ps.setLong(6, creneau.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Creneau> findCreneauxByJourId(Long jourId) {
        String sql = "SELECT * FROM creneau_horaire WHERE jour_agenda_id = ? ORDER BY heure_debut";
        List<Creneau> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, jourId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCreneauHoraire(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Creneau> findCreneauxDisponiblesByJourId(Long jourId) {
        String sql = "SELECT * FROM creneau_horaire WHERE jour_agenda_id = ? AND est_disponible = TRUE ORDER BY heure_debut";
        List<Creneau> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, jourId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCreneauHoraire(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;    }

    @Override
    public List<Creneau> findCreneauxByAgendaAndDate(Long agendaId, LocalDate date) {
        String sql = "SELECT ch.* FROM creneau_horaire ch " +
                "JOIN jour_agenda ja ON ch.jour_agenda_id = ja.id " +
                "WHERE ja.agenda_mensuel_id = ? AND ja.date_jour = ? " +
                "ORDER BY ch.heure_debut";
        List<Creneau> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCreneauHoraire(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public boolean estJourDisponible(Long agendaId, LocalDate date) {
        String sql = "SELECT est_disponible FROM jour_agenda WHERE agenda_mensuel_id = ? AND date_jour = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean("est_disponible");
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return false;
    }

    @Override
    public boolean estCreneauDisponible(Long jourId, Creneau creneau) {
        String sql = "SELECT COUNT(*) FROM creneau_horaire " +
                "WHERE jour_agenda_id = ? AND est_disponible = TRUE " +
                "AND heure_debut = ? AND heure_fin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, jourId);
            ps.setTime(2, Time.valueOf(creneau.getHeureDebut()));
            ps.setTime(3, Time.valueOf(creneau.getHeureFin()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1) > 0;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return false;
    }

    @Override
    public List<Creneau> findCreneauxDisponiblesByDate(Long agendaId, LocalDate date) {
        String sql = "SELECT ch.* FROM creneau_horaire ch " +
                "JOIN jour_agenda ja ON ch.jour_agenda_id = ja.id " +
                "WHERE ja.agenda_mensuel_id = ? AND ja.date_jour = ? AND ja.est_disponible = TRUE AND ch.est_disponible = TRUE " +
                "ORDER BY ch.heure_debut";
        List<Creneau> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCreneauHoraire(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }


}
