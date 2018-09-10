package com.iclub.eattendance.eattendance;

//student class for using in the RecyclerView adapter
public class Student {
    private String name; //name of the student
    private String reg; //register number of the student
    private boolean status; //status, if the student is presently absent or present
    private int id; //id, for the working of the application

    public Student() { //constructor, presently empty
    }

    public Student(String name, String reg, boolean status, int id) { //constructor to set the details
        this.name = name;
        this.reg = reg;
        this.status = status;
        this.id = id;
    }

    //methods to return the fields
    public String getname() {return name;}
    public boolean getstatus() {
        return status;
    }
    public String getreg() {
        return reg;
    }
    public int getid() { return id; }

    //methods to set the fields
    public void setStatus(boolean status) { this.status = status; }
}
