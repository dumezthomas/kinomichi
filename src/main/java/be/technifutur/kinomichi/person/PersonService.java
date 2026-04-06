package be.technifutur.kinomichi.person;

import be.technifutur.kinomichi.io.DataManager;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class PersonService {
    private static final String FILE = "people.dat";
    private final DataManager dataManager;
    private final List<Person> people;

    public PersonService(DataManager dataManager) {
        this.dataManager = dataManager;
        this.people = load();
    }

    public boolean add(Person person) {
        if (people.contains(person)) {
            return false;
        }

        people.add(person);
        save();
        return true;
    }

    public boolean remove(Person person) {
        people.remove(person);
        save();
        return true;
    }

    public List<Person> getPeopleSorted(Comparator<Person> comparator) {
        return people.stream().sorted(comparator).toList();
    }

    public List<Person> getPeopleSortedAndFiltered(Comparator<Person> comparator, Predicate<Person> filter) {
        return people.stream().filter(filter).sorted(comparator).toList();
    }

    public boolean isPeopleEmpty() {
        return people.isEmpty();
    }

    public void save() {
        dataManager.save(people, FILE);
    }

    private List<Person> load() {
        return dataManager.load(FILE);
    }
}
