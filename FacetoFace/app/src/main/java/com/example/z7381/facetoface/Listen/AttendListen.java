package com.example.z7381.facetoface.Listen;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.z7381.facetoface.Attendance.MainActivity;

public class AttendListen implements  View.OnClickListener{
    Context context;

    public AttendListen(Context context){
        this.context=context;
    }

    @Override
    public void onClick(View v) {
         Intent intent=new Intent(context,MainActivity.class);
         context.startActivity(intent);
    }
}
