package com.example.z7381.facetoface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.z7381.facetoface.Listen.AdminListen;
import com.example.z7381.facetoface.Listen.AttendListen;

public class Login extends AppCompatActivity {

    private Button login;
    private Button Attendance;
    private EditText Pid;
    private EditText Passwd;
    private String pid;
    private String passwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Pid = findViewById(R.id.pid);
        Passwd = findViewById(R.id.passwd);
        login = findViewById(R.id.login);
        Attendance = findViewById(R.id.Attendance);

        pid=Pid.getText().toString();
        passwd=Passwd.getText().toString();

        AttendListen attendance = new AttendListen(this);
        Attendance.setOnClickListener(attendance);
        AdminListen admin = new AdminListen(this, Pid,Passwd);
        login.setOnClickListener(admin);

    }
}
