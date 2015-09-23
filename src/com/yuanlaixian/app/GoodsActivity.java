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

import android.R.menu;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GoodsActivity extends Activity {
	private static final String TextView = null;
	private String parent;
	private String sub;
	private List<Map<String, String>> subcate;
	private List<Map<String, String>> cateArr;
	private List<Map<String, String>> goodsArr;

	private ListView goods_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods);
		Publicfooter fragment = new Publicfooter();
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.remove(fragment);
		transaction.replace(R.id.public_buttom, fragment);
		transaction.commit();
		new Thread(getCatesRunnable).start();
		new Thread(getGoodsRunnable).start();
		findViewById(R.id.return_activity).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	            Intent myIntent = new Intent();  
	            myIntent = new Intent(GoodsActivity.this, CategoryActivity.class);  
	            startActivity(myIntent);  
	            finish();
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x001) {
				LinearLayout la = (LinearLayout) findViewById(R.id.catearr);
				for (int i = 0; i < cateArr.size(); i++) {
					TextView tView = new TextView(GoodsActivity.this);
					tView.setTextSize(11);
					tView.setPadding(10, 0, 10, 0);
					tView.setText(cateArr.get(i).get("name"));
					if (cateArr.get(i).get("id").equals(parent)) {
						tView.setTextColor(Color.parseColor("#dab866"));
					} else {
						tView.setTextColor(Color.parseColor("#6c6c6c"));
					}
					tView.setId(Integer.parseInt(cateArr.get(i).get("id")));
					tView.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(GoodsActivity.this,CategoryActivity.class);
							intent.putExtra("gcate_id", Integer.toString(arg0.getId()));
							startActivity(intent);
						}
					});
					la.addView(tView);
				}
				la = (LinearLayout) findViewById(R.id.subcate);
				for (int i = 0; i < subcate.size(); i++) {
					TextView tView = new TextView(GoodsActivity.this);
					tView.setTextSize(11);
					tView.setPadding(10, 0, 10, 0);
					tView.setText(subcate.get(i).get("name"));
					if (subcate.get(i).get("id").equals(sub)) {
						tView.setTextColor(Color.parseColor("#dab866"));
					} else {
						tView.setTextColor(Color.parseColor("#6c6c6c"));
					}
					tView.setId(Integer.parseInt(subcate.get(i).get("id")));
					tView.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(GoodsActivity.this,GoodsActivity.class);
							intent.putExtra("gcate_id", Integer.toString(arg0.getId()));
							startActivity(intent);
						}
					});
					la.addView(tView);
				}
			}
			if (msg.what == 0x002) {
				goods_list = (ListView) findViewById(R.id.goods_list);
				MyListAdapter myAdapter = new MyListAdapter(GoodsActivity.this);
				goods_list.setAdapter(myAdapter);
			}
		}
	};

	private Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
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

		public BitmapWorkerTask(ImageView imageView, String url) {
			// 使用弱引用保证ImageView可以被正常回收
			imageViewReference = new WeakReference(imageView);
			this.imageView = imageView;
			this.url = url;
		}
	}

	class MyListAdapter extends BaseAdapter {

		public MyListAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return goodsArr.size();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView goods_pic = null;
			TextView goods_name = null;
			TextView goods_desc = null;
			TextView goods_price = null;
			TextView goods_measure = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.goods_item, null);
				goods_name = (TextView) convertView
						.findViewById(R.id.goods_name);
				goods_name.setText(goodsArr.get(position).get("name"));
				goods_pic = (ImageView) convertView.findViewById(R.id.goodspic);
				goods_desc = (TextView) convertView.findViewById(R.id.goods_desc);
				goods_desc.setText(goodsArr.get(position).get("keysoption")+" "+goodsArr.get(position).get("weight")+"元/"+goodsArr.get(position).get("measure"));
				goods_price = (TextView) convertView.findViewById(R.id.goods_price);
				goods_price.setText(goodsArr.get(position).get("price"));
				goods_measure = (TextView) convertView.findViewById(R.id.measure);
				goods_measure.setText("元/"+goodsArr.get(position).get("measure"));
            	String imageUrl = goodsArr.get(position).get("pic");
            	BitmapWorkerTask task = new BitmapWorkerTask(goods_pic,imageUrl); 
            	task.execute(R.id.goodspic);  
			}
			if (position % 2 == 0) {
				convertView.setBackgroundColor(Color.parseColor("#EFEFEF"));
			} else {
				convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
			}

			return convertView;
		}

		private Context mContext;
	}

	private Runnable getCatesRunnable = new Runnable() {

		@Override
		public void run() {
			subcate = new ArrayList<Map<String, String>>();
			cateArr = new ArrayList<Map<String, String>>();
			// TODO Auto-generated method stub
			String gcate_id = getIntent().getStringExtra("gcate_id");
			// 姝ラ1锛氬垱寤轰竴涓猄haredPreferences鎺ュ彛瀵硅薄
			SharedPreferences read = getSharedPreferences("lock", 0);
			// 姝ラ2锛氳幏鍙栨枃浠朵腑鐨勫�
			String token = read.getString("token", "");
			String target = UrlWays.CatesParentUrl + gcate_id + "?tk=" + token;
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpRequest = new HttpGet(target);
			HttpResponse httpResponse;
			try {
				httpResponse = httpclient.execute(httpRequest);

				try {
					JSONTokener jsonParser = new JSONTokener(
							EntityUtils.toString(httpResponse.getEntity()));
					JSONObject result_obj = (JSONObject) jsonParser.nextValue();
					Boolean isok = result_obj.getBoolean("isok");
					if (isok) {
						JSONTokener jsonParser2 = new JSONTokener(
								result_obj.getString("data"));
						JSONObject result_obj2 = (JSONObject) jsonParser2
								.nextValue();
						parent = result_obj2.getString("parent");
						sub = result_obj2.getString("sub");
						JSONArray jsonArray = result_obj2
								.getJSONArray("subCate");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = (JSONObject) jsonArray
									.opt(i);

							String name = jsonObject2.getString("name");
							String id = jsonObject2.getString("id");
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", name);
							map.put("id", id);
							subcate.add(map);
						}

						jsonArray = result_obj2.getJSONArray("cateArr");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = (JSONObject) jsonArray
									.opt(i);

							String name = jsonObject2.getString("name");
							String id = jsonObject2.getString("id");
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", name);
							map.put("id", id);
							cateArr.add(map);
						}
					}
					Message msg = handler.obtainMessage();
					msg.what = 0x001;
					handler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private Runnable getGoodsRunnable = new Runnable() {

		@Override
		public void run() {
			goodsArr = new ArrayList<Map<String, String>>();
			// TODO Auto-generated method stub
			String gcate_id = getIntent().getStringExtra("gcate_id");
			// 姝ラ1锛氬垱寤轰竴涓猄haredPreferences鎺ュ彛瀵硅薄
			SharedPreferences read = getSharedPreferences("lock", 0);
			// 姝ラ2锛氳幏鍙栨枃浠朵腑鐨勫�
			String token = read.getString("token", "");
			String target = UrlWays.GoodsListUrl + gcate_id + "?tk=" + token;
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpRequest = new HttpGet(target);
			HttpResponse httpResponse;
			try {
				httpResponse = httpclient.execute(httpRequest);

				try {
					JSONTokener jsonParser = new JSONTokener(
							EntityUtils.toString(httpResponse.getEntity()));
					JSONObject result_obj = (JSONObject) jsonParser.nextValue();
					Boolean isok = result_obj.getBoolean("isok");
					if (isok) {
						JSONTokener jsonParser2 = new JSONTokener(
								result_obj.getString("data"));
						JSONObject result_obj2 = (JSONObject) jsonParser2
								.nextValue();
						JSONArray jsonArray = result_obj2.getJSONArray("rs");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = (JSONObject) jsonArray
									.opt(i);

							String name = jsonObject2.getString("name");
							String id = jsonObject2.getString("id");
							String pic = "http://yuanlaixian.sharecarding.com/upload/"+jsonObject2.getString("pic");
							String keysoption = jsonObject2.getString("keysoption");
							String price = jsonObject2.getString("price");
							String weight = jsonObject2.getString("weight");
							String measure = jsonObject2.getString("measure");
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", name);
							map.put("id", id);
							map.put("pic", pic);
							map.put("price", price);
							map.put("weight", weight);
							map.put("measure", measure);
							map.put("keysoption", keysoption);
							
							goodsArr.add(map);
						}
					}
					Message msg = handler.obtainMessage();
					msg.what = 0x002;
					handler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

}
