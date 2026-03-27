package be.technifutur.kinomichi.stage;

import be.technifutur.kinomichi.exception.WeekdayException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;

public class Activity {
    private final String name;
    private LocalDateTime startDateTime;
    private Price price;

    public Activity(String name, LocalDateTime startDateTime, Price price) {
        this.name = Objects.requireNonNull(name);
        setStartDateTime(startDateTime);
        this.price = Objects.requireNonNull(price);
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

        if (day != DayOfWeek.SATURDAY) {
            throw new WeekdayException("Une activité doit être programmée le samedi");
        }
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;
        return name.equals(activity.name) && startDateTime.equals(activity.startDateTime);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + startDateTime.hashCode();
        return result;
    }
}
