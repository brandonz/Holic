package cos333.project_corgis.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cos333.project_corgis.HolicUtil;
import cos333.project_corgis.R;
import cos333.project_corgis.RestClient;

public class TwoFragment extends Fragment {

    /** Required Overrides for Sample Fragments */


    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<Double> bac = new ArrayList<>();
    private ArrayList<People> people = new ArrayList<>();
    private RecyclerView recyclerView;
    private TwoFragmentAdapter fAdapter;
    private Intent intent;
    private String chatRoomId;

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent currIntent = getActivity().getIntent();
        chatRoomId = currIntent.getStringExtra("chat_room_id");
        new GetAsyncTask().execute(getResources().getString(R.string.server_bac) + chatRoomId);


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        fAdapter = new TwoFragmentAdapter(people);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fAdapter);

        return rootView;

    }

    public ArrayList<People> fillList() {
        ArrayList<People> people = new ArrayList<>();
        for (int j = 0; j < names.size(); j++) {
            people.add(new People(names.get(j),bac.get(j)));
        }
        return people;
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
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject currUser = obj.getJSONObject(i);
                        names.add(currUser.getString("name"));

                        JSONObject currUserBAC = currUser.getJSONObject("bac");
                        double currbac = currUserBAC.getDouble("currbac");
                        long drinktime = currUserBAC.getLong("drinktime");
                        bac.add(HolicUtil.calcBAC(currbac, drinktime, 0, 0, 0));
                    }
                    people = fillList();
                    }
                else { // no past sessions?
                }

            } catch(Exception e) {
            }

        }
    }
}
