package be.technifutur.kinomichi.registration;

import be.technifutur.kinomichi.exception.KinomichiException;
import be.technifutur.kinomichi.exception.StageStatusException;
import be.technifutur.kinomichi.person.Person;
import be.technifutur.kinomichi.stage.Activity;
import be.technifutur.kinomichi.stage.Session;
import be.technifutur.kinomichi.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Registration {
    private final Person person;
    private final Stage stage;
    private List<Session> sessions;
    private List<Activity> activities;

    public Registration(List<Activity> activities, Person person, List<Session> sessions, Stage stage) {
        this.stage = Objects.requireNonNull(stage);
        if (!stage.isOpen()) {
            throw new StageStatusException("Le stage n'est pas ouvert.");
        }

        this.activities = activities;
        this.person = Objects.requireNonNull(person);
        this.sessions = sessions;
    }

    public boolean isEligibleToDiscount() {
        if (sessions == null || sessions.isEmpty()) {
            return false;
        }

        return stage.streamSessions().allMatch(sessions::contains);
    }

    public void addActivity(Activity activity) {
        Objects.requireNonNull(activity);

        if (!stage.isOpen()) {
            throw new StageStatusException("Le stage n'est pas ouvert.");
        }

        if (stage.streamActivities().noneMatch(a -> a.equals(activity))) {
            throw new KinomichiException("L'activité n'appartient pas au stage.");
        }

        if (this.activities == null) {
            this.activities = new ArrayList<>();
        }

        if (activities.contains(activity)) {
            throw new KinomichiException("Activité déjà ajoutée.");
        }

        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (!stage.isOpen()) {
            throw new StageStatusException("Le stage n'est pas ouvert.");
        }

        if (this.activities != null) {
            this.activities.remove(activity);
        }
    }

    public Stream<Activity> streamActivities() {
        return activities.stream();
    }

    public void addSession(Session session) {
        Objects.requireNonNull(session);

        if (!stage.isOpen()) {
            throw new StageStatusException("Le stage n'est pas ouvert.");
        }

        if (stage.streamSessions().noneMatch(s -> s.equals(session))) {
            throw new KinomichiException("La plage n'appartient pas au stage.");
        }

        if (session.getInstructor() != null && session.getInstructor().equals(person)) {
            throw new KinomichiException("Participant enregistré comme formateur pour cette session.");
        }

        if (this.sessions == null) {
            this.sessions = new ArrayList<>();
        }

        if (sessions.contains(session)) {
            throw new KinomichiException("Plage déjà ajoutée.");
        }

        sessions.add(session);
    }

    public void removeSession(Session session) {
        if (!stage.isOpen()) {
            throw new StageStatusException("Le stage n'est pas ouvert.");
        }

        if (this.sessions != null) {
            this.sessions.remove(session);
        }
    }

    public Stream<Session> streamSessions() {
        return sessions.stream();
    }

    public Person getPerson() {
        return person;
    }

    public Stage getStage() {
        return stage;
    }
}
