package com.appxy.pocketexpensepro.bills;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.overview.budgets.CreatBudgetsActivity;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class BillsFragment extends Fragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.action_add:
			
			Intent intent = new Intent();
			intent.setClass(getActivity(), CreatBudgetsActivity.class);
			startActivityForResult(intent, 8);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

}
