package com.example.mindmap;

import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-18
 * 기능 : 추가 Thread 에서 동작하는 우리말샘 api를 통해 특정단어 유의어 추출해주는 abstract class
 * 추가 설명 : CrawlingThread 상속받아서 CompleteCrawling 반드시 구현
 */
public abstract class CrawlingThread extends Thread{
    private HashMap<String, ArrayList<String>> similarWords;
    private String searchWord = "";

    public abstract void CompleteCrawling();

    @Override
    public void run(){
        if (searchWord == "")
            return;

        try {
            SimilarWordCrawling();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CompleteCrawling();
        searchWord = "";
    }

    public void start(String word){
        setSearchWord(word);
        start();
    }

    public void setSearchWord(String word){
        searchWord = word;
    }

    public void SimilarWordCrawling() throws IOException, JSONException {
        String url = "https://opendict.korean.go.kr/api/search?key=219E631E1D3E9931D7027B6B6777498D&advanced=y&q=";
        SimilarWordCrawler crawler = new SimilarWordCrawler(url);
        similarWords = crawler.crawling(searchWord);
        similarWords.put("meaning", crawler.getWordMeaning(searchWord));
    }

    public HashMap<String, ArrayList<String>> getSimilarWords(){
        return similarWords;
    }
}

