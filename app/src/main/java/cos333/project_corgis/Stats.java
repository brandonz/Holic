package cos333.project_corgis;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Stats extends AppCompatActivity {

    // Number of sessions from the past.
    private int numSessions;
    // Max bac;
    private double maxBAC;

    // Raw data of logs for each session.
    private ArrayList<ArrayList<Drink>> logs = new ArrayList<>();
    private RecyclerView recyclerView;
    private StatsAdapter sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
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


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        sAdapter = new StatsAdapter(logs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sAdapter);

        // async call to get the past data
        String id = AccessToken.getCurrentAccessToken().getUserId();
        new GetAsyncTask().execute(getResources().getString(R.string.server_pastsession) + id);
    }

    private void render() {
        sAdapter.notifyDataSetChanged();
        for (ArrayList<Drink> log : logs) {
            System.out.println(log.get(0).bac);
        }
        System.out.println("rendered?");
    }

    public class Drink {
        double amount;
        long time;
        double bac;
        public Drink(double amount, long time, double bac) {
            this.amount = amount;
            this.time = time;
            this.bac = bac;
        }
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
                    JSONObject session = obj.getJSONObject(0);
                    JSONArray histories = session.getJSONArray("drinklogs");
                    numSessions = histories.length();
                    for (int i = 0; i < numSessions; i++) {
                        // log represents a single session
                        JSONArray log = histories.getJSONObject(i).getJSONArray("log");
                        int logNumDrinks = log.length();
                        ArrayList<Drink> logArray = new ArrayList<>();
                        for (int j = 0; j < logNumDrinks; j++) {
                            JSONObject logDrink = log.getJSONObject(j);
                            Drink drink = new Drink(logDrink.getDouble("drinkamount"),
                                    logDrink.getLong("drinktime"), logDrink.getDouble("currbac"));
                            logArray.add(drink);
                            System.out.println("session added");
                        }
                        logs.add(logArray);
                    }
                    render();
                } else { // no past sessions?
                }
            } catch(Exception e) {
            }

        }
    }

}
