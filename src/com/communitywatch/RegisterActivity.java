
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.community.watch.R;
import com.communitywatch.library.DatabaseHandler;
import com.communitywatch.library.UserFunctions;

public class RegisterActivity extends Activity {
	

	// flag for Internet connection status
	Boolean isInternetPresent = false;
	
	// Connection detector class
	ConnectionDetector cd;
	Button btnRegister;
	Button btnLinkToLogin;
	EditText inputFullName;
	EditText inputEmail;
	EditText inputPassword,confirmPass;
	TextView registerErrorMsg;
	
	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		 if (android.os.Build.VERSION.SDK_INT > 9) {
	            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	            StrictMode.setThreadPolicy(policy);
	        }
		 
		inputFullName = (EditText) findViewById(R.id.registerName);
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		confirmPass = (EditText) findViewById(R.id.confirmPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);
		// creating connection detector class instance
				cd = new ConnectionDetector(getApplicationContext());

		btnRegister.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View view) {
				isInternetPresent = cd.isConnectingToInternet();

				// check for Internet status
				if (isInternetPresent) {
					String name = inputFullName.getText().toString();
					String email = inputEmail.getText().toString();
					String password = inputPassword.getText().toString();
					String confirmPas=confirmPass.getText().toString();
					if(name.equals("")||password.equals("")||email.equals("")||password.equals("")||confirmPas.equals(""))
					{
							Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
							return;
					}
					if(!password.equals(confirmPas))
					{
						Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
						return;
					}
					UserFunctions userFunction = new UserFunctions();
					JSONObject json = userFunction.registerUser(name, email, password);
					
					try {
						if (json.getString(KEY_SUCCESS) != null) {
							registerErrorMsg.setText("");
							String res = json.getString(KEY_SUCCESS); 
							if(Integer.parseInt(res) == 1){
								// user successfully registred
								// Store user details in SQLite Database
								DatabaseHandler db = new DatabaseHandler(getApplicationContext());
								JSONObject json_user = json.getJSONObject("user");
								
								// Clear all previous data in database
								userFunction.logoutUser(getApplicationContext());
								db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));						
								
								Intent dashboard = new Intent(getApplicationContext(), TabLayoutActivity.class);
								dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(dashboard);
								// save user data
								SharedPreferences prefs = PreferenceManager
										.getDefaultSharedPreferences(RegisterActivity.this);
								
				    			  Editor edit = prefs.edit();
				    			  edit.putString("email",inputEmail.getText().toString());				    			  
				    			  edit.commit();
				    			  
				    			  edit.putString("pass",inputPassword.getText().toString());
				    			  edit.commit();
								finish();
								registerErrorMsg.setText("Registration Successful");
								
							}else{
								// Error in registration
								registerErrorMsg.setText("Error Occured in registration i.e username exists");
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					// Internet connection is not present
					showAlertDialog(RegisterActivity.this, "No Internet Connection",
							"You don't have internet connection.", false);
				}
				
			}
		});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent login = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(login);
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

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return Utils.inflateMenu(this,menu);
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		return  Utils.handleMenuOption(this,item);
	}
}
