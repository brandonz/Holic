package cos333.project_corgis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;

public class ViewProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Pull Holic saved name.
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        // 0 - for private mode
        String fname = pref.getString("fname", "");
        String lname = pref.getString("lname", "");
        int weight = pref.getInt("weight", 100);
        String gender = pref.getString("gender", "Male");

        TextView name = (TextView)findViewById(R.id.prof_name);
        name.setText(String.format("%s %s", fname, lname));
        TextView weightText = (TextView)findViewById(R.id.prof_weight);
        weightText.setText(Integer.toString(weight));
        TextView gendText = (TextView)findViewById(R.id.prof_gender);
        gendText.setText(gender);


        Profile currProf = Profile.getCurrentProfile();
        ImageView img= (ImageView) findViewById(R.id.imageView2);
        img.setImageURI(currProf.getProfilePictureUri(135, 139));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
            case R.id.action_profile:
                Intent intent = new Intent(this, EditProfile.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
