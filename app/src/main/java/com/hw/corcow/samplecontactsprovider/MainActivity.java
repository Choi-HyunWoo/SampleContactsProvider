package com.hw.corcow.samplecontactsprovider;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText keywordView;
    SimpleCursorAdapter mAdapter;

    String[] projection = {ContactsContract.Contacts._ID , ContactsContract.Contacts.DISPLAY_NAME};          // projection = column
    String selection = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Contacts.DISPLAY_NAME + " != ''))";
    String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView)findViewById(R.id.listView);
        keywordView = (EditText)findViewById(R.id.edit_keyword);

        String[] from = {ContactsContract.Contacts.DISPLAY_NAME};
        int[] to = {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);
        listView.setAdapter(mAdapter);

        // 검색 (edittext - keyword changed listener)
        keywordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString();
                getContacts(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getContacts(String keyword) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;        // 주소록 데이터의 uri 가져오기
        if (!TextUtils.isEmpty(keyword)) {
            uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(keyword));           // 필터!
        }
        Cursor c = getContentResolver().query(uri, projection, selection, null, sortOrder);         // 내가 원하는 URI , 검색조건(column;projection, selection) , SORT ORDER
        mAdapter.changeCursor(c);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContacts(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.changeCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
