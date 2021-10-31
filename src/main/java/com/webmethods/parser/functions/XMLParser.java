package com.webmethods.parser.functions;

import com.webmethods.parser.pojo.DBMetadata;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

public interface XMLParser {
    public Document readXMLFile(String filename);

    public Map<String, DBMetadata> fetchMetadataFromDoc(Document dbDoc, List<String> requiredFields);

    public void writeXMLFile(Document doc, String filename);

    public Document iterateThroughXML(Document sourceDoc, Map<String, DBMetadata> dbMetadataMap);

}
