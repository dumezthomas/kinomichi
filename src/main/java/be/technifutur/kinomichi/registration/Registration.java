package be.technifutur.kinomichi.registration;

import be.technifutur.kinomichi.exception.KinomichiException;
import be.technifutur.kinomichi.exception.StageStatusException;
import be.technifutur.kinomichi.person.Person;
import be.technifutur.kinomichi.stage.Activity;
import be.technifutur.kinomichi.stage.Session;
import be.technifutur.kinomichi.stage.Stage;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Registration implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Person person;
    private final Stage stage;
    private final List<Session> sessions = new ArrayList<>();
    private final List<Activity> activities = new ArrayList<>();
    private final LocalDateTime createdAt = LocalDateTime.now();
    private boolean paid = false;

    public Registration(Person person, Stage stage) {
        this.person = Objects.requireNonNull(person);
        this.stage = Objects.requireNonNull(stage);
        checkStageOpen();
    }

    public void addActivity(Activity activity) {
        checkStageOpen();

        if (!stage.getActivities().contains(activity)) {
            throw new KinomichiException("L'activité n'appartient pas au stage choisi.");
        }

        if (activities.contains(activity)) {
            throw new KinomichiException("Activité déjà réservée.");
        }

        this.activities.add(activity);
        this.activities.sort(Comparator.comparing(Activity::getName));
    }

    public void removeActivity(Activity activity) {
        checkStageOpen();
        this.activities.remove(activity);
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void addSession(Session session) {
        checkStageOpen();

        if (!stage.getSessions().contains(session)) {
            throw new KinomichiException("La session n'appartient pas au stage choisi.");
        }

        if (session.getInstructor().equals(person)) {
            throw new KinomichiException("Le participant est enregistré comme formateur pour cette session.");
        }

        if (sessions.contains(session)) {
            throw new KinomichiException("Session déjà réservée.");
        }

        this.sessions.add(session);
        this.sessions.sort(Comparator.comparing(Session::getStartDateTime));
    }

    public void removeSession(Session session) {
        checkStageOpen();
        this.sessions.remove(session);
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public Person getPerson() {
        return person;
    }

    public Stage getStage() {
        return stage;
    }

    public String getName() {
        return person.getFullName() + " - " + stage.getName();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    private void checkStageOpen() {
        if (!stage.isOpen()) {
            throw new StageStatusException("Le stage n'est pas en mode OPEN mais " + stage.getStatus() + ".");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Registration that = (Registration) o;
        return person.equals(that.person) && stage.equals(that.stage);
    }

    @Override
    public int hashCode() {
        int result = person.hashCode();
        result = 31 * result + stage.hashCode();
        return result;
    }
}
