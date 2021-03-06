package com.communitywatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.community.watch.R;


public class AddSuggestion extends Activity implements OnClickListener{

	// flag for Internet connection status
	Boolean isInternetPresent = false;
	
	// Connection detector class
	ConnectionDetector cd;
	private TextView category;
	private EditText title, message;
	private Button  mSubmit;
	public AddSuggestion(){}
	 // Progress Dialog
    private ProgressDialog pDialog;
 
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    
    //php add a comment script
    
    //localhost :  
    //testing on your device
    //put your local ip instead,  on windows, run CMD > ipconfig
    //or in mac's terminal type ifconfig and look for the ip under en0 or en1
   // private static final String POST_COMMENT_URL = "http://xxx.xxx.x.x:1234/webservice/addcomment.php";
    
    //testing on Emulator:
    private static final String POST_COMMENT_URL = "http://scottbm.5gbfree.com/webservice/addcomment.php";
    
  //testing from a real server:
    //private static final String POST_COMMENT_URL = "http://www.mikespux.5gbfree.com/webservice/addcomment.php";
    Button btnPic;
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private TextView textTransDate;
    private final int DATE_DIALOG = 1;
    private int day, month, year,minute,hour,second;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_suggestion);
		 if (android.os.Build.VERSION.SDK_INT > 9) {
	            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	            StrictMode.setThreadPolicy(policy);
	        }
		title = (EditText)findViewById(R.id.title);
		message = (EditText)findViewById(R.id.message);
		textTransDate = (TextView) this.findViewById(R.id.textTransDate);
		// creating connection detector class instance
		category= ( TextView ) findViewById(R.id.category);
		SharedPreferences ct = PreferenceManager.getDefaultSharedPreferences(AddSuggestion.this);
        String category1 = ct.getString("category", "anon");
        category.setText(category1);
		cd = new ConnectionDetector(getApplicationContext());
		 final Calendar c = Calendar.getInstance();
	        year = c.get(Calendar.YEAR);
	        month = c.get(Calendar.MONTH);
	        day = c.get(Calendar.DAY_OF_MONTH);
	       hour= c.get(Calendar.HOUR);
	       minute = c.get(Calendar.MINUTE);
	        second = c.get(Calendar.SECOND);
	      //  am=c.get(Calendar.AM_PM);
	        updateDateDisplay();
		
		mSubmit = (Button)findViewById(R.id.submit);
		mSubmit.setOnClickListener(this);
		 btnPic= (Button) findViewById(R.id. btnPic);
    	
		 btnPic.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intentPIC=new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intentPIC);
							}
		});
    }
		
	
	private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int pYear,int pMonth, int pDay) {
                    year = pYear;
                    month = pMonth;
                    day = pDay;
                    updateDateDisplay();
                }
            };
            
            @SuppressWarnings("deprecation")
			public void showDateDialog(View v) {
      		  showDialog(DATE_DIALOG);
      	}
      	
      	@SuppressWarnings("deprecation")
		@Override
      	protected Dialog onCreateDialog(int id) {
      		super.onCreateDialog(id);
      		
      	    switch (id) {
      	    case DATE_DIALOG:
      	        return new DatePickerDialog(this,
      	                    dateSetListener, year, month, day);
      	    }
      	    return null;
      	}
      	private void updateDateDisplay() {
            // Month is 0 based so add 1
      		 textTransDate.setText( String.format("%d:%d:%d  on %d-%d-%d",hour,minute,second,year,month + 1,day));
	}
	@Override
	public void onClick(View v) {
		// get Internet status
		isInternetPresent = cd.isConnectingToInternet();

		// check for Internet status
		if (isInternetPresent) {
			new PostComment().execute();
			
			// Internet Connection is Present
			// make HTTP requests
		//	showAlertDialog(AddComment.this, "Internet Connection",
				//	"You have internet connection", true);
		} else {
			// Internet connection is not present
			// Ask user to connect to Internet
			showAlertDialog(AddSuggestion.this, "No Internet Connection",
					"You don't have internet connection.", false);
		}
		
			
	}
	
	
	class PostComment extends AsyncTask<String, String, String> {
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddSuggestion.this);
            pDialog.setMessage("Writing Something...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
            int success;
            String post_title = title.getText().toString();
            String post_message = message.getText().toString();
            String post_date =textTransDate.getText().toString();
            
            //We need to change this:
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AddSuggestion.this);
            String post_username = prefs.getString("email", "anon");
            SharedPreferences pt = PreferenceManager.getDefaultSharedPreferences(AddSuggestion.this);
            String path = pt.getString("pathfull", "anon");
            
            SharedPreferences ct = PreferenceManager.getDefaultSharedPreferences(AddSuggestion.this);
            String category1 = ct.getString("category", "anon");
            
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("path", path));
                params.add(new BasicNameValuePair("pathfull", path));
                params.add(new BasicNameValuePair("category", category1));
                params.add(new BasicNameValuePair("title", post_title));
                params.add(new BasicNameValuePair("message", post_message));
                params.add(new BasicNameValuePair("date", post_date));
                
 
                Log.d("request!", "starting");
                
                //Posting user data to script 
                JSONObject json = jsonParser.makeHttpRequest(
                		POST_COMMENT_URL, "POST", params);
 
                // full json response
                Log.d("Post Comment attempt", json.toString());
 
                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                	
                	Log.d("Suggestion Added!", json.toString());  
                	Intent newActivity = new Intent(AddSuggestion.this,TabLayoutActivity.class);
            		startActivity(newActivity);
                	finish();
                	return json.getString(TAG_MESSAGE);
                }else{
                	Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
                	return json.getString(TAG_MESSAGE);
                	
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
			
		}
		
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
            	Toast.makeText(AddSuggestion.this, file_url, Toast.LENGTH_LONG).show();
            }
 
        }
		
	}
	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message, Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);
		
		// Setting alert dialog icon
		alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	} 

}
