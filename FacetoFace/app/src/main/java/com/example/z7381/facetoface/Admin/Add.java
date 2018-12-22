package com.example.z7381.facetoface.Admin;

import com.baidu.aip.face.AipFace;

import org.json.JSONObject;

import java.util.HashMap;

public class Add {

    public static boolean Add(AipFace client, String name, String num, String image,String groupId){
        System.out.println(name);
        System.out.println(num);

        JSONObject res=null;
        HashMap<String,String> options=new HashMap<>();
        options.put("user_info",name);
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");

        String imageType = "BASE64";
        String userId = num;
        res=client.addUser(image,imageType,groupId,userId,options);
        System.out.println(res.optString("error_code"));
        if (res.optString("error_code").equals("0")==true)
            return true;
        else
            return false;

    }
}
