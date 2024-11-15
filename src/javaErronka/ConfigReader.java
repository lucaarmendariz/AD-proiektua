package javaErronka;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    // Fitxategitik irakurtzen diren konfigurazioak gordetzeko atributuak
    private String dbUrl;         // Datu-basearen URL-a gordetzeko atributua
    private String dbUser;        // Datu-basean sartzeko erabiltzaile izena gordetzeko atributua
    private String dbPassword;    // Datu-basean sartzeko pasahitza gordetzeko atributua
    private String xmlFilePath;   // XML fitxategiaren bidea gordetzeko atributua

    // Konfigurazio fitxategiaren ibilbidea hartzen duen eraikitzailea
    public ConfigReader(String configFilePath) throws IOException {
        Properties properties = new Properties(); // Konfigurazioak kargatzeko Properties objektua sortzen da
        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            // Propietate fitxategia irakurtzen da
            properties.load(reader);
            // Fitxategitik irakurri diren balioak atributu egokietara esleitzen dira
            dbUrl = properties.getProperty("db.url");
            dbUser = properties.getProperty("db.user");
            dbPassword = properties.getProperty("db.password");
            xmlFilePath = properties.getProperty("xml.file.path");
        }
    }

    // Datu-basearen URL-a itzultzen duen metodoa
    public String getDbUrl() {
        return dbUrl;
    }

    // Datu-basean sartzeko erabiltzaile izena itzultzen duen metodoa
    public String getDbUser() {
        return dbUser;
    }

    // Datu-basean sartzeko pasahitza itzultzen duen metodoa
    public String getDbPassword() {
        return dbPassword;
    }

    // XML fitxategiaren bidea itzultzen duen metodoa
    public String getXmlFilePath() {
        return xmlFilePath;
    }
}
