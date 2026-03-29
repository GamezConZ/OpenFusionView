package com.openfusionview.core;

public class SeriesRecord {
    public String patientId;
    public String patientName;
    public String modality; // CT o NM
    public String date;
    public String folderPath; // La ruta que le pasaremos al DicomLoader

    public SeriesRecord(String id, String name, String modality, String date, String path) {
        this.patientId = id;
        this.patientName = name;
        this.modality = modality;
        this.date = date;
        this.folderPath = path;
    }
}