package be.technifutur.kinomichi.stage;

import be.technifutur.kinomichi.exception.KinomichiException;
import be.technifutur.kinomichi.exception.StageStatusException;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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
    private StageStatus status;

    public Stage(String name, String shortDescription, LocalDate startDate, BigDecimal cappedPrice) {
        if (!isTodayOrFuture(Objects.requireNonNull(startDate))) {
            throw new KinomichiException("La date entrée est passée.");
        }
        if (!isSaturday(Objects.requireNonNull(startDate))) {
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

        this.status = StageStatus.DRAFT;
    }

    public boolean isOpen() {
        return status == StageStatus.OPEN;
    }

    public boolean isDraft() {
        return status == StageStatus.DRAFT;
    }

    public void open() {
        if (!isDraft()) {
            throw new StageStatusException("Le stage n'est pas en mode DRAFT mais " + status.toString() + ".");
        }
        status = StageStatus.OPEN;
    }

    public void close() {
        status = StageStatus.CLOSED;
    }

    public void addActivity(Activity activity) {
        if (!isDraft()) {
            throw new StageStatusException("Le stage n'est pas en mode DRAFT mais " + status.toString() + ".");
        }

        LocalDate startDate = activity.getStartDateTime().toLocalDate();
        if (!isSaturday(startDate)) {
            throw new KinomichiException("La date entrée n'est pas un samedi.");
        }

        this.activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (!isDraft()) {
            throw new StageStatusException("Le stage n'est pas en mode DRAFT mais " + status.toString() + ".");
        }

        this.activities.remove(activity);
    }

    public Stream<Activity> streamActivities() {
        return activities.stream();
    }

    public void addSession(Session session) {
        if (!isDraft()) {
            throw new StageStatusException("Le stage n'est pas en mode DRAFT mais " + status.toString() + ".");
        }

        LocalDate startDate = session.getStartDateTime().toLocalDate();
        if (!isSaturday(startDate) && !isSunday(startDate)) {
            throw new KinomichiException("La date entrée n'est pas un samedi ou un dimanche.");
        }

        DayOfWeek day = startDate.getDayOfWeek();

        long count = sessions.stream()
                .filter(s -> s.getStartDateTime().getDayOfWeek() == day)
                .count();

        if (day == DayOfWeek.SATURDAY && count >= 5) {
            throw new KinomichiException("Maximum 5 sessions le samedi.");
        }

        if (day == DayOfWeek.SUNDAY && count >= 3) {
            throw new KinomichiException("Maximum 3 sessions le dimanche.");
        }

        this.sessions.add(session);
    }

    public void removeSession(Session session) {
        if (!isDraft()) {
            throw new StageStatusException("Le stage n'est pas en mode DRAFT mais " + status.toString() + ".");
        }

        this.sessions.remove(session);
    }

    public Stream<Session> streamSessions() {
        return sessions.stream();
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
        if (!isDraft()) {
            throw new StageStatusException("Le stage n'est pas en mode DRAFT mais " + status.toString() + ".");
        }

        this.cappedPrice = cappedPrice;
    }

    @Override
    public String toString() {
        String color = switch (status) {
            case OPEN -> GREEN;
            case DRAFT -> YELLOW;
            case CLOSED -> RED;
        };

        return BOLD + name + RESET
                + color + " [" + status.toString().toUpperCase() + "]" + RESET
                + " : " + formatWeekend(startDate) + "\n"
                + "  |  Description : " + shortDescription + "\n"
                + "  |  Prix maximum : " + cappedPrice + "€\n"
                + "  |  Sessions :\n" + sessions
                + "  |  Activités :\n" + activities;
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
