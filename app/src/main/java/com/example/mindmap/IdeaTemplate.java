package com.example.mindmap;

/**
 * 개발자 : 20191583 나민형
 * 마지막 수정일 : 2020-11-20
 * 기능 : 마인드맵, 또는 아이디어의 주제와 설명을 담아두기 위한 클래스
 */

public class IdeaTemplate {
    IdeaTemplate(String name, String description){
        this.name = name;
        this.description = description;
    }
    public String name;
    public String description;
}
