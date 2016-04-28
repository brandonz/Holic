package cos333.project_corgis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder> {

    private ArrayList<ArrayList<Drink>> logList;

    public class StatsViewHolder extends RecyclerView.ViewHolder {
        public TextView date, numDrinks, maxBAC;

        public StatsViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            numDrinks = (TextView) view.findViewById(R.id.num_drinks);
            maxBAC = (TextView) view.findViewById(R.id.bac);
        }
    }

    public StatsAdapter(ArrayList<ArrayList<Drink>> logs) {
        this.logList = logs;
    }

    @Override
    public StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_row, parent, false);

        return new StatsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatsViewHolder holder, int position) {
        ArrayList<Drink> log = logList.get(position);
        double max = 0;
        double numDrinks = 0;
        for (Drink drink : log) {
            if (drink.bac > max)
                max = drink.bac;
            numDrinks += drink.amount;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
        Date resultDate = new Date(log.get(0).time);
        String date = sdf.format(resultDate);

        holder.date.setText(date);
        holder.numDrinks.setText(String.format("Number of drinks: %.1f", numDrinks));
        holder.maxBAC.setText(String.format("Highest BAC: %.3f", max));
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }
}
