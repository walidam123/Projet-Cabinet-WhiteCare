package ma.whitecare.entities.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.PrioriteNotification;
import ma.whitecare.entities.enums.TitreNotification;
import ma.whitecare.entities.enums.TypeNotification;
import ma.whitecare.entities.user.Utilisateur;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification extends BaseEntity {

    private Long id;
    private TitreNotification titre;
    private String message;
    private LocalDate date;
    private LocalTime time;
    private TypeNotification type;
    private PrioriteNotification Priorite;

    private List<Utilisateur> utilisateur;

}
