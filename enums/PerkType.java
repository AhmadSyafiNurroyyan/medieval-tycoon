/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

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
