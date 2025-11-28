package ma.whitecare.repository.modules.patient.api;

import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.repository.common.CrudRepository;


import java.time.LocalDate;
import java.util.List;


public interface PatientRepository extends CrudRepository<Patient, Long> {

    List<Patient> findByEmail(String email);
    List<Patient> findByTelephone(String telephone);
    List<Patient> searchByNomPrenom(String keyword); // LIKE %keyword%
    List<Patient> findByAssurance(String assurance);
    List<Patient> findByDateNaissanceBetween(LocalDate startDate, LocalDate endDate);
    boolean existsById(Long id);
    long count();
    List<Patient> findPage(int limit, int offset);//Limit = nombre maximum de résultats à retourner,Offset = nombre de résultats à sauter au début


    void addAntecedentToPatient(Long patientId, Long antecedentId);
    void removeAntecedentFromPatient(Long patientId, Long antecedentId);
    void removeAllAntecedentsFromPatient(Long patientId);
    List<Antecedents> getAntecedentsOfPatient(Long patientId);
    List<Patient> getPatientsByAntecedent(Long antecedentId);

    // Statistics and reporting

    Long countByAssurance(String assurance);
    Long countBySexe(String sexe);
    List<Patient> findBySexe(String sexe);

}