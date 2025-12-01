package ma.whitecare.repository.modules.agenda.api;

import ma.whitecare.entities.agenda.AgendaMensuel;
import ma.whitecare.entities.agenda.Creneau;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.entities.enums.Mois;
import ma.whitecare.repository.common.CrudRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendaRepository extends CrudRepository<AgendaMensuel,Long> {

    List<AgendaMensuel> findByMedecinId(Long medecinId);
    Optional<AgendaMensuel> findByMoisAndAnneeAndMedecin(Mois mois, int annee, Long medecinId);



    // === JOUR MANAGEMENT ===
    void ajouterJour(Long agendaId, Jour jour);
    void supprimerJour(Long jourId);
    void mettreAJourJour(Jour jour);
    List<Jour> findJoursByAgendaId(Long agendaId);
    List<Jour> findJoursNonDisponiblesByAgendaId(Long agendaId);
    List<Jour> findJoursDisponiblesByAgendaId(Long agendaId);
    Optional<Jour> findJourByDate(Long agendaId, LocalDate date);
    // === CRENEAU MANAGEMENT ===
    void ajouterCreneau(Long jourId, Creneau creneau);
    void supprimerCreneau(Long creneauId);
    void mettreAJourCreneau(Creneau creneau);
    List<Creneau> findCreneauxByJourId(Long jourId);
    List<Creneau> findCreneauxDisponiblesByJourId(Long jourId);
    List<Creneau> findCreneauxByAgendaAndDate(Long agendaId, LocalDate date);
    // === AVAILABILITY CHECKS ===
    boolean estJourDisponible(Long agendaId, LocalDate date);
    boolean estCreneauDisponible(Long jourId, Creneau creneau);
    List<Creneau> findCreneauxDisponiblesByDate(Long agendaId, LocalDate date);
}
