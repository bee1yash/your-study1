package com.example.yourstudy;

public class putPDF {
    public String getName() {
        return name;
    }
        public putPDF(String name, String url)
        {
            this.name = name;
            this.url = url;
        }
    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String name;
    public String url;
}
