package be.technifutur.kinomichi.person;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String firstName;
    private final String lastName;
    private String email;
    private String phoneNumber;
    private String club;
    private final boolean instructor;

    private final LocalDate dateOfBirth;

    public Person(String firstName, String lastName, String email, String phoneNumber, String club, LocalDate dateOfBirth, boolean instructor) {
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.club = club;
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth);
        this.instructor = instructor;
    }

    public int getAge() {
        try {
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public boolean isChild() {
        return getAge() < 18;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean isInstructor() {
        return instructor;
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
