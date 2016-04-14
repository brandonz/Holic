package cos333.project_corgis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.facebook.Profile;

public class CreateProfile extends AppCompatActivity {
    public final static String WEIGHT_MESSAGE = "cos333.project_corgis.WEIGHT_MESSAGE";
    public final static String BODY_TYPE_MESSAGE = "cos333.project_corgis.BODY_TYPE_MESSAGE";
    private String body_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Profile currProf = Profile.getCurrentProfile();
        super.onCreate(savedInstanceState);
        EditText firstname = (EditText)findViewById(R.id.editText);
        firstname.setText(currProf.getFirstName());
        EditText lastname = (EditText)findViewById(R.id.editText2);
        lastname.setText(currProf.getLastName());
        setContentView(R.layout.activity_create_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


        intent.putExtra(WEIGHT_MESSAGE, weight);
        intent.putExtra(BODY_TYPE_MESSAGE, body_type);
        startActivity(intent);
    }

}
