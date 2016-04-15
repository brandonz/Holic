package cos333.project_corgis;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.login_activity);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        // TODO: go to MainActivity if server lookup fails
        // TODO: go to LandingActivity if server lookup succeeds
        // final Intent homeScreen = new Intent(LoginActivity.this, LandingActivity.class);
        if (isLoggedIn()) {
            new GetAsyncTask().execute(getResources().getString(R.string.server)
                    + AccessToken.getCurrentAccessToken().getUserId());
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                info.setText(
//                        "User ID: "
//                                + loginResult.getAccessToken().getUserId()
//                                + "\n" +
//                                "Auth Token: "
//                                + loginResult.getAccessToken().getToken()
//                );

                //loginButton.setVisibility(View.INVISIBLE);


                // GET request using fbid
                new GetAsyncTask().execute(getResources().getString(R.string.server)
                        + loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    //Async task for get
    private class GetAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return RestClient.Get(url[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray obj = new JSONArray(result);

                if (obj.length() > 0) {
                    JSONObject person = obj.getJSONObject(0);
                    final Intent landing = new Intent(LoginActivity.this, LandingActivity.class);

                    landing.putExtra(MainActivity.WEIGHT_MESSAGE,
                            Integer.toString(person.getInt("weight")));
                    landing.putExtra(MainActivity.BODY_TYPE_MESSAGE,
                            mfToGender(person.getString("gender")));
                    startActivity(landing);
                } else {
                    final Intent newUserScreen = new Intent(LoginActivity.this, CreateProfile.class);
                    startActivity(newUserScreen);
                    finish();
                }
            } catch(Exception e) {
            }
        }

        private String mfToGender(String g) {
            String genders[] = getResources().getStringArray(R.array.gender_choices);
            if (g.equals("M"))
                return genders[0];
            else
                return genders[1];
        }
    }
}

