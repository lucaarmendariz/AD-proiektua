package javaErronka;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLImporter {
    private Connection connection;

    public XMLImporter(Connection connection) {
        this.connection = connection;
    }

    public void importHerriak (String xmlFilePath) throws NumberFormatException, Exception {
        PreparedStatement pstmt = null;

        try {
            // XML fitxategia kargatu eta aztertu
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // XML-ko row guztiak (datuak) lortu
            NodeList nList = document.getElementsByTagName("row");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    // <municipality> eta <municipalitycode> elementuak hautatu
                    String udalerri = eElement.getElementsByTagName("municipality").item(0).getTextContent();
                    String udalerriKode = eElement.getElementsByTagName("municipalitycode").item(0).getTextContent();

                    try {
                        // Udalerriaren existitzen den egiaztatu
                        if (!udalerriExists(connection, Integer.parseInt(udalerriKode))) {
                            // Udalerri ez badago, txertatu
                            String sql = "INSERT INTO HERRIAK (KODEA, IZENA) VALUES (?, ?)";
                            pstmt = connection.prepareStatement(sql);
                            pstmt.setInt(1, Integer.parseInt(udalerriKode)); // municipalitycode osoa bihurtu
                            pstmt.setString(2, udalerri);
                            pstmt.executeUpdate();
                            System.out.println("Udalerria sartuta: " + udalerri + " Hurrengo kodearekin: " + udalerriKode);
                        } else {
                            // Udalerria dagoela adierazi
                            System.out.println("Udalerria ya existitzen da: " + udalerri);
                        }
                    } catch (SQLException e) {
                        // Errorea sartzean
                        System.err.println("Errorea Udalerria sartzerakoan: " + udalerri + ". " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            // Errorea XML fitxategia prozesatzerakoan
            System.err.println("Errorea XML fitxategia prozesatzerakoan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Udalerri baten existitzen den egiaztatzeko metodoa
    private static boolean udalerriExists(Connection connection, int udalerriKode) throws Exception {
        String sql = "SELECT KODEA FROM HERRIAK WHERE KODEA = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, udalerriKode);
        ResultSet rs = pstmt.executeQuery();
        return rs.next(); // Emaitza badago, udalerria existitzen da
    }

    
    public void importProbintziak (String xmlFilePath) throws NumberFormatException, Exception {
        PreparedStatement pstmt = null;

        try {
            // XML fitxategia kargatu eta aztertu
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // XML-ko row guztiak (datuak) lortu
            NodeList nList = document.getElementsByTagName("row");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    // <territory> eta <territorycode> elementuak hautatu (probintzia eta probintziaKode)
                    String probintzia = eElement.getElementsByTagName("territory").item(0).getTextContent();
                    String probintziaKode = eElement.getElementsByTagName("territorycode").item(0).getTextContent();

                    try {
                        // Probintzia existitzen den egiaztatu
                        if (!probintziaExists(connection, Integer.parseInt(probintziaKode))) {
                            // Probintzia ez badago, txertatu
                            String sql = "INSERT INTO PROBINTZIAK (KODEA, IZENA) VALUES (?, ?)";
                            pstmt = connection.prepareStatement(sql);
                            pstmt.setInt(1, Integer.parseInt(probintziaKode)); // probintziaKode osoa bihurtu
                            pstmt.setString(2, probintzia);
                            pstmt.executeUpdate();
                            System.out.println("Probintzia sartuta: " + probintzia + " Hurrengo kodearekin: " + probintziaKode);
                        } else {
                            // Probintzia dagoela adierazi
                            System.out.println("Probintzia ya existitzen da: " + probintzia);
                        }
                    } catch (SQLException e) {
                        // Errorea sartzean
                        System.err.println("Errorea Probintzia sartzerakoan: " + probintzia + ". " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            // Errorea XML fitxategia prozesatzerakoan
            System.err.println("Errorea XML fitxategia prozesatzerakoan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Probintzia baten existitzen den egiaztatzeko metodoa
    private static boolean probintziaExists(Connection connection, int probintziaKode) throws Exception {
        String sql = "SELECT KODEA FROM PROBINTZIAK WHERE KODEA = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, probintziaKode);
        ResultSet rs = pstmt.executeQuery();
        return rs.next(); // Emaitza badago, probintzia existitzen da
    }

    
    
    public void importEtiketak(String xmlFilePath) throws Exception {
        PreparedStatement pstmt = null;

        try {
            // XML fitxategia kargatu eta aztertu
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // XML-ko row guztiak (datuak) lortu
            NodeList nList = document.getElementsByTagName("row");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    // Dokumentuaren izena hautatu
                    String documentName = getTextContentOrDefault(eElement, "marks", null);
                    
                    // Etiketa existitzen den egiaztatu (aukerakoa)
                    try {
                        // Etiketa datu-basean txertatu
                        String sql = "INSERT INTO ETIKETAK (ETIKETA) VALUES (?)";
                        pstmt = connection.prepareStatement(sql);
                        pstmt.setString(1, documentName); // documentName erabiltzen da ETIKETA gisa
                        pstmt.executeUpdate();
                        System.out.println("Etiqueta sartuta: " + documentName);
                    } catch (SQLException e) {
                        // Errorea etiketa sartzean
                        System.err.println("Errorea Etiketa sartzerakoan: " + documentName + ". " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            // Errorea XML fitxategia prozesatzerakoan
            System.err.println("Errorea XML fitxategia prozesatzerakoan: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // PreparedStatement-a itxi
            if (pstmt != null) {
                try {
                    pstmt.close(); // PreparedStatement-a itxi
                } catch (SQLException e) {
                    // Errorea PreparedStatement-a itxi ezin izan delako
                    System.err.println("Error al cerrar el PreparedStatement: " + e.getMessage());
                }
            }
        }
    }
    
    public void importKanpinak(String xmlFilePath) throws Exception {
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtCheck = null;
        try {
            // XML fitxategia kargatu eta aztertu
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // XML-ko row guztiak (datuak) lortu
            NodeList nList = document.getElementsByTagName("row");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    // XML-tik beharrezko eremuak hautatu
                    String kodea = getTextContentOrDefault(eElement, "signatura", null);
                    String izena = getTextContentOrDefault(eElement, "documentName", null);
                    String deskribapena = getTextContentOrDefault(eElement, "documentDescription", null);
                    String kokalekua = getTextContentOrDefault(eElement, "country", null);
                    String telefonoa = getTextContentOrDefault(eElement, "phone", null);
                    String helbidea = getTextContentOrDefault(eElement, "address", null);
                    String emaila = getTextContentOrDefault(eElement, "tourismEmail", null);
                    String webgunera = getTextContentOrDefault(eElement, "web", null);
                    String kategoria = getTextContentOrDefault(eElement, "category", null);
                    int edukiera = Integer.parseInt(getTextContentOrDefault(eElement, "capacity", "0")); // Ez badago, 0 balio bezala
                    String postakodea = getTextContentOrDefault(eElement, "postalCode", null);
                    int herriKodea = Integer.parseInt(getTextContentOrDefault(eElement, "municipalitycode", "0")); // Ez badago, 0 balio bezala
                    int probintziaKodea = Integer.parseInt(getTextContentOrDefault(eElement, "territorycode", "0")); // Ez badago, 0 balio bezala
                    String friendlyUrl = getTextContentOrDefault(eElement, "friendlyUrl", null);
                    String physicalUrl = getTextContentOrDefault(eElement, "physicalUrl", null);
                    String dataXml = getTextContentOrDefault(eElement, "dataXML", null);
                    String metadataXml = getTextContentOrDefault(eElement, "metadataXML", null);
                    String zipFile = getTextContentOrDefault(eElement, "zipFile", null);

                    try {
                        // Erregistroa existitzen den egiaztatu
                        String checkSql = "SELECT COUNT(*) FROM KANPINAK WHERE KODEA = ?";
                        pstmtCheck = connection.prepareStatement(checkSql);
                        pstmtCheck.setString(1, kodea);
                        
                        ResultSet rs = pstmtCheck.executeQuery();
                        rs.next();
                        int count = rs.getInt(1);

                        if (count == 0) {
                            // Kamping-a sartzen da, baldin eta ez bada existitzen
                            String sql = "INSERT INTO KANPINAK (KODEA, IZENA, DESKRIBAPENA, KOKALEKUA, TELEFONOA, HELBIDEA, "
                                    + "EMAILA, WEBGUNEA, KATEGORIA, EDUKIERA, POSTAKODEA, HERRI_KODEA, "
                                    + "PROBINTZIA_KODEA, FRIENDLY_URL, PHYSICAL_URL, DATA_XML, METADATA_XML, ZIP_FILE) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            
                            pstmtInsert = connection.prepareStatement(sql);
                            pstmtInsert.setString(1, kodea); // KODEA
                            pstmtInsert.setString(2, izena); // IZENA
                            pstmtInsert.setString(3, deskribapena); // DESKRIBAPENA
                            pstmtInsert.setString(4, kokalekua); // KOKALEKUA
                            pstmtInsert.setString(5, telefonoa); // TELEFONOA
                            pstmtInsert.setString(6, helbidea); // HELBIDEA
                            pstmtInsert.setString(7, emaila); // EMAILA
                            pstmtInsert.setString(8, webgunera); // WEBGUNEA
                            pstmtInsert.setString(9, kategoria); // KATEGORIA
                            pstmtInsert.setInt(10, edukiera); // EDUKIERA
                            pstmtInsert.setString(11, postakodea); // POSTAKODEA
                            pstmtInsert.setInt(12, herriKodea); // HERRI_KODEA
                            pstmtInsert.setInt(13, probintziaKodea); // PROBINTZIA_KODEA
                            pstmtInsert.setString(14, friendlyUrl); // FRIENDLY_URL
                            pstmtInsert.setString(15, physicalUrl); // PHYSICAL_URL
                            pstmtInsert.setString(16, dataXml); // DATA_XML
                            pstmtInsert.setString(17, metadataXml); // METADATA_XML
                            pstmtInsert.setString(18, zipFile); // ZIP_FILE

                            pstmtInsert.executeUpdate();
                            System.out.println("Kamping sartuta: " + izena + " Hurrengo kodearekin: " + kodea);
                        } else {
                            System.out.println("Kamping jada existitzen da: " + izena + " Kodearekin: " + kodea);
                        }
                    } catch (SQLException e) {
                        // Errorea kamping-a sartzean
                        System.err.println("Errorea Kamping sartzerakoan: " + izena + ". " + e.getMessage());
                    } 
                }
            }

        } catch (Exception e) {
            // Errorea XML fitxategia prozesatzerakoan
            System.err.println("Errorea XML fitxategia prozesatzerakoan: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // PreparedStatement-a itxi
            if (pstmtInsert != null) {
                try {
                    pstmtInsert.close(); // PreparedStatement-a itxi
                } catch (SQLException e) {
                    // Errorea PreparedStatement-a itxi ezin izan delako
                    System.err.println("Error al cerrar el PreparedStatement: " + e.getMessage());
                }
            }
        }
    }

    // Testuaren edukia edo balio bat itzultzeko metodo laguntzailea
    private String getTextContentOrDefault(Element element, String tagName, String defaultValue) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return defaultValue; // Balio lehenetsia itzuli
    }

  

    public void importKanpinEtiketak(String xmlFilePath) throws Exception {
        PreparedStatement pstmtSelectEtiqueta = null;
        PreparedStatement pstmtInsertEtiqueta = null;
        PreparedStatement pstmtSelectKanpinEtiketa = null;
        PreparedStatement pstmtInsertKanpinEtiketa = null;

        try {
            // XML fitxategia kargatu eta aztertu
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // XML-ko row guztiak (datuak) lortu
            NodeList nList = document.getElementsByTagName("row");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    // Etiquetaren izena eta udalerri zein lurralde kodeak lortu
                    String documentName = getTextContentOrDefault2(eElement, "marks", null);
                    String municipalityCode = getTextContentOrDefault2(eElement, "municipalitycode", null);
                    String territoryCode = getTextContentOrDefault2(eElement, "territorycode", null);

                    // ETIKETAK taulan etiketa existitzen den egiaztatu
                    int etiketaId = -1;
                    pstmtSelectEtiqueta = connection.prepareStatement("SELECT ID FROM ETIKETAK WHERE ETIKETA = ?");
                    pstmtSelectEtiqueta.setString(1, documentName);
                    ResultSet rs = pstmtSelectEtiqueta.executeQuery();

                    if (rs.next()) {
                        etiketaId = rs.getInt("ID");
                    } else {
                        // ETIKETAK taulan etiketa sartzen da, ez badago
                        pstmtInsertEtiqueta = connection.prepareStatement("INSERT INTO ETIKETAK (ETIKETA) VALUES (?)", 
                                                                         new String[] { "ID" });
                        pstmtInsertEtiqueta.setString(1, documentName);
                        pstmtInsertEtiqueta.executeUpdate();

                        // Sartutako etiketa berriaren IDa lortu
                        ResultSet rsGeneratedKeys = pstmtInsertEtiqueta.getGeneratedKeys();
                        if (rsGeneratedKeys.next()) {
                            etiketaId = rsGeneratedKeys.getInt(1);
                        }
                    }

                    // Etiketaren IDa baliozkoa den ziurtatu
                    if (etiketaId == -1) {
                        System.err.println("Etiketarentzako ID bat lortzea edo sortzea ez da posible: " + documentName);
                        continue; // Hurrengo irteerara pasa
                    }

                    // Kanpinaren kodea udalerri eta lurralde kodeekin lortu
                    String kanpinKodea = obtenerKanpinKodea(connection, municipalityCode, territoryCode);

                    // KANPIN_ETIKETAK taulan erlazioa existitzen den egiaztatu
                    pstmtSelectKanpinEtiketa = connection.prepareStatement(
                        "SELECT COUNT(*) FROM KANPIN_ETIKETAK WHERE ID_ETIKETA = ? AND KANPIN_KODEA = ?");
                    pstmtSelectKanpinEtiketa.setInt(1, etiketaId);
                    pstmtSelectKanpinEtiketa.setString(2, kanpinKodea);
                    ResultSet rsCheck = pstmtSelectKanpinEtiketa.executeQuery();

                    if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                        // KANPIN_ETIKETAK taulan erlazioa sartzen da, ez bada existitzen
                        pstmtInsertKanpinEtiketa = connection.prepareStatement(
                            "INSERT INTO KANPIN_ETIKETAK (ID_ETIKETA, KANPIN_KODEA) VALUES (?, ?)");
                        pstmtInsertKanpinEtiketa.setInt(1, etiketaId);
                        pstmtInsertKanpinEtiketa.setString(2, kanpinKodea);
                        pstmtInsertKanpinEtiketa.executeUpdate();

                        System.out.println("Erlazioa sartuta: Camping " + kanpinKodea + " - Etiqueta " + documentName);
                    } else {
                        System.out.println("Erlazioa jada existitzen da: Camping " + kanpinKodea + " - Etiqueta " + documentName);
                    }
                }
            }
        } catch (Exception e) {
            // XML fitxategia prozesatzerakoan errorea
            System.err.println("Error al procesar el archivo XML: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Baliabide guztiak itxi
            if (pstmtSelectEtiqueta != null) pstmtSelectEtiqueta.close();
            if (pstmtInsertEtiqueta != null) pstmtInsertEtiqueta.close();
            if (pstmtSelectKanpinEtiketa != null) pstmtSelectKanpinEtiketa.close();
            if (pstmtInsertKanpinEtiketa != null) pstmtInsertKanpinEtiketa.close();
        }
    }

    // XML elementu batetik testua ateratzeko metodo laguntzailea
    private String getTextContentOrDefault2(Element element, String tagName, String defaultValue) {
        Node node = element.getElementsByTagName(tagName).item(0);
        return node != null ? node.getTextContent() : defaultValue; // Balio lehenetsia itzuli
    }

    // Udalerri eta lurralde kodeen arabera kanpinaren kodea lortzeko metodoa
    private String obtenerKanpinKodea(Connection connection, String municipalityCode, String territoryCode) throws SQLException {
        String kanpinKodea = null;
        String query = "SELECT KODEA FROM KANPINAK WHERE HERRI_KODEA = ? AND PROBINTZIA_KODEA = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, municipalityCode);
            pstmt.setString(2, territoryCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                kanpinKodea = rs.getString("KODEA");
            }
        }
        return kanpinKodea; // Kanpinaren kodea itzuli
    }
}
