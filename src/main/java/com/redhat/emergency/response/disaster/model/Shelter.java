package com.redhat.emergency.response.disaster.model;

import java.math.BigDecimal;

public class Shelter {
    private String id;
    private String name;
    private BigDecimal lon;
    private BigDecimal lat;
    private int rescued;

    public Shelter() {   
    }

    public Shelter(String id, String name, BigDecimal lon, BigDecimal lat, int rescued) {
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

    public BigDecimal getLon() {
        return this.lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public BigDecimal getLat() {
        return this.lat;
    }

    public void setLat(BigDecimal lat) {
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

    public Shelter lon(BigDecimal lon) {
        this.lon = lon;
        return this;
    }

    public Shelter lat(BigDecimal lat) {
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