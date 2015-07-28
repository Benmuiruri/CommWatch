package com.communitywatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.community.watch.R;

public class ResolvedHome extends Activity 
{
	Button Suggestion,Issue;
	LikesIssueDataBaseAdapter likesDataBaseAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.adminmain);
	     
	     // create a instance of SQLite Database
	     likesDataBaseAdapter=new LikesIssueDataBaseAdapter(this);
	     likesDataBaseAdapter=likesDataBaseAdapter.open();
	     
	     // Get The Refference Of Buttons
	     Suggestion=(Button)findViewById(R.id.btnSuggestion);
	     Issue=(Button)findViewById(R.id.btnIssue);
			
	    // Set OnClick Listener on SignUp button 
	     Issue.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			/// Create Intent for SignUpActivity  and Start The Activity
			Intent intentSignUP=new Intent(getApplicationContext(),ResolvedIssueActivity.class);
			startActivity(intentSignUP);
			}
		});
		// Set On ClickListener
	     Suggestion.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// get The User name and Password
				
										
					 Intent intentSignIn=new Intent(getApplicationContext(),ResolvedSuggestionActivity.class);
						startActivity(intentSignIn);
					
			}
		});
	}
	
			
		
			
			
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	    // Close The Database
		likesDataBaseAdapter.close();
	}

}
