package be.technifutur.kinomichi.person;

import be.technifutur.kinomichi.exception.KinomichiException;
import be.technifutur.kinomichi.util.DateUtil;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import static be.technifutur.kinomichi.util.ConsoleUtil.*;
import static be.technifutur.kinomichi.util.DateUtil.isTodayOrFuture;

public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;
    private String club;
    private boolean instructor;


    public Person(String firstName, String lastName, LocalDate dateOfBirth, String email, String phoneNumber, String club, boolean instructor) {
        if (isTodayOrFuture(Objects.requireNonNull(dateOfBirth))) {
            throw new KinomichiException("La date de naissance doit être dans le passé.");
        }

        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.club = club;

        if (instructor && isChild(LocalDate.now())) {
            throw new KinomichiException("Un enfant ne peut pas être formateur !");
        } else {
            this.instructor = instructor;
        }
    }

    public int getAge(LocalDate eventDate) {
        try {
            return Period.between(dateOfBirth, eventDate).getYears();
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public boolean isChild(LocalDate eventDate) {
        return getAge(eventDate) < 18;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean isInstructor() {
        return instructor;
    }

    public void becameInstructor() {
        this.instructor = true;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setClub(String club) {
        this.club = club;
    }

    @Override
    public String toString() {
        String instructor = isInstructor() ? (YELLOW + " [ Formateur ]" + RESET) : "";
        return BOLD + getFullName() + RESET + instructor + "\n" +
                "  |  Age : " + getAge(LocalDate.now()) + " (" + DateUtil.format(dateOfBirth) + ")\n" +
                "  |  E-mail : " + email + "\n" +
                "  |  Téléphone : " + phoneNumber + "\n" +
                "  |  Club : " + club + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;
        return firstName.equals(person.firstName) && lastName.equals(person.lastName) && dateOfBirth.equals(person.dateOfBirth);
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + dateOfBirth.hashCode();
        return result;
    }
}
