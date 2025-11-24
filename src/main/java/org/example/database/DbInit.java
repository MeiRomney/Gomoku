package org.example.database;

import org.h2.tools.RunScript;
import org.h2.tools.Server;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Initializing database
 */
public class DbInit {
    static Server webServer;

    public static void initInMemoryDb() throws Exception {
        String jdbcUrl = "jdbc:h2:mem:gomoku;DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "")) {
            InputStream is = DbInit.class.getResourceAsStream("/createDbScript.txt");
            if(is == null) {
                throw new IllegalStateException("db creating script not found in resources");
            }
            RunScript.execute(conn, new InputStreamReader(is, StandardCharsets.UTF_8));
            webServer = Server.createWebServer("-web", "-webPort", "8082").start();
            System.out.println("You can see the db with H2 console started at: http://localhost:8082");
        }
    }

    /**
     * Get data from memory url
     */
    public static String getInMemoryUrl() {
        return "jdbc:h2:mem:gomoku;DB_CLOSE_DELAY=-1";
    }

    /**
     * Initialize database
     */
    public static void initDb() {
        try {
            initInMemoryDb();
            System.out.println("In-memory database initialized.");
        } catch(Exception e) {
            System.err.println("DB INIT FAILED: " + e.getMessage());
        }
    }

    /**
     * Stop the database
     */
    public static void stopWebServer() {
        if(webServer != null) {
            webServer.stop();
        }
    }
}
