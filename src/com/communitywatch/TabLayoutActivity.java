package com.communitywatch;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import com.community.watch.R;

@SuppressWarnings("deprecation")
public class TabLayoutActivity extends TabActivity implements
ActionBar.OnNavigationListener, TabListener {
	
	SessionCreator session;
	private MenuItem refreshMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionCreator(getApplicationContext());
        if(session.checkLogin())
            finish();
        setContentView(R.layout.tab_activity);
     
        TabHost tabHost = getTabHost();

        TabSpec issues = tabHost.newTabSpec("ISSUES");
        issues.setIndicator("ISSUES");
        Intent ISSUES = new Intent(this, ReadIssues.class);
        issues.setContent(ISSUES);
   

        TabSpec suggestions = tabHost.newTabSpec("SUGGESTIONS");
        suggestions.setIndicator("SUGGESTIONS");
        Intent SUGGESTIONS = new Intent(this, ReadSuggestions.class);
        suggestions.setContent(SUGGESTIONS);
        
        TabSpec resolved = tabHost.newTabSpec("RESOLVED");
        resolved.setIndicator("RESOLVED");
        Intent RESOLVED = new Intent(this,ResolvedHome.class);
        resolved.setContent(RESOLVED);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(issues); 
        tabHost.addTab(suggestions); 
        tabHost.addTab(resolved); 
        ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
    }
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return super.onCreateOptionsMenu(menu);
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_search:
			// search action
			Search();
			return true;
	
		case R.id.action_refresh:
			// refresh
			refreshMenuItem = item;
			// load the data from server
			
			new SyncData().execute();
			return true;
		case R.id.action_help:
			// help action
			return true;
		case R.id.action_check_updates:
			// check for updates action
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	
}
	
	private void Search() {
		Intent i = new Intent(TabLayoutActivity.this, SearchResultsActivity.class);
		startActivity(i);
	}

	/**
	 * Async task to load the data from server
	 * **/
	private class SyncData extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// set the progress bar view
			refreshMenuItem.setActionView(R.layout.action_progressbar);
			Intent i = new Intent(TabLayoutActivity.this, TabLayoutActivity.class);
			startActivity(i);
			refreshMenuItem.expandActionView();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			refreshMenuItem.collapseActionView();
			// remove the progress bar view
			refreshMenuItem.setActionView(null);
		}
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
	