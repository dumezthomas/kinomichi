package be.technifutur.kinomichi.registration;

import be.technifutur.kinomichi.exception.KinomichiException;
import be.technifutur.kinomichi.exception.StageStatusException;
import be.technifutur.kinomichi.person.Person;
import be.technifutur.kinomichi.stage.Activity;
import be.technifutur.kinomichi.stage.Session;
import be.technifutur.kinomichi.stage.Stage;
import be.technifutur.kinomichi.util.DateUtil;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static be.technifutur.kinomichi.util.ConsoleUtil.BOLD;
import static be.technifutur.kinomichi.util.ConsoleUtil.RESET;
import static be.technifutur.kinomichi.util.DateUtil.format;
import static be.technifutur.kinomichi.util.DateUtil.formatWeekend;

public class Registration implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Person person;
    private final Stage stage;
    private final List<Session> sessions = new ArrayList<>();
    private final List<Activity> activities = new ArrayList<>();
    private final LocalDate createdAt = LocalDate.now();

    public Registration(Person person, Stage stage) {
        this.person = Objects.requireNonNull(person);
        this.stage = Objects.requireNonNull(stage);
        checkStageOpen();
    }

    public void registerActivity(Activity activity) {
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

    public void unregisterActivity(Activity activity) {
        checkStageOpen();
        this.activities.remove(activity);
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public List<Activity> getAvailableActivities() {
        return stage.getActivities().stream()
                .filter(a -> !activities.contains(a))
                .toList();
    }

    public void registerSession(Session session) {
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

    public void unregisterSession(Session session) {
        checkStageOpen();
        this.sessions.remove(session);
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<Session> getAvailableSessions() {
        return stage.getSessions().stream()
                .filter(s -> !sessions.contains(s))
                .filter(s -> !s.getInstructor().equals(person))
                .toList();
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

    private void checkStageOpen() {
        if (!stage.isOpen()) {
            throw new StageStatusException("Le stage n'est pas en mode OPEN mais " + stage.getStatus() + ".");
        }
    }

    @Override
    public String toString() {
        String registration = BOLD + getName() + RESET + "\n"
                + "  |  Date du stage : " + formatWeekend(stage.getStartDate()) + "\n"
                + "  |  Date de la réservation : " + format(createdAt) + "\n"
                + "  |  Sessions : " + sessions.size() + "\n"
                + "  |  Activités : " + activities.size() + "\n";

        StringBuilder sbSessions = new StringBuilder();
        if (!sessions.isEmpty()) {
            sbSessions.append("\n");
            sbSessions.append(String.format("%-3s %-25s %-25s %-10s %-35s%n", "#", "Session", "Formateur", "Coût", "Horaire"));
            sbSessions.append("--------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < sessions.size(); i++) {
                Session session = sessions.get(i);
                sbSessions.append(String.format("%-3s %-25s %-25s %-10s %-35s%n",
                        BOLD + (i + 1) + RESET + ". ",
                        session.getName(),
                        session.getInstructor().getFullName(),
                        session.getPrice().forPerson(person, session.getStartDateTime().toLocalDate()) + "€",
                        DateUtil.formatDateTime(session.getStartDateTime(), session.getDuration())));
            }
        }

        StringBuilder sbActivity = new StringBuilder();
        if (!activities.isEmpty()) {
            sbActivity.append("\n");
            sbActivity.append(String.format("%-3s %-25s %-10s%n", "#", "Activité", "Coût"));
            sbActivity.append("-------------------------------------------------------------------\n");

            for (int i = 0; i < activities.size(); i++) {
                Activity activity = activities.get(i);
                sbActivity.append(String.format("%-3s %-25s %-10s%n",
                        BOLD + (i + 1) + RESET + ". ",
                        activity.getName(),
                        activity.getPrice().forPerson(person, stage.getStartDate()) + "€"));
            }
        }

        return registration + sbSessions + sbActivity;
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
