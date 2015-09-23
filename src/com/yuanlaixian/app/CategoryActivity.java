package com.yuanlaixian.app;

import java.io.IOException;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class CategoryActivity extends Activity {
	private ListView listView;
	private List<Map<String, String>> mylist;
	MyListAdapter myAdapter = null;
	private int current_item = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		findViewById(R.id.return_activity).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent myIntent = new Intent();
						myIntent = new Intent(CategoryActivity.this,
								MainActivity.class);
						startActivity(myIntent);
						finish();
					}
				});
		listView = (ListView) findViewById(R.id.category_list);
		mylist = new ArrayList<Map<String, String>>();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 姝ラ1锛氬垱寤轰竴涓猄haredPreferences鎺ュ彛瀵硅薄
				SharedPreferences read = getSharedPreferences("lock",
						MODE_PRIVATE);
				// 姝ラ2锛氳幏鍙栨枃浠朵腑鐨勫�
				String token = read.getString("token", "");
				String target = UrlWays.CatelistUrl + "?tk=" + token;
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpRequest = new HttpGet(target);
				HttpResponse httpResponse;
				try {
					httpResponse = httpclient.execute(httpRequest);

					try {

						JSONTokener jsonParser = new JSONTokener(EntityUtils
								.toString(httpResponse.getEntity()));
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
								Map<String, String> map = new HashMap<String, String>();
								map.put("ItemText", name);
								map.put("id", id);
								mylist.add(map);
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

		Publicfooter fragment = new Publicfooter();
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.remove(fragment);
		transaction.replace(R.id.public_buttom, fragment);
		transaction.commit();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x003) {
				CatesFragment fragment = new CatesFragment();
				Bundle args = new Bundle();
				String id;
				if (getIntent().getStringExtra("gcate_id") == null) {
					id = mylist.get(0).get("id");
				} else {
					id = getIntent().getStringExtra("gcate_id");
				}
				args.putString("id", id);
				fragment.setArguments(args);
				FragmentTransaction transaction = getFragmentManager()
						.beginTransaction();
				transaction.remove(fragment);
				transaction.replace(R.id.category_table, fragment);
				transaction.commit();

				myAdapter = new MyListAdapter(CategoryActivity.this);
				listView.setAdapter(myAdapter);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						getIntent().putExtra("gcate_id", "");
						myAdapter = new MyListAdapter(CategoryActivity.this);
						current_item = arg2;
						listView.setAdapter(myAdapter);

						String id = mylist.get(arg2).get("id");
						CatesFragment fragment = new CatesFragment();
						Bundle args = new Bundle();
						args.putString("id", id);
						fragment.setArguments(args);
						FragmentTransaction transaction = getFragmentManager()
								.beginTransaction();
						transaction.remove(fragment);
						transaction.replace(R.id.category_table, fragment);
						transaction.commit();
					}

				});

			}
		}

	};

	class MyListAdapter extends BaseAdapter {

		public MyListAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return mylist.size();
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
			TextView title = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.category_list, null);
				title = (TextView) convertView.findViewById(R.id.category_text);
				title.setText(mylist.get(position).get("ItemText"));
			}
			if (getIntent().getStringExtra("gcate_id") != null
					&& getIntent().getStringExtra("gcate_id") != "") {
				if (mylist.get(position).get("id")
						.equals(getIntent().getStringExtra("gcate_id"))) {
					convertView.setBackgroundColor(Color.WHITE);
					if (title != null) {
						title.setTextColor(Color.GREEN);
					}
				}
			} else {
				if (position == current_item) {
					convertView.setBackgroundColor(Color.WHITE);
					if (title != null) {
						title.setTextColor(Color.GREEN);
					}
				}
			}

			return convertView;
		}

		private Context mContext;
	}
}