package com.webmethods.parser.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBMetadata {
    private String XmlField;
    private String XmlTrans;
    private String OldValue;
    private String NewValue;

    public DBMetadata(String xmlField, String xmlTrans, String oldValue, String newValue) {
        XmlField = xmlField;
        XmlTrans = xmlTrans;
        OldValue = oldValue;
        NewValue = newValue;
    }

    public DBMetadata() {
    }

    public void setPropertiesBasedOnKeyNames(String keyName, String value) {
        switch (keyName) {
            case "XMLFIELD":
                this.setXmlField(value);
                break;
            case "XMLTRANS":
                this.setXmlTrans(value);
                break;
            case "OLDVALUE":
                this.setOldValue(value);
                break;
            case "NEWVALUE":
                this.setNewValue(value);
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "DBMetadata{" +
                "XmlField='" + XmlField + '\'' +
                ", XmlTrans='" + XmlTrans + '\'' +
                ", OldValue='" + OldValue + '\'' +
                ", NewValue='" + NewValue + '\'' +
                '}';
    }
}
