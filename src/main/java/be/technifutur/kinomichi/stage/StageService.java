package be.technifutur.kinomichi.stage;

import be.technifutur.kinomichi.io.DataManager;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class StageService {
    private static final String FILE = "stages.dat";
    private final DataManager dataManager;
    private final List<Stage> stages;

    public StageService(DataManager dataManager) {
        this.dataManager = dataManager;
        this.stages = load();
    }

    public boolean add(Stage stage) {
        if (stages.contains(stage)) {
            return false;
        }

        stages.add(stage);
        save();
        return true;
    }

    public boolean remove(Stage stage) {
        if (stage.getStatus() == StageStatus.OPEN) {
            return false;
        }

        stages.remove(stage);
        save();
        return true;
    }

    public List<Stage> getStagesSorted(Comparator<Stage> comparator) {
        return stages.stream().sorted(comparator).toList();
    }

    public List<Stage> getStagesSortedAndFiltered(Comparator<Stage> comparator, Predicate<Stage> filter) {
        return stages.stream().filter(filter).sorted(comparator).toList();
    }

    public boolean isStagesEmpty() {
        return stages.isEmpty();
    }

    public boolean isStageUnique(String name) {
        return stages.stream().noneMatch(stage -> stage.getName().equals(name));
    }

    public void save() {
        dataManager.save(stages, FILE);
    }

    private List<Stage> load() {
        return dataManager.load(FILE);
    }
}
