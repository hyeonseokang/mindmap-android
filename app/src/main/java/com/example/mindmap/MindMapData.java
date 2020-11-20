package com.example.mindmap;

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
