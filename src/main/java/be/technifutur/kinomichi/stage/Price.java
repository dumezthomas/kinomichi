package be.technifutur.kinomichi.stage;

import be.technifutur.kinomichi.person.Person;

import java.math.BigDecimal;
import java.util.Objects;

public record Price(BigDecimal adult, BigDecimal child, BigDecimal instructor) {
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

    public BigDecimal forParticipant(Person person) {
        if (person != null) {
            if (person.isInstructor()) {
                return instructor;
            }
            if (person.isChild()) {
                return child;
            }
        }
        return adult;
    }
}