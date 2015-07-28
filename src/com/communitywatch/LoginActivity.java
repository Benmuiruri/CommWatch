package com.communitywatch;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.community.watch.R;
import com.communitywatch.library.DatabaseHandler;
import com.communitywatch.library.UserFunctions;



public class LoginActivity extends Activity {

	// flag for Internet connection status
	Boolean isInternetPresent = false;
	  Editor edit;
	// Connection detector class
	ConnectionDetector cd;

	Button btnLogin;
	Button btnLinkToRegister;
	EditText inputEmail;
	EditText inputPassword;
	TextView loginErrorMsg;
	SessionCreator session;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		 if (android.os.Build.VERSION.SDK_INT > 9) {
	            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	            StrictMode.setThreadPolicy(policy);
	        }
		 
		 
		 session = new SessionCreator(getApplicationContext());
	        
		inputEmail = (EditText) findViewById(R.id.loginEmail);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        String post_username = prefs.getString("email", "");
      
        String post_pass = prefs.getString("pass", "");
        
        inputEmail.setText(post_username);
        inputPassword.setText(post_pass);
		cd = new ConnectionDetector(getApplicationContext());

		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				// get Internet status
				isInternetPresent = cd.isConnectingToInternet();

				// check for Internet status
				if (isInternetPresent) {
					// Internet Connection is Present
					// make HTTP requests
					String email = inputEmail.getText().toString();
					String password = inputPassword.getText().toString();
					String name = null;
					if(email.equals("")||password.equals(""))
					{
							Toast.makeText(getApplicationContext(), "Please enter Username and Password", Toast.LENGTH_LONG).show();
							return;
					}
					
					UserFunctions userFunction = new UserFunctions();
					Log.d("Button", "Login");
					JSONObject json = userFunction.loginUser(email, password,name);
					

					// check for login response
					try {
						if (json.getString(KEY_SUCCESS) != null) {
							loginErrorMsg.setText("");
							String res = json.getString(KEY_SUCCESS); 
							if(Integer.parseInt(res) == 1){
								// user successfully logged in
								// Store user details in SQLite Database
								DatabaseHandler db = new DatabaseHandler(getApplicationContext());
								JSONObject json_user = json.getJSONObject("user");
								
								// Clear all previous data in database
								userFunction.logoutUser(getApplicationContext());
								db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));						
								  session.createLoginSession(inputEmail.getText().toString(),inputPassword.getText().toString());
								  
								  
								Intent dashboard = new Intent(getApplicationContext(), TabLayoutActivity.class);
								dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								dashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(dashboard);
								
								// save user data
								SharedPreferences prefs = PreferenceManager
										.getDefaultSharedPreferences(LoginActivity.this);
								
				    			   edit = prefs.edit();
				    			  edit.putString("email",inputEmail.getText().toString());	
				    			  
				    			  edit.commit();
				    			  
				    			  edit.putString("pass",inputPassword.getText().toString());
				    			  edit.commit();
				    			  edit.putString("uid",json.getString(KEY_UID));
				    			  edit.commit();
				    			  
				    			  
				    			  
								// Close Login Screen
								finish();
								
								loginErrorMsg.setText("Login successfull");
							}else{
								// Error in login
								loginErrorMsg.setText("Incorrect username/password");
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				} else {
					// Internet connection is not present
					showAlertDialog(LoginActivity.this, "No Internet Connection",
							"You don't have internet connection.", false);
				}
				
				
			}
			
		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message, Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();


		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// get Internet status
		isInternetPresent = cd.isConnectingToInternet();

		// check for Internet status
		if (isInternetPresent) {
			// Internet Connection is Present
			// make HTTP requests
			String email = inputEmail.getText().toString();
			String password = inputPassword.getText().toString();
			String name = null;
			if(email.equals("")||password.equals(""))
			{
					Toast.makeText(getApplicationContext(), "Please enter Username and Password", Toast.LENGTH_LONG).show();
					return;
			}
			
			UserFunctions userFunction = new UserFunctions();
			Log.d("Button", "Login");
			JSONObject json = userFunction.loginUser(email, password,name);
			

			// check for login response
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					loginErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS); 
					if(Integer.parseInt(res) == 1){
						// user successfully logged in
						// Store user details in SQLite Database
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");
						
						// Clear all previous data in database
						userFunction.logoutUser(getApplicationContext());
						db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));						
						  session.createLoginSession(inputEmail.getText().toString(),inputPassword.getText().toString());
						  
						  
						Intent dashboard = new Intent(getApplicationContext(), TabLayoutActivity.class);
						dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						dashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(dashboard);
						
				
		    			  
		    			  
						// Close Login Screen
						finish();
						
					}else{
						
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} else {
			// Internet connection is not present
			showAlertDialog(LoginActivity.this, "No Internet Connection",
					"You don't have internet connection.", false);
		}
		
		
	}
	  public void logoutUser() {
	       
	        inputEmail.setText("");
	        inputPassword.setText("");
	       
	    }
	}

