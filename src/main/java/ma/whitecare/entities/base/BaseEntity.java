package ma.whitecare.entities.base;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntity {
    protected Long idEntite;
    protected LocalDate dateCreation;
    protected LocalDate dateDerniereModification;
    protected String modifiePar;
    protected String creePar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return idEntite != null && idEntite.equals(that.idEntite);
    }

    @Override
    public int hashCode() {
        return idEntite != null ? idEntite.hashCode() : 0;
    }

}
