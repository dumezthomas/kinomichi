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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static be.technifutur.kinomichi.util.ConsoleUtil.*;
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

    public BigDecimal getRegistrationCost() {
        return getFinalSessionsCost().add(getActivitiesCost());
    }

    private BigDecimal getSessionsCost() {
        return sessions.stream()
                .map(s -> s.getPrice().forPerson(person, s.getStartDateTime().toLocalDate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getFinalSessionsCost() {
        return stage.getCappedPrice().compareTo(BigDecimal.ZERO) == 0 ?
                getSessionsCost() :
                getSessionsCost().min(stage.getCappedPrice());
    }

    private BigDecimal getActivitiesCost() {
        return activities.stream()
                .map(a -> a.getPrice().forPerson(person, stage.getStartDate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        BigDecimal discount = getSessionsCost().add(getActivitiesCost()).subtract(getRegistrationCost());
        String discountString = (discount.compareTo(BigDecimal.ZERO) > 0 ? GREEN : GRAY) + discount + "€" + RESET;

        String registration = BOLD + getName() + RESET + "\n"
                + String.format("  |  %-18s : %s%n", "Date du stage", formatWeekend(stage.getStartDate()))
                + String.format("  |  %-18s : %s%n", "Date réservation", format(createdAt))
                + String.format("  |  %-18s : %s (%s€)%n", "Sessions", sessions.size(), getSessionsCost())
                + String.format("  |  %-18s : %s (%s€)%n", "Activités", activities.size(), getActivitiesCost())
                + String.format("  |  %-18s : %s%n", "Réduction", discountString)
                + String.format("  |  %-18s : %s%n", "Coût total", BOLD + getRegistrationCost() + "€" + RESET);

        StringBuilder sbSessions = new StringBuilder();
        if (!sessions.isEmpty()) {
            sbSessions.append("\n");
            sbSessions.append(String.format("%-3s %-25s %-10s %-25s %-35s%n", "#", "Session", "Coût", "Formateur", "Horaire"));
            sbSessions.append("--------------------------------------------------------------------------------------------------------------------\n");

            for (int i = 0; i < sessions.size(); i++) {
                Session session = sessions.get(i);
                sbSessions.append(String.format("%-3s %-25s %-10s %-25s %-35s%n",
                        BOLD + (i + 1) + RESET + ". ",
                        session.getName(),
                        session.getPrice().forPerson(person, session.getStartDateTime().toLocalDate()) + "€",
                        session.getInstructor().getFullName(),
                        DateUtil.formatDateTime(session.getStartDateTime(), session.getDuration())));
            }
        }

        StringBuilder sbActivity = new StringBuilder();
        if (!activities.isEmpty()) {
            sbActivity.append("\n");
            sbActivity.append(String.format("%-3s %-25s %-10s%n", "#", "Activité", "Coût"));
            sbActivity.append("------------------------------------------\n");

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
