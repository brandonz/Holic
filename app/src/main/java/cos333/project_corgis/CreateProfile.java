package cos333.project_corgis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.facebook.AccessToken;
import com.facebook.Profile;

public class CreateProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public final static String WEIGHT_MESSAGE = "cos333.project_corgis.WEIGHT_MESSAGE";
    public final static String BODY_TYPE_MESSAGE = "cos333.project_corgis.BODY_TYPE_MESSAGE";
    private String body_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_profile);
        Profile currProf = Profile.getCurrentProfile();
        super.onCreate(savedInstanceState);
        EditText firstname = (EditText)findViewById(R.id.editText);
        firstname.setText(currProf.getFirstName());
        EditText lastname = (EditText)findViewById(R.id.editText2);
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
        Intent intent = new Intent(this, DrinkLogActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_weight);
        String weight = editText.getText().toString();
        if (weight.isEmpty() || Integer.parseInt(weight) == 0 || Integer.parseInt(weight) > 1000) {
            AlertDialog.Builder builder  = new AlertDialog.Builder(this);

            builder.setMessage("Please give a valid weight");
            builder.setTitle("Error Message");
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

        String formatString = "fbid=%s&fname=%s&lname=%s&weight=%s&gender=%s";
        String id = AccessToken.getCurrentAccessToken().getUserId();
        Profile prof = Profile.getCurrentProfile();
        String firstName = prof.getFirstName();
        String lastName = prof.getLastName();
        String urlParameters = String.format(formatString, id, firstName, lastName, weight,
                toMF(body_type));
        new PostAsyncTask().execute(getResources().getString(R.string.server), urlParameters);



        intent.putExtra(WEIGHT_MESSAGE, weight);
        intent.putExtra(BODY_TYPE_MESSAGE, body_type);
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
