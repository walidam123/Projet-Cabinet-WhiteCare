package ma.whitecare.service.modules.patient.api;

import ma.whitecare.entities.patient.Patient;

import java.time.LocalDate;
import java.util.List;

public interface PatientService {

    List<Patient> getAllPatients();

    Patient getPatientById(Long id);

    void createPatient(Patient patient);

    void updatePatient(Patient updatedPatient);

    void deletePatient(Patient patient);

    void deletePatientById(Long id);

    List<Patient> findByEmail(String email);

    List<Patient> findByTelephone(String telephone);

    List<Patient> findByAssurance(String assurance);

    List<Patient> findBySexe(String sexe);

    List<Patient> findByDateNaissanceBetween(LocalDate start, LocalDate end);
}
