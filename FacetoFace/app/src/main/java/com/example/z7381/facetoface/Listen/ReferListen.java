package com.example.z7381.facetoface.Listen;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.z7381.facetoface.Attendance.MainActivity;
import com.example.z7381.facetoface.Refer.Attend;

public class ReferListen implements  View.OnClickListener{
    Context context;

    public ReferListen(Context context){
        this.context=context;
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(context,Attend.class);
        context.startActivity(intent);
    }
}