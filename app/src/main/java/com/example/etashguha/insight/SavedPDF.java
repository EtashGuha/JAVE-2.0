package com.example.etashguha.insight;

public class SavedPDF {

    protected String filePath;
    public SavedPDF(String filePath){
        this.filePath = filePath;
    }

    public String getFilePath(){
        return filePath;
    }
}
