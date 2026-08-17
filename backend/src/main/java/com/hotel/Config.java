package com.hotel;

/**
 * Configuration centrale de l'application.
 * Toutes les valeurs peuvent être surchargées par des variables d'environnement,
 * ce qui évite de mettre des identifiants en dur dans le code.
 */
public class Config {

    // ----- Base de données -----
    public static final String DB_URL =
        env("DB_URL", "jdbc:mysql://localhost:3306/hotel_reservation?useSSL=false&serverTimezone=UTC");
    public static final String DB_USER = env("DB_USER", "root");
    public static final String DB_PASSWORD = env("DB_PASSWORD", "");

    // ----- Serveur -----
    public static final int SERVER_PORT = Integer.parseInt(env("SERVER_PORT", "9090"));

    // ----- SMTP (envoi d'email) -----
    public static final String SMTP_HOST = env("SMTP_HOST", "smtp.gmail.com");
    public static final int SMTP_PORT = Integer.parseInt(env("SMTP_PORT", "587"));
    public static final String SMTP_USER = env("SMTP_USER", "");
    public static final String SMTP_PASSWORD = env("SMTP_PASSWORD", "");
    public static final boolean SMTP_ENABLED = SMTP_USER != null && !SMTP_USER.isBlank();

    private static String env(String key, String defaultValue) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? defaultValue : v;
    }
}
