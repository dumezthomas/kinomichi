package be.technifutur.kinomichi.stage;

import be.technifutur.kinomichi.exception.NotAnInstructorException;
import be.technifutur.kinomichi.exception.WeekdayException;
import be.technifutur.kinomichi.person.Person;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;

public class Session {
    private final String name;
    private LocalDateTime startDateTime;
    private Price price;
    private Person instructor;

    public Session(String name, LocalDateTime startDateTime, Price price, Person instructor) {
        this.name = Objects.requireNonNull(name);
        setStartDateTime(startDateTime);
        this.price = Objects.requireNonNull(price);
        setInstructor(instructor);

    }

    public String getName() {
        return name;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = Objects.requireNonNull(price);
    }

    private void setStartDateTime(LocalDateTime startDateTime) {
        Objects.requireNonNull(startDateTime);
        DayOfWeek day = startDateTime.getDayOfWeek();

        if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
            throw new WeekdayException("Une plage doit être programmée le samedi ou le dimanche");
        }
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    private void setInstructor(Person instructor) {
        Objects.requireNonNull(instructor);
        if (instructor.isInstructor()) {
            this.instructor = instructor;
        } else {
            throw new NotAnInstructorException(instructor.getFullName() + " n'est pas un formateur !");
        }
    }

    public Person getInstructor() {
        return instructor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;
        return name.equals(session.name) && startDateTime.equals(session.startDateTime);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + startDateTime.hashCode();
        return result;
    }
}
