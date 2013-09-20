package features.steps;

import java.util.UUID;

public class CucumberSession {
    private static String username;
    private static UUID uuid;

    private CucumberSession() {};

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        CucumberSession.username = username;
    }

    public static UUID getUUID() {
        return uuid;
    }

    public static void setUUID(UUID UUID) {
        CucumberSession.uuid = UUID;
    }
}
