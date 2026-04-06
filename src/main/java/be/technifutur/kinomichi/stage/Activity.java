package be.technifutur.kinomichi.stage;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Activity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Price price;

    public Activity(String name, Price price) {
        this.name = Objects.requireNonNull(name);
        this.price = Objects.requireNonNull(price);
    }

    public String getName() {
        return name;
    }

    public Price getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;
        return name.equals(activity.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
