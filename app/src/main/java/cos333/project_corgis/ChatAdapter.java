package cos333.project_corgis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private ArrayList<String> logList;

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView userAdded;

        public ChatViewHolder(View view) {
            super(view);
            //TODO change R.id.date
            userAdded = (TextView) view.findViewById(R.id.date);
        }
    }

    public ChatAdapter(ArrayList<String> logs) {
        this.logList = logs;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO change stats_row
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_row, parent, false);

        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        String user = logList.get(position);
        holder.userAdded.setText(user);
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }
}

