package com.yuanlaixian.app;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Publicfooter extends Fragment {
    private Button login_btn,main_btn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.public_footer, null);
		// TODO Auto-generated method stub
        login_btn = (Button) view.findViewById(R.id.button_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //姝ラ1锛氬垱寤轰竴涓猄haredPreferences鎺ュ彛瀵硅薄
                        SharedPreferences read = getActivity().getSharedPreferences("lock", 0);
                        //姝ラ2锛氳幏鍙栨枃浠朵腑鐨勫�
                        String token = read.getString("token", "");
                        String target = UrlWays.CkloginUrl + "?tk=" + token;
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpGet httpRequest = new HttpGet(target);
                        HttpResponse httpResponse;
                        try {
                            httpResponse = httpclient.execute(httpRequest);
                            Message m = handler.obtainMessage();
                            m.obj = EntityUtils.toString(httpResponse.getEntity());
                            m.what = 0x002;
                            handler.sendMessage(m);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        main_btn = (Button) view.findViewById(R.id.button_main);
        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	startActivity(new Intent(getActivity(),MainActivity.class));
            }
        });
		return view;
	}
	
    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x002) {
                //鍒ゆ柇鐧婚檰
                String result = msg.obj.toString();
                if (result != null) {
                    try {
                        JSONTokener jsonParser = new JSONTokener(result);
                        JSONObject result_obj = (JSONObject) jsonParser.nextValue();
                        Boolean isok = result_obj.getBoolean("isok");
                        if (isok) {
                            Intent intent = new Intent(getActivity(), MembermainActivity.class);
                            startActivity(intent);
                        } else {

                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    };

}
