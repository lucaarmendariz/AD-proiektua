package javaErronka;

import java.sql.Connection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;

public class CampingXMLGenerator {
    private Connection connection; // Datu-basearekin konektatzeko erabiltzen den objektua

    // Eraikitzailea: Connection objektua jasotzen du
    public CampingXMLGenerator(Connection connection) {
        this.connection = connection; // Datu-basearen konektibitatea gordetzen du
    }
    
    // XML fitxategira esportatzeko metodoa
    public void exportToXML (String xmlFilePath) {
        try {
            // Datu-basearekin konektatu
            Statement stmt = connection.createStatement();

            // XML dokumentua prestatzea
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Sustrai nodoa <kanpinak>
            Element rootElement = doc.createElement("kanpinak");
            doc.appendChild(rootElement);

            // Kanpinak kontsultatu
            String sql = "SELECT * FROM KANPINAK";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Kanpin bakoitzeko <kanpina> nodoa sortzen da
                Element kanpina = doc.createElement("kanpina");
                kanpina.setAttribute("id", rs.getString("KODEA")); // Kanpinaren identifikatzailea
                rootElement.appendChild(kanpina);

                // Datu oinarrizkoak gehitu
                addElementWithText(doc, kanpina, "izena", rs.getString("IZENA"));
                addElementWithText(doc, kanpina, "deskribapena", rs.getString("DESKRIBAPENA"));
                addElementWithText(doc, kanpina, "kategoria", rs.getString("KATEGORIA"));
                addElementWithText(doc, kanpina, "edukiera", String.valueOf(rs.getInt("EDUKIERA")));
                addElementWithText(doc, kanpina, "kokalekua", rs.getString("KOKALEKUA"));

                // <helbidea> nodoa eta bere azpi-elementuak
                Element helbidea = doc.createElement("helbidea");
                kanpina.appendChild(helbidea);
                addElementWithText(doc, helbidea, "kalea", rs.getString("HELBIDEA"));
                addElementWithText(doc, helbidea, "postaKodea", rs.getString("POSTAKODEA"));

                // HERRIAK eta PROBINTZIAK kontsultatzea izenak lortzeko
                String herriKodea = rs.getString("HERRI_KODEA");
                String probintziaKodea = rs.getString("PROBINTZIA_KODEA");

                Statement stmt2 = connection.createStatement();
                ResultSet herriaRS = stmt2.executeQuery("SELECT IZENA FROM HERRIAK WHERE KODEA = " + herriKodea);
                if (herriaRS.next()) {
                    Element herria = doc.createElement("herria");
                    herria.setAttribute("id", herriKodea);
                    herria.setTextContent(herriaRS.getString("IZENA"));
                    helbidea.appendChild(herria); // <helbidea> nodoaren barruan gehitu
                }
                ResultSet probintziaRS = stmt2.executeQuery("SELECT IZENA FROM PROBINTZIAK WHERE KODEA = " + probintziaKodea);
                if (probintziaRS.next()) {
                    Element probintzia = doc.createElement("probintzia");
                    probintzia.setAttribute("id", probintziaKodea);
                    probintzia.setTextContent(probintziaRS.getString("IZENA"));
                    helbidea.appendChild(probintzia); // <helbidea> nodoaren barruan gehitu
                }

                // Informazio gehigarria
                addElementWithText(doc, kanpina, "telefonoa", rs.getString("TELEFONOA"));
                addElementWithText(doc, kanpina, "emaila", rs.getString("EMAILA"));
                addElementWithText(doc, kanpina, "webgunea", rs.getString("WEBGUNEA"));

                // <estekak> nodoa eta bere URLak
                Element estekak = doc.createElement("estekak");
                kanpina.appendChild(estekak);
                addElementWithText(doc, estekak, "friendly", rs.getString("FRIENDLY_URL"));
                addElementWithText(doc, estekak, "physical", rs.getString("PHYSICAL_URL"));

                // <fitxategiak> nodoa XML eta ZIP fitxategietarako estekekin
                Element fitxategiak = doc.createElement("fitxategiak");
                kanpina.appendChild(fitxategiak);
                addElementWithText(doc, fitxategiak, "dataXML", rs.getString("DATA_XML"));
                addElementWithText(doc, fitxategiak, "metadataXML", rs.getString("METADATA_XML"));
                addElementWithText(doc, fitxategiak, "zipFile", rs.getString("ZIP_FILE"));

                // Itxi datu-baseko ResultSetak
                herriaRS.close();
                probintziaRS.close();
            }

            // DOM-a XML fitxategi bihurtzea
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);

            System.out.println("XML fitxategia sortuta!");

            // Konektzioak itxi
            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace(); // Salbuespena tratatzea
        }
    }

    // Elementu XML bat sortzeko laguntzaile metodoa, testuarekin
    private static void addElementWithText(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        parent.appendChild(element); // Atributu gurasoari gehitzen zaio
    }
}
