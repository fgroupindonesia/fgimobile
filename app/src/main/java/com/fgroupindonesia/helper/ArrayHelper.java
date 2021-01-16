package com.fgroupindonesia.helper;

import com.fgroupindonesia.beans.Document;

import java.util.ArrayList;

public class ArrayHelper {

    public static ArrayList<Document> fillArrayList(ArrayList <Document> dataOut, Document [] dataIn){

        for(Document satuan: dataIn){
            dataOut.add(satuan);
        }

        return dataOut;

    }

    public static ArrayList<Document> copyBack(ArrayList<Document> arraySource, ArrayList<Document> arrayTarget){

        arrayTarget.clear();
        for(Document d:arraySource){
            arrayTarget.add(d);
        }

        return arrayTarget;

    }

}
