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
import java.util.Set;

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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.community.watch.R;
public class ResolvedTweet extends ListActivity implements OnClickListener {
	
	public static final int DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    public String ImageFullPath;
    public ArrayList<HashMap<String, Object>> MyArrList;
	Boolean isInternetPresent = false;

	ConnectionDetector cd;
	private String username;
	private TextView _titleView,_post_id;
	private TextView _username,_category;
	private TextView _bodyView;
	private TextView _dateView;
	private EditText _message_comment;
	private Button _post;
	private ProgressDialog pDialog;
	String title,category, message,postid,date;
	private TextView textTransDate,textlike,textliked,textunliked;
	Set<String> textlike1;

	JSONParser jsonParser = new JSONParser();
	Button like,unlike;
	int liked=0;
	private static final String POST_COMMENT_URL = "http://scottbm.5gbfree.com/webservice/addmessage.php";
    private static final String READ_COMMENTS_URL = "http://scottbm.5gbfree.com/webservice/commented.php";


	private static final String TAG_SUCCESS = "success";

	private static final String TAG_SUCCESS1 = "success";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_POST_ID = "post_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_MESSAGE1 = "message";
	private static final String TAG_MESSAGE = "message_comment";
    private static final String TAG_DATE = "date";
	private static final String TAG_TITLE1= "post_id";
	private static final String TAG_USERNAME1 = "username";
	private static final String TAG_MESSAGE2 = "message_comment";
	private static final String TAG_DATE1 = "date";
	
 // An array of all of our comments
 	private JSONArray mComments = null;
 	// manages all of our comments in a list.
 	private ArrayList<HashMap<String, String>> mCommentList1;
	
	LikesIssueDataBaseAdapter likesDataBaseAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resolvedtweet);
		
		 String fontPath = "fonts/KeralaQuest.ttf";
		 String fontPath1 = "fonts/DS-DIGIT.TTF";
	        Intent intent= getIntent();
			likesDataBaseAdapter=new LikesIssueDataBaseAdapter(this);
			likesDataBaseAdapter=likesDataBaseAdapter.open();
	        ImageFullPath = String.valueOf(intent.getStringExtra("pathfull"));
	        
		_username = ( TextView ) findViewById(R.id.username);
		_category = ( TextView ) findViewById(R.id.txtCateg);
		_titleView = ( TextView ) findViewById(R.id.tweet_title);
		_bodyView = ( TextView ) findViewById(R.id.tweet_body);
		_dateView = ( TextView ) findViewById(R.id.tweet_date);
		
		
		// creating connection detector class instance
	    cd = new ConnectionDetector(getApplicationContext());
		_post_id= ( TextView ) findViewById(R.id.pid);
		textlike= ( TextView ) findViewById(R.id.textLikes);
		textliked= ( TextView ) findViewById(R.id.TextView01);
		textunliked= ( TextView ) findViewById(R.id.TextView03);
		Typeface tf = Typeface.createFromAsset(this.getAssets(), fontPath1);
		textlike.setTypeface(tf);
		Typeface likes = Typeface.createFromAsset(this.getAssets(), fontPath);
		textliked.setTypeface(likes);
		Typeface tl = Typeface.createFromAsset(this.getAssets(), fontPath);
		textunliked.setTypeface(tl);
	
		_message_comment= ( EditText ) findViewById(R.id.message_comment);
		  like=(Button)findViewById(R.id.like);
	       unlike=(Button)findViewById(R.id.unlike);
	        textlike.setText(String.valueOf(liked));
		textTransDate = (TextView) this.findViewById(R.id.textTransDate);
		_post=(Button) findViewById(R.id.post);
		_post.setOnClickListener(this);
		like.setOnClickListener(this);
		unlike.setOnClickListener(this);      
	}


	/**
	 * Retrieves recent post data from the server.
	 */
	public void updateJSONdata() {

		// Instantiate the arraylist to contain all the JSON data.
		// we are going to use a bunch of key-value pairs, referring
		// to the json element name, and the content, for example,
		// message it the tag, and "I'm awesome" as the content..

		mCommentList1 = new ArrayList<HashMap<String, String>>();

		// Bro, it's time to power up the J parser
		JSONParser jParser = new JSONParser();
		// Feed the beast our comments url, and it spits us
		// back a JSON object. Boo-yeah Jerome.
		JSONObject json = jParser.getJSONFromUrl(READ_COMMENTS_URL);

		// when parsing JSON stuff, we should probably
		// try to catch any exceptions:
		try {
			int success = json.getInt(TAG_SUCCESS1);
			// I know I said we would check if "Posts were Avail." (success==1)
			// before we tried to read the individual posts, but I lied...
			// mComments will tell us how many "posts" or comments are
			// available
			if (success == 1) {
				 // keySearch
		        TextView strKeySearch = (TextView )findViewById(R.id.postid); 
		        SharedPreferences pid = PreferenceManager.getDefaultSharedPreferences(ResolvedTweet.this);
	            String post = pid.getString("postid", "anon");
	            strKeySearch.setText(post);
		        // Disbled Keyboard auto focus
		        InputMethodManager imm = (InputMethodManager)getSystemService(
		      	      Context.INPUT_METHOD_SERVICE);
		      	imm.hideSoftInputFromWindow(strKeySearch.getWindowToken(), 0);
		      	//paste paramaters
		      	//List<NameValuePair> params = new ArrayList<NameValuePair>();
		     //   params.add(new BasicNameValuePair("txtKeyword", strKeySearch.getText().toString()));
			mComments = json.getJSONArray(TAG_POSTS);
			//JSONArray data = new JSONArray(getJSONUrl( READ_COMMENTS_URL,params));
			// looping through all posts according to the json object returned
			for (int i = 0; i < mComments.length(); i++) {
				JSONObject c = mComments.getJSONObject(i);

				// gets the content of each tag
				//String post_id = c.getString(TAG_POST_ID);
				String title = c.getString(TAG_TITLE1);
				String content = c.getString(TAG_MESSAGE2);
				String username = c.getString(TAG_USERNAME1);
			    String date = c.getString(TAG_DATE1);

				// creating new HashMap
			
				HashMap<String, String> map = new HashMap<String, String>();
				//map.put(TAG_POST_ID, post_id );
				map.put(TAG_TITLE1, title);
				map.put(TAG_MESSAGE2, content);
				map.put(TAG_USERNAME1, username);
			    map.put(TAG_DATE1, date);

				// adding HashList to ArrayList
				mCommentList1.add(map);

				// annndddd, our JSON data is up to date same with our array
				// list
				
				
			}

		} else {
			// no products found
			// Launch Add New product Activity
			
			
		}
			} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts the parsed data into the listview.
	 */
	private void updateList() {
		// For a ListActivity we need to set the List Adapter, and in order to do
		//that, we need to create a ListAdapter.  This SimpleAdapter,
		//will utilize our updated Hashmapped ArrayList, 
		//use our single_post xml template for each item in our list,
		//and place the appropriate info from the list to the
		//correct GUI id.  Order is important here.
		ListAdapter adapter= new SimpleAdapter(this, mCommentList1,
				R.layout.single_comment, new String[] { TAG_MESSAGE2,
						TAG_USERNAME1,TAG_DATE1}, new int[] { R.id.message,
						R.id.username,R.id.post_date});
		
		setListAdapter(adapter);
	
	
	}

	
	public static String getTagPostId() {
		
		return TAG_POST_ID;
	}

	public class LoadComments extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ResolvedTweet.this);
			pDialog.setMessage("Loading Commented...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			updateJSONdata();
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			updateList();
			
		}
	}
	

	@Override
	public void onClick(View v) {
			
				switch(v.getId()){
			
				case R.id.unlike:
					liked--;
					textlike.setText(String.valueOf(liked));
					
					SharedPreferences i = PreferenceManager
							.getDefaultSharedPreferences(ResolvedTweet.this);
					Editor edit = i.edit();
					edit.putStringSet("textlike", textlike1);
					edit.commit();
				  

					break;
				case R.id.like:
					liked++;
					textlike.setText(String.valueOf(liked));
					break;
				case R.id.post:
					// get Internet status
					isInternetPresent = cd.isConnectingToInternet();

					// check for Internet status
					if (isInternetPresent) {
						new PostComment().execute();
						// Internet Connection is Present
						// make HTTP requests
						//showAlertDialog(TweetActivity.this, "Internet Connection",
						//		"You have internet connection", true);
					} else {
						// Internet connection is not present
						// Ask user to connect to Internet
						showAlertDialog(ResolvedTweet.this, "No Internet Connection",
								"You don't have internet connection.", false);
					}
					
					
					break;
				/*case R.id.btnCustomCheckBoxLike:
					
					
					
				
					
					break;*/
				}
				//new PostComment().execute();
	}
	
	
	class PostComment extends AsyncTask<String, String, String> {
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ResolvedTweet.this);
            pDialog.setMessage("Commenting...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
            int success;
                       String post_comment = _message_comment.getText().toString();
                       String date = textTransDate.getText().toString();
            //We need to change this:
            SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(ResolvedTweet.this);
            String post_username = prefs.getString("email", "anon");
            SharedPreferences id = PreferenceManager.getDefaultSharedPreferences(ResolvedTweet.this);
            id.getString("title", "anon");
            SharedPreferences pid = PreferenceManager.getDefaultSharedPreferences(ResolvedTweet.this);
            String post = pid.getString("postid", "anon");
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
               params.add(new BasicNameValuePair("post_id", post));
                params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("message_comment", post_comment));
                params.add(new BasicNameValuePair("date", date));
 
                Log.d("request!", "starting");
                
                //Posting user data to script 
                JSONObject json = jsonParser.makeHttpRequest(
                		POST_COMMENT_URL, "POST", params);
 
                // full json response
                Log.d("Post Comment attempt", json.toString());
 
                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                	Log.d("Comment Added!", json.toString());    
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
            //Intent i = new Intent(getApplicationContext(),
					//TweetActivity.class);
           // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//startActivity(i);
           
            if (file_url != null){
            	Toast.makeText(ResolvedTweet.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
		
	}
		
	
    public void ShowData()
    {
    	 
   
        
        // keySearch
        TextView strKeySearch = (TextView )findViewById(R.id.tweet_title); 
        
        // Disbled Keyboard auto focus
        InputMethodManager imm = (InputMethodManager)getSystemService(
      	      Context.INPUT_METHOD_SERVICE);
      	imm.hideSoftInputFromWindow(strKeySearch.getWindowToken(), 0);
        
	
		String url = "http://mikespux.5gbfree.com/webservice/showAllData.php";
		
		// Paste Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("txtKeyword", strKeySearch.getText().toString()));

		try {
			JSONArray data = new JSONArray(getJSONUrl(url,params));
			
			
			MyArrList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map;
			
			for(int i = 0; i < data.length(); i++){
                JSONObject c = data.getJSONObject(i);
                
    			map = new HashMap<String, Object>();
    			map.put("post_id", c.getString("post_id"));
    			map.put("username", c.getString("username"));
    			map.put("title", c.getString("title"));
    			map.put("message", c.getString("message"));
    			//map.put("Email", c.getString("Email"));
    			//map.put("Tel", c.getString("Tel"));
    			MyArrList.add(map);
    			
			}

			
			
	        
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
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
	


	@Override
	public void onStart() {
		super.onStart();
		
		new LoadComments().execute();
		// Show Image Full
        new DownloadFullPhotoFileAsync().execute();
		postid = this.getIntent().getStringExtra("post_id");
		username = this.getIntent().getStringExtra("username");
		category = this.getIntent().getStringExtra("category");
		title = this.getIntent().getStringExtra("title");
		message = this.getIntent().getStringExtra("message");
		date = this.getIntent().getStringExtra("date");
		Log.d("Truetea", "username : " + username);
		Log.d("Truetea", "title : " + title);
		Log.d("Truetea", "message : " + message);
		Log.d("Truetea", "Post id : " + postid);
		
        Intent in = getIntent();
       

        // Get XML values from previous intent
        final String post_id = in.getStringExtra(TAG_POST_ID );
      final  String username = in.getStringExtra(TAG_USERNAME );
       
        String title = in.getStringExtra(TAG_TITLE );

        String message = in.getStringExtra(TAG_MESSAGE1);
        String date = in.getStringExtra(TAG_DATE);
        String fontPath = "fonts/Shadow Boxing.ttf";
        _category.setText(category);
   	 
		_titleView.setText(title);
		SharedPreferences pid = PreferenceManager
				.getDefaultSharedPreferences(ResolvedTweet.this);
		Editor edit1 = pid.edit();
		edit1.putString("postid", post_id);
		edit1.commit();
		_bodyView.setText(message);
		_username.setText(username);
		Typeface likes = Typeface.createFromAsset(this.getAssets(), fontPath);
		_username.setTypeface(likes);
		_post_id.setText(post_id);
		_dateView.setText(date);
		SharedPreferences id = PreferenceManager
				.getDefaultSharedPreferences(ResolvedTweet.this);
		Editor edit = id.edit();
		edit.putString("title", title);
		edit.commit();
		
		try{
			TextView textliketotal= ( TextView ) findViewById(R.id.txtLiketotal);
			TextView textunliketotal= ( TextView ) findViewById(R.id.txtunLiketotal);

			int storedLike=likesDataBaseAdapter.dbSyncCount(post_id);
			textliketotal.setText(String.valueOf( storedLike));
			
			
			int storedLike1=likesDataBaseAdapter.dbSyncCount1(post_id);
			textunliketotal.setText(String.valueOf(storedLike1));
			
		} catch (Exception ex) {
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		CheckBox btnCustomCheckBoxLike = (CheckBox) findViewById(R.id.btnCustomCheckBoxLike);
		btnCustomCheckBoxLike.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			

			@SuppressWarnings("deprecation")
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub

				 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ResolvedTweet.this);
		            String post_username = prefs.getString("email", "anon");   
				Cursor like =likesDataBaseAdapter.fetchLikes(post_username, post_id);
				if (like == null) {
					Toast.makeText(getApplicationContext(), "O likes",
					          Toast.LENGTH_SHORT).show();
				}
				else {
		    		startManagingCursor(like);
		    		
		    		//Check for duplicate usernames
		    		if (like.getCount() > 0) {
		    			Toast.makeText(getApplicationContext(), "You have already liked",
		  			          Toast.LENGTH_SHORT).show();
		    			stopManagingCursor(like);
		        		like.close();
		    			return;
		    		}
		    		}
				if(isChecked){
					
				liked++;
			   
				
				textlike.setText(String.valueOf(liked));
			
					
					

				}else  {
					
					
					liked--;
					String Like=textlike.getText().toString();
					textlike.setText(String.valueOf(liked));
		
				    likesDataBaseAdapter.updateEntry(Like,post_username);
				    Toast.makeText(getApplicationContext(), "unlike succesful... ", Toast.LENGTH_LONG).show();
				}
			}
			
		
		});
			 
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
       
    }
    
    // Show Image Full
    public void ShowImageFull(Bitmap imgFull)
    {
    	
		// fullimage
		//TextView imgName = (TextView) findViewById(R.id.txtImageName);
		//imgName.setText(username);

		// fullimage
		ImageView image = (ImageView) findViewById(R.id.tweet_image);

		try {
			image.setImageBitmap(imgFull);
		} catch (Exception e) {
			// When Error
			image.setImageResource(android.R.drawable.ic_menu_report_image);
		}

    }
  
 
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS:
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading.....");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            return mProgressDialog; 
        default:
            return null;
        }
    }
        
    // Download Full Photo in Background
    public class DownloadFullPhotoFileAsync extends AsyncTask<String, Void, Void> {
    	
    	  SharedPreferences pf = PreferenceManager.getDefaultSharedPreferences(ResolvedTweet.this);
	        String path = pf.getString("path", "anon");
    	Bitmap ImageFullBitmap = null;

        @SuppressWarnings("deprecation")
		protected void onPreExecute() {
        	super.onPreExecute();
        	showDialog(DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS);
        }

        @Override
        protected Void doInBackground(String... params) {
        	
            ImageFullBitmap = (Bitmap)loadBitmap(ImageFullPath);
    		return null;
        }

        @SuppressWarnings("deprecation")
		protected void onPostExecute(Void unused) {
        	ShowImageFull(ImageFullBitmap); // When Finish Show Images
            dismissDialog(DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS);
            removeDialog(DIALOG_DOWNLOAD_FULL_PHOTO_PROGRESS);
        }
        
    }
    
    
	public String getHttpPost(String url,List<NameValuePair> params) {
		StringBuilder str = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = client.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) { // Status OK
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
    
       
    
	
}
