package com.example.mindmap;

import java.util.ArrayList;

public class Node
{
    public String text;

    public Node parent;
    public ArrayList<Node> children;

    public NodeFragment fragment;

    public Node(NodeFragment fragment, String text)
    {
        this.text = text;

        this.parent = null;
        this.children = new ArrayList<>();

        this.fragment = fragment;
    }

    public void addChild(Node child)
    {
        child.parent = this;
        children.add(child);
    }

    public void removeChild(Node child)
    {
        children.remove(child);
    }

    public void removeChild(int index)
    {
        children.remove(index);
    }
}
