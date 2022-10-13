package com.example.newprojectmishanxx.Model;

public class Appointment {
    private String id, details, publisher, time, serviceName, date, assignedto;

    public Appointment() {
    }

    public Appointment(String id, String details, String publisher, String time, String serviceName, String date, String assignedto) {
        this.id = id;
        this.details = details;
        this.publisher = publisher;
        this.time = time;
        this.serviceName = serviceName;
        this.date = date;
        this.assignedto = assignedto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAssignedto() {
        return assignedto;
    }

    public void setAssignedto(String assignedto) {
        this.assignedto = assignedto;
    }
}
