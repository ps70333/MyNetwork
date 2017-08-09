package tw.com.ei.mynetwork;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void Button1(View v){
        //主緒內部允許執行http所以要開一條執行緒-->ANDROID 4.0之後
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.tcca.org.tw");
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    InputStream is=conn.getInputStream();
                    BufferedReader br= new BufferedReader((new InputStreamReader(is)));
                    String line="";
                    while((line=br.readLine())!=null){
                        Log.i("simon",line);
                    }

                }catch (Exception e){
                    Log.i("simon",e.toString());
                }
            }
        }.start();

    }
    public void Button2(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.tcca.org.tw/manasystem/files/banner/1c636b36-b9cf-4549-b963-958fbace44d2.jpg");
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    InputStream is=conn.getInputStream();
                    

                }catch (Exception e){
                    Log.i("simon",e.toString());
                }
            }
        }.start();
    }
    public void Button3(View v){

    }
}
