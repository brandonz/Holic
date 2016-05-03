package cos333.project_corgis.chat.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cos333.project_corgis.R;
import cos333.project_corgis.chat.activity.TwoFragment;

public class TwoFragmentAdapter extends RecyclerView.Adapter<TwoFragmentAdapter.TwoFragmentViewHolder> {

    private ArrayList<People> people;
    private Context mContext;

    public class TwoFragmentViewHolder extends RecyclerView.ViewHolder {
        TextView names;
        TextView peopleBacs;
        TextView nameTitle;
        TextView peopleBacTitle;

        public TwoFragmentViewHolder(View view) {
            super(view);

            names = (TextView)itemView.findViewById(R.id.name);
            peopleBacs = (TextView) itemView.findViewById(R.id.person_bac);
        }
    }

    public TwoFragmentAdapter(Context mContext, ArrayList<People> bac) {
        this.people = bac;
    }

    @Override
    public TwoFragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.two_fragments_row, parent, false);

        return new TwoFragmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TwoFragmentViewHolder holder, int position) {
//        holder.names.setText("Test");
//        holder.peopleBacs.setText("Test2");

        holder.names.setText(people.get(position).name);
        holder.peopleBacs.setText(String.format("%.3f",people.get(position).bac));
    }

    @Override
    public int getItemCount() {
        return people.size();
    }
}

