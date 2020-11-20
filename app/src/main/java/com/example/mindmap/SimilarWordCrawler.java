package com.example.mindmap;

import android.util.Log;

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
 * 기능 : 특정 단어의 뜻과 비슷한 단어, 반대말같은 유의어들을 추출해주는 Class
 */

public class SimilarWordCrawler {
    private String baseUrl;
    private HttpRequest httpRequest;
    private XmlParser xmlParser;
    private SimilarWordJsonParser jsonParser;
    private org.w3c.dom.Document doc;
    private Document doc2;

    public SimilarWordCrawler(String baseUrl){
        this.baseUrl = baseUrl;
        httpRequest = new HttpRequest();
        xmlParser = new XmlParser();
        jsonParser = new SimilarWordJsonParser();
    }

    public HashMap<String, ArrayList<String>> crawling(String word) throws JSONException, IOException {
        ArrayList<String> links = getLink(word);
        String jsonFile = parsingJson(links);
        HashMap<String, ArrayList<String>> map = new HashMap<>();

        if(jsonFile == null){
            ArrayList<String> mean = new ArrayList<>();
            String wordMean = doc2.selectFirst("#content > div.cont_box_lr.group > div.section.floatL > div.group.mt30 > div > div > div.search_view_list.group.mt30 > dl:nth-child(1) > dt > span.word_dis").text();
            mean.add(wordMean);
            map.put("meaning",mean);
        }
        else{
            map = jsonParser.parsing(jsonFile);
            Log.d("Map Test", map.toString());
        }


        return map;
    }
    public ArrayList<String> getWordMeaning(String word){
        return parsingXml(word, "definition");
    }

    private ArrayList<String> getLink(String word){
        return parsingXml(word, "link");
    }

    private ArrayList<String> parsingXml(String word, String wordMehtod){
        doc = httpRequest.request(baseUrl, word);
        return xmlParser.parsing(doc, wordMehtod);
    }

    private String parsingJson(ArrayList<String> links) throws IOException{
        if(links.size() < 1)
            return "";
        Document doc = Jsoup.connect(links.get(0)).get();
        doc2 = doc;
        Elements elements = doc.select("#wordmap_json_str");
        String jsonFile = "";

        if(elements.size() ==0)
            return null;

        jsonFile = elements.get(0).text();
        return jsonFile;
    }
}
