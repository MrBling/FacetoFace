package com.example.z7381.facetoface.Admin;

import android.util.Log;

import com.baidu.aip.face.AipFace;

import org.json.JSONObject;

import java.util.HashMap;


public class Update {
    public static boolean Update(AipFace client, String userId, String image, String groupId){

        JSONObject res=null;

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");


        String imageType = "BASE64";



        // 人脸更新
             res = client.updateUser(image, imageType, groupId, userId, options);
             System.out.println("res"+res);
        if (res.optString("error_code").equals("0")==true)
            return true;
        else
            return false;

    }
}
