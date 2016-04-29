package cos333.project_corgis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.facebook.AccessToken;
import com.facebook.Profile;

public class CreateProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String body_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        Profile currProf = Profile.getCurrentProfile();
        EditText firstname = (EditText)findViewById(R.id.edit_fname);
        firstname.setText(currProf.getFirstName());
        EditText lastname = (EditText)findViewById(R.id.edit_lname);
        lastname.setText(currProf.getLastName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        Spinner gender_spinner = (Spinner) findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> gender_adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_choices, android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(gender_adapter);
        gender_spinner.setOnItemSelectedListener(this);
    }

    public void sendInfo(View view) {
        Intent intent = new Intent(this, LandingActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_weight);
        String weight = editText.getText().toString();
        if (weight.isEmpty() || Integer.parseInt(weight) == 0 || Integer.parseInt(weight) > 1000) {
            AlertDialog.Builder builder  = new AlertDialog.Builder(this);

            builder.setMessage(R.string.enter_valid_weight);
            builder.setTitle(R.string.error_message);
            builder.setCancelable(true);

            builder.setPositiveButton(
                    R.string.okay,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();

            return;
        }
        
        String id = AccessToken.getCurrentAccessToken().getUserId();
        EditText editText2 = (EditText) findViewById(R.id.edit_fname);
        String firstName = editText2.getText().toString();
        EditText editText3 = (EditText) findViewById(R.id.edit_lname);
        String lastName = editText3.getText().toString();
        EditText editText4 = (EditText) findViewById(R.id.edit_contact_name);
        String contactName = editText4.getText().toString();
        EditText editText5 = (EditText) findViewById(R.id.edit_contact_num);
        String contactNum = editText5.getText().toString();
        EditText thresholdEdit = (EditText) findViewById(R.id.edit_threshold);
        String threshold = thresholdEdit.getText().toString();

        // Check for required threshold fields
        CheckBox checkBox = (CheckBox) findViewById(R.id.enable_texting);
        if (checkBox.isChecked()) {
            if (threshold.isEmpty()) {
                AlertDialog.Builder builder  = new AlertDialog.Builder(this);

                builder.setTitle(R.string.error_message);
                builder.setMessage(R.string.enter_threshold);
                builder.setCancelable(true);

                builder.setPositiveButton(
                        R.string.okay,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
                return;
            }
            if (contactNum.isEmpty()) {
                AlertDialog.Builder builder  = new AlertDialog.Builder(this);

                builder.setTitle(R.string.error_message);
                builder.setMessage(R.string.enter_number);
                builder.setCancelable(true);

                builder.setPositiveButton(
                        R.string.okay,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
                return;
            }
        }

        // Save stuff locally
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", id);
        editor.putString("fname", firstName);
        editor.putString("lname", lastName);
        editor.putInt("weight", Integer.parseInt(weight));
        editor.putString("gender", body_type);
        editor.putString("contact", contactName);
        editor.putString("contactnum", contactNum);
        editor.putBoolean("textingEnabled", checkBox.isChecked());
        editor.putFloat("threshold", Float.parseFloat(threshold)); //only saved locally
        editor.apply();

        // Send info to the server
        String formatString = "fbid=%s&fname=%s&lname=%s&weight=%s&gender=%s&contactname=%s&contactnumber=%s";
        String urlParameters = String.format(formatString, id, firstName, lastName, weight,
                toMF(body_type), contactName, contactNum);
        new PostAsyncTask().execute(getResources().getString(R.string.server), urlParameters);
        // create empty pastsession
        String idParam = String.format("fbid=%s", id);
        new PostAsyncTask().execute(getResources().getString(R.string.server_pastsession), idParam);

        finish();
        startActivity(intent);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        body_type = (String) parent.getItemAtPosition(position);
    }

    private String toMF(String gender) {
        String genders[] = getResources().getStringArray(R.array.gender_choices);
        if (gender.equals(genders[0]))
            return "M";
        else
            return "F";
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Async task for post
    private class PostAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return RestClient.Post(url[0], url[1]);
        }
    }

}
