package com.oceanview.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private int billId;
    private int reservationId;
    private int nights;
    private BigDecimal rate;
    private BigDecimal total;
    private LocalDateTime printedAt;

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public LocalDateTime getPrintedAt() { return printedAt; }
    public void setPrintedAt(LocalDateTime printedAt) { this.printedAt = printedAt; }
}