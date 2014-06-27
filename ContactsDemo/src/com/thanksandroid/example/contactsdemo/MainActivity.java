package com.thanksandroid.example.contactsdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.view.View;

public class MainActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final int ACTION_GET_CONTACT = 100;
	private static final int CONTACT_QUERY_ID = 1;

	// These are the columns we want to get from contact. You can also get
	// email/address etc.
	private String PHONE_NUMBER = Phone.NUMBER;
	private String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY
			: Contacts.DISPLAY_NAME;

	private Uri mContactUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	// compose intent and start activity
	public void getContact(View view) {
		Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
				Uri.parse("content://contacts"));

		// Show user only contacts with phone numbers
		pickContactIntent.setType(Phone.CONTENT_TYPE);

		// start activity
		startActivityForResult(pickContactIntent, ACTION_GET_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == ACTION_GET_CONTACT) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				// The user picked a contact.
				// The Intent's data Uri identifies which contact was selected.
				mContactUri = data.getData();

				// Perform the query on the contact to get the NUMBER column
				// We will use CursorLoader to perform the query in a separate
				// thread to avoid blocking of our app's UI thread.
				// You can also uset initLoader() method, but restartLoader()
				// can be called multiple times
				// First param to restartLoader() is a loader id which can be
				// used to keep track of multiple loaders
				getLoaderManager().restartLoader(CONTACT_QUERY_ID, null, this);

			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled operation
			} else {
				// some error occurred
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		if (id == CONTACT_QUERY_ID) {
			// Define projection or columns which are required

			String[] projection = { PHONE_NUMBER, DISPLAY_NAME };

			return new CursorLoader(this, mContactUri, projection, null, null,
					null);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		if (mContactUri == null)
			return;

		if (loader.getId() == CONTACT_QUERY_ID) {
			if (cursor.moveToFirst()) {

				// Retrieve the phone number from the NUMBER column
				String number = cursor.getString(cursor
						.getColumnIndex(PHONE_NUMBER));
				String name = cursor.getString(cursor
						.getColumnIndex(DISPLAY_NAME));

				// display the number
				showNumber(name, number);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

	// show name and phone number in alert dialog
	private void showNumber(String name, String number) {
		// compose message
		String message = "Name: " + name + "\n" + "Phone Number: " + number;

		new AlertDialog.Builder(this).setTitle("Contact Demo")
				.setMessage(message).setNegativeButton("OK", null).create()
				.show();
	}
}
