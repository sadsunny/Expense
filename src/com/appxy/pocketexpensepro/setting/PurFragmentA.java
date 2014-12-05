package com.appxy.pocketexpensepro.setting;

import org.json.JSONException;
import org.json.JSONObject;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.util.IabHelper;
import com.appxy.pocketexpensepro.util.IabResult;
import com.appxy.pocketexpensepro.util.Purchase;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PurFragmentA extends Fragment {
	private LayoutInflater mInflater;
	private RelativeLayout relativeLayout1;
	private Button upgradeButton;
	private int position;

	static final int RC_REQUEST = 10001;
	private IabHelper mHelper;
	public static final String Paid_Id_VF = "upgrade";
	static final String TAG = "Expense";
	private static final String PREFS_NAME = "SAVE_INFO";
	private boolean iap_is_ok = false;
	private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi803lugKTJdERpN++BDhRYY5hr0CpTsuj+g3fIZGBLn+LkZ+me0it3lP375tXqMlL0NLNlasu9vWli3QkCFBbERf+KysqUCsrqqcoq3hUini6LSiKkyuISM2Y4gWUqSVT+vkLP4psshnwJTbF6ii2jZfXFxLVoT5P30+y4rgCwncgRsX14x2bCpJlEdxrNfoxL4EqlHAt9/9vsc0PoW8QH/ChKJFkTDOsB9/42aur4zF9ua568ny1K6vlE/lnkffBP6DvsHFrIdpctRyUdrBVnUyMl+1k2ufUHJudfeGpKuExLcNOxuryCTolIFj44dB2TugNFzQwOE4xoRyCfJ7bQIDAQAB";
	private ImageView mImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle != null) {
			position = bundle.getInt("position");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_pur, container, false);
		mInflater = inflater;

		relativeLayout1 = (RelativeLayout) view
				.findViewById(R.id.RelativeLayout1);
		upgradeButton = (Button) view.findViewById(R.id.upgrade_but);

		if (position == 0) {
			relativeLayout1.setBackgroundResource(R.drawable.pura);
		} else {
			relativeLayout1.setBackgroundResource(R.drawable.purb);
		}

		try {

			mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

				@Override
				public void onIabSetupFinished(IabResult result) {
					// TODO Auto-generated method stub
					if (!result.isSuccess()) {
						return;
					}
					if (mHelper == null)
						return;
					iap_is_ok = true;
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}

		upgradeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (iap_is_ok && mHelper != null) {
					String payload = "";
					mHelper.launchPurchaseFlow(getActivity(), Paid_Id_VF,
							RC_REQUEST, mPurchaseFinishedListener);
				}
			}
		});

		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// very important:
		   
        if (mHelper != null){
            try {
            	mHelper.dispose();
            }catch (IllegalArgumentException ex){
                ex.printStackTrace();
            }finally{}
        }
        mHelper = null;
        
	}

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {

		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (!result.isSuccess()) {
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				return;
			}

			if (purchase.getSku().equals(Paid_Id_VF)) {
				// bought the premium upgrade!
				alert("Thank you for upgrading to pro!");
				Common.mIsPaid = true;
				SharedPreferences sharedPreferences = getActivity()
						.getSharedPreferences(PREFS_NAME,
								getActivity().MODE_PRIVATE); // 已经设置密码
				SharedPreferences.Editor meditor = sharedPreferences.edit();
				meditor.putBoolean("isPaid", true);
				meditor.commit();

			}
		}

	};

	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();
		return true;
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		bld.create().show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (mHelper == null) return;
		
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
			 if (requestCode == RC_REQUEST) {     
			      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			        
			      if (resultCode == getActivity().RESULT_OK) {
			         try {
			             JSONObject jo = new JSONObject(purchaseData);
			             String sku = jo.getString("productId");
			             alert("Thank you for upgrading to pro! ");
			             SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME,0);   //已经设置密码 
					     SharedPreferences.Editor meditor = sharedPreferences.edit();  
						 meditor.putBoolean("isPaid",true ); 
						 meditor.commit();
			          }
			          catch (JSONException e) {
			             alert("Failed to parse purchase data.");
			             e.printStackTrace();
			          }
			      }
			   }
			 
        }
	}

}
