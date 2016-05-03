package cos333.project_corgis.chat.activity;

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

    private ArrayList<People> bac;

    public class TwoFragmentViewHolder extends RecyclerView.ViewHolder {
        TextView names;
        TextView peopleBacs;

        public TwoFragmentViewHolder(View view) {
            super(view);
            names = (TextView)itemView.findViewById(R.id.name);
            peopleBacs = (TextView) itemView.findViewById(R.id.person_bac);
        }
    }

    public TwoFragmentAdapter(ArrayList<People> bacs) {
        this.bac = bacs;
    }

    @Override
    public TwoFragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.two_fragments_row, parent, false);

        return new TwoFragmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TwoFragmentViewHolder holder, int position) {
        holder.names.setText(bac.get(position).name);
        holder.peopleBacs.setText(String.valueOf(bac.get(position).bac));
    }

    @Override
    public int getItemCount() {
        return bac.size();
    }
}

