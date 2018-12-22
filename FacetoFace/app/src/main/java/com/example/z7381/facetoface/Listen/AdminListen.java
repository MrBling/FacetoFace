package com.example.z7381.facetoface.Listen;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.z7381.facetoface.Admin.adminActivity;

public class AdminListen implements  View.OnClickListener{
    Context context;
    EditText pid;
    EditText pwd;

    public AdminListen(Context context, EditText pid, EditText pwd){
        this.context=context;
        this.pid=pid;
        this.pwd=pwd;
    }


    public void onClick(View v) {

     //  System.out.println(pid.getText()+","+pwd.getText());
        if (pid.getText().toString().trim().equals("123456")==true&&pwd.getText().toString().trim().equals("123456")==true)
        {
            Intent intent = new Intent(context, adminActivity.class);
            context.startActivity(intent);
        }
        else
            Toast.makeText(context, "您无权限登入，请确认密码和账号", Toast.LENGTH_LONG).show();
    }

}