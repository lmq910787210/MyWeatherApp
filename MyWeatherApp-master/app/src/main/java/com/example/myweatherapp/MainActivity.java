package com.example.myweatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final static String MyFileName="myWeatherFile";
    TextView tex_show;
    TextView tex_top;
    String Name=null;
    String message="Hello！";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri smsToUri = Uri.parse("smsto:18810911869");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                intent.putExtra("sms_body",message);
                startActivity(intent);
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        //获取当前时间
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

        final EditText ect_getCity= (EditText) findViewById(R.id.edt_getCity);

        tex_top= (TextView) findViewById(R.id.tex_top);
        tex_show= (TextView) findViewById(R.id.tex_show);
        //显示数据
        show_City();
        show_data();

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case 0:
                        Toast.makeText(MainActivity.this, "没有该数据源信息", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "获取成功", Toast.LENGTH_SHORT).show();
                        tex_show.setText(msg.obj.toString());
                        break;
                }
            }
        };
        final Handler handlerCity = new Handler(){
            @Override
            public void handleMessage(Message msgCity) {

                switch (msgCity.what) {
                    case 0:
                        Toast.makeText(MainActivity.this, "获取成功", Toast.LENGTH_SHORT).show();

                        tex_top.setText(msgCity.obj.toString()+"天气预报");
                        break;
                }
            }
        };

        final Runnable getWeather=new Runnable() {
            @Override
            public void run() {

                try {
                    final String url="http://v.juhe.cn/weather/index?format=1&cityname="+Name+"&key=be45c8d5d4b076008e0e06b32eb68701";
                    //http://v.juhe.cn/weather/index?format=1&cityname=北京&key=be45c8d5d4b076008e0e06b32eb68701
                    URL httpUrl=new URL(url);
                    Log.v("url",url);
                    try {
                        HttpURLConnection conn= (HttpURLConnection) httpUrl.openConnection();
                        conn.setReadTimeout(3000);//超时处理
                        conn.setRequestMethod("GET");//GET获取

                        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String str;
                        str=bufferedReader.readLine();
                        try {
                            JSONObject object=new JSONObject(str);
                            int result=object.getInt("resultcode");
                            if (result==200)
                            {
                                JSONObject resultJson=object.getJSONObject("result");
                                JSONObject todayJson=resultJson.getJSONObject("today");
                                String temperature=todayJson.getString("temperature"); //获取温度
                                String weather=todayJson.getString("weather");//获取天气状态
                                Log.v("A",weather);

                                JSONObject skJson=resultJson.getJSONObject("sk");
                                String time=skJson.getString("time");//数据获取时间
                                String humidity=skJson.getString("humidity");//获取湿度
                                String wind_direction=skJson.getString("wind_direction");//风向
                                String wind_strength=skJson.getString("wind_strength");//风力等级
                                //today
                                String wind=todayJson.getString("wind");//获取风的状态
                                String city=todayJson.getString("city");//获取当前城市
                                String date_y=todayJson.getString("date_y");//获取当前日期
                                String dressing_index=todayJson.getString("dressing_index");//获取当前体感温度
                                String dressing_advice=todayJson.getString("dressing_advice");//今日天气推荐
                                String week=todayJson.getString("week");//获取星期几

                                Log.v("A",date_y+city+"天气预报"+weather+"当前气温"+temperature
                                        +"当前体感温度"+dressing_index+wind+dressing_advice);
                               // date_y+"，"+city+"气温"+temperature+"，"+weather+"，"+dressing_index+"，"+dressing_advice
                                message=date_y+"，"+city+"气温"+temperature+"，"+weather+"，"+dressing_index+"，"+dressing_advice;

                                //future
                                SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");
                                Date curDate = new Date(System.currentTimeMillis());
                                String str_date = formatter.format(curDate);
                                //获取当前时间

                                //明天数据
                                int one=Integer.parseInt(str_date);
                                one=one+1;
                                String one_day=Integer.toString(one);
                                one_day="day_"+one_day;
                                JSONObject futureJson=resultJson.getJSONObject("future");
                                JSONObject one_dayJson=futureJson.getJSONObject(one_day);
                                String one_week=one_dayJson.getString("week");
                                String one_wind=one_dayJson.getString("wind");
                                String one_date=one_dayJson.getString("date");
                                String one_temperature=one_dayJson.optString("temperature");
                                String one_weather=one_dayJson.optString("weather");

                                //后天数据
                                int two=Integer.parseInt(str_date);
                                two=two+2;
                                String two_day=Integer.toString(two);
                                two_day="day_"+two_day;
                                Log.v("One_DAY",one_day);
                                Log.v("tow",two_day);
                                JSONObject two_dayJson=futureJson.getJSONObject(two_day);
                                String two_week=two_dayJson.getString("week");
                                String two_wind=two_dayJson.getString("wind");
                                String two_date=two_dayJson.getString("date");
                                String two_temperature=two_dayJson.getString("temperature");
                                String two_weather=two_dayJson.getString("weather");

                                String wdata="更新时间:"+date_y+time+"\t\t"+week+
                                        "\n"+weather+ "\t\t"+temperature+"\t\t\t湿度"+humidity+"\t\t\t"+ wind_direction+wind_strength+
                                        "\n\n温馨提示:"+dressing_advice+

                                        "\n\n"+"\t\t\t\t\t\t\t\t\t\t\t\t\t未来几天天气情况\n\n"+

                                        "明天："+one_date+"\t\t\t"+one_week+"\n"+
                                        one_weather+"\t\t\t"+one_temperature+"\t\t\t"+one_wind+"\n\n" +
                                        "后天："+two_date+"\t\t\t"+two_week+"\n"+
                                        two_weather+"\t\t\t"+two_temperature+"\t\t\t"+two_wind;

                                //储存天气信息
                                OutputStream out=null;
                                try {
                                    FileOutputStream fileOutputStream=openFileOutput(MyFileName,MODE_PRIVATE);
                                    out=new BufferedOutputStream(fileOutputStream);
                                    try {
                                        out.write(wdata.getBytes(StandardCharsets.UTF_8));
                                    }
                                    finally {
                                        if(out!=null)
                                            out.close();
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }

                                //储存城市信息
                                try {
                                    FileOutputStream fileOutputStream=openFileOutput("CityName",MODE_PRIVATE);
                                    out=new BufferedOutputStream(fileOutputStream);
                                    try {
                                        out.write(city.getBytes(StandardCharsets.UTF_8));
                                    }
                                    finally {
                                        if(out!=null)
                                            out.close();
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                Message msg=new Message();
                                msg.obj=wdata;
                                msg.what=1;
                                handler.sendMessage(msg);
                                Message msgCity=new Message();
                                msgCity.obj=city;
                                msgCity.what=0;
                                handlerCity.sendMessage(msgCity);
                            }
                            else {
                                Message msg=new Message();
                                msg.what=0;
                                handler.sendMessage(msg);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        };

        Button button= (Button) findViewById(R.id.btn_show);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name=ect_getCity.getText().toString();
                Thread thread=new Thread(null,getWeather,"thread");
                thread.start();
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            Uri smsToUri = Uri.parse("smsto:10086");
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            intent.putExtra("sms_body",message);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void show_data()
    {
        try {
            FileInputStream fis = openFileInput(MyFileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedReader reader = new BufferedReader (new InputStreamReader(bis));
            StringBuilder stringBuilder=new StringBuilder("");
            try{
                while (reader.ready()) {
                    stringBuilder.append((char)reader.read());
                }
                String show=stringBuilder.toString();
                Log.v("log",show);
                tex_show.setText(show);
            }
            finally {
                if(reader!=null)
                    reader.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void show_City()
    {
        try {
            FileInputStream fis = openFileInput("CityName");
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedReader reader = new BufferedReader (new InputStreamReader(bis));
            StringBuilder stringBuilder=new StringBuilder("");
            try{
                while (reader.ready()) {
                    stringBuilder.append((char)reader.read());
                }
                String show=stringBuilder.toString();
                tex_top.setText(show+"天气预报");
            }
            finally {
                if(reader!=null)
                    reader.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
