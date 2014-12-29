package com.example.parsingexample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class BooksResult extends ListActivity {
	
	
	ListView lv;
    String url;
    String data = "";
	TableLayout tl;
	TableRow tr;
	TextView label;
    // Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JsonParser jParser = new JsonParser();

	ArrayList<HashMap<String, String>> productsList;

	// url to get all products list
	private static String url_all_products = "http://newlifefoundation.co.in/vignan/books.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_EVENTS = "books";
	private static final String TAG_EDITION = "edition";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_PUBLISHER = "publisher";
	private static final String TAG_TITLE = "title";

	// products JSONArray
	JSONArray products = null;

	String title,author,edition;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_title);
        
		Intent myIntent = getIntent(); 
		// gets the arguments from previously created intent
		title = myIntent.getStringExtra("Title"); 
		author = myIntent.getStringExtra("Author"); 
		edition = myIntent.getStringExtra("Edition"); 
		Log.d("Edition", edition);
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

	
			new LoadAllProducts().execute();
			
		// Get listview
			
		lv = getListView();
		

		// on seleting single product
		// launching Edit Product Screen
		        
	}
	

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(BooksResult.this);
			pDialog.setMessage("Loading. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("author", author));
			params.add(new BasicNameValuePair("edition", edition));
			
			// getting JSON string from URL
			JSONObject json = null;
			try {
				json = jParser.makeHttpRequest(url_all_products, "GET", params);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_EVENTS);

					
					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String title = c.getString(TAG_TITLE);
						String description = c.getString(TAG_PUBLISHER);
						String date = c.getString(TAG_EDITION);
						String month = c.getString(TAG_AUTHOR);
						//url = c.getString(TAG_URL);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_TITLE, title);
						map.put(TAG_PUBLISHER, description);
						map.put(TAG_EDITION, date);
						map.put(TAG_AUTHOR, month);
						//map.put(TAG_URL, url);

						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					// no products found
					// Launch Add New product Activity
					/*Intent i = new Intent(getApplicationContext(),
							NewProductActivity.class);*/
					// Closing all previous activities
					/*i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);*/
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					
					ListAdapter adapter = new SimpleAdapter(
							BooksResult.this, productsList,
							R.layout.books_result, new String[] { TAG_AUTHOR,TAG_PUBLISHER, TAG_TITLE,
									TAG_EDITION},
							new int[] { R.id.result_author, R.id.result_pubisher, R.id.result_title, R.id.result_edition});
					// updating listview
					setListAdapter(adapter);
				}
			});

		}
	
	}
	
	private void alert(String title, String message) {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		//dialog.setIcon(R.drawable.logo);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton("Ok", null);
		dialog.show();
	}

}
