package reseptiarkisto.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import reseptiarkisto.Annos;
import reseptiarkisto.database.Database;

public class AnnosDao implements Dao<Annos, Integer> {

    private Database database;

    public AnnosDao(Database database) {
        this.database = database;
    }

    @Override
    public Annos findOne(Integer key) throws SQLException {
        return findAll().stream().filter(u -> key.equals(u.getId())).findFirst().get();
    }

    @Override
    public List<Annos> findAll() throws SQLException {
        List<Annos> users = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db")) {
            ResultSet result = conn.prepareStatement("SELECT id, nimi FROM Annos").executeQuery();

            while (result.next()) {
                users.add(new Annos(result.getInt("id"), result.getString("nimi")));
            }
        }

        return users;
    }

    @Override
    public Annos saveOrUpdate(Annos object) throws SQLException {
//        Annos byName = findByName(object.getNimi());
//
//        if (byName != null) {
//            return byName;
//        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db")) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Annos (nimi) VALUES (?)");
            stmt.setString(1, object.getNimi());
            stmt.executeUpdate();
        }

        return findByName(object.getNimi());

    }

    private Annos findByName(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nimi FROM Annos WHERE nimi = ?");
            stmt.setString(1, name);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Annos(result.getInt("id"), result.getString("nimi"));
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db")) {
            PreparedStatement stmt
            = conn.prepareStatement("DELETE FROM Annos WHERE id = ?");
            stmt.setInt(1, key);

            stmt.executeUpdate();
            System.out.println("Poistettu!");
            
            conn.close();
        }
    }



}
