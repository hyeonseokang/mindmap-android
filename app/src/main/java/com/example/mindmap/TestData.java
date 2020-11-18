package com.example.mindmap;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TestData {
    public String name;

    public TestData(){

    }

    public TestData(String _name){

    }

    public String getName(){
        return name;
    }

    public void setName(String userName){
        name = userName;
    }

    @Override
    public String toString(){
        return "name : " + name;
    }
}


