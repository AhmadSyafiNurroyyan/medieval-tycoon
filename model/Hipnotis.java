package model;

public class Hipnotis extends Item  {        
        private boolean sudahDipakai;
    
        public Hipnotis() {
            super("Hipnotis", "Meningkatkan peluang pembeli langsung membeli tanpa menawar (+40%)", 5000, 1000, "icons/hipnotis.png");
            sudahDipakai = false;
        }
    
        public boolean gunakan() {
            if (!sudahDipakai) {
                sudahDipakai = true;
                return true;
            }
            return false;
        }
    
        public void resetPenggunaan() {
            sudahDipakai = false;
        }
    }
   