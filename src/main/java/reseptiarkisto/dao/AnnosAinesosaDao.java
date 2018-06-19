package reseptiarkisto.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import reseptiarkisto.AnnosAinesosa;
import reseptiarkisto.database.Database;

public class AnnosAinesosaDao implements Dao<AnnosAinesosa, Integer> {

    private Database database;

    public AnnosAinesosaDao(Database database) {
        this.database = database;
    }

    public AnnosAinesosa findOne(Integer annos, Integer ainesosa) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AnnosAinesosa"
                + " WHERE annos_id = ? AND ainesosa_id = ?");
        stmt.setInt(1, annos);
        stmt.setInt(2, ainesosa);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        AnnosAinesosa a = new AnnosAinesosa(rs.getInt("annos_id"), rs.getInt("ainesosa_id"), rs.getString("maara"));

        stmt.close();
        rs.close();
        conn.close();

        return a;

    }

    public List<AnnosAinesosa> etsiAnnoksella(Integer annos) throws SQLException {
        List<AnnosAinesosa> lista = new ArrayList<>();
        
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AnnosAinesosa WHERE annos_id = ?");
        stmt.setInt(1, annos);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
             AnnosAinesosa a = new AnnosAinesosa(rs.getInt("annos_id"), rs.getInt("ainesosa_id"), rs.getString("maara"));
             lista.add(a);
        }

        stmt.close();
        rs.close();
        conn.close();

        return lista;
    
    }
    
    public List<AnnosAinesosa> etsiAinesosalla(Integer ainesosa) throws SQLException {
        List<AnnosAinesosa> lista = new ArrayList<>();
        
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AnnosAinesosa WHERE ainesosa_id = ?");
        stmt.setInt(1, ainesosa);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
             AnnosAinesosa a = new AnnosAinesosa(rs.getInt("annos_id"), rs.getInt("ainesosa_id"), rs.getString("maara"));
             lista.add(a);
        }

        stmt.close();
        rs.close();
        conn.close();

        return lista;
    
    }
    
    
    @Override
    public List<AnnosAinesosa> findAll() throws SQLException {
        List<AnnosAinesosa> lista = new ArrayList<>();
        
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AnnosAinesosa");
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
             AnnosAinesosa a = new AnnosAinesosa(rs.getInt("annos_id"), rs.getInt("ainesosa_id"), rs.getString("maara"));
             lista.add(a);
        }

       
        stmt.close();
        rs.close();
        conn.close();

        return lista;
    
    }

    @Override
    public AnnosAinesosa saveOrUpdate(AnnosAinesosa object) throws SQLException {
        
            List<AnnosAinesosa> lista = findAll();
            
            for (AnnosAinesosa a : lista) {
                Integer aines = a.ainesosaId();
                Integer annos = a.annosId();
                
                if (object.ainesosaId() == aines) {
                    if (object.annosId() == annos) {
                        return update(object);
                    }
                }
            }
            
            return save(object);
            
    }
    
    private AnnosAinesosa save(AnnosAinesosa a) throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
        
        PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO AnnosAinesosa (annos_id, ainesosa_id, maara) VALUES (?, ?, ?)");
        stmt.setInt(1, a.annosId());
        stmt.setInt(2, a.ainesosaId());
        stmt.setString(3, a.maara());

        stmt.executeUpdate();
        stmt.close();

        stmt = conn.prepareStatement("SELECT * FROM AnnosAinesosa"
                + " WHERE annos_id = ? AND ainesosa_id = ?");
        stmt.setInt(1, a.annosId());
        stmt.setInt(2, a.ainesosaId());

        ResultSet rs = stmt.executeQuery();
        rs.next(); // vain 1 tulos

        AnnosAinesosa t = new AnnosAinesosa(rs.getInt("annos_id"), rs.getInt("ainesosa_id"), rs.getString("maara"));

        stmt.close();
        rs.close();

        conn.close();

        return t;
    }

    private AnnosAinesosa update(AnnosAinesosa a) throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
        PreparedStatement stmt = conn.prepareStatement("UPDATE AnnosAinesosa SET maara = ?");
        stmt.setString(1, a.maara());

        stmt.executeUpdate();

        stmt.close();
        conn.close();

        return a;
    }
    

    public void delete(Integer annos, Integer ainesosa) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM AnnosAinesosa"
                + " WHERE annos_id = ? AND ainesosa_id = ?");
        stmt.setInt(1, annos);
        stmt.setInt(2, ainesosa);
        
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AnnosAinesosa findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
