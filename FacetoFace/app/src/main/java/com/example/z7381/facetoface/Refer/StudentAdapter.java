package com.example.z7381.facetoface.Refer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.z7381.facetoface.R;

import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student> {

    private int resourceId;

    public StudentAdapter(Context context,int textViewResourceId,List<Student> object){
        super(context,textViewResourceId,object);
        resourceId=textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Student student=getItem(position);
        View view=LayoutInflater.from(getContext()).inflate(resourceId,null);
        ImageView Judgeimage=(ImageView)view.findViewById(R.id.Judgeimage);
        TextView content=(TextView)view.findViewById(R.id.content);
        Judgeimage.setImageResource(student.getImageId());
        content.setText("班级："+student.getClasses()+"           姓名："+student.getName()
        +"\n学号："+student.getPId());
        return view;
    }

}
