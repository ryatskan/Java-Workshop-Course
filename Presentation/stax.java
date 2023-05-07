package org.example;

import java.io.FileReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;

class stax {

    public static void main(String[] args) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileReader("sax.xml"));

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT -> {
                        System.out.println(event.asStartElement().getName() + " enter");
                    }
                    case XMLStreamConstants.CHARACTERS -> {
                        String data = event.asCharacters().getData().trim();
                        if (data.length() > 0)
                            System.out.println(data);
                    }
                    case XMLStreamConstants.END_ELEMENT -> {
                        System.out.println(event.asEndElement().getName() + " exit");
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();
        }
    }
}
