package tikape.huonekalut;

public class Huonekalu {

    int id;
    String nimi;

    public Huonekalu(int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }

    public int getId() {
        return id;
    }

    public String getNimi() {
        return nimi;
    }

}
