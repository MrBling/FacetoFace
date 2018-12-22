package com.example.z7381.facetoface.Admin;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.aip.face.AipFace;
import com.example.z7381.facetoface.Listen.ReferListen;
import com.example.z7381.facetoface.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@SuppressLint("NewApi")
public class adminActivity extends AppCompatActivity {


    private Button getImage, detect, Camera, add, delete, update,refer;
    private ImageView myPhoto;
    private Bitmap myBitmapImage = null;
    private String ImagePath = null;
    private EditText pid, name,classes;
    private String Pid, Name,Classes;
    private Uri imageUri;
    private int Photo_ALBUM = 1, CAMERA = 2;
    private JSONObject res = null;
    private Bitmap bitmapSmall;
    private Bitmap lastp;
    private Double score;
    private boolean temp;

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
        setContentView(R.layout.admin);
        readRequest();
        getImage = findViewById(R.id.getImage);
        detect = findViewById(R.id.detect);
        Camera = findViewById(R.id.Camera);
        add = findViewById(R.id.add);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);
        pid = findViewById(R.id.pid);
        name = findViewById(R.id.name);
        classes=findViewById(R.id.classes);
        myPhoto = findViewById(R.id.showphoto);
        refer=findViewById(R.id.refer);

     ReferListen ReferListen=new ReferListen(this);
     refer.setOnClickListener(ReferListen);

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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = name.getText().toString();
                Pid = pid.getText().toString();
                Classes=classes.getText().toString();


                if (myBitmapImage == null) {
                    Toast.makeText(adminActivity.this, "请添加图片!!!", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("传输中");
                    int degree = getPicRotate(ImagePath);
                    System.out.println("degree" + degree);
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
                            AipFace client = new AipFace("15119543", "lwxkzZOqjm4bcN2DmHoe8giy", "skYUhhZAfsUCFsBud7VQPIdWPvMt7tOM");
                            client.setConnectionTimeoutInMillis(2000);
                            client.setSocketTimeoutInMillis(6000);
                            temp = Add.Add(client, Name, Pid, pic,Classes);

                            try {
                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = res;
                                handleradd.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Message message = Message.obtain();
                                message.what = 2;
                                handleradd.sendMessage(message);
                            }

                        }


                    }).start();
                    ;
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pid = pid.getText().toString();
                Classes=classes.getText().toString();

                if (myBitmapImage == null) {
                    Toast.makeText(adminActivity.this, "请添加图片!!!", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("传输中");
                    int degree = getPicRotate(ImagePath);
                    System.out.println("degree" + degree);
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
                            AipFace client = new AipFace("15119543", "lwxkzZOqjm4bcN2DmHoe8giy", "skYUhhZAfsUCFsBud7VQPIdWPvMt7tOM");
                            client.setConnectionTimeoutInMillis(2000);
                            client.setSocketTimeoutInMillis(6000);
                            temp = Update.Update(client, Pid, pic,Classes);

                            try {
                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = res;
                                handlerupdate.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Message message = Message.obtain();
                                message.what = 2;
                                handlerupdate.sendMessage(message);
                            }

                        }


                    }).start();
                    ;
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pid = pid.getText().toString();
                Classes=classes.getText().toString();
                if (Pid == null) {
                    Toast.makeText(adminActivity.this, "请添加账号!!!", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("删除中");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AipFace client = new AipFace("15119543", "lwxkzZOqjm4bcN2DmHoe8giy", "skYUhhZAfsUCFsBud7VQPIdWPvMt7tOM");
                            client.setConnectionTimeoutInMillis(2000);
                            client.setSocketTimeoutInMillis(6000);
                            temp = Delete.Delete(client,Pid,Classes);

                            try {
                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = res;
                                handlerdelete.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Message message = Message.obtain();
                                message.what = 2;
                                handlerdelete.sendMessage(message);
                            }

                        }


                    }).start();
                    ;
                }
            }
        });





    }

    private Handler handleradd = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (temp == true) {
                    Toast.makeText(adminActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                    myBitmapImage = null;
                    Bitmap InitBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.show, null);
                    myPhoto.setImageBitmap(InitBitmap);

                } else
                    Toast.makeText(adminActivity.this, "添加失败，请重新选择照片", Toast.LENGTH_LONG).show();
            }

        }
    };

    private Handler handlerupdate=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (temp == true) {
                    Toast.makeText(adminActivity.this, "更新成功", Toast.LENGTH_LONG).show();
                    myBitmapImage = null;
                    Bitmap InitBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.show, null);
                    myPhoto.setImageBitmap(InitBitmap);

                } else
                    Toast.makeText(adminActivity.this, "更新失败，请重新选择照片", Toast.LENGTH_LONG).show();
            }

        }
    };

    private Handler handlerdelete = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (temp == true) {
                    Toast.makeText(adminActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(adminActivity.this, "删除失败，请重新确认删除用户信息", Toast.LENGTH_LONG).show();
            }

        }
    };



}