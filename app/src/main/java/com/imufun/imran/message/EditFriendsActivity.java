package com.imufun.imran.message;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> MFriendRelation;
    protected ParseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        MFriendRelation = mCurrentUser.getRelation(ParseConstance.FRIEND_RELATION_KEY);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstance.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    //  Success

                    mUsers = parseUsers;
                    String[] username = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        username[i] = user.getUsername();
                        i++;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            EditFriendsActivity.this,
                            R.layout.abc_list_menu_item_checkbox,
                            username);

                    //setListAdapter(adapter);
                    setListAdapter(arrayAdapter);
                    addFriendCheckmark();

                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(getListView().isItemChecked(position)){

            MFriendRelation.add(mUsers.get(position));


        }else {
            //remove friend
            MFriendRelation.remove(mUsers.get(position));

        }
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                }
            }

        });



    }

    public void addFriendCheckmark() {
        MFriendRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friendd, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < mUsers.size(); i++) {

                        ParseUser user = mUsers.get(i);


                        for (ParseUser friends : friendd) {
                            if (friends.getObjectId().equals(user.getObjectId())) ;
                            getListView().setItemChecked(i, true);
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }


}
