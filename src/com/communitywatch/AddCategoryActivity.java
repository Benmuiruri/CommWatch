package com.communitywatch;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.community.watch.R;

public class AddCategoryActivity extends Activity implements OnItemSelectedListener {

	private Button btnAddNewCategory,proceed;
	private TextView txtCategory;
	private Spinner spinnerCategory;
	// array list for spinner adapter
	private ArrayList<Category> categoriesList;
	ProgressDialog pDialog;

	// API urls
	// Url to create new category
	private String URL_NEW_CATEGORY = "http://scottbm.5gbfree.com/webservice/category/new_category.php";
	// Url to get all categories
	private String URL_CATEGORIES = "http://scottbm.5gbfree.com/webservice/category/get_categories.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createcategory);

		btnAddNewCategory = (Button) findViewById(R.id.btnAddNewCategory);
		proceed = (Button) findViewById(R.id.proceed);
		spinnerCategory = (Spinner) findViewById(R.id.spinCategory);
		txtCategory = (TextView) findViewById(R.id.txtCateg);
		
		categoriesList = new ArrayList<Category>();

		// spinner item select listener
		spinnerCategory.setOnItemSelectedListener(this);

		// Add new category click event
		btnAddNewCategory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (txtCategory.getText().toString().trim().length() > 0) {
					
					// new category name
					String newCategory = txtCategory.getText().toString();

					// Call Async task to create new category
					new AddNewCategory().execute(newCategory);
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter category name", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		proceed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(AddCategoryActivity.this, AddSuggestion.class);
				startActivity(i);
			}
		});

		new GetCategories().execute();

	}

	/**
	 * Adding spinner data
	 * */
	private void populateSpinner() {
		List<String> lables = new ArrayList<String>();
		
		txtCategory.setText("");

		for (int i = 0; i < categoriesList.size(); i++) {
			lables.add(categoriesList.get(i).getName());
		}

		// Creating adapter for spinner
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, lables);

		// Drop down layout style - list view with radio button
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinnerCategory.setAdapter(spinnerAdapter);
	}

	/**
	 * Async task to get all food categories
	 * */
	private class GetCategories extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddCategoryActivity.this);
			pDialog.setMessage("Fetching categories..");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			ServiceHandler jsonParser = new ServiceHandler();
			String json = jsonParser.makeServiceCall(URL_CATEGORIES, ServiceHandler.GET);

			Log.e("Response: ", "> " + json);

			if (json != null) {
				try {
					JSONObject jsonObj = new JSONObject(json);
					if (jsonObj != null) {
						JSONArray categories = jsonObj
								.getJSONArray("categories");						

						for (int i = 0; i < categories.length(); i++) {
							JSONObject catObj = (JSONObject) categories.get(i);
							Category cat = new Category(catObj.getInt("id"),
									catObj.getString("name"));
							categoriesList.add(cat);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Log.e("JSON Data", "Didn't receive any data from server!");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			populateSpinner();
		}

	}

	/**
	 * Async task to create a new food category
	 * */
	private class AddNewCategory extends AsyncTask<String, Void, Void> {

		boolean isNewCategoryCreated = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddCategoryActivity.this);
			pDialog.setMessage("Creating new category..");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(String... arg) {

			String newCategory = arg[0];

			// Preparing post params
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", newCategory));

			ServiceHandler serviceClient = new ServiceHandler();

			String json = serviceClient.makeServiceCall(URL_NEW_CATEGORY,
					ServiceHandler.POST, params);

			Log.d("Create Response: ", "> " + json);

			if (json != null) {
				try {
					JSONObject jsonObj = new JSONObject(json);
					boolean error = jsonObj.getBoolean("error");
					// checking for error node in json
					if (!error) {	
						// new category created successfully
						isNewCategoryCreated = true;
					} else {
						Log.e("Create Category Error: ", "> " + jsonObj.getString("message"));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Log.e("JSON Data", "Didn't receive any data from server!");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			if (isNewCategoryCreated) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// fetching all categories
						new GetCategories().execute();
					}
				});
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(
				getApplicationContext(),
						parent.getItemAtPosition(position).toString() + " Selected" ,
				Toast.LENGTH_LONG).show();
		//save user data
		 String category =parent.getItemAtPosition(position).toString();		
		SharedPreferences ct = PreferenceManager
				.getDefaultSharedPreferences(AddCategoryActivity.this);
		Editor edit = ct.edit();
		edit.putString("category", category);
		edit.commit();
				

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {		
	}
}
