package com.sd.spartan.shc.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sd.spartan.shc.R;
import com.sd.spartan.shc.adapter.VolunteerListAdapter;
import com.sd.spartan.shc.constants.Constraints;
import com.sd.spartan.shc.model.Volunteer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolunteerListActivity extends AppCompatActivity {

    private RecyclerView mContactListRV ;
    private ImageButton mCancelBtnImg;
    private VolunteerListAdapter mContactListAdapter ;
    private List<Volunteer> mVolunteerList ;
    private EditText mSearchET ;
    private LinearLayout mLinearLayout ;
    private ProgressBar mProgressContact ;
    private TextView mRefreshTV ;
    private SwipeRefreshLayout mSwipeRefreshContact;
    private RelativeLayout mRelativeLayout ;

    private String  mLanguage;
    private MenuItem searchMenu ;
    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 2000 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_list);

        Toolbar toolbar = findViewById(R.id.volunteer_list_toolbar) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Volunteer") ;


        mLanguage = getIntent().getStringExtra(Constraints.LANGUAGE) ;

        initialize() ;
        LayoutItemClickListenner();

        mVolunteerList = new ArrayList<>() ;
        mContactListAdapter = new VolunteerListAdapter(getApplicationContext(), mVolunteerList, mLanguage ) ;
        mContactListRV.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        mContactListRV.setAdapter(mContactListAdapter);

        loadVolunteerList();

        AddTextChange();

        mSwipeRefreshContact.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        mSwipeRefreshContact.setOnRefreshListener(this::setAutoRefresh);

    }

    private void LayoutItemClickListenner() {
        mCancelBtnImg.setOnClickListener(v -> {
            searchMenu.setVisible(true);
            mLinearLayout.setVisibility(View.GONE);
            mSearchET.getText().clear();
        });
    }


    private void setAutoRefresh(){
        if(remainingRefreshTime<=0){
            if(countDownTimer!= null){
                countDownTimer.cancel();
            }
            return;
        }
        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(remainingRefreshTime, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mRelativeLayout.setVisibility(View.GONE);
                }

                @Override
                public void onFinish() {
                    mRelativeLayout.setVisibility(View.VISIBLE);
                    mSwipeRefreshContact.setRefreshing(false);
                    loadVolunteerList();
                    cancelAutoRefresh() ;
                }
            };

            countDownTimer.start() ;
        }
    }
    private void cancelAutoRefresh(){
        if(countDownTimer!= null){
            countDownTimer.cancel();
            countDownTimer=null;
        }
    }






    private void initialize() {
        mContactListRV = findViewById(R.id.recyclerview_contact_list) ;
        mSearchET = findViewById(R.id.search_contact_et) ;
        mLinearLayout = findViewById(R.id.linear_search) ;
        mProgressContact = findViewById(R.id.progress_contact) ;
        mRefreshTV = findViewById(R.id.text_refresh) ;
        mSwipeRefreshContact = findViewById(R.id.swipe_refresh_contact) ;
        mRelativeLayout = findViewById(R.id.relative_layout_contact) ;
        mCancelBtnImg = findViewById(R.id.imgbtn_cancel) ;
    }

    private void loadVolunteerList() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        @SuppressLint("NotifyDataSetChanged")
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constraints.GET_VOLUNTEERS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString(Constraints.ERROR).equalsIgnoreCase(Constraints.FALSE)){
                    mProgressContact.setVisibility(View.GONE);
                    mRefreshTV.setVisibility(View.GONE);
                    JSONArray jsonArray = object.getJSONArray(Constraints.DATA) ;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        mVolunteerList.add(new Volunteer(
                                jsonObject.getString(Constraints.ID),
                                jsonObject.getString(Constraints.NAME_BAN),
                                jsonObject.getString(Constraints.NAME_ENG),
                                jsonObject.getString(Constraints.ADDRESS_BAN),
                                jsonObject.getString(Constraints.ADDRESS_ENG),
                                jsonObject.getString(Constraints.PHONE)
                        ));

                    }
                    mContactListAdapter.notifyDataSetChanged();
                }
            } catch (JSONException ignored) {
            }
        }, error -> {
            mProgressContact.setVisibility(View.GONE);
            mRefreshTV.setVisibility(View.VISIBLE);
            if(mLanguage.equalsIgnoreCase(Constraints.LAN_ENG))
                Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_ENG, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_BAN, Toast.LENGTH_SHORT).show();
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);
        searchMenu  = menu.findItem(R.id.volunteer_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.volunteer_search){
            searchMenu.setVisible(false);
            mLinearLayout.setVisibility(View.VISIBLE);
            if(mLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
                mSearchET.setHint(Constraints.VOLUNTEER_SEARCH_BAN);
            }else if(mLanguage.equalsIgnoreCase(Constraints.LAN_ENG)){
                mSearchET.setHint(Constraints.VOLUNTEER_SEARCH_ENG);
            }
        }
        return false;
    }


    public void AddTextChange(){
        mSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }
    private void filter(String text) {
        List<Volunteer> filteredList = new ArrayList<>();
        for (Volunteer item : mVolunteerList) {
            if (item.getName_ban().toLowerCase().contains(text.toLowerCase()) && mLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
                filteredList.add(item);
            }else if (item.getName_eng().toLowerCase().contains(text.toLowerCase()) && mLanguage.equalsIgnoreCase(Constraints.LAN_ENG)){
                filteredList.add(item);
            }
        }
        mContactListAdapter.searchFilterList(filteredList);
    }
}