/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseptiarkisto;


public class Aines {
    
    String maara;
    String nimi;

    public Aines(String nimi, String maara) {
        this.nimi = nimi;
        this.maara = maara;
    }

    public String nimi() {
        return this.nimi;
    }
    
    public String maara() {
        return this.maara;
    }
    
    public String toString() {
        return this.nimi + ": " + this.maara;
    }
}
