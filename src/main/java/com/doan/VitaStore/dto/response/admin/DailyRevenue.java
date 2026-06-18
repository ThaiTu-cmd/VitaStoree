package com.doan.VitaStore.dto.response.admin;

import java.math.BigDecimal;

public class DailyRevenue {
    private String date;
    private BigDecimal revenue;

    public DailyRevenue() {}

    public DailyRevenue(String date, BigDecimal revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
