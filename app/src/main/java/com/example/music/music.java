package com.example.music;

public class music {
    private String name;
    private String id;
    private String length="-1";

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId(){
        return  id;
    }
    public void setId(String s){
        id=s;
    }
}
