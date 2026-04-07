package be.technifutur.kinomichi.stage;

import be.technifutur.kinomichi.exception.KinomichiException;
import be.technifutur.kinomichi.exception.StageStatusException;
import be.technifutur.kinomichi.util.DateUtil;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static be.technifutur.kinomichi.util.ConsoleUtil.*;
import static be.technifutur.kinomichi.util.DateUtil.*;

public class Stage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String shortDescription;
    private final LocalDate startDate;
    private final List<Session> sessions = new ArrayList<>();
    private final List<Activity> activities = new ArrayList<>();
    private BigDecimal cappedPrice;
    private StageStatus status = StageStatus.DRAFT;

    public Stage(String name, String shortDescription, LocalDate startDate, BigDecimal cappedPrice) {
        if (!isTodayOrFuture(Objects.requireNonNull(startDate))) {
            throw new KinomichiException("La date entrée est passée.");
        }
        if (!isSaturday(startDate)) {
            throw new KinomichiException("La date entrée n'est pas un samedi.");
        }

        this.startDate = startDate;
        this.name = Objects.requireNonNull(name);
        this.shortDescription = shortDescription;

        if (Objects.requireNonNull(cappedPrice).compareTo(BigDecimal.ZERO) > 0) {
            this.cappedPrice = cappedPrice;
        } else {
            this.cappedPrice = BigDecimal.ZERO;
        }
    }

    public boolean isOpen() {
        return status == StageStatus.OPEN;
    }

    public boolean isDraft() {
        return status == StageStatus.DRAFT;
    }

    public void open() {
        checkDraft();
        status = StageStatus.OPEN;
    }

    public void close() {
        status = StageStatus.CLOSED;
    }

    public void addActivity(Activity activity) {
        checkDraft();
        if (activities.contains(activity)) {
            throw new KinomichiException("Cette activité existe déjà.");
        }
        this.activities.add(activity);
        this.activities.sort(Comparator.comparing(Activity::getName));
    }

    public void removeActivity(Activity activity) {
        checkDraft();
        this.activities.remove(activity);
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public boolean isActivitiesEmpty() {
        return activities.isEmpty();
    }

    public boolean isActivityUnique(String name) {
        return activities.stream().noneMatch(activity -> activity.getName().equals(name));
    }

    public void addSession(Session session) {
        checkDraft();

        if (sessions.contains(session)) {
            throw new KinomichiException("Cette session existe déjà.");
        }

        if (!isWeekend(session.getStartDateTime())) {
            throw new KinomichiException("La date entrée n'est pas un samedi ou un dimanche.");
        }

        DayOfWeek day = session.getStartDateTime().getDayOfWeek();
        if (!canAddSession(day)) {
            if (day == DayOfWeek.SATURDAY) {
                throw new KinomichiException("Maximum 5 sessions le samedi.");
            } else {
                throw new KinomichiException("Maximum 3 sessions le dimanche.");
            }
        }

        checkNoOverlap(session);

        this.sessions.add(session);
        this.sessions.sort(Comparator.comparing(Session::getStartDateTime));
    }

    public void removeSession(Session session) {
        checkDraft();
        this.sessions.remove(session);
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public boolean isSessionsEmpty() {
        return sessions.isEmpty();
    }

    public boolean isSessionUnique(String name) {
        return sessions.stream().noneMatch(session -> session.getName().equals(name));
    }

    public boolean canAddSession(DayOfWeek day) {
        long count = sessions.stream()
                .filter(s -> s.getStartDateTime().getDayOfWeek() == day)
                .count();

        return switch (day) {
            case SATURDAY -> count < 5;
            case SUNDAY -> count < 3;
            default -> false;
        };
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public BigDecimal getCappedPrice() {
        return cappedPrice;
    }

    public StageStatus getStatus() {
        return status;
    }

    public void setCappedPrice(BigDecimal cappedPrice) {
        checkDraft();
        this.cappedPrice = cappedPrice;
    }

    private void checkDraft() {
        if (!isDraft()) {
            throw new StageStatusException("Le stage n'est pas en mode DRAFT mais " + status + ".");
        }
    }

    private void checkNoOverlap(Session newSession) {
        for (Session existing : sessions) {
            boolean overlap =
                    newSession.getStartDateTime().isBefore(existing.getEndDateTime())
                            && existing.getStartDateTime().isBefore(newSession.getEndDateTime());

            if (overlap) {
                throw new KinomichiException("La session chevauche une session existante.");
            }
        }
    }

    @Override
    public String toString() {
        String color = switch (status) {
            case OPEN -> GREEN;
            case DRAFT -> YELLOW;
            case CLOSED -> RED;
        };

        String stage = BOLD + name + RESET
                + color + " [" + status.toString().toUpperCase() + "]" + RESET
                + " : " + formatWeekend(startDate) + "\n"
                + "  |  Description : " + shortDescription + "\n"
                + "  |  Coût maximum : " + cappedPrice + "€\n"
                + "  |  Sessions : " + sessions.size() + "\n"
                + "  |  Activités : " + activities.size() + "\n";

        StringBuilder sbSessions = new StringBuilder();
        if (!sessions.isEmpty()) {
            sbSessions.append("\n");
            sbSessions.append(String.format("%-3s %-25s %-25s %-10s %-10s %-10s %-35s%n", "#", "Activité", "Formateur", "Adulte", "Enfant", "Formateur", "Date"));
            sbSessions.append("--------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < sessions.size(); i++) {
                Session session = sessions.get(i);
                sbSessions.append(String.format("%-3s %-25s %-25s %-10s %-10s %-10s %-35s%n",
                        BOLD + (i + 1) + RESET + ". ",
                        session.getName(),
                        session.getInstructor().getFullName(),
                        session.getPrice().adult(),
                        session.getPrice().child(),
                        session.getPrice().instructor(),
                        DateUtil.formatDateTime(session.getStartDateTime(), session.getDuration())));
            }
        }

        StringBuilder sbActivity = new StringBuilder();
        if (!activities.isEmpty()) {
            sbActivity.append("\n");
            sbActivity.append(String.format("%-3s %-25s %-12s %-12s %-12s%n", "#", "Activité", "Adulte", "Enfant", "Formateur"));
            sbActivity.append("-------------------------------------------------------------------\n");

            for (int i = 0; i < activities.size(); i++) {
                Activity activity = activities.get(i);
                sbActivity.append(String.format("%-3s %-25s %-12s %-12s %-12s%n",
                        BOLD + (i + 1) + RESET + ". ",
                        activity.getName(),
                        activity.getPrice().adult(),
                        activity.getPrice().child(),
                        activity.getPrice().instructor()));
            }
        }

        return stage + sbSessions + sbActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Stage stage = (Stage) o;
        return name.equals(stage.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
