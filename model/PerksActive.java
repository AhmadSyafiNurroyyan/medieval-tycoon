package model;

import enums.PerkType;

public class PerksActive extends Perk {

    public PerksActive() {
        super(PerkType.ACTIVE.getNama(), "Meningkatkan peluang untuk bertemu pembeli", PerkType.ACTIVE, 0, 0, 0);
    }

//    @Override
//    public double getPerkEffect() {
//        // perlu implementasi dari kelas pembeli 
//    }
//  
    @Override
    public boolean upgradeLevel() {
        if (level < MAX_LEVEL) {
            level++;
            double tambahKesaktian = kesaktianAwal * 0.1;
            kesaktianSekarang += tambahKesaktian;
            return true;
        } else {
            System.out.println("Level Perks sudah maksimal");
            return false;
        }
    }

    @Override
    public void tampilkanDetail() {
        System.out.println("Nama Perk: " + nama);
        System.out.println("Deskripsi Perk: " + deskripsi);
        System.out.println("Type Perk: " + type);
        System.out.println("Harga: " + harga);
        System.out.println("Kesaktian: " + kesaktianSekarang);
        System.out.println("Biaya Upgrade per Level: " + biayaUpgrade);
    }
}
