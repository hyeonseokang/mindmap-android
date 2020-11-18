package com.example.mindmap;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-18
 * 기능 : 특정 단어에 비슷한 단어, 반대말같은 유의어들을 추출해주는 Class
 */

public class SimilarWordCrawler {
    private String baseUrl;
    private HttpRequest httpRequest;
    private XmlParser xmlParser;
    private SimilarWordJsonParser jsonParser;

    public SimilarWordCrawler(String baseUrl){
        this.baseUrl = baseUrl;
        httpRequest = new HttpRequest();
        xmlParser = new XmlParser();
        jsonParser = new SimilarWordJsonParser();
    }

    public HashMap<String, ArrayList<String>> crawling(String word) throws JSONException, IOException {
        ArrayList<String> links = getLink(word);
        String jsonFile = parsingJson(links);

        HashMap<String, ArrayList<String>> map = jsonParser.parsing(jsonFile);

        return map;
    }

    private ArrayList<String> getLink(String word){
        org.w3c.dom.Document doc = httpRequest.request(baseUrl, word);
        ArrayList<String> links = xmlParser.parsing(doc, "link");

        return links;
    }

    private String parsingJson(ArrayList<String> links) throws IOException{
        Document doc = Jsoup.connect(links.get(0)).get();
        Elements elements = doc.select("#wordmap_json_str");

        String jsonFile = elements.get(0).text();

        return jsonFile;
    }
}
