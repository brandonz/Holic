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

    private ArrayList<String> newPeople;

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView userAdded;

        public ChatViewHolder(View view) {
            super(view);
            userAdded = (TextView) view.findViewById(R.id.name);
        }
    }

    public ChatAdapter(ArrayList<String> people) {
        this.newPeople = people;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_add_person, parent, false);

        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        String user = newPeople.get(position);
        holder.userAdded.setText(user);
    }

    @Override
    public int getItemCount() {
        return newPeople.size();
    }
}

