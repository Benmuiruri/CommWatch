package com.communitywatch;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.community.watch.R;



public class Utils {
	 SessionCreator session;
	
    public static boolean  inflateMenu(Activity activity, Menu menu) {
    	MenuInflater inflater = activity.getMenuInflater();
		inflater.inflate( R.menu.common_menu, menu);   
		return true; 
		
    }
    
    public static boolean handleMenuOption(Activity activity, MenuItem item) {
    	Intent intent;
    	
		switch(item.getItemId()) {

		case R.id.txtCatego :
			  intent = new Intent(activity,UpdateActivity.class);
			  activity.startActivity(intent);
			  break;
			  
		
		case R.id.txtlogout:
			  intent = new Intent(activity,LoginOffsesion.class);
			  activity.startActivity(intent);

			  break;

		}
			
		
return true;
    }
    
}

