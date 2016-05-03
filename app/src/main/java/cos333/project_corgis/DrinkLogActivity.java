package cos333.project_corgis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    private double prevBAC = 0;
    private long prevDrinkTime = 0;

    // Variables for server-based BAC calculation.
    // amount of latest drink
    private double drinkAmount = 0;
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

    private boolean textingEnabled = false;
    private double threshold = 0.08;
    private boolean hasTexted;
    private boolean inSession;

    // flag for saving night at the end of bac calculation refresh
    private boolean saveNight = false;

    // flag for the second add
    private boolean flagAddDrink = false;
    // bac for the second add O:
    private double addBac;

    // stuff to disable while waiting
    private ImageButton plusOne;
    private ImageButton plusHalf;
    private ImageButton endButt;
    // For helping to wait for the server
    private long mLastClickTime;

    // loading dialog, reusable?
    private ProgressDialog loading;

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
        textingEnabled = pref.getBoolean("textingEnabled", false);
        threshold = pref.getFloat("threshold", (float) 0.08);
        inSession = true;
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("inSession", true);
        editor.apply();


        String genders[] = getResources().getStringArray(R.array.gender_choices);
        if (gender.equals(genders[0]))
            r = 0.68; // L/kg
        else
            r = 0.55; // L/kg

        // initialize buttons
        plusOne = (ImageButton) findViewById(R.id.plusOneButton);
        plusHalf = (ImageButton) findViewById(R.id.plusHalfButton);
        endButt = (ImageButton) findViewById(R.id.end_night_button);

        // initialize loading screen for refreshdisplay
        loading = new ProgressDialog(this);
        loading.setTitle(getResources().getString(R.string.loading));
        loading.setMessage(getResources().getString(R.string.loading_message));
        loading.setCancelable(false);

        refreshDisplay();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Calculate the BAC level. Called from displayBAC(), which is called after GET request.
     */
    private double calcBAC() {
        // Curr bac from server.
        double bac = HolicUtil.calcBAC(prevBAC, prevDrinkTime, 0, r, weight);

        if (newDrinkFlag) {
            num_drinks += drinkAmount;
            double newBac = HolicUtil.calcBAC(prevBAC, prevDrinkTime, drinkAmount, r, weight);
            addBac = newBac;

            // Send both to the server, using same time
            flagAddDrink = true;
            String formatStringOld = "type=add&drinktime=%s&drinkamount=%s&currbac=%.7f";
            String paramsOld = String.format(formatStringOld, System.currentTimeMillis(), 0, bac);
            new PutAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                    AccessToken.getCurrentAccessToken().getUserId(), paramsOld);

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
                contactname, firstname, lastname, threshold);

        if (textingEnabled && (BAC >= threshold) && !hasTexted && (num != null) && !num.isEmpty()) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("hasTexted", true);
            editor.apply();
            hasTexted = pref.getBoolean("hasTexted", false);

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
        if (!updateClick()) {
            return;
        }
        addDrinks(1);
    }

    public void addHalfDrink(View view) {
        if (!updateClick()) {
            return;
        }
        addDrinks(0.5);
    }

    /**
     * Adds drinks, recalculates and displays BAC.
     * @param drinks
     */
    public void addDrinks(double drinks) {
        drinkAmount = drinks;
        newDrinkFlag = true;
        loading.show();
        refreshDisplay();
    }


    public void refreshBAC(View view) {
        refreshDisplay();
    }

    /**
     * Called to end a session. User is prompted whether or not to save the session.
     */
    public void endSession(View view) {
        if (!updateClick()) {
            return;
        }
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
                        editor.putBoolean("inSession", false);
                        editor.apply();

                        // save a final bac
                        saveNight = true;
                        refreshDisplay();
                    }
                });

        builder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("hasTexted", false);
                        editor.putBoolean("inSession", false);
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

    public void undo(View view) {
        AlertDialog confirm;
        AlertDialog.Builder builder = new AlertDialog.Builder(DrinkLogActivity.this);

        builder.setMessage(R.string.undo_confirm_message);
        builder.setTitle(R.string.undo_confirm_title);
        builder.setCancelable(true);

        builder.setNeutralButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.setPositiveButton(R.string.undo,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String params = "type=remove";
                        new PutAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                                AccessToken.getCurrentAccessToken().getUserId(), params);
                    }
                });
        confirm = builder.create();
        confirm.show();
    }

    public void refreshDisplay() {
        disableButtons();
        new GetAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                AccessToken.getCurrentAccessToken().getUserId());
    }

    public void disableButtons() {
        plusOne.setEnabled(false);
        plusHalf.setEnabled(false);
        endButt.setEnabled(false);
        plusOne.setClickable(false);
        plusHalf.setClickable(false);
        endButt.setClickable(false);
    }
    public void enableButtons() {
        plusOne.setEnabled(true);
        plusHalf.setEnabled(true);
        endButt.setEnabled(true);
        plusOne.setClickable(true);
        plusHalf.setClickable(true);
        endButt.setClickable(true);
    }

    // Updates the last click time. Used for preventing double clicking of buttons while waiting
    // for server response.
    public boolean updateClick() {
        // Double-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_drinklog, menu);
//        return true;
//    }

    /**
     * Handles menu selection.
     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.reset_drink:
//                endSession();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

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

    //Async task to put new session info (e.g. set baccalcindex; add drinks)
    private class PutAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return RestClient.Put(url[0], url[1]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if (saveNight) {
                // callback for save night
                saveNight = false;
                DrinkLogActivity.this.finish();
                return;
            }
            if (flagAddDrink) {
                // callback for add 0 drink
                String formatStringNew = "type=add&drinktime=%s&drinkamount=%s&currbac=%.7f";
                String paramsNew = String.format(formatStringNew, System.currentTimeMillis(), drinkAmount, addBac);
                new PutAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                        AccessToken.getCurrentAccessToken().getUserId(), paramsNew);

                flagAddDrink = false;
            } else {
                // callback for undo
                refreshDisplay();
            }
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
                    int index = drinkLogs.length() - 1;

                    //calculate total number of drinks
                    double drinks = 0; // total number of drinks
                    for (int i = 0; i < drinkLogs.length(); i++) {
                        double amount = drinkLogs.getJSONObject(i).getDouble("drinkamount");
                        drinks += amount;
                    }
                    num_drinks = drinks;

                    // locally save prevBAC and prevDrinkTime
                    prevBAC = 0;
                    prevDrinkTime = 0;
                    try {
                        prevBAC = drinkLogs.getJSONObject(index).getDouble("currbac");
                        prevDrinkTime = drinkLogs.getJSONObject(index).getLong("drinktime");
                    } catch(Exception e) {}

                    if (saveNight) {
                        double bac = HolicUtil.calcBAC(prevBAC, prevDrinkTime, 0, r, weight);

                        String formatString = "type=end&drinktime=%s&drinkamount=%s&currbac=%.7f";
                        String params = String.format(formatString, System.currentTimeMillis(), 0, bac);
                        new PutAsyncTask().execute(getResources().getString(R.string.server_currsession) +
                                AccessToken.getCurrentAccessToken().getUserId(), params);
                    }

                    displayBAC();
                    displayDrinks();
                } else { // no such session! shouldn't happen, set to 0 in case
                    num_drinks = 0;
                    prevBAC = 0;
                    prevDrinkTime = 0;

                    displayBAC();
                    displayDrinks();
                }
            } catch(Exception e) {
            }
            enableButtons();
            loading.dismiss();
        }
    }
}
