package com.communitywatch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.community.watch.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ReadIssues extends Activity  { 
	Button btnLogout;
	public static final int DIALOG_DOWNLOAD_JSON_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, Object>> MyArrList;
    LikesIssueDataBaseAdapter likesDataBaseAdapter;
	HashMap<String, String> queryValues;
    ProgressDialog prgDialog;
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_issues);
    	likesDataBaseAdapter=new LikesIssueDataBaseAdapter(this);
		likesDataBaseAdapter=likesDataBaseAdapter.open();
		 prgDialog = new ProgressDialog(this);
	        prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
	        prgDialog.setCancelable(false);
	        
	    // Download JSON File	
		new DownloadJSONFileAsync().execute();
		btnLogout = (Button) findViewById(R.id.btnLogout);  	
    	btnLogout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				new DownloadJSONFileAsync().execute();
			}
		});
    	
    	  // btnSearch
        final Button btnSearch = (Button) findViewById(R.id.btnSearch);
        
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ShowData();	
            }
        });
    }
    
    public void onStart() {
		super.onStart();
		
		   
  
		
    }
       
    public void addComment(View v) {
		Intent i = new Intent(ReadIssues.this, CategoryIssueActivity.class);
		startActivity(i);
	}
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_DOWNLOAD_JSON_PROGRESS:
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading Issues.....");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            return mProgressDialog;
        default:
            return null;
        }
    }
    public void ShowData()
    { // keySearch
        EditText strKeySearch = (EditText)findViewById(R.id.txtSearch); 
        
        // Disbled Keyboard auto focus
        InputMethodManager imm = (InputMethodManager)getSystemService(
      	      Context.INPUT_METHOD_SERVICE);
      	imm.hideSoftInputFromWindow(strKeySearch.getWindowToken(), 0);
    	String url = "http://scottbm.5gbfree.com/webservice/getIssues.php";
    	// Paste Parameters
 		List<NameValuePair> params = new ArrayList<NameValuePair>();
         params.add(new BasicNameValuePair("txtKeyword", strKeySearch.getText().toString()));
    	JSONArray data;
		try {
			data = new JSONArray(getJSONUrl(url,params));
			
	    	MyArrList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map;
			
			for(int i = 0; i < data.length(); i++){
                JSONObject c = data.getJSONObject(i);
    			map = new HashMap<String, Object>();
    			map.put("post_id", (String)c.getString("post_id"));
    			map.put("category", (String)c.getString("category"));
    			map.put("title", (String)c.getString("title"));
    			map.put("username", (String)c.getString("username"));
    			
    			// Thumbnail Get ImageBitmap To Object
    			map.put("path", (String)c.getString("path"));
    			map.put("path", (Bitmap)loadBitmap(c.getString("path")));
    			
    			// Full (for View Popup)
    			map.put("pathfull", (String)c.getString("pathfull"));
    			//map.put("Fullpath", (String)c.getString("path"));
    			map.put("message", (String)c.getString("message"));
    			
    			map.put("date", (String)c.getString("date"));
    			
    			MyArrList.add(map);	
    		
    			
			}
    		
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    // Show All Content
    public void ShowAllContent()
    {
        final ListView lstView1 = (ListView)findViewById(R.id.listView1); 
        lstView1.setAdapter(new ImageAdapter(ReadIssues.this,MyArrList));
        	
        lstView1.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View selectedView, int arg2,long arg3){

				
				TextView  post_id = (TextView) selectedView.findViewById(R.id.post_id);
				TextView  username = (TextView) selectedView.findViewById(R.id.username);
				TextView  categ = (TextView) selectedView.findViewById(R.id.txtCat);
				TextView  title = (TextView) selectedView.findViewById(R.id.title);
				TextView  message = (TextView) selectedView.findViewById(R.id.message);
				String strImagePath =MyArrList.get(arg2).get("pathfull").toString();
			    TextView  date = (TextView) selectedView.findViewById(R.id.post_date);
				Log.d("Username", "Username : " + username.getText().toString());
				Intent intent = new Intent(ReadIssues.this, TweetIssueActivity.class);
				
				intent.putExtra("post_id", post_id.getText().toString());
				intent.putExtra("username", username.getText().toString());
				intent.putExtra("category", categ.getText().toString());
				intent.putExtra("title", title.getText().toString());
				intent.putExtra("message", message.getText().toString());
				intent.putExtra("date", date.getText().toString());
				intent.putExtra("pathfull", strImagePath);
				startActivity(intent);
				
			
				
			}
		});

		
    }
    
    

    public class ImageAdapter extends BaseAdapter 
    {
        private Context context;
        private ArrayList<HashMap<String, Object>> MyArr = new ArrayList<HashMap<String, Object>>();

        public ImageAdapter(Context c, ArrayList<HashMap<String, Object>> myArrList) 
        {
        	// TODO Auto-generated method stub
            context = c;
            MyArr = myArrList;
        }
 
        public int getCount() {
        	// TODO Auto-generated method stub
            return MyArr.size();
        }
 
        public Object getItem(int position) {
        	// TODO Auto-generated method stub
            return position;
        }
 
        public long getItemId(int position) {
        	// TODO Auto-generated method stub
            return position;
        }
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			String ImagePath = MyArr.get(position).get("pathfull").toString();
			String PostID = MyArr.get(position).get("post_id").toString();
			// save user data
			SharedPreferences pt = PreferenceManager
					.getDefaultSharedPreferences(ReadIssues.this);
			Editor edit = pt.edit();
			edit.putString("pathfull", ImagePath);
			edit.commit();
			edit.putString("post_id", PostID );
			edit.commit();
		
			 
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
		 
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.single_post, null); 
			}

			// ColImage
			ImageView imageView = (ImageView) convertView.findViewById(R.id.path);
			imageView.getLayoutParams().height =200;
			imageView.getLayoutParams().width = 200;
			imageView.setPadding(10, 10, 10, 10);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        	 try
        	 {
        		 imageView.setImageBitmap((Bitmap)MyArr.get(position).get("path"));
        	 } catch (Exception e) {
        		 // When Error
        		 imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        	 }
        	 
        	 TextView post_id = (TextView) convertView.findViewById(R.id.post_id);
        	 post_id.setPadding(5, 0, 0, 0);
        	 post_id.setText("" + MyArr.get(position).get("post_id").toString());	
        	// ColCategory
 			TextView category = (TextView) convertView.findViewById(R.id.txtCat);
 			category.setPadding(5, 0, 0, 0);
 			category.setText("Category: " + MyArr.get(position).get("category").toString());
				
			// ColTopic
			TextView title = (TextView) convertView.findViewById(R.id.title);
			title.setPadding(5, 0, 0, 0);
			title.setText("" + MyArr.get(position).get("title").toString());
			// ColMessage
			TextView message = (TextView) convertView.findViewById(R.id.message);
			message.setPadding(5, 0, 0, 0);
			message.setText("" + MyArr.get(position).get("message").toString());
			// ColUsername
			
			TextView userName = (TextView) convertView.findViewById(R.id.username);
			userName.setPadding(5, 0, 0, 0);
			userName.setText("@"+MyArr.get(position).get("username").toString());	
			// ColDate
						TextView date = (TextView) convertView.findViewById(R.id.post_date);
						 date.setPadding(5, 0, 0, 0);
						 date.setText("" + MyArr.get(position).get("date").toString());
						 
			 TextView textliketotal= ( TextView ) convertView.findViewById(R.id.post_likes);			
			 textliketotal.setPadding(5, 0, 0, 0);	
				int storedLike=likesDataBaseAdapter.dbSyncCount(PostID);
				textliketotal.setText(String.valueOf( storedLike));
			return convertView;
				
		}

    } 
        
    
    // Download JSON in Background
    public class DownloadJSONFileAsync extends AsyncTask<String, Void, Void> {
    	
        @SuppressWarnings("deprecation")
		protected void onPreExecute() {
        	super.onPreExecute();
        	showDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
        	   EditText strKeySearch = (EditText)findViewById(R.id.txtSearch); 
               
               // Disbled Keyboard auto focus
               InputMethodManager imm = (InputMethodManager)getSystemService(
             	      Context.INPUT_METHOD_SERVICE);
             	imm.hideSoftInputFromWindow(strKeySearch.getWindowToken(), 0);
           	String url = "http://scottbm.5gbfree.com/webservice/getIssues.php";
           	// Paste Parameters
        		List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("txtKeyword", strKeySearch.getText().toString()));
        	
        	JSONArray data;
			try {
				data = new JSONArray(getJSONUrl(url,param));
				
		    	MyArrList = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> map;
				
				for(int i = 0; i < data.length(); i++){
	                JSONObject c = data.getJSONObject(i);
	    			map = new HashMap<String, Object>();
	    			map.put("post_id", (String)c.getString("post_id"));
	    			map.put("category", (String)c.getString("category"));
	    			map.put("title", (String)c.getString("title"));
	    			map.put("username", (String)c.getString("username"));
	    			
	    			// Thumbnail Get ImageBitmap To Object
	    			map.put("path", (String)c.getString("path"));
	    			map.put("path", (Bitmap)loadBitmap(c.getString("path")));
	    			
	    			// Full (for View Popup)
	    			map.put("pathfull", (String)c.getString("pathfull"));
	    			//map.put("Fullpath", (String)c.getString("path"));
	    			map.put("message", (String)c.getString("message"));
	    			
	    			map.put("date", (String)c.getString("date"));
	    			
	    			MyArrList.add(map);	
	    		
	    			
				}
	    		
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    		return null;
        }

        @SuppressWarnings("deprecation")
		protected void onPostExecute(Void unused) {
        	ShowAllContent(); // When Finish Show Content
        	   
    		syncSQLiteMySQLDB1();
    
            dismissDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
            removeDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
        }
        

    }

    
    /*** Get JSON Code from URL ***/
    public String getJSONUrl(String url,List<NameValuePair> params) {
		StringBuilder str = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = client.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) { // Download OK
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					str.append(line);
				}
			} else {
				Log.e("Log", "Failed to download result..");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str.toString();
	}
   
  	
  	/***** Get Image Resource from URL (Start) *****/
	private static final String TAG = "Image";
	private static final int IO_BUFFER_SIZE = 4 * 1024;
	public static Bitmap loadBitmap(String url) {
	    Bitmap bitmap = null;
	    InputStream in = null;
	    BufferedOutputStream out = null;

	    try {
	        in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);

	        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	        out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
	        copy(in, out);
	        out.flush();

	        final byte[] data = dataStream.toByteArray();
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        //options.inSampleSize = 1;

	        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
	    } catch (IOException e) {
	        Log.e(TAG, "Could not load Bitmap from: " + url);
	    } finally {
	        closeStream(in);
	        closeStream(out);
	    }

	    return bitmap;
	}

	 private static void closeStream(Closeable stream) {
	        if (stream != null) {
	            try {
	                stream.close();
	            } catch (IOException e) {
	                android.util.Log.e(TAG, "Could not close stream", e);
	            }
	        }
	    }
	 
	 private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
	 /***** Get Image Resource from URL (End) *****/
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			return Utils.inflateMenu(this,menu);
		}
		
		@Override 
		public boolean onOptionsItemSelected(MenuItem item) {
			return  Utils.handleMenuOption(this,item);
		}
		
		// Method to Sync MySQL to SQLite DB
				public void syncSQLiteMySQLDB1() {
					// Create AsycHttpClient object
					AsyncHttpClient client = new AsyncHttpClient();
					// Http Request Params Object
					RequestParams params = new RequestParams();
					// Show ProgressBar
					prgDialog.show();
					// Make Http call to getusers.php
					client.post("http://scottbm.5gbfree.com/webservice/mysqlsqlitesync/getusers.php", params, new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(String response) {
								// Hide ProgressBar
								prgDialog.hide();
								// Update SQLite DB with response sent by getusers.php
								updateSQLite(response);
								
							}
							// When error occured
							@Override
							public void onFailure(int statusCode, Throwable error, String content) {
								// TODO Auto-generated method stub
								// Hide ProgressBar
								prgDialog.hide();
								if (statusCode == 404) {
									Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
								} else if (statusCode == 500) {
									Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
											Toast.LENGTH_LONG).show();
								}
							}
					});
				}
				
				
				@SuppressWarnings("deprecation")
				public void updateSQLite(String response){
					ArrayList<HashMap<String, String>> likesynclist;
					likesynclist = new ArrayList<HashMap<String, String>>();
					// Create GSON object
					Gson gson = new GsonBuilder().create();
					try {
						// Extract JSON array from the response
						JSONArray arr = new JSONArray(response);
						System.out.println(arr.length());
						// If no of array elements is not zero
						if(arr.length() != 0){
							// Loop through each array element, 
							for (int i = 0; i < arr.length(); i++) {
								// Get JSON object
								JSONObject obj = (JSONObject) arr.get(i);
								System.out.println(obj.get("ID"));
								System.out.println(obj.get("LIKES"));
								System.out.println(obj.get("USERNAME"));
								System.out.println(obj.get("POST"));
								// DB QueryValues Object to insert into SQLite
								queryValues = new HashMap<String, String>();
								// Add userID extracted from Object
								queryValues.put("ID", obj.get("ID").toString());
								// Add userName extracted from Object
								queryValues.put("LIKES", obj.get("LIKES").toString());
								queryValues.put("USERNAME", obj.get("USERNAME").toString());
								queryValues.put("POST", obj.get("POST").toString());
								SharedPreferences pt= PreferenceManager.getDefaultSharedPreferences(ReadIssues.this);
					            String post_id = pt.getString("post_id", "anon");  
								  
								Cursor like =likesDataBaseAdapter.fetchLikes(obj.get("USERNAME").toString(),post_id );
								if (like == null) {
									Toast.makeText(getApplicationContext(), "O likes",
									          Toast.LENGTH_SHORT).show();
								}
								else {
						    		startManagingCursor(like);
						    		
						    		//Check for duplicate usernames
						    		if (like.getCount() > 0) {
						    		
						    			stopManagingCursor(like);
						        		like.close();
						    			return;
						    		}
						    		}
								// Insert Like into SQLite DB
								likesDataBaseAdapter.insertEntry(queryValues);
								HashMap<String, String> map = new HashMap<String, String>();
								// Add status for each Like in Hashmap
								map.put("Id", obj.get("ID").toString());
								map.put("status", "0");
								likesynclist.add(map);
							}
							// Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Lokes
							updateMySQLSyncSts(gson.toJson(likesynclist));
							// Reload the Main Activity
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// Method to inform remote MySQL DB about completion of Sync activity
				public void updateMySQLSyncSts(String json) {
					System.out.println(json);
					AsyncHttpClient client = new AsyncHttpClient();
					RequestParams params = new RequestParams();
					params.put("syncsts", json);
					// Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
					client.post("http://scottbm.5gbfree.com/webservice/mysqlsqlitesync/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							
						}

						@Override
						public void onFailure(int statusCode, Throwable error, String content) {
								Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
						}
					});
		}
   
}
