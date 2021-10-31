package com.webmethods.parser.functions;

import com.webmethods.parser.pojo.DBMetadata;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParserImpl implements XMLParser {
    @Override
    public Document readXMLFile(String filename) {
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            File inputFile = new File(classLoader.getResource(filename).getFile());
            //File inputFile = new File("C:/Vikas/Documents/" + filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, DBMetadata> fetchMetadataFromDoc(Document dbDoc, List<String> requiredFields) {
        Map<String, DBMetadata> dbMetadataMap = new HashMap<>();
        //System.out.println("Root Element: " + dbDoc.getDocumentElement().getNodeName());
        Element rootElement = dbDoc.getDocumentElement(); //IDataXMLCoder
        NodeList ns_array = rootElement.getElementsByTagName("array"); //Expects hardcoded array TODO
        for(int index00 = 0; index00 < ns_array.getLength(); index00++) {
            Node nNodeArray = ns_array.item(index00);
            if(nNodeArray.getNodeType() == Node.ELEMENT_NODE) {
                Element aElement = (Element) nNodeArray;
                //System.out.println("Another Root Element: " + aElement.getNodeName() + " | Attribute: " + aElement.getAttribute("name"));

                NodeList ns_record = aElement.getElementsByTagName("record"); //Expects hardcoded record TODO
                for (int index01 = 0; index01 < ns_record.getLength(); index01++) {
                    Node nNodeRecord = ns_record.item(index01);
                    if(nNodeArray.getNodeType() == Node.ELEMENT_NODE) {
                        Element bElement = (Element) nNodeRecord;
                        //System.out.println("Another Root Element: " + bElement.getNodeName() + " | Attribute: " + bElement.getAttribute("javaclass"));

                        DBMetadata dbMetadata = new DBMetadata();

                        NodeList ns_value = bElement.getElementsByTagName("value"); //Expects hardcoded value TODO
                        for (int index02 = 0 ; index02 < ns_value.getLength(); index02++) {
                            Node nNodeValue = ns_value.item(index02);

                            if(nNodeValue.getNodeType() == Node.ELEMENT_NODE) {
                                Element cElement = (Element)  nNodeValue;
                                //System.out.println("Child-> Child Attribute : --- " + cElement.getAttribute("name"));

                                requiredFields.forEach(field -> {
                                    if(field.equalsIgnoreCase(cElement.getAttribute("name"))) {
                                        if(cElement.getTextContent() != null && cElement.getTextContent().trim().length() != 0) {
                                            dbMetadata.setPropertiesBasedOnKeyNames(field.toUpperCase(), cElement.getTextContent());
                                        }
                                    }
                                });
                            }
                        }
                        dbMetadataMap.put(dbMetadata.getXmlField(), dbMetadata);
                    }
                }
            }
        }
        dbMetadataMap.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + "--" + entry.getValue().toString());
        });
        return dbMetadataMap;
    }

    @Override
    public void writeXMLFile(Document doc, String filename) {
        try {

            System.out.println("Writing File to the Output.... in /src/main/resources");
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("./src/main/resources/" + filename));
            transformer.transform(source, result);

            // Output to console for testing
            //StreamResult consoleResult = new StreamResult(System.out);
            //transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Document iterateThroughXML(Document sourceDoc, Map<String, DBMetadata> dbMetadataMap) {
        Element classElement = sourceDoc.getDocumentElement();
        System.out.println("Output XML Root Element: " + classElement.getNodeName()); //class

        NodeList nodeList = sourceDoc.getElementsByTagName("class");
        Node nNode = nodeList.item(0);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            System.out.println("\nCurrent Element :" + nNode.getNodeName() + " | XML Children Node Length: " + nNode.getChildNodes().getLength());
            verifyChildAndReplace(sourceDoc, nNode, dbMetadataMap);
        }
        return sourceDoc;
    }

    //Recursive Function
    private Node verifyChildAndReplace(Document sourceDoc, Node rootNode, Map<String, DBMetadata> dbMetadataMap) {
        try {
            if(rootNode.getChildNodes().getLength() > 1) {
                NodeList childNodes = rootNode.getChildNodes();
                for (int j=0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        verifyChildAndReplace(sourceDoc, childNode, dbMetadataMap);
                    }
                }
            } else {
                if(dbMetadataMap.keySet().contains(rootNode.getNodeName().toUpperCase())) {
                    //System.out.println("Found element in KeySet: " + dbMetadataMap.get(rootNode.getNodeName().toUpperCase()));
                    DBMetadata dbMetadata = dbMetadataMap.get(rootNode.getNodeName().toUpperCase());
                    Node parentNode = rootNode.getParentNode();
                    Node newNode = sourceDoc.createElement(dbMetadata.getXmlTrans().toString());
                    if(rootNode.getTextContent() != null && rootNode.getTextContent().equals(dbMetadata.getOldValue())) {
                        newNode.setTextContent(dbMetadata.getNewValue());
                    } else {
                        newNode.setTextContent(rootNode.getTextContent());
                    }
                    System.out.println("OLDXMLTAG: " + rootNode.getNodeName() + " | "+ rootNode.getTextContent() + "; NEWXMLTAG: " + newNode.getNodeName() + " | " + newNode.getTextContent() + ";");
                    parentNode.replaceChild(newNode, rootNode);
                }
            }
        } catch (DOMException domException) {
            domException.printStackTrace();
        }

        return rootNode;
    }
}
