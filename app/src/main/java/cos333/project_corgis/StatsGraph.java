package cos333.project_corgis;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatsGraph extends AppCompatActivity {

    // All the Drinks for this session.
    private ArrayList<Drink> log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        log = intent.getParcelableArrayListExtra("session");

        // Set title to include date
        String date = formatDate(log.get(0).time);
        setTitle(String.format(getResources().getString(R.string.graph_title), date));

        populateGraph();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Formats the given time (from System.currentTimeMillis) as a date String
    public String formatDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.date_format));
        Date resultDate = new Date(time);
        return sdf.format(resultDate);
    }

    // Uses the log data to fill in the graph
    public void populateGraph() {
        int numData = log.size();
        DataPoint[] drinks = new DataPoint[numData];
        double sumDrinks = 0;
        DataPoint[] bac = new DataPoint[numData];
        double max = 0;
        for (int i = 0; i < numData; i++) {
            Drink drink = log.get(i);
            Date date = new Date(drink.time);

            // Running total of drinks in the night
            sumDrinks += drink.amount;
            DataPoint drinkPoint = new DataPoint(date, sumDrinks);
            //DataPoint drinkPoint = new DataPoint(drink.time, sumDrinks);
            drinks[i] = drinkPoint;

            // BAC level at time of drink
            // TODO: include bac decay
            DataPoint bacPoint = new DataPoint(date, drink.bac);
            //DataPoint bacPoint = new DataPoint(i, drink.bac);
            if (drink.bac > max) {
                max = drink.bac;
            }
            bac[i] = bacPoint;
        }

        // Initialize graphs
        GraphView drinkGraph = (GraphView) findViewById(R.id.drinkGraph);
        LineGraphSeries<DataPoint> drinkSeries = new LineGraphSeries<>(drinks);
        drinkGraph.addSeries(drinkSeries);
        drinkGraph.setTitle("Drinks");

        GraphView bacGraph = (GraphView) findViewById(R.id.bacGraph);
        LineGraphSeries<DataPoint> bacSeries = new LineGraphSeries<>(bac);
        bacGraph.addSeries(bacSeries);
        bacGraph.setTitle("BAC");

        // set date label formatter
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        DateAsXAxisLabelFormatter formatter = new DateAsXAxisLabelFormatter(getApplicationContext(), sdf);
        drinkGraph.getGridLabelRenderer().setLabelFormatter(formatter);
        drinkGraph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
        bacGraph.getGridLabelRenderer().setLabelFormatter(formatter);
        bacGraph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space

        // set manual x bounds to have nice steps
//        graph.getViewport().setMinX(log.get(0).time);
//        graph.getViewport().setMaxX(log.get(numData - 1).time);
//        graph.getViewport().setXAxisBoundsManual(true);

        // Set Y axis bounds
        drinkGraph.getViewport().setMinY(0);
        drinkGraph.getViewport().setMaxY(sumDrinks);
        drinkGraph.getViewport().setYAxisBoundsManual(true);
        bacGraph.getViewport().setMinY(0);
        bacGraph.getViewport().setMaxY(max);
        bacGraph.getViewport().setYAxisBoundsManual(true);
    }

}
