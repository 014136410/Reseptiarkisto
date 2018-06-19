package reseptiarkisto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import reseptiarkisto.dao.AinesosaDao;
import reseptiarkisto.dao.AnnosAinesosaDao;
import reseptiarkisto.dao.AnnosDao;
import reseptiarkisto.database.Database;
import reseptiarkisto.AnnosAinesosa;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        
        Database database = new Database("jdbc:sqlite:reseptiarkisto.db");
        AnnosDao annokset = new AnnosDao(database);
        AinesosaDao ainesosat = new AinesosaDao(database);
        AnnosAinesosaDao ainekset = new AnnosAinesosaDao(database);
        
        System.out.println("Hello world!");

        
        Spark.get("/annokset", (req, res) -> {  
            
            HashMap map = new HashMap<>();
            map.put("annokset", annokset.findAll());

            return new ModelAndView(map, "annos");
        }, new ThymeleafTemplateEngine());
        
        
        Spark.post("/annokset/lisaa", (req, res) -> {
            Annos annos = new Annos(-1, req.queryParams("nimi"));
            annokset.saveOrUpdate(annos);

            res.redirect("/annokset");
            return "";
        });
        
        
        Spark.post("/annokset/delete/:id", (req, res) -> {
            String kysely = req.params(":id");
            annokset.delete(Integer.parseInt(kysely));

            System.out.println("poistettu!");
            
            res.redirect("/annokset");
            return "";
        });
        
        
        Spark.get("/ainekset/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            List<AnnosAinesosa> ainelista = new ArrayList<AnnosAinesosa>();
            
            Integer id = Integer.parseInt(req.params(":id"));
            Annos a = new Annos(id, annokset.findOne(id).nimi);
            
            map.put("annos", a);
            
//            List<String> aineet = new ArrayList<String>();
//            ainelista = ainekset.etsiAnnoksella(id);
//            for (AnnosAinesosa aaa : ainelista) {
//                Ainesosa n = ainesosat.findOne(aaa.ainesosaId());
//                aineet.add(n.getNimi());
//            }
            
            map.put("ainekset", ainelista);

            return new ModelAndView(map, "aines");
        }, new ThymeleafTemplateEngine());
        
        
        Spark.post("/ainekset/:id", (req, res) -> {
            Integer id = Integer.parseInt(req.params(":id"));
            Ainesosa a = new Ainesosa(-1, req.queryParams("nimi"));
            String maara = req.queryParams("maara");
            
            ainesosat.saveOrUpdate(a);
            
            Ainesosa b = ainesosat.findByName(a.nimi);
            AnnosAinesosa aa = new AnnosAinesosa(id, b.getId(), maara);
            ainekset.saveOrUpdate(aa);
            
            System.out.println("Lisätty!");
            
            res.redirect("/ainekset");
            return "";
        });
        
    }
    
    public static Connection getConnection() throws Exception {
        String dbUrl = System.getenv("HEROKU_POSTGRESQL_PINK_URL");
        if (dbUrl != null && dbUrl.length() > 0) {
            return DriverManager.getConnection(dbUrl);
        }
        
        System.out.println("Connected to the PostgreSQL server successfully.");

        return DriverManager.getConnection("jdbc:sqlite:reseptiarkisto.db");
    }

}
