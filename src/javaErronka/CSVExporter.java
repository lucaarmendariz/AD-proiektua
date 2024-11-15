package javaErronka;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class CSVExporter {
    private Connection connection;

    // Eraikitzailea: konexioa parametro gisa jasotzen du
    public CSVExporter(Connection connection) {
        this.connection = connection;  
    }

    // Datuak CSV fitxategi batera esportatzeko metodoa
    public void exportData(String csvFilePath) {
        // SQL kontsulta, eskatutako datu-eremuak lortzeko
        String sql = "SELECT K.KODEA, K.IZENA, K.KOKALEKUA, K.HELBIDEA, K.POSTAKODEA, " +
                     "H.IZENA AS HERRIA, P.IZENA AS PROBINTZIA, K.KATEGORIA, K.EDUKIERA " +
                     "FROM KANPINAK K " +
                     "JOIN HERRIAK H ON K.HERRI_KODEA = H.KODEA " +
                     "JOIN PROBINTZIAK P ON K.PROBINTZIA_KODEA = P.KODEA";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);
             FileWriter fileWriter = new FileWriter(csvFilePath)) {

            // CSV fitxategiaren goiburua idatzi
            fileWriter.append("Kodea,Izena,Kokalekua,Helbidea,PostaKodea,Herria,Probintzia,Kategoria,Edukiera\n");

            // ResultSet erregistroak irakurri eta CSV fitxategian idatzi
            while (resultSet.next()) {
                String kodea = resultSet.getString("KODEA");
                String izena = resultSet.getString("IZENA");
                String kokalekua = resultSet.getString("KOKALEKUA");
                String helbidea = resultSet.getString("HELBIDEA");
                
                // Helbide-eremuko komak kendu, CSV formatuan arazoak saihesteko
                String helbidea1 = helbidea.replaceAll(",", "");
                
                String postaKodea = resultSet.getString("POSTAKODEA");
                String herria = resultSet.getString("HERRIA");
                String probintzia = resultSet.getString("PROBINTZIA");
                String kategoria = resultSet.getString("KATEGORIA");
                int edukiera = resultSet.getInt("EDUKIERA");
                
                // Lortu diren balioak kontsolan bistaratu
                System.out.println(kodea + ":" + izena+ ":" + kokalekua+ ":" + helbidea1+ ":" + postaKodea+ ":" + herria+ ":" + probintzia+ ":" + kategoria );
                
                // Idatzi erregistro bakoitza CSV fitxategian, komak dituzten eremuetarako komatxoak erabiliz
                fileWriter.append(String.join(",",
                        kodea,
                        "\"" + izena + "\"",           // Komatxoak komak dituzten eremuetarako
                        "\"" + kokalekua + "\"",
                        "\"" + helbidea1 + "\"",
                        postaKodea.trim(),
                        "\"" + herria + "\"",
                        "\"" + probintzia + "\"",
                        kategoria,
                        String.valueOf(edukiera)));
                fileWriter.append("\n");
            }

            // CSV esportazioa ondo burutu dela adierazi
            System.out.println("Datuak CSVra esportatu dira behar bezala.");

        } catch (SQLException e) {
            // SQL errore bat gertatzen bada, mezua inprimatu eta errorearen arrastoa erakutsi
            System.out.println("Errorea datu-basearekin konexioan: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // CSV fitxategian idazterakoan errore bat gertatzen bada, mezua inprimatu eta errorearen arrastoa erakutsi
            System.out.println("Errorea CSV fitxategian idazterakoan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
