package tikape.huonekalut;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        
        System.out.println("Hello world!");

        Spark.get("*", (req, res) -> {

            List<Huonekalu> huonekalut = new ArrayList<>();

            // avaa yhteys tietokantaan
            Connection conn
                    = DriverManager.getConnection("jdbc:sqlite:huonekalut.db");
            // tee kysely
            PreparedStatement stmt
                    = conn.prepareStatement("SELECT id, nimi FROM Huonekalu");
            ResultSet tulos = stmt.executeQuery();

            // käsittele kyselyn tulokset
            while (tulos.next()) {
                huonekalut.add(new Huonekalu(tulos.getInt("id"), tulos.getString("nimi")));
            }
            // sulje yhteys tietokantaan
            conn.close();

            HashMap map = new HashMap<>();

            map.put("lista", huonekalut);

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        Spark.post("/create", (req, res) -> {
            // avaa yhteys tietokantaan
            Connection conn
                    = DriverManager.getConnection("jdbc:sqlite:huonekalut.db");

            // tee kysely
            PreparedStatement stmt
                    = conn.prepareStatement("INSERT INTO Huonekalu (nimi) VALUES ");
            stmt.setString(1, req.queryParams("huonekalu"));

            stmt.executeUpdate();

            // sulje yhteys tietokantaan
            conn.close();

            res.redirect("/");
            return "";
        });

        Spark.post("/delete/:id", (req, res) -> {
            // avaa yhteys tietokantaan
            Connection conn
                    = DriverManager.getConnection("jdbc:sqlite:huonekalut.db");

            // tee kysely
            String kysely = req.params(":id");
            
            PreparedStatement stmt
            = conn.prepareStatement("DELETE FROM Huonekalu WHERE id  '" + kysely + "'");
            stmt.setInt(1, Integer.parseInt(kysely));

            stmt.executeUpdate();

            // sulje yhteys tietokantaan
            conn.close();

            res.redirect("/");
            return "";
        });
    }
    
    public static Connection getConnection() throws Exception {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if (dbUrl != null && dbUrl.length() > 0) {
            return DriverManager.getConnection(dbUrl);
        }

        return DriverManager.getConnection("jdbc:sqlite:huonekalut.db");
    }
    

}
