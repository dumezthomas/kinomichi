package be.technifutur.kinomichi.stage;

import be.technifutur.kinomichi.exception.StageStatusException;
import be.technifutur.kinomichi.exception.TooManySessionsException;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Stage {
    private final String name;
    private final String shortDescription;
    private List<Session> sessions;
    private List<Activity> activities;
    private final int discountPercentage;
    private StageStatus status;

    public Stage(String name, String shortDescription, int discountPercentage) {
        this.name = Objects.requireNonNull(name);
        this.shortDescription = Objects.requireNonNull(shortDescription);
        this.discountPercentage = discountPercentage;
        this.status = StageStatus.DRAFT;
    }

    public void open() {
        if (status != StageStatus.DRAFT) {
            throw new StageStatusException("Stage déjà ouvert ou fermé.");
        }
        status = StageStatus.OPEN;
    }

    public void close() {
        status = StageStatus.CLOSED;
    }

    public boolean isStageOpen() {
        return status == StageStatus.OPEN;
    }

    public void addActivity(Activity activity) {
        Objects.requireNonNull(activity);

        if (status != StageStatus.DRAFT) {
            throw new StageStatusException("Stage déjà ouvert ou fermé.");
        }

        if (this.activities == null) {
            this.activities = new ArrayList<>();
        }
        this.activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (status != StageStatus.DRAFT) {
            throw new StageStatusException("Stage déjà ouvert ou fermé.");
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

        if (status != StageStatus.DRAFT) {
            throw new StageStatusException("Stage déjà ouvert ou fermé.");
        }

        if (this.sessions == null) {
            this.sessions = new ArrayList<>();
        }

        DayOfWeek day = session.getStartDateTime().getDayOfWeek();

        long count = sessions.stream()
                .filter(s -> s.getStartDateTime().getDayOfWeek() == day)
                .count();

        if (day == DayOfWeek.SATURDAY && count >= 5) {
            throw new TooManySessionsException("Maximum 5 sessions le samedi.");
        }

        if (day == DayOfWeek.SUNDAY && count >= 3) {
            throw new TooManySessionsException("Maximum 3 sessions le dimanche.");
        }

        this.sessions.add(session);
    }

    public void removeSession(Session session) {
        if (status != StageStatus.DRAFT) {
            throw new StageStatusException("Stage déjà ouvert ou fermé.");
        }

        if (this.sessions != null) {
            this.sessions.remove(session);
        }
    }

    public Stream<Session> streamSessions() {
        return sessions.stream();
    }

    public String getName() {
        return name;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public String toString() {
        return name + " [" + status.toString().toUpperCase() + "]\n"
                + shortDescription + "\n"
                + " - Réduction éventuelle : " + discountPercentage + "%\n"
                + " - Plages :\n" + sessions
                + " - Activités :\n" + activities;
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
