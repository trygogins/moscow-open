package com.masters.moscowopen.model;

/**
 * @author Georgii Ovsiannikov
 * @since 4/18/17
 */
public class AirQualityModel {

    private Long globalId;
    private Double period;
    private Double monthlyAveragePDKss;
    private String SurveillanceZoneCharacteristics;
    private String AdmArea;
    private String District;
    private String Location;
    private String Parameter;
    private Double MonthlyAverage;
    private String StationName;

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public Double getPeriod() {
        return period;
    }

    public void setPeriod(Double period) {
        this.period = period;
    }

    public Double getMonthlyAveragePDKss() {
        return monthlyAveragePDKss;
    }

    public void setMonthlyAveragePDKss(Double monthlyAveragePDKss) {
        this.monthlyAveragePDKss = monthlyAveragePDKss;
    }

    public String getSurveillanceZoneCharacteristics() {
        return SurveillanceZoneCharacteristics;
    }

    public void setSurveillanceZoneCharacteristics(String surveillanceZoneCharacteristics) {
        SurveillanceZoneCharacteristics = surveillanceZoneCharacteristics;
    }

    public String getAdmArea() {
        return AdmArea;
    }

    public void setAdmArea(String admArea) {
        AdmArea = admArea;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getParameter() {
        return Parameter;
    }

    public void setParameter(String parameter) {
        Parameter = parameter;
    }

    public Double getMonthlyAverage() {
        return MonthlyAverage;
    }

    public void setMonthlyAverage(Double monthlyAverage) {
        MonthlyAverage = monthlyAverage;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }
}
