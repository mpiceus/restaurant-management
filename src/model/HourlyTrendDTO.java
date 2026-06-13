package model;

public class HourlyTrendDTO {
    private int hour;
    private String label;
    private double averageInvoices;

    public HourlyTrendDTO(int hour, String label, double averageInvoices) {
        this.hour = hour;
        this.label = label;
        this.averageInvoices = averageInvoices;
    }

    public int getHour() {
        return hour;
    }

    public String getLabel() {
        return label;
    }

    public double getAverageInvoices() {
        return averageInvoices;
    }
}
