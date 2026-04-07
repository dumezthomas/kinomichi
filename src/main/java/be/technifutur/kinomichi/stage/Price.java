package be.technifutur.kinomichi.stage;

import be.technifutur.kinomichi.person.Person;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record Price(BigDecimal adult, BigDecimal child, BigDecimal instructor) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Price {
        Objects.requireNonNull(adult);
        Objects.requireNonNull(child);
        Objects.requireNonNull(instructor);

        if (adult.compareTo(BigDecimal.ZERO) < 0 ||
                child.compareTo(BigDecimal.ZERO) < 0 ||
                instructor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Les prix ne peuvent être négatifs!");
        }
    }

    public BigDecimal forPerson(Person person, LocalDate eventDate) {
        if (person != null) {
            if (person.isInstructor()) {
                return instructor;
            }
            if (person.isChild(eventDate)) {
                return child;
            }
        }
        return adult;
    }
}