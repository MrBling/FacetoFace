package com.example.z7381.facetoface.Attendance;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.*;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.aip.face.AipFace;
import com.example.z7381.facetoface.R;

import android.annotation.SuppressLint;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity {
    private Button getImage, detect, Camera;
    private ImageView myPhoto;
    private Bitmap myBitmapImage = null;
    private String ImagePath = null;
    private TextView tip;
    private EditText Class;
    private Uri imageUri;
    private int Photo_ALBUM = 1, CAMERA = 2;
    private JSONObject res = null;
    private Bitmap bitmapSmall;
    private Bitmap lastp;
    private Double score;
    private Bitmap InitBitmap;
    private   String error_code;
    private  String temp;

    public void addrecord(final String classes,final String Record_class,final String PId,final String name){
        Log.d("addrecord",classes+","+PId+","+name);
        //请求地址,需要换接口

        String url="http://47.106.10.15:8080/FtoFserver/FtoFserver/addRecord";
      //  String url = "http://139.199.184.59:8080/getParentId";
        String tag = "addrecord";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            temp = jsonObject.getString("canLogin");
                            if(temp.equals("true")){
                                //等待接口
                                System.out.println("发布成功");
                                Toast.makeText(MainActivity.this, "打卡成功！！！", Toast.LENGTH_LONG).show();
                            }else {
                                System.out.println("发布失败");
                                Toast.makeText(MainActivity.this, "网络延迟，请重新上传！！！", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）;
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.d("error",error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("classes", classes);
                params.put("Record_class",Record_class);
                params.put("PId",PId);
                params.put("name",name);
                System.out.println(name);
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,1,1.0f));
        //将请求添加到队列中
        requestQueue.add(request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相册选择图片
        if (requestCode == Photo_ALBUM) {
            if (data != null) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToNext();
                ImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));   //获得图片的绝对路径
                cursor.close();
                resizePhoto();
                int degree = getPicRotate(ImagePath);
                Matrix m = new Matrix();    //对图形处理
                m.setRotate(degree);        //旋转
                lastp = Bitmap.createBitmap(myBitmapImage, 0, 0, myBitmapImage.getWidth(), myBitmapImage.getHeight(), m, true);
                myPhoto.setImageBitmap(lastp);       //显示图片
                Log.i("图片路径", ImagePath);
            }
        } else if (requestCode == CAMERA) {
            try {
                resizePhoto();
                int degree = getPicRotate(ImagePath);
                Matrix m = new Matrix();    //对图形处理
                m.setRotate(degree);        //旋转
                //       bitmapSmall=Bitmap.createBitmap(myBitmapImage,0,0,myBitmapImage.getWidth(),myBitmapImage.getHeight());
                lastp = Bitmap.createBitmap(myBitmapImage, 0, 0, myBitmapImage.getWidth(), myBitmapImage.getHeight(), m, true);
                myPhoto.setImageBitmap(lastp);       //显示图片
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resizePhoto() {
        BitmapFactory.Options options = new BitmapFactory.Options();   //控制大小压缩的操作类
        options.inJustDecodeBounds = true;//返回图片宽高信息
        BitmapFactory.decodeFile(ImagePath, options);   //从地址解析出位图对象
        //让图片小于1024
        double radio = Math.max(options.outWidth * 1.0d / 1024f, options.outHeight * 1.0d / 1024f);   //让图片长宽都为1024
        options.inSampleSize = (int) Math.ceil(radio);//向上取整倍数   //压缩图片
        options.inJustDecodeBounds = false;//显示图片
        myBitmapImage = BitmapFactory.decodeFile(ImagePath, options);   //获取压缩图片用左边存储
    }

    void readRequest() {             //获取相机拍摄读写权限
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    public int getPicRotate(String path) {          //旋转图片
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);          //命名空间 命名空间所属属性
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readRequest();
        getImage = (Button) findViewById(R.id.getImage);
        myPhoto = (ImageView) findViewById(R.id.showphoto);
        detect = (Button) findViewById(R.id.detect);
        Camera = (Button) findViewById(R.id.Camera);
        Class=(EditText)findViewById(R.id.Class);
        tip = (TextView) findViewById(R.id.tip);        //识别中提示

        Class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class.setText("");
            }
        });

        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_PICK);      //选择数据
                in.setType("image/*");
                startActivityForResult(in, Photo_ALBUM);
            }
        });

        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();            //7.0拍照必加
                File outputImage = new File(Environment.getExternalStorageDirectory() + File.separator + "face.jpg");     //临时照片存储地
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);              //获取Uri
                ImagePath = outputImage.getAbsolutePath();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //跳转相机
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);                          //相片输出路径
                startActivityForResult(intent, CAMERA);                        //返回照片路径
            }
        });
        tip.setVisibility(View.GONE);
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res = null;
                tip.setVisibility(View.VISIBLE);
                if (myBitmapImage == null) {
                    Toast.makeText(MainActivity.this, "请添加图片!!!", Toast.LENGTH_LONG).show();
                    tip.setVisibility(View.GONE);
                }
                else if(Class.getText().toString().trim().equals("请输入查课节次")==true){
                    Toast.makeText(MainActivity.this, "请输入查课节次!!!", Toast.LENGTH_LONG).show();
                    tip.setVisibility(View.GONE);
                    }
                else {
                    System.out.println(Class.getText());
                    int degree = getPicRotate(ImagePath);
                    Matrix m = new Matrix();    //对图形处理
                    m.setRotate(degree);        //旋转
                    bitmapSmall = Bitmap.createBitmap(myBitmapImage, 0, 0, myBitmapImage.getWidth(), myBitmapImage.getHeight(), m, true);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //图片转数据流
                    bitmapSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);     //图片压缩格式，压缩率，文件输出流对象
                    final byte[] arrays = stream.toByteArray();
                    final String pic = android.util.Base64.encodeToString(arrays, Base64.DEFAULT);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String, String> options = new HashMap<>();
                            options.put("quality_control", "NORMAL");
                            options.put("liveness_control", "LOW");
                            options.put("max_user_num", "3");

                            String groupId = "ruan1,ruan2";
                            String imageType = "BASE64";

                            AipFace client = new AipFace("15119543", "lwxkzZOqjm4bcN2DmHoe8giy", "skYUhhZAfsUCFsBud7VQPIdWPvMt7tOM");
                            client.setConnectionTimeoutInMillis(2000);
                            client.setSocketTimeoutInMillis(6000);

                            res = client.search(pic, imageType, groupId, options);

                            try {
                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = res;
                                handler.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Message message = Message.obtain();
                                message.what = 2;
                                handler.sendMessage(message);
                            }


                        }
                    }).start();
                }
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject jsonObject=null;
            String classes = null;
            String Record_class = null;
            String PId =  null;
            String name =  null;
            if (msg.what == 1) {
                JSONObject res = (JSONObject) msg.obj;
                System.out.println(res);
                error_code = res.optString("error_code");
                System.out.println(error_code);
                if (error_code.equals("0") == true) {
                    JSONArray TEMP = res.optJSONObject("result").optJSONArray("user_list");

                    try {
                        jsonObject = TEMP.getJSONObject(0);
                        String Tscore = jsonObject.getString("score");
                         classes = jsonObject.getString("group_id");
                         Record_class = Class.getText().toString().trim();
                         PId = jsonObject.getString("user_id");
                         name = jsonObject.getString("user_info");
                        System.out.println(classes+Record_class+ PId+ name);
                        System.out.println(jsonObject);
                        System.out.println(Tscore);
                        score = Double.parseDouble(Tscore);
                        System.out.println(score);

                        //  score=Math.ceil(Double.parseDouble(Tscore));
                    } catch (JSONException SE) {
                        SE.printStackTrace();
                    }
                    if (score >= 75) {
                        addrecord(classes,Record_class,PId,name);
                        } else
                        Toast.makeText(MainActivity.this, "打卡失败，请重新导入照片", Toast.LENGTH_LONG).show();

                        myBitmapImage = null;
                        InitBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.show, null);
                        tip.setVisibility(View.GONE);
                         myPhoto.setImageBitmap(InitBitmap);
                }
                else
                    Toast.makeText(MainActivity.this, "打卡失败，请重新导入照片", Toast.LENGTH_LONG).show();
                    tip.setVisibility(View.GONE);
            }

        }
    };



}




