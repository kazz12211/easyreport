package jp.tsubakicraft.easyreport.util;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XMLUtil {

	public static String getTextContentFromElement(Element senderElement, String value) {
        return senderElement.getElementsByTagName(value).item(0).getTextContent();
    }

    public static Node getNodeFromElement(Element deliveryElement, String value) {
        return deliveryElement.getElementsByTagName(value).item(0);
    }
        
    public static Element getElementFromElement(Element deliveryElement, String value) {
    	NodeList list = deliveryElement.getElementsByTagName(value);
    	int numElements = list.getLength();
    	for(int i = 0; i < numElements; i++) {
    		Node node = list.item(i);
    		if(node.getParentNode() == deliveryElement) {
    			return (Element)node;
    		}
    	}
    	return null;
    }
        
    public static Element getElementFromDocument(Document document, String value) {
        return (Element) document.getElementsByTagName(value).item(0);
    }
    
    public static Element getChildElementFromDocument(Document document, String value) {
    	NodeList list = document.getElementsByTagName(value);
    	int numElements = list.getLength();
    	for(int i = 0; i < numElements; i++) {
    		Node node = list.item(i);
    		if(node.getParentNode() == document.getDocumentElement()) {
    			return (Element)node;
    		}
    	}
    	return null;
    }
    
    public static String toXmlString(Document document) {
		StringWriter sw = new StringWriter();
		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer transformer = factory.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(sw));
			String string = sw.toString();
			return string;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

}
