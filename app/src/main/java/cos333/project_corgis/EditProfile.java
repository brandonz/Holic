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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;

public class EditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String body_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Populate with saved Holic data
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String fname = pref.getString("fname", "");
        String lname = pref.getString("lname", "");
        int weight = pref.getInt("weight", 100); // default? should never go there
        String gender = pref.getString("gender", "Male"); // default? also problematic lol

        EditText firstname = (EditText) findViewById(R.id.fname_edit);
        firstname.setText(fname);
        EditText lastname = (EditText) findViewById(R.id.lname_edit);
        lastname.setText(lname);
        EditText weightEdit = (EditText) findViewById(R.id.edit_weight);
        weightEdit.setText(Integer.toString(weight));

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

        Spinner gender_spinner = (Spinner) findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> gender_adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_choices, android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(gender_adapter);
        gender_spinner.setOnItemSelectedListener(this);

        // set spinner to Holic saved value
        String genders[] = getResources().getStringArray(R.array.gender_choices);
        if (gender.equals(genders[0]))
            gender_spinner.setSelection(0);
        else
            gender_spinner.setSelection(1);
    }

    public void goBack(View view) {
        this.finish();
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
        // save stuff locally
        String id = AccessToken.getCurrentAccessToken().getUserId();
        EditText fnameEdit =(EditText) findViewById(R.id.fname_edit);
        EditText lnameEdit =(EditText) findViewById(R.id.lname_edit);
        String firstName = fnameEdit.getText().toString();
        String lastName = lnameEdit.getText().toString();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", id);
        editor.putString("fname", firstName);
        editor.putString("lname", lastName);
        editor.putInt("weight", Integer.parseInt(weight));
        editor.putString("gender", body_type);
        editor.apply();

        String formatString = "type=add&fbid=%s&fname=%s&lname=%s&weight=%s&gender=%s";
        String urlParameters = String.format(formatString, id, firstName, lastName, weight,
                toMF(body_type));
        new PutAsyncTask().execute(getResources().getString(R.string.server), urlParameters);

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

    //Async task for updating a user
    private class PutAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return RestClient.Put(url[0], url[1]);
        }
    }

}
