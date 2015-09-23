package com.yuanlaixian.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements OnPageChangeListener {
    private ViewPager mViewPager = null;
    private LinearLayout mViewGroup = null;
    private String[] banner_path;
    private ImageView[] mImageViews = null;
    private ImageView[] mTips = null;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem = 1;
    private RelativeLayout category_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File sd = Environment.getExternalStorageDirectory();
        String path = sd.getPath() + "/yuanlaixian";
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
        //涓嬭浇banner鍥�
        path = sd.getPath() + "/yuanlaixian/banner";
        file = new File(path);
        if (!file.exists())
            file.mkdir();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String target = UrlWays.BannerUrl;
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpRequest = new HttpGet(target);
                HttpResponse httpResponse;
                try {
                    httpResponse = httpclient.execute(httpRequest);
                    Message msg = handler.obtainMessage();
                    msg.obj = EntityUtils.toString(httpResponse.getEntity());
                    msg.what = 0x003;

                    try {
                        JSONArray jsonArray = new JSONArray(String.valueOf(msg.obj));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);

                            String urlStr = jsonObject2.getString("pic");

                            String path = "yuanlaixian/banner";
                            String fileName = "banner" + i + ".jpg";

                            ByteArrayOutputStream output = null;
                            try {
                            /*
                             * 閫氳繃URL鍙栧緱HttpURLConnection
                             * 瑕佺綉缁滆繛鎺ユ垚鍔燂紝闇�湪AndroidMainfest.xml涓繘琛屾潈闄愰厤缃�
                             * <uses-permission android:name="android.permission.INTERNET" />
                             */
                                URL url = new URL(urlStr);
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                //鍙栧緱inputStream锛屽苟灏嗘祦涓殑淇℃伅鍐欏叆SDCard

                /*
                 * 鍐欏墠鍑嗗
                 * 1.鍦ˋndroidMainfest.xml涓繘琛屾潈闄愰厤缃�
                 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                 * 鍙栧緱鍐欏叆SDCard鐨勬潈闄�
                 * 2.鍙栧緱SDCard鐨勮矾寰勶細 Environment.getExternalStorageDirectory()
                 * 3.妫�煡瑕佷繚瀛樼殑鏂囦欢涓婃槸鍚﹀凡缁忓瓨鍦�
                 * 4.涓嶅瓨鍦紝鏂板缓鏂囦欢澶癸紝鏂板缓鏂囦欢
                 * 5.灏唅nput娴佷腑鐨勪俊鎭啓鍏DCard
                 * 6.鍏抽棴娴�
                 */
                                String SDCard = Environment.getExternalStorageDirectory() + "";
                                String pathName = SDCard + "/" + path + "/" + fileName;//鏂囦欢瀛樺偍璺緞

                                File file = new File(pathName);
                                InputStream input = conn.getInputStream();
                                Bitmap mBitmap = BitmapFactory.decodeStream(input);
                                file.deleteOnExit();
                                file.createNewFile();//鏂板缓鏂囦欢
                                BufferedOutputStream bos = new BufferedOutputStream(
                                        new FileOutputStream(file));
                                mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                                bos.flush();
                                bos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        setContentView(R.layout.activity_main);
        
	    Publicfooter fragment = new Publicfooter();
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();  		    
	    transaction.remove(fragment);
	    transaction.replace(R.id.public_buttom, fragment);  
        transaction.commit();  

        category_layout = (RelativeLayout) findViewById(R.id.category);
        category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //姝ラ1锛氬垱寤轰竴涓猄haredPreferences鎺ュ彛瀵硅薄
                        SharedPreferences read = getSharedPreferences("lock", MODE_PRIVATE);
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
                            m.what = 0x004;
                            handler.sendMessage(m);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        //鍋滄鍥剧墖鍒囨崲
        scheduledExecutorService.shutdown();

        super.onStop();
    }

    //鐢ㄦ潵瀹屾垚鍥剧墖鍒囨崲鐨勪换鍔�
    private class ViewPagerTask implements Runnable {

        public void run() {
            //瀹炵幇鎴戜滑鐨勬搷浣�
            //鏀瑰彉褰撳墠椤甸潰
            currentItem = (currentItem + 1) % mImageViews.length;
            //Handler鏉ュ疄鐜板浘鐗囧垏鎹�
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x004) {
                //鍒ゆ柇鐧婚檰
                String result = msg.obj.toString();
                if (result != null) {
                    try {
                        JSONTokener jsonParser = new JSONTokener(result);
                        JSONObject result_obj = (JSONObject) jsonParser.nextValue();
                        Boolean isok = result_obj.getBoolean("isok");
                        if (isok) {
                            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else if (msg.what == 0x003) {
                try {
                    JSONArray jsonArray = new JSONArray(String.valueOf(msg.obj));
                    int json_len = jsonArray.length();
                    banner_path = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        banner_path[i] = "banner" + i + ".jpg";
                    }

                    mViewGroup = (LinearLayout) findViewById(R.id.viewGroup);
                    mViewPager = (ViewPager) findViewById(R.id.viewPager);

                    mTips = new ImageView[banner_path.length];
                    //鍔ㄦ�鍒涘缓灏忕偣骞跺姞鍒板竷灞�腑
                    for (int i = 0; i < mTips.length; i++) {
                        ImageView iv = new ImageView(MainActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(5, 0, 5, 0);
                        iv.setLayoutParams(layoutParams);
                        iv.setScaleType(ImageView.ScaleType.CENTER);
                        mTips[i] = iv;

                        if (i == 0) {
                            iv.setBackgroundResource(R.drawable.page_indicator_focused);
                        } else {
                            iv.setBackgroundResource(R.drawable.page_indicator_unfocused);
                        }
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        mViewGroup.addView(iv, lp);
                    }
                    mImageViews = new ImageView[banner_path.length];
                    for (int i = 0; i < mImageViews.length; i++) {
                        ImageView iv = new ImageView(MainActivity.this);
                        iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                        iv.setScaleType(ImageView.ScaleType.FIT_XY);

                        int reqWidth = getWindowManager().getDefaultDisplay().getWidth();
                        int reqHeight = getWindowManager().getDefaultDisplay().getHeight();

                        String myJpgPath = Environment.getExternalStorageDirectory() + "/yuanlaixian/banner/" + banner_path[i];
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
                        iv.setImageBitmap(bm);
                        iv.setImageBitmap(bm);

                        mImageViews[i] = iv;
                    }

                    mViewPager.setAdapter(new MyPagerAdapter());
                    mViewPager.setOnPageChangeListener(MainActivity.this);

                    //鐢ㄤ竴涓畾鏃跺櫒  鏉ュ畬鎴愬浘鐗囧垏鎹�
                    //Timer 涓�ScheduledExecutorService 瀹炵幇瀹氭椂鍣ㄧ殑鏁堟灉

                    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                    //閫氳繃瀹氭椂鍣�鏉ュ畬鎴�姣�绉掗挓鍒囨崲涓�釜鍥剧墖
                    //缁忚繃鎸囧畾鐨勬椂闂村悗锛屾墽琛屾墍鎸囧畾鐨勪换鍔�
                    //scheduleAtFixedRate(command, initialDelay, period, unit)
                    //command 鎵�鎵ц鐨勪换鍔�
                    //initialDelay 绗竴娆″惎鍔ㄦ椂 寤惰繜鍚姩鏃堕棿
                    //period  姣忛棿闅斿娆℃椂闂存潵閲嶆柊鍚姩浠诲姟
                    //unit 鏃堕棿鍗曚綅
                    scheduledExecutorService.scheduleAtFixedRate(new ViewPagerTask(), 1, 8, TimeUnit.SECONDS);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //璁惧畾viewPager褰撳墠椤甸潰
                mViewPager.setCurrentItem(currentItem);
            }
        }
    };


    class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return banner_path.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(mImageViews[position]);
            } catch (Exception e) {
            }
            return mImageViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        for (int i = 0; i < mTips.length; i++) {
            if (arg0 == i) {
                mTips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                mTips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }
}
