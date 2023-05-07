package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

// https://mkyong.com/java/how-to-read-xml-file-in-java-sax-parser/
class sax extends DefaultHandler {

    public static void main(String[] args) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            sax handler = new sax();
            saxParser.parse(new File("sax.xml"), handler);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }


    boolean inside = false;
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(qName.equals("specialElement")) {
            inside = true;
            System.out.println(qName);
            System.out.println(attributes.getValue("id"));
        }
        if (inside) {
            System.out.println(qName + " enter");
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (inside) {
            System.out.println(qName + " exit");
        }
        if(qName.equals("specialElement")) {
            inside = false;
        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        String i = new String(ch, start, length).trim();
        if(inside && i.length() > 1) {
            System.out.println(i);
        }
    }
}
