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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.community.watch.R;

public class ResolvedIssueActivity extends Activity
{
	
	public static final int DIALOG_DOWNLOAD_JSON_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    LikesIssueDataBaseAdapter likesDataBaseAdapter;
	HashMap<String, String> queryValues;
    ProgressDialog prgDialog;
    ArrayList<HashMap<String, Object>> MyArrList;

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resolved_issue);
    	likesDataBaseAdapter=new LikesIssueDataBaseAdapter(this);
		likesDataBaseAdapter=likesDataBaseAdapter.open();
		 
        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // btnSearch
       
	    // Download JSON File	
		new DownloadJSONFileAsync().execute();

    }
       
    public void addComment(View v) {
		Intent i = new Intent(ResolvedIssueActivity.this, SuggestionCategoryActivity.class);
		startActivity(i);
	}
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_DOWNLOAD_JSON_PROGRESS:
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading Issues....");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            return mProgressDialog;
        default:
            return null;
        }
    }
    
  
    public void ShowAllContent()
    {
    	 final ListView lstView1 = (ListView)findViewById(R.id.listView1); 
         lstView1.setAdapter(new ImageAdapter(ResolvedIssueActivity.this,MyArrList));
         	
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
 				Intent intent = new Intent(ResolvedIssueActivity.this, ResolvedTweet.class);
 				
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
					.getDefaultSharedPreferences(ResolvedIssueActivity.this);
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
			 int storedLike=likesDataBaseAdapter.dbSyncCount(PostID);
				textliketotal.setText(String.valueOf( storedLike));
		 /*
			// ColratingBar
			RatingBar Rating = (RatingBar) convertView.findViewById(R.id.message);
			Rating.setPadding(10, 0, 0, 0);
			Rating.setEnabled(false);
			Rating.setMax(5);
			Rating.setRating(Float.valueOf(MyArr.get(position).get("message").toString()));
	*/
			
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
        	
        	String url = "http://scottbm.5gbfree.com/webservice/resolvedIssues.php";
        	// Paste Parameters
        	// keySearch
           
            
            // Disbled Keyboard auto focus
          
          	
          	List<NameValuePair> mapp = new ArrayList<NameValuePair>();
            
        	JSONArray data;
			try {
				data = new JSONArray(getJSONUrl(url,mapp));
				
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
        	ShowAllContent();
        	
        	 // When Finish Show Content
            dismissDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
            removeDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
        }
        

    }

    
    /*** Get JSON Code from URL ***/
  	public String getJSONUrl(String url ,List<NameValuePair> mapp) {
  		StringBuilder str = new StringBuilder();
  		HttpClient client = new DefaultHttpClient();
  		HttpGet httpGet = new HttpGet(url);
  		try {
  			HttpResponse response = client.execute(httpGet);
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
  				Log.e("Log", "Failed to download file..");
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
