package com.appxy.pocketexpensepro.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.EditTransactionActivity;
import com.appxy.pocketexpensepro.accounts.EditTransferActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends BaseHomeActivity {

	private ListView searchListView;
	private ListViewAdapter  adapter;
	private SearchView searchView;
	private List<Map<String, Object>> searchDataList;
	private String queryWordString = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		searchDataList = new ArrayList<Map<String, Object>>();
		searchListView = (ListView) findViewById(R.id.searchlist);
		searchListView.setDividerHeight(0);
		
		adapter = new ListViewAdapter (this);
		searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,

			long arg3) {
				// TODO Auto-generated method stub

				if (searchDataList != null) {
					final int tId = (Integer) searchDataList.get(arg2).get("_id");
					int expenseAccount = (Integer) (Integer) searchDataList.get(arg2).get("expenseAccount");
					int incomeAccount = (Integer) searchDataList.get(arg2).get("incomeAccount");

					if (expenseAccount > 0 && incomeAccount > 0) {

						Intent intent = new Intent();
						intent.putExtra("_id", tId);
						intent.setClass(SearchActivity.this,
								EditTransferActivity.class);
						startActivityForResult(intent, 13);

					} else {

						Intent intent = new Intent();
						intent.putExtra("_id", tId);
						intent.setClass(SearchActivity.this,
								EditTransactionActivity.class);
						startActivityForResult(intent, 13);

					}
				}
			}
		});
		searchListView.setAdapter(adapter);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: 
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void getDate(String keyWords){
		searchDataList = SearchDao.selectTransactionByKeywords(this, keyWords);
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.search, menu);
		searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setIconified(false);

		int mid = getResources().getIdentifier("android:id/search_src_text",
				null, null);
		TextView textView = (TextView) searchView.findViewById(mid);
		textView.setHintTextColor(Color.WHITE); // rgb(130, 136, 139)
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(16);

		searchView.setQueryHint("Payee");
		searchView.onActionViewExpanded();

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				queryWordString = newText;

				if (newText != null && newText.length() != 0) {
					getDate(queryWordString);
					adapter.setAdapterDate(searchDataList);
					adapter.notifyDataSetChanged();
				}
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {

		case 13:
			if (data != null) {
				getDate(queryWordString);
				adapter.setAdapterDate(searchDataList);
				adapter.notifyDataSetChanged();
			}

			break;

		}

	}

}
