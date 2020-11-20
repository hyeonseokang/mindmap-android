package com.example.mindmap;


/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-19
 * 기능 : MindMap 데이터를 저장하기위해 만든 Class
 */
public class MindMapData {
    private String id;
    private String image;
    private String explain;
    private Node rootNode;

    public MindMapData(String id, String image, String explain, Node rootNode){
        this.id = id;
        this.image = image;
        this.explain = explain;
        this.rootNode = rootNode;
    }

    public String getId(){return id;}
    public String getImage(){return image;}
    public String getExplain(){return explain;}
    public Node getRootNode(){return rootNode;}

    @Override
    public String toString() {
        return "MindMapData{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", explain='" + explain + '\'' +
                ", rootNode=" + rootNode +
                '}';
    }
}
