package com.appxy.pocketexpensepro.table;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import com.appxy.pocketexpensepro.entity.MEntity;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class AccountsTable {

    private DbxDatastore mDatastore;
    private DbxTable mTable;
    
    
    public class Accounts{
    	  private DbxRecord mRecord;

    	  private String name;
    	  private String state;
    	  private Date datetime;
    	  private int autoclear;
    	  private double amount;
    	  private  String accountType;
    	  private int orderindex;
    	  private Date dateTime_sync;
    	  private String uuid;
    	  
          public Accounts() {
        	  
          }
          
          public void setAccountsData(Map<String, Object> mMap) {
			
        	  name = (String) mMap.get("name");
        	  state = (String)mMap.get("state");
        	  datetime = MEntity.getMilltoDropBox((Long)mMap.get("datetime"));
        	  autoclear = (Integer) mMap.get("autoclear");
        	  amount = (Double) mMap.get("amount");
        	  accountType = (String) mMap.get("accountType");
        	  orderindex = (Integer) mMap.get("orderindex");
        	  dateTime_sync = MEntity.getMilltoDropBox( System.currentTimeMillis() );
        	  uuid = (String) mMap.get("uuid");
        	  
        	  Log.v("mtag", "datetime"+datetime);
		}
          
          public DbxFields getFields() {
        	  DbxFields accountsFields = new DbxFields();
        	  accountsFields.set("name", name);
        	  accountsFields.set("state", state);
        	  accountsFields.set("datetime", datetime);
        	  accountsFields.set("autoclear", autoclear);
        	  accountsFields.set("amount", amount);
        	  accountsFields.set("accountType", accountType);
        	  accountsFields.set("orderindex", orderindex);
        	  accountsFields.set("dateTime_sync", dateTime_sync);
        	  accountsFields.set("uuid", uuid);
        	  return accountsFields;
		}
          
    }
    
    public Accounts getAccounts() {
		Accounts accounts = new Accounts();
		return accounts;
	}
    
    public AccountsTable(DbxDatastore datastore){
    	
  	  mDatastore = datastore;
      mTable = datastore.getTable("db_account_table");
        
  }
    
    public void insertRecords( DbxFields accountsFields ) throws DbxException {
		
//    	  DbxFields queryParams = new DbxFields().set("uuid", accountsFields.getString("uuid"));
//      	  DbxTable.QueryResult results = mTable.query(queryParams);
//          Iterator<DbxRecord> it=results.iterator();
//      
//      	if(it.hasNext())
//      		{
//      		DbxRecord firstResult= it.next();
//      		if(firstResult.getDate("dateTime_sync").getTime()<=accountsFields.getDate("dateTime_sync").getTime())
//      		{
////      			firstResult.setAll(dbxfield);
////      			while(it.hasNext())
////      			{	DbxRecord  r=it.next();
////      			r.deleteRecord();
////      			}
//      		}
//      		
//      		}
//      	else
           Log.v("mtag", "accountsFields"+accountsFields);
      	   mTable.insert(accountsFields);
      	   mDatastore.sync();

      	
	}
    
    
}
