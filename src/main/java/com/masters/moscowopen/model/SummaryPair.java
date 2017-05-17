package com.masters.moscowopen.model;

/**
 * @author Georgii Ovsiannikov
 * @since 5/17/17
 */
public class SummaryPair {

    private String name;
    private Double pollution;

    public SummaryPair(String name, Double pollution) {
        this.name = name;
        this.pollution = pollution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPollution() {
        return pollution;
    }

    public void setPollution(Double pollution) {
        this.pollution = pollution;
    }

    public static SummaryPair of(String name, Double pollution) {
        return new SummaryPair(name, pollution);
    }
}
