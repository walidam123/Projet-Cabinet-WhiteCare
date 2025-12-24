package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.SimulationFinanciereDto.CreateSimulationFinanciereDTO;
import ma.whitecare.mvc.dto.SimulationFinanciereDto.UpdateSimulationFinanciereDTO;

import java.util.ArrayList;
import java.util.List;

public class SimulationFinanciereValidator {

    public static List<String> validateCreateSimulationFinanciere(CreateSimulationFinanciereDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation du dossier médical (obligatoire)
        if (dto.getDossierMedicaleId() == null) {
            errors.add("L'ID du dossier médical est obligatoire");
        } else if (dto.getDossierMedicaleId() <= 0) {
            errors.add("L'ID du dossier médical doit être positif");
        }

        // Validation de totaleDesActes
        if (dto.getTotaleDesActes() == null) {
            errors.add("Le total des actes est obligatoire");
        } else if (dto.getTotaleDesActes() < 0) {
            errors.add("Le total des actes ne peut pas être négatif");
        } else if (dto.getTotaleDesActes() > 10000000) {
            errors.add("Le total des actes ne peut pas dépasser 10 000 000 DH");
        }

        // Validation de totalePaye
        if (dto.getTotalePaye() == null) {
            errors.add("Le total payé est obligatoire");
        } else if (dto.getTotalePaye() < 0) {
            errors.add("Le total payé ne peut pas être négatif");
        } else if (dto.getTotalePaye() > 10000000) {
            errors.add("Le total payé ne peut pas dépasser 10 000 000 DH");
        }

        // Validation de credit
        if (dto.getCredit() == null) {
            errors.add("Le crédit est obligatoire");
        } else if (dto.getCredit() < 0) {
            errors.add("Le crédit ne peut pas être négatif");
        } else if (dto.getCredit() > 10000000) {
            errors.add("Le crédit ne peut pas dépasser 10 000 000 DH");
        }

        // Validation de la cohérence : totalePaye ne peut pas dépasser totaleDesActes
        if (dto.getTotaleDesActes() != null && dto.getTotalePaye() != null) {
            if (dto.getTotalePaye() > dto.getTotaleDesActes()) {
                errors.add("Le total payé ne peut pas dépasser le total des actes");
            }
        }

        // Validation de la cohérence : credit = totaleDesActes - totalePaye
        if (dto.getTotaleDesActes() != null && dto.getTotalePaye() != null && dto.getCredit() != null) {
            double expectedCredit = dto.getTotaleDesActes() - dto.getTotalePaye();
            if (Math.abs(dto.getCredit() - expectedCredit) > 0.01) {
                errors.add("Le crédit doit être égal à (total des actes - total payé)");
            }
        }

        return errors;
    }

    public static List<String> validateUpdateSimulationFinanciere(UpdateSimulationFinanciereDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation du dossier médical (si fourni)
        if (dto.getDossierMedicaleId() != null && dto.getDossierMedicaleId() <= 0) {
            errors.add("L'ID du dossier médical doit être positif");
        }

        // Validation de totaleDesActes (si fourni)
        if (dto.getTotaleDesActes() != null) {
            if (dto.getTotaleDesActes() < 0) {
                errors.add("Le total des actes ne peut pas être négatif");
            } else if (dto.getTotaleDesActes() > 10000000) {
                errors.add("Le total des actes ne peut pas dépasser 10 000 000 DH");
            }
        }

        // Validation de totalePaye (si fourni)
        if (dto.getTotalePaye() != null) {
            if (dto.getTotalePaye() < 0) {
                errors.add("Le total payé ne peut pas être négatif");
            } else if (dto.getTotalePaye() > 10000000) {
                errors.add("Le total payé ne peut pas dépasser 10 000 000 DH");
            }
        }

        // Validation de credit (si fourni)
        if (dto.getCredit() != null) {
            if (dto.getCredit() < 0) {
                errors.add("Le crédit ne peut pas être négatif");
            } else if (dto.getCredit() > 10000000) {
                errors.add("Le crédit ne peut pas dépasser 10 000 000 DH");
            }
        }

        // Validation de la cohérence si tous les montants sont fournis
        if (dto.getTotaleDesActes() != null && dto.getTotalePaye() != null) {
            if (dto.getTotalePaye() > dto.getTotaleDesActes()) {
                errors.add("Le total payé ne peut pas dépasser le total des actes");
            }
        }

        // Validation de la cohérence du crédit si tous les montants sont fournis
        if (dto.getTotaleDesActes() != null && dto.getTotalePaye() != null && dto.getCredit() != null) {
            double expectedCredit = dto.getTotaleDesActes() - dto.getTotalePaye();
            if (Math.abs(dto.getCredit() - expectedCredit) > 0.01) {
                errors.add("Le crédit doit être égal à (total des actes - total payé)");
            }
        }

        return errors;
    }

    public static List<String> validateMontant(Double montant, String fieldName) {
        List<String> errors = new ArrayList<>();

        if (montant == null) {
            errors.add(fieldName + " est obligatoire");
        } else if (montant < 0) {
            errors.add(fieldName + " ne peut pas être négatif");
        } else if (montant > 10000000) {
            errors.add(fieldName + " ne peut pas dépasser 10 000 000 DH");
        }

        return errors;
    }
}
