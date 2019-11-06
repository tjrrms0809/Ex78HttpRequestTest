package com.ahnsafety.ex78httprequesttest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText etName;
    EditText etMsg;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName= findViewById(R.id.et_name);
        etMsg= findViewById(R.id.et_message);
        tv= findViewById(R.id.tv);

    }

    public void clickGet(View view) {

        //서버에 name, message를 전송하도록
        //네트워크작업은 반드시 별도의 Thread

        new Thread(){
            @Override
            public void run() {

                String name= etName.getText().toString();
                String msg= etMsg.getText().toString();

                //Get방식을 데이터를 보낼 서버주소
                String serverUrl= "http://tjrrms0809.dothome.co.kr/Android/getTest.php";

                //Get방식은 보낼데이터(name, msg)를 URL뒤에 붙여서 보내느 방식
                //한글은 인터넷 URL에 사용불가!! 그래서 한글을 utf-8 인코딩

                try {
                    name= URLEncoder.encode(name,"utf-8");
                    msg= URLEncoder.encode(msg,"utf-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String getUrl= serverUrl+"?name=" + name +"&msg="+msg;

                //서보와 연결작업 시작!

                try {
                    URL url= new URL(getUrl);
                    //URL 객체는 데이터를 읽어 들이기 위한
                    //InputStream을 여는 것만 가능하고
                    //데이터를 서버로 보낼 수 없음.
                    //즉, OutputStream을 열수는 없음.

                    //HTTP 통신 규약에 따라 데이터를
                    //주고받는 역할을 수행하는 갱체 얻어오기
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");//반드시 대문자!
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false); //캐쉬저장을 원하지 않음 데이터 속도 성능을 위해서

                    //보낼데이터가 있다면 여기서 OutputStream을 통해 전송
                    //Get방식으로 이미 URL에 데이터를 붙여 보냈기에 필요 없음.

                    //getTest.php로부터 echo된 결과Data를 읽어와서 보여주기
                    InputStream is=connection.getInputStream();
                    InputStreamReader isr= new InputStreamReader(is); //바이트를 문자로 변환
                    BufferedReader reader= new BufferedReader(isr);   //문자를 문장으로

                    final StringBuffer buffer = new StringBuffer();

                    String line= reader.readLine();
                    while (line!=null){
                        buffer.append(line+"\n");
                        line= reader.readLine();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(buffer.toString());
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();

    }

    public void clickPost(View view) {

        new Thread(){
            @Override
            public void run() {

                String name = etName.getText().toString();
                String msg = etMsg.getText().toString();

                String serverUrl = "http://tjrrms0809.dothome.co.kr/Android/postTest.php";

                try {
                    URL url = new URL(serverUrl);

                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    //보낼 데이터
                    String query="name=" + name + "&msg=" + msg;

                    OutputStream os= connection.getOutputStream();
                    OutputStreamWriter writer= new OutputStreamWriter(os);

                    writer.write(query,0,query.length());
                    writer.flush();
                    writer.close();

                    //postTest.php로부터 echo결과 받기
                    InputStream is= connection.getInputStream();
                    InputStreamReader isr= new InputStreamReader(is);
                    BufferedReader reader= new BufferedReader(isr);

                    final StringBuffer buffer = new StringBuffer();

                    String line= reader.readLine();
                    while (line!=null){
                        buffer.append(line+"\n");
                        line= reader.readLine();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(buffer.toString());
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
