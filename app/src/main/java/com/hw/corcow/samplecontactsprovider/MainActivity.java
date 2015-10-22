package com.hw.corcow.samplecontactsprovider;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

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
                // getContacts(keyword);
                Bundle args = new Bundle();
                args.putString("keyword", keyword);
                getSupportLoaderManager().restartLoader(0, args, MainActivity.this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getSupportLoaderManager().initLoader(0, null, this);   // 첫번째인자 : Loader의 id코드(0) / 두번째인자 : 내가 LoaderManager에게 전달하는 argument
        // >> onCreateLoader가 바로 호출됨. ( initLoader의 1,2번째 인자를 넘겨준다)

    }

    private void getContacts(String keyword) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;        // 주소록 데이터의 uri 가져오기
        if (!TextUtils.isEmpty(keyword)) {
            uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(keyword));           // 필터!
        }
        Cursor c = getContentResolver().query(uri, projection, selection, null, sortOrder);         // 내가 원하는 URI , 검색조건(column;projection, selection) , SORT ORDER
        mAdapter.changeCursor(c);
    }

    /* Loader로 처리했으므로 불필요
    @Override
    protected void onResume() {
        super.onResume();
        // getContacts(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.changeCursor(null);
    }
    */

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

    // Loader

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        if (args != null) {
            String keyword = args.getString("keyword");
            if(!TextUtils.isEmpty(keyword)) {
                uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(keyword));
            }
        }
        return new CursorLoader(this, uri, projection, selection, null, sortOrder);         // LoaderManager가 얻은 데이터가 이것이라고 말해줌
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {            // 획득한 데이터를 알려줌
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {                          // loader가 유효하지 않다면
        mAdapter.swapCursor(null);
        /** 결과를 release 하는 것을 Loader가 내부적으로 처리
         * Activity가 finish되면 >> 이전엔 Cursor를 close 해주어야했음.
         * Activity가 finish되면, onLoaderReset 가 호출되면서 내부적으로 처리해줌
         */
    }
}
