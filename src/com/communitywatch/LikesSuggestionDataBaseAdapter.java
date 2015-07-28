 package com.communitywatch;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LikesSuggestionDataBaseAdapter 
{
		static final String DATABASE_NAME = "likesugg.db";
		static final int DATABASE_VERSION = 1;
		public static final int NAME_COLUMN = 1;
		// TODO: Create public field for each column in your table.
		// SQL Statement to create a new database.
		static final String DATABASE_CREATE = "create table "+"LIKES"+
		                             "( " +"ID"+" integer primary key ,"+ "LIKES  varchar,USERNAME text,POST text,STATUS text); ";
		
		// Variable to hold the database instance
		public  SQLiteDatabase db;
		// Context of the application using the database.
		private final Context context;
		// Database open/upgrade helper
		private LikeIssueDataBaseHelper dbHelper;
		public  LikesSuggestionDataBaseAdapter(Context _context) 
		{
			context = _context;
			dbHelper = new LikeIssueDataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		public  LikesSuggestionDataBaseAdapter open() throws SQLException 
		{
			db = dbHelper.getWritableDatabase();
			return this;
		}
		public void close() 
		{
			db.close();
		} 

		public  SQLiteDatabase getDatabaseInstance()
		{
			return db;
		}

		public void insertEntry(HashMap<String, String> queryValues)
		{
	       ContentValues newValues = new ContentValues();

			// Assign values for each row.
			newValues.put("LIKES", queryValues.get("LIKES"));
			newValues.put("USERNAME",queryValues.get("USERNAME"));
			newValues.put("POST",queryValues.get("POST"));
			newValues.put("STATUS","no");
			
			// Insert the row into your table
			db.insert("LIKES", null, newValues);
			
			///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
		}
		 
	    /**
	     * Get list of Users from SQLite DB as Array List
	     * @return
	     */
	    public ArrayList<HashMap<String, String>> getAllUsers() {
	        ArrayList<HashMap<String, String>> LikeList;
	        LikeList = new ArrayList<HashMap<String, String>>();
	        String selectQuery = "SELECT  * FROM LIKES";
	      
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        if (cursor.moveToFirst()) {
	            do {
	                HashMap<String, String> map = new HashMap<String, String>();
	                map.put("ID", cursor.getString(0));
	                map.put("LIKES", cursor.getString(1));
	                map.put("USERNAME", cursor.getString(2));
	                map.put("POST", cursor.getString(3));
	                LikeList.add(map);
	            } while (cursor.moveToNext());
	        }
	      
	        return LikeList;
	    }
	    /**
	     * Compose JSON out of SQLite records
	     * @return
	     */
	    public String composeJSONfromSQLite(){
	        ArrayList<HashMap<String, String>> wordList;
	        wordList = new ArrayList<HashMap<String, String>>();
	        String selectQuery = "SELECT  * FROM LIKES where STATUS = '"+"no"+"'";
	        
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        if (cursor.moveToFirst()) {
	            do {
	                HashMap<String, String> map = new HashMap<String, String>();
	                map.put("ID", cursor.getString(0));
	                map.put("LIKES", cursor.getString(1));
	                map.put("USERNAME", cursor.getString(2));
	                map.put("POST", cursor.getString(3));
	                wordList.add(map);
	            } while (cursor.moveToNext());
	        }

	        Gson gson = new GsonBuilder().create();
	        //Use GSON to serialize Array List to JSON
	        return gson.toJson(wordList);
	    }
	    
	    /**
	     * Get Sync status of SQLite
	     * @return
	     */
	    public String getSyncStatus(){
	        String msg = null;
	        if(this.dbSyncCount() == 0){
	            msg = "SQLite and Remote MySQL DBs are in Sync!";
	        }else{
	            msg = "DB Sync needed\n";
	        }
	        return msg;
	    }
	    
	    
	    /**
	     * Get SQLite records that are yet to be Synced
	     * @return
	     */
	    public int dbSyncCount(){
	        int count = 0;
	        String selectQuery = "SELECT  * FROM LIKES where STATUS = '"+"no"+"'";
	       
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        count = cursor.getCount();
	 
	        return count;
	    }
		public Cursor fetchLikes(String post_username, String post_id) {
			Cursor myCursor = db.query("LIKES", 
					new String[] { "ID", "USERNAME", "POST" }, 
					"USERNAME" + "='" + post_username + "' AND " + 
					"POST" + "='" + post_id + "'", null, null, null, null);
			
			if (myCursor != null) {
				myCursor.moveToFirst();
			}
			return myCursor;
		}
		public Cursor fetchLikes1( String post_id) {
			Cursor myCursor = db.query("LIKES", 
					new String[] { "ID", "USERNAME", "POST" }, 
					"POST" + "='" + post_id + "'"
					, null, null, null, null);
			
			if (myCursor != null) {
				myCursor.moveToFirst();
			}
			return myCursor;
		}
		public int deleteEntry(String like)
		{
			//String id=String.valueOf(ID);
		    String where="LIKES=?";
		    int numberOFEntriesDeleted= db.delete("LIKES", where, new String[]{like}) ;
	       // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
	        return numberOFEntriesDeleted;
		}	
		public String getSinlgeEntry(String like)
		{
			Cursor cursor=db.query("LIKES", null, " LIKES=?", new String[]{like}, null, null, null);
	        if(cursor.getCount()<1) // UserName Not Exist
	        {
	        	cursor.close();
	        	return "No Likes";
	        }
	        
			return like;
		   
						
		}
		   public int dbSyncCount(String post_id){
			   
		        int count = 0;
		        String selectQuery = "SELECT  * FROM LIKES where LIKES='0' and POST='"+ post_id +"' ";
		      
		        Cursor cursor = db.rawQuery(selectQuery, null);
		        count = cursor.getCount();
		      
		        return count;
		    }
		   public int dbSyncCount1(String post_id){
			   
		        int count = 0;
		        String selectQuery = "SELECT  * FROM LIKES where LIKES='1' and POST='"+ post_id +"' ";
		      
		        Cursor cursor = db.rawQuery(selectQuery, null);
		        count = cursor.getCount();
		      
		        return count;
		    }
	
	
		public void  updateEntry(String Like ,String post_username)
		{
			   String updateQuery = "Update LIKES set LIKES = '"+ Like+"' where USERNAME="+"'"+ post_username +"'";
		        Log.d("query",updateQuery);       
		        db.execSQL(updateQuery);			   
		}	
		  /**
	     * Update Sync status against each User ID
	     * @param id
	     * @param status
	     */
	    public void updateSyncStatus(String id, String status){
	        
	        String updateQuery = "Update LIKES set STATUS= '"+ status +"' where ID="+"'"+ id +"'";
	        Log.d("query",updateQuery);       
	        db.execSQL(updateQuery);
	     
	    
	    
	    }
	    
		
}

