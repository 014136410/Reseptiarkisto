package reseptiarkisto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import reseptiarkisto.dao.AinesosaDao;
import reseptiarkisto.dao.AnnosAinesosaDao;
import reseptiarkisto.dao.AnnosDao;
import reseptiarkisto.database.Database;
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
            Annos annos = new Annos(-1, req.queryParams("annos"));
            annokset.saveOrUpdate(annos);
            System.out.println(annos.getNimi());
            
            System.out.println("Lisätty!");

            res.redirect("/annokset");
            return "";
        });
        
        
        Spark.post("/annokset/delete/:id", (req, res) -> {
            String kysely = req.params(":id");
            annokset.delete(Integer.parseInt(kysely));

            System.out.println("Poistettu!");
            
            res.redirect("/annokset");
            return "";
        });
        
        
        Spark.get("/annokset/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            List<Annos> annoslista = new ArrayList<Annos>();
            List<AnnosAinesosa> annosaineslista = new ArrayList<AnnosAinesosa>();
            
            Integer id = Integer.parseInt(req.params(":id"));
            Ainesosa a = new Ainesosa(id, ainesosat.findOne(id).nimi);
            
            map.put("aines", a);
            
            annosaineslista = ainekset.etsiAinesosalla(id);
            
            for(AnnosAinesosa aa : annosaineslista) {
                Annos annos = new Annos(aa.annosId(), annokset.findOne(aa.annos_id).getNimi());
                annoslista.add(annos);
                System.out.println(annos.getNimi());
            }
            
            map.put("ainekset", annoslista);

            return new ModelAndView(map, "aines");
        }, new ThymeleafTemplateEngine());
        
        
        Spark.get("/ainekset/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            List<AnnosAinesosa> ainelista = new ArrayList<AnnosAinesosa>();
            List<Aines> aineet = new ArrayList<Aines>();
            
            Integer id = Integer.parseInt(req.params(":id"));
            Annos a = new Annos(id, annokset.findOne(id).nimi);
            
            map.put("annos", a);
            
            ainelista = ainekset.etsiAnnoksella(id);
            
            for(AnnosAinesosa aa : ainelista) {
                Aines aines = new Aines(ainesosat.findOne(aa.ainesosa_id).getNimi(), aa.maara());
                aineet.add(aines);
                System.out.println(aines.nimi());
            }
            
            map.put("ainekset", aineet);

            return new ModelAndView(map, "annosaines");
        }, new ThymeleafTemplateEngine());
        
        
        Spark.post("/ainekset/:id/lisaa", (req, res) -> {
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
