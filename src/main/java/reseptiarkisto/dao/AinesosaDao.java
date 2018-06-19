package reseptiarkisto.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import reseptiarkisto.Ainesosa;
import reseptiarkisto.database.Database;

public class AinesosaDao implements Dao<Ainesosa, Integer> {

    private Database database;

    public AinesosaDao(Database database) {
        this.database = database;
    }

    @Override
    public Ainesosa findOne(Integer key) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Ainesosa WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Ainesosa a = new Ainesosa(rs.getInt("id"), rs.getString("nimi"));

        stmt.close();
        rs.close();
        conn.close();

        return a;
    }

    @Override
    public List<Ainesosa> findAll() throws SQLException {
        List<Ainesosa> tasks = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db")){
            ResultSet result = conn.prepareStatement("SELECT id, nimi FROM Ainesosa").executeQuery();

            while (result.next()) {
                tasks.add(new Ainesosa(result.getInt("id"), result.getString("nimi")));
            }
        }

        return tasks;
    }


    @Override
    public Ainesosa saveOrUpdate(Ainesosa object) throws SQLException {
        // simply support saving -- disallow saving if task with 
        // same name exists
        Ainesosa byName = findByName(object.getNimi());

        if (byName != null) {
            return byName;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db")) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Ainesosa (nimi) VALUES (?)");
            stmt.setString(1, object.getNimi());
            stmt.executeUpdate();
        }

        return findByName(object.getNimi());

    }

    public Ainesosa findByName(String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nimi FROM Ainesosa WHERE nimi = ?");
            stmt.setString(1, name);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Ainesosa(result.getInt("id"), result.getString("nimi"));
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
        
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Ainesosa WHERE id = ?");

        stmt.setInt(1, key);
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }


}
