package com.example.z7381.facetoface.Refer;

public class Student {
    private String name;
    private String Classes;
    private String PId;
    private int imageId;

    public Student(String name,String Classes,String PId,int imageId){
        this.name=name;
        this.Classes=Classes;
        this.PId=PId;
        this.imageId=imageId;
    }

        public String getName(){
        return name;
        }

        public String getClasses(){
        return Classes;
        }

        public  String getPId(){
        return PId;
        }

        public int getImageId(){
        return imageId;
        }
}
