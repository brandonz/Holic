package cos333.project_corgis;

import android.content.Intent;
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

public class LandingActivity extends AppCompatActivity {
    private String weight;
    private String gender;

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
        // Saves info to pass to Drink Logger.
        Intent intent = getIntent();
        weight = intent.getStringExtra(MainActivity.WEIGHT_MESSAGE);
        gender = intent.getStringExtra(MainActivity.BODY_TYPE_MESSAGE);
        System.out.println(weight);
        System.out.println(gender);
    }

    // TODO: Open the drink log activity. Modify the drink log activity to get gender and weight
    // from the server instead of the other page.
    public void startDrinkActivity(View view) {
        Intent intent = new Intent(this, DrinkLogActivity.class);
        intent.putExtra(MainActivity.WEIGHT_MESSAGE, weight);
        intent.putExtra(MainActivity.BODY_TYPE_MESSAGE, gender);
        startActivity(intent);
    }

    // TODO: Open the settings activity.
    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openLogout() {
        System.out.println("in logout");
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
