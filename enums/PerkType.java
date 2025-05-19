package enums;

public enum PerkType {
    ELEGAN("Elegan"),
    CHARMING("Charming"),
    ACTIVE("Active");

    private final String nama;

    PerkType(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

}
