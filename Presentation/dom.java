


package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class dom {

    public static void main(String[] args) {
        try {
            File inputFile = new File("DOM.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            System.out.println(Validate.validateXMLSchema("DOM.xsd", "DOM.xml"));
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize(); // REMOVE EMPTY TEXT AND MERGE TEXT


            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("student"); // TO GET ALL ELEMENTS PUT *
            System.out.println("----------------------------");
            // ONE WAY
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Student id : "
                            + eElement.getAttribute("id"));
                    System.out.println("First Name : "
                            + eElement
                            .getElementsByTagName("firstname")
                            .item(0)
                            .getTextContent());
                }
            }
            // SECOND WAY
            doSomething(doc.getDocumentElement(),0 );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doSomething(Node node, int j) {
        // do something with the current node instead of System.out
        System.out.println(node.getNodeName() + " " + j);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                doSomething(currentNode, j+1);
            }
        }

    }
}
