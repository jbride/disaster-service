package com.redhat.emergency.response.disaster.model;

import java.math.BigDecimal;

public class DisasterCenter {
    private String name;
    private BigDecimal lon;
    private BigDecimal lat;

    public DisasterCenter() {
    }

    public DisasterCenter(String name, BigDecimal lon, BigDecimal lat) {
        this.name = name;
        this.lon = lon;
        this.lat = lat;
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

    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", lon='" + getLon() + "'" +
            ", lat='" + getLat() + "'" +
            "}";
    }
}