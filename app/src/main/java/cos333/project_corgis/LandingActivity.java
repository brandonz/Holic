package cos333.project_corgis;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import com.facebook.AccessToken;
import com.facebook.Profile;

import cos333.project_corgis.chat.activity.ChatMainActivity;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    // TODO: Open the drink log activity. Modify the drink log activity to get gender and weight
    // from the server instead of the other page.
    public void startDrinkActivity(View view) {
        // This format string is hardcoded for now because I had problems with the & symbol in the
        // strings resources file lol. TODO: put it in the resources
        String formatString = "fbid=%s";
        String id = AccessToken.getCurrentAccessToken().getUserId();
        String urlParameters = String.format(formatString, id);
        new PostAsyncTask().execute(getResources().getString(R.string.server_currsession), urlParameters);
    }

    public void startChatActivity(View view) {
        startActivity(new Intent(this, ChatMainActivity.class));
    }

    // TODO: Open the settings activity.
    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openLogout() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        this.finish();
    }

    public void openStats(View view) {
        Intent intent = new Intent(this, Stats.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing, menu);
        return true;
    }

    /**
     * Handles menu selection.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_logout:
                openLogout();
                return true;
            case R.id.action_profile:
                Intent intent = new Intent(this, ViewProfile.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Async task for post
    private class PostAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return RestClient.Post(url[0], url[1]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(LandingActivity.this, DrinkLogActivity.class);
            startActivity(intent);
        }
    }

}
