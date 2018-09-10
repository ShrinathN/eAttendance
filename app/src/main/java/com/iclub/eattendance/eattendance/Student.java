package com.iclub.eattendance.eattendance;

public class Student {
    private String name,reg;
    private boolean status;
    private int id;

    public Student() {
    }

    public Student(String name, String reg, boolean status, int id) {
        this.name = name;
        this.reg = reg;
        this.status = status;
        this.id = id;
    }

    public String getname() {return name;}
    public boolean getstatus() {
        return status;
    }
    public String getreg() {
        return reg;
    }
    public int getid() { return id; }

    public void setStatus(boolean status) { this.status = status; }
}
