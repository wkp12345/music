package com.example.music;

public class music {
    private String name;
    private String id;
    private String url="";

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
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
