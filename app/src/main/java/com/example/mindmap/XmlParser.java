package com.example.mindmap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;


/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-17
 * 기능 : Document doc에 담겨져 있는 Xml 파일에 특정 tag 값 추출
 */
public class XmlParser {

    private String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if (nValue == null)
            return null;
        return nValue.getNodeValue();
    }

    public ArrayList<String> parsing(Document doc, String tag) {
        ArrayList<String> parsingItems = new ArrayList<>();
        // 파싱할 tag
        NodeList nList = doc.getElementsByTagName("item");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String value = getTagValue(tag, eElement);

                if (value != null)
                    parsingItems.add(value);
            }
        }

        return parsingItems;
    }

}
