package com.webmethods.parser.main;

import com.webmethods.parser.functions.XMLParserImpl;
import com.webmethods.parser.pojo.DBMetadata;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ManipulateXMLMain {
    public static void main(String args[]) {
        XMLParserImpl xmlParser = new XMLParserImpl();

        try {
            Document dbDoc = xmlParser.readXMLFile("DB.xml");

            List<String> requiredFields = Arrays.asList("XMLFIELD", "XMLTRANS", "OLDVALUE", "LINEADDR");
            Map<String, DBMetadata> dbMetadata = xmlParser.fetchMetadataFromDoc(dbDoc, requiredFields);

            Document inputDoc = xmlParser.readXMLFile("OutputFile.xml");
            Document outputDoc = xmlParser.iterateThroughXML(inputDoc, dbMetadata);

            xmlParser.writeXMLFile(outputDoc, "TransformedOutput.xml");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
