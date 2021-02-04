package com.octopus.mapmarks.overlay;

import android.os.Parcel;

import com.amap.api.services.core.LatLonPoint;

public class MLatLonPoint extends LatLonPoint {

    private String titel;
    private String note;
    private int type;

    public MLatLonPoint(double v, double v1) {
        super(v, v1);
    }

    protected MLatLonPoint(Parcel parcel) {
        super(parcel);
    }

    public MLatLonPoint(double v, double v1, String titel) {
        super(v, v1);
        this.titel = titel;
    }

    public MLatLonPoint(Parcel parcel, String titel) {
        super(parcel);
        this.titel = titel;
    }

    public MLatLonPoint(double v, double v1, String titel, String note) {
        super(v, v1);
        this.titel = titel;
        this.note = note;
    }

    public MLatLonPoint(Parcel parcel, String titel, String note) {
        super(parcel);
        this.titel = titel;
        this.note = note;
    }

    public MLatLonPoint(double v, double v1, String titel, String note, int type) {
        super(v, v1);
        this.titel = titel;
        this.note = note;
        this.type = type;
    }

    public MLatLonPoint(Parcel parcel, String titel, String note, int type) {
        super(parcel);
        this.titel = titel;
        this.note = note;
        this.type = type;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
