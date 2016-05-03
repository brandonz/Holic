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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cos333.project_corgis.HolicUtil;
import cos333.project_corgis.R;
import cos333.project_corgis.RestClient;
import cos333.project_corgis.chat.app.MyApplication;
import cos333.project_corgis.chat.model.Message;
import cos333.project_corgis.chat.model.User;

public class TwoFragment extends Fragment {

    /** Required Overrides for Sample Fragments */


    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<Double> bac = new ArrayList<>();
    private ArrayList<People> people;
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);

        chatRoomId = ((ChatRoomActivity)getActivity()).getChatRoomId();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view2);
        TextView nameTitle = (TextView) rootView.findViewById(R.id.name_title);
        TextView peopleBacTitle = (TextView) rootView.findViewById(R.id.person_bac_title);
        nameTitle.setText("USER");
        peopleBacTitle.setText("BAC");

        people = new ArrayList<>();

        fAdapter = new TwoFragmentAdapter(getActivity(), people);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fAdapter);

//        new GetAsyncTask().execute(getResources().getString(R.string.server_bac) + chatRoomId);
        fetchBAC();

        return rootView;

    }

    public ArrayList<People> fillList() {
        ArrayList<People> peoples = new ArrayList<>();
        for (int j = 0; j < names.size(); j++) {
            peoples.add(new People(names.get(j),bac.get(j)));
        }
        return peoples;
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
                System.out.println("in exception");
            }

        }
    }

    private void fetchBAC() {
        String endPoint = "https://holic-server.herokuapp.com/api/chats/bac/" + chatRoomId;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TwoFragment", "response: " + response);

                try {
                    JSONArray obj = new JSONArray(response);

                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject personObj = (JSONObject) obj.get(i);

                        String name = personObj.getString("name");
                        JSONObject bacObj = personObj.getJSONObject("bac");
                        long drinktime = bacObj.getLong("drinktime");
                        double currbac = bacObj.getDouble("currbac");

                        currbac = HolicUtil.calcBAC(currbac,drinktime,0,1,1);

                        People person = new People(name, currbac);

                        people.add(person);
                    }

                    fAdapter.notifyDataSetChanged();
                    if (fAdapter.getItemCount() > 1) {
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, fAdapter.getItemCount() - 1);
                    }

                } catch (JSONException e) {
                    Log.e("TwoFragment", "json parsing error: " + e.getMessage());
                    Toast.makeText(getActivity().getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e("TwoFragment", "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getActivity().getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

}
