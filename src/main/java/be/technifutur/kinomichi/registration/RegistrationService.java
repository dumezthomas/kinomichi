package be.technifutur.kinomichi.registration;

import be.technifutur.kinomichi.io.DataManager;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class RegistrationService {
    private static final String FILE = "registrations.dat";
    private final DataManager dataManager;
    private final List<Registration> registrations;

    public RegistrationService(DataManager dataManager) {
        this.dataManager = dataManager;
        this.registrations = load();
    }

    public boolean add(Registration registration) {
        if (registrations.contains(registration)) {
            return false;
        }

        registrations.add(registration);
        save();
        return true;
    }

    public boolean remove(Registration registration) {
        registrations.remove(registration);
        save();
        return true;
    }

    public List<Registration> getRegistrationsSorted(Comparator<Registration> comparator) {
        return registrations.stream().sorted(comparator).toList();
    }

    public List<Registration> getRegistrationsSortedAndFiltered(Comparator<Registration> comparator, Predicate<Registration> filter) {
        return registrations.stream().filter(filter).sorted(comparator).toList();
    }

    public List<Registration> getRegistrationsFiltered(Predicate<Registration> filter) {
        return registrations.stream().filter(filter).toList();
    }

    public boolean isRegistrationsEmpty() {
        return registrations.isEmpty();
    }

    public void save() {
        dataManager.save(registrations, FILE);
    }

    private List<Registration> load() {
        return dataManager.load(FILE);
    }
}
