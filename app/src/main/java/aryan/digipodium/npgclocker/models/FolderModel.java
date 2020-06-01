package aryan.digipodium.npgclocker.models;

public class FolderModel {
    public String name;

    public FolderModel() {
    }

    public FolderModel(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
