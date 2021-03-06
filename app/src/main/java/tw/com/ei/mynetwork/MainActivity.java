package tw.com.ei.mynetwork;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    private EditText pwd,username;
    private Bitmap bmp;
    private UIHander hander;
    private boolean isPermissionOK;
    private File sdroot,savePDF;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=(EditText)findViewById(R.id.username);
        pwd=(EditText)findViewById(R.id.pwd);
        hander = new UIHander();
        img = (ImageView)findViewById(R.id.img);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            // no
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }else {
            isPermissionOK = true;
            init();
        }

    }

    private void init(){
        if (!isPermissionOK) {
            finish();
        }else{
            go();
        }
    }

    private void go(){
        sdroot = Environment.getExternalStorageDirectory();
        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Download...");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isPermissionOK = true;

            }
            init();
        }
    }
    public void Button1(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.tcca.org.tw");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream in = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = br.readLine()) != null){
                        Log.i("brad", line);
                    }
                    in.close();


                }catch(Exception e){
                    Log.i("brad", e.toString());
                }
            }
        }.start();


    }
    public void Button2(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.tcca.org.tw/img/t_03.jpg");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream in = conn.getInputStream();

                    bmp = BitmapFactory.decodeStream(in);
                    hander.sendEmptyMessage(0);
                }catch(Exception e){
                    Log.i("brad", e.toString());
                }
            }
        }.start();


    }
    public void Button3(View view){
        progressDialog.show();
        new Thread(){
            @Override
            public void run() {
                getWebPDF("http://pdfmyurl.com/?url=http://www.tcca.org.tw");
            }
        }.start();
    }
    private void getWebPDF(String urlString){

        try {

            savePDF = new File(sdroot, "test01.pdf");
            FileOutputStream fout = new FileOutputStream(savePDF);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream in = conn.getInputStream();
            byte[] buf = new byte[4096]; int len = 0;
            while ( (len = in.read(buf)) != -1){
                fout.write(buf, 0, len);
            }

            fout.flush();
            fout.close();
            hander.sendEmptyMessage(1);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private class UIHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    img.setImageBitmap(bmp); break;
                case 1:
                    progressDialog.dismiss();
            }

        }
    }
    public void Button4(View view){
        new Thread(){
            @Override
            public void run() {
                getJSONString("http://opendata2.epa.gov.tw/AQX.json");
            }
        }.start();
    }
    private void getJSONString(String jSonString){
        try {
            URL url = new URL(jSonString);
            URLConnection urlConnection=url.openConnection();
            urlConnection.connect();
            BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String JsonStr=br.readLine();//有多行就用FOR
            Log.i("simon", JsonStr);
            parseJSONString(JsonStr);

        }catch (Exception e){
            Log.i("simon", e.toString());
        }
    }
    public void Button5(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.brad.tw/cloudfitness/login.php?account="+username+"&passwd="+pwd+"");
                    HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                    conn.connect();
                    //conn.getInputStream();//一去一回才會送料
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String aa=br.readLine();
                    Log.i("simon","aa="+aa);
                }catch (Exception e ){
                    Log.i("simon",e.toString());
                }

            }
        }.start();
    }
    /*
 * 新增/登入帳號密碼
 * url: http://www.brad.tw/cloudfitness/login.php?account=xxx&passwd=xxx
 * return: {"result":"結果值"}
 * return: {"result":"0", id:"123", "gender":"m/f", "age":"20", "height":"170"}
 * 結果值:
 *       0 : 帳密驗證符合, 傳回 id 值
 *       1 : 帳號不存在, 已新增該組帳密, 傳回新的 id 值
 *      -1 : http request error
 *      -2 : mysql connect error
 *      -3 : 帳號已存在, 而密碼驗證錯誤
 */
    private void parseJSONString(String json){
        try {
            JSONArray jsonArray=new JSONArray(json);
            Log.i("simon","Length:"+jsonArray.length());
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String country=jsonObject.getString("County");
                String sitename=jsonObject.getString("SiteName");
                String pm25=jsonObject.getString("PM2.5");
                Log.i("simon",country+":"+sitename+":"+pm25);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void Button6(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    MultipartUtility mu=new MultipartUtility("http://10.0.1.1/brad02.php", "UTF-8");
                    mu.addFormField("data1", "1111");
                    mu.addFormField("data2", "2222");
                    List<String> ret = mu.finish();
                    for (String line : ret){
                        Log.i("simon", line);
                    }
                } catch (IOException e) {
                    Log.i("simon", e.toString());
                }

            }
        }.start();
    }
    public void Button7(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    MultipartUtility mu=new MultipartUtility("http://10.0.1.1/brad04.php", "UTF-8");
                    mu.addFilePart("test01.pdf",savePDF);
                    List<String> ret = mu.finish();
                    for (String line : ret){
                        Log.i("simon", line);
                    }
                } catch (IOException e) {
                    Log.i("simon", e.toString());
                }

            }
        }.start();
    }
}
