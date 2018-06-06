package tikape.huonekalut;

import fi.helsinki.cs.tmc.edutestutils.Points;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

@Points("05-01")
public class HuonekalutSQLInjektioTest extends FluentTest {

    public WebDriver webDriver = new HtmlUnitDriver();

    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }

    @ClassRule
    public static ServerRule server = new ServerRule();

    @Test
    public void contentFromDatabaseListed() throws SQLException {
        List<String> added = addContent(5);
        goTo("http://localhost:" + server.getPort() + "/");

        for (String item : added) {
            assertThat(pageSource()).contains(item);
        }

        removeContent(added);

        goTo("http://localhost:" + server.getPort() + "/");

        for (String item : added) {
            assertThat(pageSource()).doesNotContain(item);
        }
    }

    @Test
    public void canRemoveASingleItem() throws Exception {
        List<String> added = addContent(3);
        goTo("http://localhost:" + server.getPort() + "/");

        for (String item : added) {
            assertThat(pageSource()).contains(item);
        }

        Collections.shuffle(added);

        for (String item : added) {
            int id = getId(item);
            postToUrl("http://localhost:" + server.getPort() + "/delete/" + id);

            goTo("http://localhost:" + server.getPort() + "/");
            assertThat(pageSource()).doesNotContain(item);
        }

    }

    @Test
    public void canInjectSQLIntoUrl() throws Exception {
        List<String> added = addContent(5);
        goTo("http://localhost:" + server.getPort() + "/");

        for (String item : added) {
            assertThat(pageSource()).contains(item);
        }

        postToUrl("http://localhost:" + server.getPort() + "/delete/1%20OR%201=1");

        goTo("http://localhost:" + server.getPort() + "/");

        for (String item : added) {
            assertThat(pageSource()).doesNotContain(item);
        }
    }

    @Test
    public void canInjectSQLIntoUrl2() throws Exception {
        List<String> added = addContent(5);
        goTo("http://localhost:" + server.getPort() + "/");

        for (String item : added) {
            assertThat(pageSource()).contains(item);
        }

        Collections.shuffle(added);
        int firstId = getId(added.get(0));
        int secondId = getId(added.get(1));

        postToUrl("http://localhost:" + server.getPort() + "/delete/" + firstId + "%20OR%20id=" + secondId);

        goTo("http://localhost:" + server.getPort() + "/");

        assertThat(pageSource()).doesNotContain(added.get(0));
        assertThat(pageSource()).doesNotContain(added.get(1));

        added.remove(0);
        added.remove(0);

        for (String item : added) {
            assertThat(pageSource()).contains(item);
        }
    }

    private List<String> addContent(int howMany) throws SQLException {
        List<String> addedContent = new ArrayList<>();
        File dbFile = new File("huonekalut.db");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath())) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Huonekalu (nimi) VALUES (?)");

            for (int i = 0; i < howMany; i++) {
                String randomName = UUID.randomUUID().toString().substring(0, 6);
                stmt.setString(1, randomName);
                stmt.execute();

                addedContent.add(randomName);
            }
        }

        return addedContent;

    }

    private void removeContent(List<String> content) throws SQLException {
        File dbFile = new File("huonekalut.db");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath())) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Huonekalu WHERE nimi = ?");

            for (String item : content) {
                stmt.setString(1, item);
                stmt.execute();

            }
        }
    }

    private int getId(String item) throws SQLException {
        File dbFile = new File("huonekalut.db");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath())) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Huonekalu WHERE nimi = ?");

            stmt.setString(1, item);
            ResultSet res = stmt.executeQuery();

            if (!res.next()) {
                return -1;
            }

            return res.getInt("id");
        }
    }

    public String postToUrl(String url) throws Exception {
        URL address = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) address.openConnection();
        conn.setRequestMethod("POST");

        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write("".getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder res = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                res.append(inputLine);
            }
            in.close();

            return res.toString();
        } else {
            return "INVALID REQUEST";
        }
    }
}
