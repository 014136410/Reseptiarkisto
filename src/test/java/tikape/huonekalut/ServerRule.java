package tikape.huonekalut;

import java.util.Random;
import org.junit.rules.ExternalResource;
import spark.Spark;

public class ServerRule extends ExternalResource {

    private int randomPort;

    public ServerRule() {
        Random random = new Random();
        this.randomPort = 1024 + random.nextInt(50000);
    }

    @Override
    protected void before() throws Throwable {
        Spark.port(this.randomPort);
        Main.main(new String[]{});
        Spark.awaitInitialization();
    }

    @Override
    protected void after() {
        stopServer();
    }

    public int getPort() {
        return this.randomPort;
    }

    // https://github.com/perwendel/spark/issues/731
    public static void stopServer() {
        try {
            Spark.stop();
            while (true) {
                try {
                    Spark.port();
                    Thread.sleep(50);
                } catch (final IllegalStateException ignored) {
                    break;
                }
            }
        } catch (final Exception ex) {
            // Ignore
        }
    }
}
