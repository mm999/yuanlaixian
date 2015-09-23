package com.yuanlaixian.app;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CatesFragment extends Fragment {
	private List<Map<String, String>> mylist2;
	private String id;
	TableLayout tb;
	
	private	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x003){
			
		        int numberOfRow =(int)Math.ceil(((double)mylist2.size()/3)); 
		        int numberOfColumn =3;  
		        int cellDimension =LayoutParams.WRAP_CONTENT;  
		        int tb_position_id = 0;
		        for (int row = 0;row<numberOfRow; row ++){
		            TableRow tableRow = new TableRow(getActivity());
		            tableRow.setLayoutParams(new LayoutParams(cellDimension,LayoutParams.WRAP_CONTENT));  
		            for (int column =0;column<numberOfColumn; column++){  
		            	if(tb_position_id>=mylist2.size()){break;}
		            	LinearLayout lla = new LinearLayout(getActivity());
		            	LinearLayout.LayoutParams llap = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
		        		lla.setLayoutParams(llap);
		            	View view = getActivity().getLayoutInflater().inflate(R.layout.cate_item, null);
		            	view.setId(Integer.parseInt(mylist2.get(tb_position_id).get("id")));
		            	ImageView iv = (ImageView)view.findViewById(R.id.iv);
		            	String imageUrl = mylist2.get(tb_position_id).get("icon");
		            	BitmapWorkerTask task = new BitmapWorkerTask(iv,imageUrl); 
		            	 task.execute(R.id.iv);  
		            	TextView tv = (TextView) view.findViewById(R.id.tv);
		            	if(tv != null){
		            		tv.setText(mylist2.get(tb_position_id).get("name"));
		            	}
		            	view.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),GoodsActivity.class);
								intent.putExtra("gcate_id", Integer.toString(arg0.getId()));
								startActivity(intent);
							}
						});
		        		tableRow.addView(view);
		                
		                tb_position_id++;
		            }  
		            tb.addView(tableRow);
		        } 
			}
		}
	};
	
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
    	mylist2 = new ArrayList<Map<String, String>>();
    	id = getArguments().getString("id");
    	tb = new TableLayout(getActivity());
    	
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 姝ラ1锛氬垱寤轰竴涓猄haredPreferences鎺ュ彛瀵硅薄
				SharedPreferences read = getActivity().getSharedPreferences("lock",0);
				// 姝ラ2锛氳幏鍙栨枃浠朵腑鐨勫�
				String token = read.getString("token", "");
				String target = UrlWays.CatelistsubUrl + id + "?tk=" + token;
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpRequest = new HttpGet(target);
				HttpResponse httpResponse;
				try {
					httpResponse = httpclient.execute(httpRequest);

					try {
						JSONTokener jsonParser = new JSONTokener(EntityUtils.toString(httpResponse.getEntity()));
						JSONObject result_obj = (JSONObject) jsonParser
								.nextValue();
						Boolean isok = result_obj.getBoolean("isok");
						if (isok) {
							JSONArray jsonArray = result_obj
									.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject2 = (JSONObject) jsonArray
										.opt(i);

								String name = jsonObject2.getString("name");
								String id = jsonObject2.getString("id");
								String icon = "http://yuanlaixian.sharecarding.com/upload/" + jsonObject2.getString("icon");
								Map<String, String> map = new HashMap<String, String>();
								map.put("name", name);
								map.put("id", id);
								map.put("icon", icon);
								mylist2.add(map);
							}
		                    Message msg = handler.obtainMessage();
		                    msg.what = 0x003;
		                    handler.sendMessage(msg);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}




	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		return tb;
    }
	
	private Bitmap returnBitMap(String url) { 
		URL myFileUrl = null; 
		Bitmap bitmap = null; 
		try { 
		myFileUrl = new URL(url); 
		} catch (MalformedURLException e) { 
		e.printStackTrace(); 
		} 
		try { 
		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection(); 
		conn.setDoInput(true); 
		conn.connect(); 
		InputStream is = conn.getInputStream(); 
		bitmap = BitmapFactory.decodeStream(is); 
		is.close(); 
		} catch (IOException e) { 
		e.printStackTrace(); 
		} 
		return bitmap; 
	} 
	
	class BitmapWorkerTask extends AsyncTask {
		
	    @Override
		protected Object doInBackground(Object... arg0) {
	    	bt = returnBitMap(url);
	        return bt;
		}

		@Override
		protected void onPostExecute(Object result) {
	         imageView.setImageBitmap(bt);
		}
		private Bitmap bt;
		private ImageView imageView;
		private String url;
		private final WeakReference imageViewReference;
	    private int data = 0;

	    public BitmapWorkerTask(ImageView imageView,String url) {
	        //使用弱引用保证ImageView可以被正常回收
	        imageViewReference = new WeakReference(imageView);
	        this.imageView = imageView;
	        this.url = url;
	    }
	}
    
}
