package com.uottawa.mitchell.project;

public class Service {
    private String name;
    private String type;
    private String hourlyrate;
    private String id;
    public Service (String name, String type, String hourlyrate, String id) {
        this.name=name;
        this.type=type;
        this.hourlyrate=hourlyrate;
        this.id = id;
    }


    public String getHourlyrate (){
        return hourlyrate;
    }
    public String getId () { return id; }
    public String getName (){
        return name;
    }
    public String getType (){
        return type;
    }
    public String toString  (){
        return (name+": "+type+": "+hourlyrate);
    }
}
