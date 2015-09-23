package com.yuanlaixian.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Button login_btn;
    private EditText lg_username;
    private EditText lg_pwd;
    private Handler handler;


    /*
     * 登陆处理函数
     */
    private void login_process() {
        String target = UrlWays.LoginUrl;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpRequest = new HttpPost(target);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("lg_username", lg_username.getText().toString()));
        params.add(new BasicNameValuePair("lg_pwd", lg_pwd.getText().toString()));

        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            Message m = handler.obtainMessage();
            m.obj = EntityUtils.toString(httpResponse.getEntity());
            handler.sendMessage(m);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

	    Publicfooter fragment = new Publicfooter();
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();  		    
	    transaction.remove(fragment);
	    transaction.replace(R.id.public_buttom, fragment);  
        transaction.commit();  
        login_btn = (Button) findViewById(R.id.login_btn);
        lg_username = (EditText) findViewById(R.id.lg_username);
        lg_pwd = (EditText) findViewById(R.id.lg_pwd);
        
        login_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        login_process();
                    }
                }).start();
			}
		});

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.obj.toString();
                if (result != null) {
                    try {
                        JSONTokener jsonParser = new JSONTokener(result);
                        JSONObject result_obj = (JSONObject) jsonParser.nextValue();
                        Boolean isok = result_obj.getBoolean("isok");
                        if (isok) {
                            SharedPreferences.Editor editor = getSharedPreferences("lock", MODE_PRIVATE).edit();
                            //姝ラ2-2锛氬皢鑾峰彇杩囨潵鐨勫�鏀惧叆鏂囦欢
                            editor.putString("token", result_obj.getString("data"));
                            //姝ラ3锛氭彁浜�
                            editor.commit();
                            startActivity(new Intent(LoginActivity.this,MembermainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                super.handleMessage(msg);
            }
        };
    }
}
