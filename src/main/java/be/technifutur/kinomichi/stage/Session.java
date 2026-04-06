package be.technifutur.kinomichi.stage;

import be.technifutur.kinomichi.exception.KinomichiException;
import be.technifutur.kinomichi.person.Person;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import static be.technifutur.kinomichi.util.DateUtil.isTodayOrFuture;
import static be.technifutur.kinomichi.util.DateUtil.isWeekend;

public class Session implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final LocalDateTime startDateTime;
    private final int duration;
    private final Price price;
    private final Person instructor;

    public Session(String name, LocalDateTime startDateTime, int duration, Price price, Person instructor) {
        if (!isTodayOrFuture(Objects.requireNonNull(startDateTime))) {
            throw new KinomichiException("La date entrée est passée.");
        }
        if (!isWeekend(startDateTime)) {
            throw new KinomichiException("La date entrée n'est pas un samedi ou un dimanche.");
        }

        this.startDateTime = startDateTime;
        this.name = Objects.requireNonNull(name);
        this.duration = (int) Math.round(duration / 15.0) * 15;
        this.price = Objects.requireNonNull(price);

        if (Objects.requireNonNull(instructor).isInstructor()) {
            this.instructor = instructor;
        } else {
            throw new KinomichiException(instructor.getFullName() + " n'est pas un formateur !");
        }
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getEndDateTime() {
        return startDateTime.plusMinutes(duration);
    }

    public Price getPrice() {
        return price;
    }

    public Person getInstructor() {
        return instructor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;
        return name.equals(session.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
