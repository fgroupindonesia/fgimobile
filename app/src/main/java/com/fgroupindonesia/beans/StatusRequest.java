package com.fgroupindonesia.beans;

public class StatusRequest {

    private int id;
    private int transDoc;
    private int devApp;
    private int formatPc;
    private int designGraph;
    private int others;
    private int progress;
    private String title;
    private String description;
    private String othersText;
    private String tanggal;
    private String status;
    private String userFullName;



    public String getType(){

        if(this.getTransDoc()==1){
            return ("Translate Document");
        }else if(this.getDevApp()==1){
            return ("Develop App");
        } else if(this.getDesignGraph()==1){
            return ("Design Graphic");
        }else if(this.getFormatPc()==1){
            return ("Format PC");
        }else if(this.getOthers()==1){
            return ("Lain-lain");
        }

        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransDoc() {
        return transDoc;
    }

    public void setTransDoc(int transDoc) {
        this.transDoc = transDoc;
    }

    public int getDevApp() {
        return devApp;
    }

    public void setDevApp(int devApp) {
        this.devApp = devApp;
    }

    public int getFormatPc() {
        return formatPc;
    }

    public void setFormatPc(int formatPc) {
        this.formatPc = formatPc;
    }

    public int getDesignGraph() {
        return designGraph;
    }

    public void setDesignGraph(int designGraph) {
        this.designGraph = designGraph;
    }

    public int getOthers() {
        return others;
    }

    public void setOthers(int others) {
        this.others = others;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOthersText() {
        return othersText;
    }

    public void setOthersText(String othersText) {
        this.othersText = othersText;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
