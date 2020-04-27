package com.redhat.emergency.response.disaster.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.redhat.emergency.response.disaster.util.DoubleContextualSerializer;
import com.redhat.emergency.response.disaster.util.Precision;

public class Shelter {
    private String id;
    private String name;

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 4)
    private double lon;
    
    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 4)
    private double lat;
    
    private int rescued;

    public Shelter() {   
    }

    public Shelter(String id, String name, double lon, double lat, int rescued) {
        this.id = id;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.rescued = rescued;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLon() {
        return this.lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getRescued() {
        return this.rescued;
    }

    public void setRescued(int rescued) {
        this.rescued = rescued;
    }

    public Shelter id(String id) {
        this.id = id;
        return this;
    }

    public Shelter name(String name) {
        this.name = name;
        return this;
    }

    public Shelter lon(double lon) {
        this.lon = lon;
        return this;
    }

    public Shelter lat(double lat) {
        this.lat = lat;
        return this;
    }

    public Shelter rescued(int rescued) {
        this.rescued = rescued;
        return this;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", lon='" + getLon() + "'" +
            ", lat='" + getLat() + "'" +
            ", rescued='" + getRescued() + "'" +
            "}";
    }
}