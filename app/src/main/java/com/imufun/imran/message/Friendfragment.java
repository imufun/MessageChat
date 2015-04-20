package com.imufun.imran.message;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by imran on 3/26/15.
 */
public class Friendfragment extends android.support.v4.app.ListFragment {
    public static  final String TAG=Friendfragment.class.getSimpleName();

    protected List<ParseUser> mFriend;
    protected ParseRelation<ParseUser> MFriendRelation;
    protected ParseUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        MFriendRelation = mCurrentUser.getRelation(ParseConstance.FRIEND_RELATION_KEY);

        ParseQuery<ParseUser>query=MFriendRelation.getQuery();
        query.addAscendingOrder(ParseConstance.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friendd, ParseException e) {

                if (e==null){
                mFriend=friendd;


                String[] username = new String[mFriend.size()];
                int i = 0;
                for (ParseUser user : mFriend) {
                    username[i] = user.getUsername();
                    i++;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getListView().getContext(),
                        android.R.layout.simple_list_item_1,
                        username);

                //setListAdapter(adapter);
                setListAdapter(arrayAdapter);


            }else {

                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

        });


    }

}