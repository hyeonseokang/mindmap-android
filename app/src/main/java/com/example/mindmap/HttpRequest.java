package com.example.mindmap;


import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-17
 * 기능 : open api 이용 http 리퀘스트
 */
public class HttpRequest {
    public Document request(String _url, String key) {
        String url = _url + key;
        Document doc = null;

        DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactoty.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            doc = dBuilder.parse(url);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();


        return doc;
    }

}
