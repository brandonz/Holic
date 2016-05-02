package cos333.project_corgis;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cos333.project_corgis.chat.activity.ChatMainActivity;
import cos333.project_corgis.chat.activity.ChatRoomActivity;

/**
 * Created by belinda on 4/28/2016.
 */
public class Chat extends AppCompatActivity{

    // List of users to add to the chat.
    private ArrayList<String> logs = new ArrayList<>();
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

        cAdapter = new ChatAdapter(logs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);

        //Add item when 'Enter' is clicked
        final Button enter = (Button) findViewById(R.id.btn_enter);
        enter.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.add_user);
                String fb_id = editText.getText().toString();
                logs.add(fb_id);
                // instead, you can add names. we will have pulled friends.
                // if the name matches, add the id (two separate lists).
                // otherwise popup: invalid name
                cAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        });

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
        for (int i = 0; i < logs.size(); i++) {
            fbids = fbids.concat(logs.get(i));
            if (i != logs.size()-1)
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
