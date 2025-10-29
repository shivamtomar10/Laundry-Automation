/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.image.BufferedImage;
import java.util.Date;

/**
 *
 * @author Shivam
 */
public class LaundryOrder {
     private int orderId;
    private int studentId;
    private String serviceType;
    private Date bookingDate;
    private String status;
    private boolean qrGenerated;
    private int employeeId;
    private boolean free;
    private double amount;
    private String employeeName;
    private String qrBase64;
    // --- Constructors ---
    public LaundryOrder() {}

    public LaundryOrder(int orderId, int studentId, String serviceType, Date bookingDate,
                        String status, boolean qrGenerated,int employeeId,boolean free,double amount) {
        this.orderId = orderId;
        this.studentId = studentId;
        this.serviceType = serviceType;
        this.bookingDate = bookingDate;
//        this.slotTime = slotTime;
        this.status = status;
        this.qrGenerated = qrGenerated;
//        this.slotTimeId = slotTimeId;
        this.employeeId = employeeId;
        this.amount=amount;
        this.free=free;
    }

    // --- Getters & Setters ---
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public boolean isFree() { return free; }
    public void setFree(boolean free) { this.free = free; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isQrGenerated() { return qrGenerated; }
    public void setQrGenerated(boolean qrGenerated) { this.qrGenerated = qrGenerated; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    public String getEmployeeName(){ return this.employeeName;}
   

public String getQrBase64() {
    return qrBase64;
}

public void setQrBase64(String qrBase64) {
    this.qrBase64 = qrBase64;
}


}
