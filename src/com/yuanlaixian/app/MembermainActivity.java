package com.yuanlaixian.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class MembermainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membermain);
	    Publicfooter fragment = new Publicfooter();
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();  		    
	    transaction.remove(fragment);
	    transaction.replace(R.id.public_buttom, fragment);  
        transaction.commit();  
    }
}
