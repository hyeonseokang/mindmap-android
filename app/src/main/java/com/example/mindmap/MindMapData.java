package com.example.mindmap;

public class MindMapData {
    private String id;
    private String image;
    private String explain;

    public MindMapData(String id, String image, String explain){
        this.id = id;
        this.image = image;
        this.explain = explain;
    }

    public String getId(){return id;}
    public String getImage(){return image;}
    public String getExplain(){return explain;}

    @Override
    public String toString() {
        return "MindMapData{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", explain='" + explain + '\'' +
                '}';
    }
}
