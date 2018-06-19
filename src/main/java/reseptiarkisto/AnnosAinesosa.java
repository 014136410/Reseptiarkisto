package reseptiarkisto;

import static java.util.Collections.list;

public class AnnosAinesosa {

    int annos_id;
    int ainesosa_id;
    String maara;
    Ainesosa aines;

    public AnnosAinesosa(int annos_id, int ainesosa_id, String maara) {
        this.annos_id = annos_id;
        this.ainesosa_id = ainesosa_id;
        this.maara = maara;
    }

    public int annosId() {
        return this.annos_id;
    }

    public int ainesosaId() {
        return this.ainesosa_id;
    }
    
    public String maara() {
        return this.maara;
    }
    
    public String toString() {
        return this.aines.getNimi() + ": " + this.maara;
    }
}
