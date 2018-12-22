package com.example.z7381.facetoface.Refer;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.z7381.facetoface.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class Attend extends AppCompatActivity {
    private Spinner referclass;
    private EditText refertime;
    private Button refer;
    private String selectClass = null;
    private String selectTime = null;
    private RadioGroup attention;
    private String Attention = "到课";
    private JSONObject jsonObject = null;
    private List<Student> StudentList = new ArrayList<Student>();
    private PieChartView chart;
    private int[] tempCount;
    private int arriveCount=0;

    public void Referall(final String classes){
        Log.d("Referall",classes);
        //请求地址,需要换接口

        String url="http://47.106.10.15:8080/FtoFserver/FtoFserver/Refercount";
        //  String url = "http://139.199.184.59:8080/getParentId";
        final String tag = "Refercount";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(Attend.this);
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
                            String temp = jsonObject.getString("num");
                            tempCount[1]=Integer.parseInt(temp);
                            System.out.println(tempCount[1]);
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
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,1,1.0f));
        //将请求添加到队列中
        requestQueue.add(request);
    }


    public void attendrecord(final String classes, final String Record_class,final String flag) {
        Log.d("attend", classes + "," + Record_class);
        //请求地址,需要换接口
        String url=null;
        if(flag.equals("到课"))
        {
          url = "http://47.106.10.15:8080/FtoFserver/FtoFserver/Attendance?classes="+classes+"&Record_class="+Record_class;
        }else if(flag.equals("缺课"))
        {
            url = "http://47.106.10.15:8080/FtoFserver/FtoFserver/NotAttend?classes="+classes+"&Record_class="+Record_class;
        }
        //  String url = "http://139.199.184.59:8080/getParentId";
        String tag = "Attendance";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(Attend.this);
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    public void onResponse(String response) {
                        System.out.println(response);
                        Log.e("response", response);
                        StudentList.clear();
                        try {
                            JSONArray json = new JSONArray(response);
                            arriveCount=json.length();
                            tempCount[0]=arriveCount;
                           // System.out.println(arriveCount+"       !!!!!!!");
                           for (int i=0;i<json.length();i++){
                               jsonObject=json.getJSONObject(i);
                               Iterator<String> it=jsonObject.keys();
                             //  System.out.println(jsonObject);
                               while (it.hasNext()){
                                   String key=it.next();
                                   String value = jsonObject.getString(key);
                                  // System.out.println(key+"  "+value);
                                   Student student;
                                   if(flag.equals("到课")){
                                   student=new Student(value,selectClass,key,R.drawable.arrive);
                                   }
                                   else {
                                       student = new Student(value, selectClass, key, R.drawable.notarrive);
                                   }
                                //   arriveCount++;
                                   StudentList.add(student);
                               }
                           }
                          //  tempCount[0]=arriveCount;
                            StudentAdapter adapter=new StudentAdapter(Attend.this,R.layout.style,StudentList);
                            ListView listView=(ListView)findViewById(R.id.list_view);
                            listView.setAdapter(adapter);
                            generateData();
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）;
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.d("error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
             // params.put("classes", "ruan2");
            // params.put("Record_class", "5.1.2");
                return params;
              //  http://192.168.43.147:8080/FtoFserver/Attendance?classes=ruan2&Record_class=5.1.2
            }

        };
        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        //将请求添加到队列中
        requestQueue.add(request);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refer);
        tempCount = new int[2];
        referclass = (Spinner) findViewById(R.id.referclass);
        refertime = (EditText) findViewById(R.id.refertime);
        refer = (Button) findViewById(R.id.refer);
        attention = (RadioGroup) findViewById(R.id.attention);
        chart=(PieChartView)findViewById(R.id.pcv);
        chart.setCircleFillRatio(0.8f);//设置饼状图占整个view的比例
        chart.setChartRotationEnabled(true);//设置饼状图可以旋转
        chart.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, SliceValue sliceValue) {

            }

            @Override
            public void onValueDeselected() {

            }
        });





        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter
                .createFromResource(this, R.array.Classes,
                        android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        referclass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectClass = parent.getItemAtPosition(position).toString();
                Toast.makeText(Attend.this, "选择的班级： " + selectClass, Toast.LENGTH_LONG).show();
                Referall(selectClass);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        attention.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.attend) {
                    Attention = "到课";
                } else
                    Attention = "缺课";
            }
        });

        refer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectTime = refertime.getText().toString().trim();
                if (Attention.equals("到课") == true) {
                     attendrecord(selectClass,selectTime,"到课");
                    chart.setVisibility(View.VISIBLE);

                }
                if (Attention.equals("缺课") == true) {
                    chart.setVisibility(View.GONE);
                    attendrecord(selectClass,selectTime,"缺课");

                }
            }
        });
    }
    private void generateData() {
        List<SliceValue> sliceValues = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            SliceValue sliceValue=new SliceValue();
            sliceValue.setColor(ChartUtils.pickColor());
            if(i==0){
            sliceValue.setLabel("到课人数："+tempCount[i]);
            System.out.println("arriveCount"+arriveCount);
            sliceValue.setValue(tempCount[i]);
            }
            else{
                sliceValue.setLabel("缺课人数："+(tempCount[i]-tempCount[0]));
                System.out.println("notarriveCount"+(tempCount[i]-tempCount[0]));
                sliceValue.setValue((tempCount[1]-tempCount[0]));
            }

            sliceValues.add(sliceValue);

        }
        PieChartData data = new PieChartData(sliceValues);

       data.setHasLabels(true);//显示文字

      data.setHasLabelsOnlyForSelected(true);//不用点击显示占的百分比
      data.setHasLabelsOutside(true);//占的百分比是否显示在饼图外面
        data.setHasCenterCircle(true);//是否是环形显示

      data.setValueLabelTextSize(20);
       data.setCenterCircleColor(Color.WHITE);//设置环形中间的颜色
        data.setCenterCircleScale(0.4f);//设置环形的大小级别

      data.setCenterText1Color(Color.BLACK);//文字颜色
       data.setCenterText1FontSize(25);//文字大小

        chart.setPieChartData(data);
        chart.setValueSelectionEnabled(true);//选择饼图某一块变大
        chart.setAlpha(0.9f);//设置透明度
        chart.setCircleFillRatio(1f);//设置饼图大小



        //只有设置中间空心才能有效设置文字


        // Get font size from dimens.xml and convert it to sp(library uses sp values).

        chart.setPieChartData(data);
    }



}
