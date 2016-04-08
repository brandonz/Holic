package cos333.project_corgis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class DrinkLogActivity extends AppCompatActivity {
    // When BAC calculations are implemented this won't suffice.
    // We will need timestamps for each button press.
    private double num_drinks = 0;

    // Variables for BAC Calculation.
    // number of drinks since 0 BAC.
    private double bac_num_drinks = 0;
    // time of first drink.
    private long millis = 0;

    // User-entered weight. To be used in BAC calculations.
    private int weight;
    // User-entered gender. To be used in BAC calculations. Will have a value from
    // @strings/gender_choices
    private String gender;
    //global variable for BAC value
    private double BAC;

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

        Intent intent = getIntent();
        String s_weight = intent.getStringExtra(MainActivity.WEIGHT_MESSAGE);
        weight = s_weight.isEmpty() ? 0 : Integer.parseInt(s_weight);
        gender = intent.getStringExtra(MainActivity.BODY_TYPE_MESSAGE);

        displayDrinks();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Calculate the BAC level
     */
    private double calcBAC() {
        if (millis == 0)
            return 0;

        //use Widmark's equation
        // Equation taken from http://www.wsp.wa.gov/breathtest/docs/webdms/Studies_Articles/Widmarks%20Equation%2009-16-1996.pdf
        // C =  0.8 A z / (W r) - b dt
        double b = 0.00017; //kg/L/hr
        double dt = (double)(System.currentTimeMillis() - millis)/(1000*3600);
        double alc = 0.6 * bac_num_drinks; //fluid ounces of alcohol

        String genders[] = getResources().getStringArray(R.array.gender_choices);
        double r;
        if (gender.equals(genders[0]))
            r = 0.68; // L/kg
        else
            r = 0.55; // L/kg
        System.out.println(r);
        double C = Math.max(0.8 * alc / (weight * 16 * r) - b * dt, 0);
        if (C == 0)
            bac_num_drinks = 0;
        return C*100;
    }

    private void displayDrinks() {
        TextView textView = (TextView) findViewById(R.id.drinks_level);
        textView.setText(Double.toString(num_drinks));
    }

    private void displayBAC() {
        TextView textView = (TextView) findViewById(R.id.bac_level);
        textView.setText(String.format("%.3f", calcBAC()));
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
        if (bac_num_drinks == 0) {
            millis = System.currentTimeMillis();
        }
        num_drinks += drinks;
        bac_num_drinks += drinks;
        displayDrinks();
        displayBAC();
    }

    /**
     * Resets drink with no argument.
     */
    public void resetDrink() {
        num_drinks = 0;
        bac_num_drinks = 0;
        millis = 0;
        displayDrinks();
        displayBAC();
    }

    public void refreshBAC(View view) {
        displayBAC();
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
                resetDrink();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
