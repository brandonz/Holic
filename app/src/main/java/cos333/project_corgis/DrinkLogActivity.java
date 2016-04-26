package cos333.project_corgis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;


public class DrinkLogActivity extends AppCompatActivity {
    // Total number of drinks
    private double num_drinks = 0;

    // Variables for BAC Calculation.
    // number of drinks since 0 BAC.
    private double bac_num_drinks = 0;
    // time of first drink.
    private long millis = 0;

    // Variables for server-based BAC calculation.
    // amount of latest drink
    private double drinkAmount = 0;
    // time of latest drink
    private long drinkTime = 0;
    // Flag to use latest values in BAC calculation
    private boolean newDrinkFlag = false;

    // User-entered weight. To be used in BAC calculations.
    private int weight;
    // User-entered gender. To be used in BAC calculations. Will have a value from
    // @strings/gender_choices
    private String gender;
    private double r; // associated gender constant

    //info for emergency texting
    private String contactname;
    private String num;
    private String firstname;
    private String lastname;

    private double threshold = 0.08;
    private boolean hasTexted;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_log);
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
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 0 - for private mode
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        weight = pref.getInt("weight", 100); // default? should never go there
        gender = pref.getString("gender", "Male"); // default? also problematic lol
        contactname = pref.getString("contact", "");
        num = pref.getString("contactnum", "");
        firstname = pref.getString("fname", "");
        lastname = pref.getString("lname", "");
        hasTexted = pref.getBoolean("hasTexted", false);


        String genders[] = getResources().getStringArray(R.array.gender_choices);
        if (gender.equals(genders[0]))
            r = 0.68; // L/kg
        else
            r = 0.55; // L/kg

        refreshDisplay();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Calculate the BAC level
     */
    private double calcBAC() {
        // Curr bac from server.
        double bac = HolicUtil.calcBAC(millis, bac_num_drinks, r, weight);

        if (newDrinkFlag) {
            num_drinks += drinkAmount;
            bac_num_drinks += drinkAmount;
            if (millis == 0) {
                millis = drinkTime; // save the first drink time
            }
            double newBac = HolicUtil.calcBAC(millis, bac_num_drinks, r, weight);

            // Send both to the server, using same time
            String formatStringOld = "type=add&drinktime=%s&drinkamount=0&currbac=%.3f";
            String paramsOld = String.format(formatStringOld, drinkTime, bac);
            new PutAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                    AccessToken.getCurrentAccessToken().getUserId(), paramsOld);
            String formatStringNew = "type=add&drinktime=%s&drinkamount=%s&currbac=%.3f";
            String paramsNew = String.format(formatStringNew, drinkTime, drinkAmount, newBac);
            new PutAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                    AccessToken.getCurrentAccessToken().getUserId(), paramsNew);

            newDrinkFlag = false;
            return newBac;
        }

        return bac;
    }

    private void displayDrinks() {
        TextView textView = (TextView) findViewById(R.id.drinks_level);
        textView.setText(String.format(getResources().getString(R.string.drinks_label), num_drinks));
    }

    private void displayBAC() {
        TextView textView = (TextView) findViewById(R.id.bac_level);
        double BAC = calcBAC();

        textView.setText(String.format(getResources().getString(R.string.bac_label), BAC));

        SmsManager emergency = SmsManager.getDefault();
        String message = getResources().getString(R.string.emergency_message_format_string,
                contactname, firstname, lastname);


        if ((BAC >= threshold) && !hasTexted && (num != null)) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("hasTexted", true);
            editor.apply();

            try {
                emergency.sendMultipartTextMessage(num, null, emergency.divideMessage(message), null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS failed.", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void addOneDrink(View view) {
        addDrinks(1);
    }

    public void addHalfDrink(View view) {
        addDrinks(0.5);
    }

    /**
     * Adds drinks, recalculates and displays BAC.
     * @param drinks
     */
    public void addDrinks(double drinks) {
        drinkAmount = drinks;
        drinkTime = System.currentTimeMillis();
        newDrinkFlag = true;

//        if (bac_num_drinks == 0) {
//            millis = System.currentTimeMillis();
//        }
//        num_drinks += drinks;
//        bac_num_drinks += drinks;
        refreshDisplay();
    }

    /**
     * Resets drink with no argument.
     */
    public void resetDrink() {
        // TODO: connect to server
        num_drinks = 0;
        bac_num_drinks = 0;
        millis = 0;
        refreshDisplay();
    }

    public void refreshBAC(View view) {
        // displayBAC();
        refreshDisplay();
    }

    /**
     * Called to end a session. User is prompted whether or not to save the session.
     */
    public void endSession() {
        AlertDialog confirm;
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);

        builder.setMessage(R.string.end_night_confirm);
        builder.setTitle(R.string.end_night);
        builder.setCancelable(true);

        builder.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("hasTexted", false);
                        editor.apply();

                        // save a final bac
                        double bac = HolicUtil.calcBAC(millis, bac_num_drinks, r, weight);
                        String formatString = "type=end&drinktime=%s&drinkamount=%s&currbac=%.3f";
                        String params = String.format(formatString, System.currentTimeMillis(), 0, bac);
                        new SaveNightAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                                AccessToken.getCurrentAccessToken().getUserId(), params);
                    }
                });

        builder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("hasTexted", false);
                        editor.apply();

                        new DeleteAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                                AccessToken.getCurrentAccessToken().getUserId());
                    }
                });
        builder.setNeutralButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        confirm = builder.create();
        confirm.show();
    }

    public void refreshDisplay() {
        new GetAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                AccessToken.getCurrentAccessToken().getUserId());
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DrinkLog Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cos333.project_corgis/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DrinkLog Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cos333.project_corgis/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drinklog, menu);
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
            case R.id.reset_drink:
                endSession();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Async task to delete the session without saving
    private class DeleteAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return RestClient.Delete(url[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            DrinkLogActivity.this.finish();
        }
    }
    //Async task to end and save night
    private class SaveNightAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return RestClient.Put(url[0], url[1]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            DrinkLogActivity.this.finish();
        }
    }

    //Async task to put new session info (e.g. set baccalcindex; add drinks)
    private class PutAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return RestClient.Put(url[0], url[1]);
        }
    }

    // Async task to get current session info and update the display.
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
                    JSONObject session = obj.getJSONObject(0);
                    JSONArray drinkLogs = session.getJSONArray("drinklogs");
                    int index = 0;
                    long firstDrink = 0;
                    try {
                        index = session.getInt("baccalcindex");
                    } catch(Exception e) {}
                    try {
                        firstDrink = drinkLogs.getJSONObject(index).getLong("drinktime");
                    } catch(Exception e) {}
                    double drinks = 0; // total number of drinks
                    double bac_drinks = 0; // number of drinks for bac calc
                    for (int i = 0; i < drinkLogs.length(); i++) {
                        double amount = drinkLogs.getJSONObject(i).getDouble("drinkamount");
                        drinks += amount;
                        if (i >= index)
                            bac_drinks += amount;
                    }
                    num_drinks = drinks;
                    bac_num_drinks = bac_drinks;
                    millis = firstDrink;
                    displayBAC();
                    displayDrinks();
                } else { // no such session! shouldn't happen, set to 0 in case
                    num_drinks = 0;
                    bac_num_drinks = 0;
                    millis = 0;

                    displayBAC();
                    displayDrinks();
                }
            } catch(Exception e) {
            }
        }
    }
}
