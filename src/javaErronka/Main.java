package javaErronka;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        // Konfigurazio fitxategia irakurri
        ConfigReader config = new ConfigReader("C:\\Users\\Ikaslea\\eclipse-workspace\\ERRONKAJAVA\\fitxategiak\\konfigurazioa.txt");
        String dbUrl = config.getDbUrl();
        String dbUser = config.getDbUser();
        String dbPassword = config.getDbPassword();
        String xmlFilePath = config.getXmlFilePath();

        Connection connection = null; // Datu-base konekzioa
        Scanner scanner = new Scanner(System.in); // Sarrerako datuak irakurtzeko
        int choice = 0; // Erabiltzailearen aukera

        try {
            // Datu-basearekin konektatu
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Konektatua datu-basearekin!");

            while (choice != 3) { // Menuko aukera irakurri
                // Menua erakutsi
            	System.out.println("**************************************");
                System.out.println("1) Inportatu datuak");
                System.out.println("2) Exportatu datuak");
                System.out.println("3) Irten");
                System.out.print("Aukeratu aukera bat: ");
                // Erabiltzailearen aukera irakurri
                choice = scanner.nextInt();
                System.out.println();
            	System.out.println("**************************************");

                
                

                // Aukeratu aukeraren arabera prozesatu
                switch (choice) {
                    case 1:
                        // Datuak inportatzeko logika
                        System.out.println("Datuak inportatzen...");
                        try {
                            // XML fitxategia inportatu
                            XMLImporter importer = new XMLImporter(connection);
                            // Hemen zure inportazio metodoak deitu ditzakezu
                            importer.importHerriak(xmlFilePath);
                            importer.importProbintziak(xmlFilePath);
                            importer.importEtiketak(xmlFilePath);
                            importer.importKanpinak(xmlFilePath);
                            importer.importKanpinEtiketak(xmlFilePath);
                        } catch (SQLException e) {
                            System.err.println("Datu-basearekin konektatzeko akatsa: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Errore bat gertatu da: " + e.getMessage());
                        }
                        break;
                    case 2:
                        // Datuak esportatzeko logika
                        System.out.println("Datuak esportatzen...");
                        // Datuak CSV eta XML formatuan esportatu
                        try {
                            CSVExporter exporter = new CSVExporter(connection);
                            exporter.exportData("C:\\Users\\Ikaslea\\eclipse-workspace\\ERRONKAJAVA\\fitxategiak\\kanpinak_export.csv");

                            CampingXMLGenerator xmlExporter = new CampingXMLGenerator(connection);
                            xmlExporter.exportToXML("C:\\Users\\Ikaslea\\eclipse-workspace\\ERRONKAJAVA\\fitxategiak\\kanpinak_export.xml");
                        } catch (Exception e) {
                            System.err.println("Errore bat gertatu da: " + e.getMessage());
                        }
                        break;
                    case 3:
                        // Irten
                        System.out.println("Irten...");
                        break;
                    default:
                        System.out.println("Aukera baliogabea, mesedez saiatu berriro.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Datu-basearekin konektatzeko akatsa: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore bat gertatu da: " + e.getMessage());
        } finally {
            // Baliabideak itxi
            if (connection != null) {
                try {
                    connection.close(); // Datu-basea itxi
                    System.out.println("Konekzioa itxita.");
                } catch (SQLException e) {
                    System.err.println("Konekzioa itxitzean errorea: " + e.getMessage());
                }
            }
            scanner.close(); // Scanner-a itxi
        }
    }
}

	       
