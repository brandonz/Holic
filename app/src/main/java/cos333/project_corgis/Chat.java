package cos333.project_corgis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import cos333.project_corgis.chat.activity.ChatMainActivity;
import cos333.project_corgis.chat.activity.ChatRoomActivity;

/**
 * Created by belinda on 4/28/2016.
 */
public class Chat extends AppCompatActivity{

    // All fb friends on Holic, courtesy of FB
    private Hashtable<String, String> fbHolicFriends = new Hashtable<>();
    // The people the user wants to add
    private ArrayList<String> names = new ArrayList<>();
    // The ids of the people the user wants to add. Indices should match up with names.
    private ArrayList<String> ids = new ArrayList<>();
    // Loading popup. Used for getting fb friends.
    private ProgressDialog loading;

    private RecyclerView recyclerView;
    private ChatAdapter cAdapter;
    private Intent intent;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        cAdapter = new ChatAdapter(names);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);


        // initialize loading screen for friend population
        loading = new ProgressDialog(this);
        loading.setTitle(getResources().getString(R.string.loading));
        loading.setMessage(getResources().getString(R.string.loading_message));
        loading.setCancelable(false);
        loading.show();
        // populate friends
        getFriends();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
        final Button create = (Button) findViewById(R.id.create_chat);
        create.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendInfo(v);
            }
        });

    }

    // Adds the user from add_user.
    public void addUser(View view) {
        EditText editText = (EditText) findViewById(R.id.add_user);
//                String fb_id = editText.getText().toString();
//                logs.add(fb_id);
        String name = editText.getText().toString();
        if (fbHolicFriends.containsKey(name)) {
            if (names.contains(name)) {
                // already added friend, notify user and do nothing
                AlertDialog confirm;
                AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);

                builder.setTitle(R.string.error_message);
                builder.setMessage(R.string.duplicate_friend);
                builder.setCancelable(true);

                builder.setPositiveButton(R.string.okay,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                confirm = builder.create();
                confirm.show();
            } else {
                String id = fbHolicFriends.get(name);
                names.add(name);
                ids.add(id);
                cAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        } else {
            // invalid name, notify user and do nothing
            AlertDialog confirm;
            AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);

            builder.setTitle(R.string.error_message);
            builder.setMessage(R.string.invalid_friend);
            builder.setCancelable(true);

            builder.setPositiveButton(R.string.okay,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            confirm = builder.create();
            confirm.show();
        }
    }

    // Gets user's friends using Holic from Facebook, parses into name -> id dict
    public void getFriends() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String id = pref.getString("id", "");
        String friendParam = String.format("/%s/friends", id);
        /* make the API call to FB */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                friendParam,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray users = response.getJSONObject().getJSONArray("data");
                            int numUsers = users.length();
                            for (int i = 0; i < numUsers; i++) {
                                try {
                                    JSONObject user = users.getJSONObject(i);
                                    String id = user.getString("id");
                                    String name = user.getString("name");
                                    fbHolicFriends.put(name, id);
                                } catch (Exception e) {
                                    System.out.println("to catch an exception");
                                }
                            }
                            // Attach the autocomplete adapter
                            int numFriends = fbHolicFriends.size();
                            String[] friends = fbHolicFriends.keySet().toArray(new String[numFriends]);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Chat.this,
                                    android.R.layout.simple_dropdown_item_1line, friends);
                            AutoCompleteTextView textView = (AutoCompleteTextView)
                                    findViewById(R.id.add_user);
                            textView.setAdapter(adapter);
                            loading.dismiss();
                        } catch (Exception e) {
                            loading.dismiss();
                            // can't get users
                            AlertDialog confirm;
                            AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);

                            builder.setTitle(R.string.error_message);
                            builder.setMessage(R.string.server_unreachable);
                            builder.setCancelable(false);

                            builder.setPositiveButton(R.string.okay,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Chat.this.finish();
                                        }
                                    });
                            confirm = builder.create();
                            confirm.show();
                        }
                    }
                }
        ).executeAsync();
    }

    // Creates the new chat
    public void sendInfo(View view) {
        Profile currProf = Profile.getCurrentProfile();
        String id = currProf.getId();
        intent = new Intent(this, ChatRoomActivity.class);
        EditText chatName = (EditText) findViewById(R.id.chat_name);
        name = chatName.getText().toString();
        if (name.isEmpty()) {
            name = "New Chat";
        }

        // Send info to the server
        String formatString = "fbids=%s&chat_name=%s";
        String fbids = id + ",";

        //Creates format string for all fbids
        for (int i = 0; i < ids.size(); i++) {
            fbids = fbids.concat(ids.get(i));
            if (i != ids.size()-1)
                fbids = fbids.concat(",");
        }
        String urlParameters = String.format(formatString, fbids, name);
        new PostAsyncTask().execute(getResources().getString(R.string.server_chat), urlParameters);
        // create empty pastsession
    }


    private class PostAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return RestClient.Post(url[0], url[1]);
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.println("trying");
                JSONObject obj = new JSONObject(result);

                if (obj.length() > 0) {
                    intent.putExtra("chat_room_id", obj.getString("chat_room_id"));
                    intent.putExtra("name", name);
                    startActivity(intent);
                } else {
                    final Intent chatFail = new Intent(Chat.this, ChatMainActivity.class);
                    startActivity(chatFail);
                    finish();
                }
            } catch(Exception e) {
            }
        }
    }
}
