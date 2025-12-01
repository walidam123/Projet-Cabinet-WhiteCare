package ma.whitecare.repository.modules.statistiques.api;

import ma.whitecare.entities.cabinet.Statistiques;
import ma.whitecare.entities.enums.CategorieStatistique;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StatistiqueRepository extends CrudRepository<Statistiques,Long> {


    // === FIND METHODS ===
    List<Statistiques> findByCabinetMedicaleId(Long cabinetId);
    List<Statistiques> findByNomContainingIgnoreCase(String nom);
    List<Statistiques> findByCategorie(CategorieStatistique categorie);
    List<Statistiques> findByCategorieAndCabinet(CategorieStatistique categorie, Long cabinetId);
    Optional<Statistiques> findByNomAndCabinetId(String nom, Long cabinetId);

    // === FIND BY DATE METHODS ===
    List<Statistiques> findByDateCalcul(LocalDate dateCalcul);
    List<Statistiques> findByDateCalculBetween(LocalDate startDate, LocalDate endDate);
    List<Statistiques> findByCabinetAndDateCalculBetween(Long cabinetId, LocalDate startDate, LocalDate endDate);
    List<Statistiques> findByCategorieAndDateCalculBetween(CategorieStatistique categorie, LocalDate startDate, LocalDate endDate);



    // === CALCUL METHODS ===
    Double calculateMoyenneByCategorie(CategorieStatistique categorie, Long cabinetId);
    Double calculateSommeByCategorie(CategorieStatistique categorie, Long cabinetId);
}
