package Alexa;

import java.util.*;
import java.io.*;

public class InfosAlexa implements Serializable {

    private String url;
    private Integer rank;
    private HashMap<String, Integer> rankInCountry;
    private HashMap<String, Float> percentUserCountry;
    private Calendar dateOfQuery;

    public InfosAlexa(String url, 
                      Integer rank, 
                      HashMap<String, Integer> rankInCountry, 
                      HashMap<String, Float> percentUserCountry) {
        this.url = url;
        this.rank = rank;
        this.rankInCountry = rankInCountry;
        this.percentUserCountry = percentUserCountry;
        this.dateOfQuery = Calendar.getInstance();
        this.dateOfQuery.setTime(new Date());
    }

    public InfosAlexa() {
        this.url = "";
        this.rank = 0;
        this.rankInCountry = new HashMap<String, Integer>();
        this.percentUserCountry = new HashMap<String, Float>();
        this.dateOfQuery = Calendar.getInstance();
        this.dateOfQuery.setTime(new Date());
    }


    public String getUrl(){
        return this.url;
    }

    public Integer getRank(){
        return this.rank;
    }

    public HashMap<String, Integer> getRankInCountry(){
        return this.rankInCountry;
    }

    public HashMap<String, Float> getPercentUserCountry(){
        return this.percentUserCountry;
    }

    public Calendar getDateOfQuery(){
        return this.dateOfQuery;
    }


    public void setUrl(String url){
        this.url = url;
    }

    public void setRank(Integer rank){
        this.rank = rank;
    }

    public void setRankInCountry(HashMap<String, Integer> rankInCountry){
        this.rankInCountry = rankInCountry;
    }

    public void setPercentUserCountry(HashMap<String, Float> percentUserCountry){
        this.percentUserCountry = percentUserCountry;
    }

    public void setDateOfQuery(Calendar dateOfQuery){
        this.dateOfQuery = dateOfQuery;
    }

}