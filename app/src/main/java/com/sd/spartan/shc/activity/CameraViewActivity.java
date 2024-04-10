package com.sd.spartan.shc.activity;

import static com.sd.spartan.shc.constants.Constraints.CONFIRMSTATE;
import static com.sd.spartan.shc.constants.Constraints.CONFIRM_SHARED;
import static com.sd.spartan.shc.constants.Constraints.LANG_SHARED;
import static com.sd.spartan.shc.constants.Constraints.NO_NAME_DEFINED;
import static com.sd.spartan.shc.constants.Constraints.PLZ_WAIT;
import static com.sd.spartan.shc.constants.Constraints.UPLOADING;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sd.spartan.shc.BuildConfig;
import com.sd.spartan.shc.R;
import com.sd.spartan.shc.adapter.SnakeNameViewAdapter;
import com.sd.spartan.shc.constants.Constraints;
import com.sd.spartan.shc.interfaces.OnClickInt;
import com.sd.spartan.shc.model.SnakeName;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CameraViewActivity extends AppCompatActivity {
    private ProgressDialog loading ;
    private FusedLocationProviderClient fusedLocationProviderClient ;
    private double latitude = 0, longitude = 0 ;
    private boolean locationState = false ;

    private ImageView mSanckImage ;
    private String sliceText, imageCropName ;
    private Bitmap bitmap ;

    private CardView mCardViewLocationName03, mCardViewLocationName04 ;
    private TextView mQuestionTV01, mQuestionTV02, mDateTV,  mClickText, mSubDistrictText, mDistrictText, mSnakeNameTV ;
    private LinearLayout mThanaZillaLinear ;
    private RadioGroup radioGroupQuestion01, radioGroupQuestion02 ;
    private String mRadioGroupText01, mRadioGroupText02, mPlaceName, mPhnNumber ;
    private TextInputLayout mPhoneNumberTextInput, mLocationNameTextInput ;
    private TextInputEditText mEditTextPhoneNumber, mEditTextLocationName ;
    private RadioButton rb1, rb2, rb3, rb4, rb5 ;
    private Button mSubmitBtn ;

    private AlertDialog.Builder dialogBuilder ;
    private AlertDialog dialog ;
    private RelativeLayout mPopupRelative ;
    private Button btnOk, btnCancel ;
    private TextView mHeadingTextPopup, mMainTextPopup ;
    private boolean confirmState = false, languageState = false ;
    private final String stateConfirm = "confirmState";
    private final String stateLanguage = "languageState";
    private String preferLanguage ;
    private String getStateConfirm;
    private SharedPreferences sharedPreferencesConfirm,  sharedPreferencesLanguage ;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss", Locale.ENGLISH);
    private String currentDateTime ;

    //popup
    private TextInputLayout mSearchTextLayout;
    private TextInputEditText mSearchEditText ;
    private ImageView mFullImage ;
    private ListView mSearchListView ;
    private SnakeNameViewAdapter mSnakeNameViewAdapter ;

    private ArrayAdapter adapter ;
    private ArrayList<String> mZillaList ;
    private ArrayList<String> mThanaList ;
    private ArrayList<Integer> mThanaIDList ;
    private ArrayList<SnakeName> mSnakeList ;

    private final String[] selcetPrev = {""} ;

    private int currentYear, currentMonth, currentDay ;
    private String mSelectDate="" ;

    private MenuItem languageMenuItem ;
    private boolean banEngBool = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE) ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        Toolbar toolbar = findViewById(R.id.camera_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(R.string.app_name_inside);

        sharedPreferencesConfirm = getSharedPreferences(CONFIRM_SHARED, MODE_PRIVATE);
        sharedPreferencesLanguage = getSharedPreferences(LANG_SHARED, MODE_PRIVATE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        Initialization();
        Calendar currentCalendar = Calendar.getInstance();
        currentYear = currentCalendar.get(Calendar.YEAR);
        currentMonth = currentCalendar.get(Calendar.MONTH);
        currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        mSanckImage.setOnClickListener(v -> CropImage.startPickImageActivity(CameraViewActivity.this));

        radioGroupQuestion01.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = (RadioButton) group.findViewById(checkedId);
            if (null != rb && checkedId > -1) {
                mRadioGroupText01 = (String) rb.getText();
                if (mRadioGroupText01.equalsIgnoreCase(getResources().getString(R.string.yes_rb)) || mRadioGroupText01.equalsIgnoreCase(Constraints.YES) ) {
                    mRadioGroupText01 = Constraints.YES ;
                } else {
                    mRadioGroupText01 = Constraints.NO;
                }
            }
        });


        dialogBuilder = new AlertDialog.Builder(CameraViewActivity.this) ;
        mZillaList = new ArrayList<>() ;
        mThanaList = new ArrayList<>() ;
        mThanaIDList = new ArrayList<>() ;
        mSnakeList = new ArrayList<>() ;
        loadSnakeNameList(preferLanguage, false);

        getStateConfirm = sharedPreferencesConfirm.getString(CONFIRMSTATE, NO_NAME_DEFINED);
        confirmState = Boolean.parseBoolean(getStateConfirm);
        if (!confirmState) {
            createPopupLanguage();
        }

        mSubmitBtn.setOnClickListener(v-> onSubmitClick());
        mDistrictText.setOnClickListener(v-> onDistrictClick());
        mSubDistrictText.setOnClickListener(v-> onSubDistrictClick());


    }

    private void getDirectLocationPick() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(CameraViewActivity.this).checkLocationSettings(builder.build());


        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(
                                    CameraViewActivity.this,
                                    LocationRequest.PRIORITY_HIGH_ACCURACY);


                        } catch (IntentSender.SendIntentException | ClassCastException ignored) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    private void onSubmitClick() {
        currentDateTime = dateFormat.format(new Date());
        getLastLocation();
        if (latitude <= 0) {
            locationState = false;
            Toast.makeText(this, Constraints.TURN_LOCATION_TOAST, Toast.LENGTH_SHORT).show();
        }
        mPhnNumber = Objects.requireNonNull(mEditTextPhoneNumber.getText()).toString();
        mPlaceName = Objects.requireNonNull(mEditTextLocationName.getText()).toString();

        if (locationState) {
            if (sliceText != null) {
                if (sliceText.equals(Constraints.CONTENT)) {
                    if (mRadioGroupText01 == null || mRadioGroupText02 == null || mPlaceName.equalsIgnoreCase("") ||
                            mSubDistrictText.getTag()==null || mSnakeNameTV.getTag()==null ||
                            mSelectDate.equalsIgnoreCase("") ) {
                        Toast.makeText(CameraViewActivity.this, Constraints.FILL_ALL_FIELDS_TOAST, Toast.LENGTH_SHORT).show();
                    } else {
                         InsertUpdateData();
                    }
                } else if (sliceText.equals(Constraints.FILE)) {
                    mSubDistrictText.setTag(null);
                    mPlaceName="";
                    mSelectDate="";

                    if (mRadioGroupText01 == null || mRadioGroupText02 == null || mSnakeNameTV.getTag()==null ) {
                        Toast.makeText(CameraViewActivity.this, Constraints.FILL_ALL_FIELDS_TOAST, Toast.LENGTH_SHORT).show();
                    } else {
                        InsertUpdateData();
                    }
                }
            } else {
                Toast.makeText(CameraViewActivity.this, Constraints.UPLOAD_IMAGE_TOAST, Toast.LENGTH_SHORT).show();
            }
        } else {
            getLastLocation();
        }
    }


    private void InsertUpdateData() {
        loading.setTitle(UPLOADING);
        loading.setMessage(PLZ_WAIT);
        loading.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.INSERT_SHC_DATA, response -> {
            loading.dismiss();
            Toast.makeText(getApplicationContext(), Constraints.SUBMIT_SUCCESS, Toast.LENGTH_SHORT).show();
            UnSelectAll(preferLanguage);
        }, error -> {
            loading.dismiss();
            if(preferLanguage.equalsIgnoreCase(Constraints.LAN_ENG))
                Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_ENG, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_BAN, Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                if(mPhnNumber.length() != 11){
                    mPhnNumber = 0+"" ;
                }

                String image = getStringImage(bitmap);
                Map<String, String> parameters = new HashMap<>();
                parameters.put(Constraints.AUTH_TOKEN_NAME, Constraints.AUTH_TOKEN);
                parameters.put(Constraints.QUESTION_1, mRadioGroupText01);
                parameters.put(Constraints.QUESTION_2, mRadioGroupText02);
                parameters.put(Constraints.PLACE, mPlaceName);
                parameters.put(Constraints.SUB_DISTRICT_ID, mSubDistrictText.getTag()+"");
                parameters.put(Constraints.SNAKE_ID, mSnakeNameTV.getTag()+"");
                parameters.put(Constraints.PHONE, mPhnNumber);
                parameters.put(Constraints.DATE, mSelectDate);
                parameters.put(Constraints.LATITUDE, String.valueOf(latitude));
                parameters.put(Constraints.LONGITUDE, String.valueOf(longitude));
                parameters.put(Constraints.IMAGE, image );
                parameters.put(Constraints.IMAGENAME, imageCropName);
                parameters.put(Constraints.CURRENT_DATE_TIME, currentDateTime);
//                parameters.put(Constraints.PATH, Constraints.MAIN_URL);
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }


    private void popupLayoutInitialize(View view) {
        mPopupRelative = view.findViewById(R.id.relative_popup);
        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        mHeadingTextPopup =  view.findViewById(R.id.heading_text_popup);
        mMainTextPopup =  view.findViewById(R.id.main_text_popup);
    }

    private void sharedSaved(SharedPreferences sharedPreferences, String state, boolean memberState) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(state, String.valueOf(memberState));
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constraints.REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                locationState = true;
            } else {
                locationState = false;
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        switch (v.getId()) {
            case R.id.radio_btn_01:
                if (checked)
                    rb1.setTypeface(null, Typeface.BOLD_ITALIC);
                rb2.setTypeface(null, Typeface.NORMAL);
                rb3.setTypeface(null, Typeface.NORMAL);
                rb4.setTypeface(null, Typeface.NORMAL);
                break;

            case R.id.radio_btn_02:
                if (checked)
                    rb2.setTypeface(null, Typeface.BOLD_ITALIC);
                rb1.setTypeface(null, Typeface.NORMAL);
                rb3.setTypeface(null, Typeface.NORMAL);
                rb4.setTypeface(null, Typeface.NORMAL);
                break;

            case R.id.radio_btn_03:
                if (checked){
                    mRadioGroupText02 = Constraints.DEAD;
                    rb3.setTypeface(null, Typeface.BOLD_ITALIC);
                    rb4.setChecked(false);
                    rb5.setChecked(false);
                }

                rb1.setTypeface(null, Typeface.NORMAL);
                rb2.setTypeface(null, Typeface.NORMAL);
                rb4.setTypeface(null, Typeface.NORMAL);
                rb5.setTypeface(null, Typeface.NORMAL);
                break;

            case R.id.radio_btn_04:
                if (checked){
                    mRadioGroupText02 = Constraints.ALIVED;
                    rb4.setTypeface(null, Typeface.BOLD_ITALIC);
                    rb3.setChecked(false);
                    rb5.setChecked(false);
                }
                rb1.setTypeface(null, Typeface.NORMAL);
                rb2.setTypeface(null, Typeface.NORMAL);
                rb3.setTypeface(null, Typeface.NORMAL);
                rb5.setTypeface(null, Typeface.NORMAL);
                break;
            case R.id.radio_btn_05:
                if (checked){
                    mRadioGroupText02 = Constraints.RESCUED;
                    rb5.setTypeface(null, Typeface.BOLD_ITALIC);
                    rb3.setChecked(false);
                    rb4.setChecked(false);
                }
                rb1.setTypeface(null, Typeface.NORMAL);
                rb2.setTypeface(null, Typeface.NORMAL);
                rb3.setTypeface(null, Typeface.NORMAL);
                rb4.setTypeface(null, Typeface.NORMAL);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            String imageUriString = String.valueOf(imageUri);
            sliceText = imageUriString.substring(0, imageUriString.indexOf(":"));

            if (!CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                startCrop(imageUri);
            }
            ViewVisibility();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = Objects.requireNonNull(result).getUri();
                String resultUriString = String.valueOf(resultUri);
                imageCropName = resultUriString.substring(resultUriString.indexOf("cache/") + 6);

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mSanckImage.setImageBitmap(bitmap);

            }
        }
    }

    public void ViewVisibility() {
        mClickText.setVisibility(View.GONE);
        if (sliceText.equals(Constraints.CONTENT) ) {
            mCardViewLocationName03.setVisibility(View.VISIBLE);
            mCardViewLocationName04.setVisibility(View.VISIBLE);
            mThanaZillaLinear.setVisibility(View.VISIBLE);
        } else if (sliceText.equals(Constraints.FILE) ) {
            mCardViewLocationName03.setVisibility(View.GONE);
            mCardViewLocationName04.setVisibility(View.GONE);
            mThanaZillaLinear.setVisibility(View.GONE);
        }
    }

    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle(getResources().getString(R.string.app_name_inside))
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(500, 500)
//                .setCropMenuCropButtonIcon(R.mipmap.green_snack)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    private void Initialization() {
        mSanckImage = findViewById(R.id.mSnakeImage);
        mCardViewLocationName03 = findViewById(R.id.card_question_03);
        mCardViewLocationName04 = findViewById(R.id.card_question_04);
        mThanaZillaLinear = findViewById(R.id.linear_layout_thanazilla);

        mQuestionTV01 = findViewById(R.id.text_question_01);
        mQuestionTV02 = findViewById(R.id.text_question_02);
        mDateTV = findViewById(R.id.text_date);
        mSnakeNameTV = findViewById(R.id.text_snake_name);
        radioGroupQuestion01 = findViewById(R.id.radio_group_question_01);
        radioGroupQuestion02 = findViewById(R.id.radio_group_question_02);

        mPhoneNumberTextInput = findViewById(R.id.text_input_phone);
        mEditTextPhoneNumber = findViewById(R.id.edit_input_phone);
        mLocationNameTextInput = findViewById(R.id.text_input_location_name);
        mEditTextLocationName = findViewById(R.id.edit_input_location_name);

        mClickText = findViewById(R.id.text_click);
        mSubDistrictText = findViewById(R.id.text_sub_district_name);
        mDistrictText = findViewById(R.id.text_zilla_name);

        rb1 = findViewById(R.id.radio_btn_01);
        rb2 = findViewById(R.id.radio_btn_02);
        rb3 = findViewById(R.id.radio_btn_03);
        rb4 = findViewById(R.id.radio_btn_04);
        rb5 = findViewById(R.id.radio_btn_05);
        mSubmitBtn = findViewById(R.id.btn_submit);

        mSubDistrictText.setText(Constraints.THANA_NAME);
        mDistrictText.setText(Constraints.DISTRICT_NAME);
        loading = new ProgressDialog(this);
    }

    public void UnSelectAll( String language) {
        mSanckImage.setImageResource(R.mipmap.image_icon);
        radioGroupQuestion01.clearCheck();
        radioGroupQuestion02.clearCheck();
        rb3.setChecked(false);
        rb4.setChecked(false);
        rb5.setChecked(false);
        rb3.setTypeface(null, Typeface.NORMAL);
        rb4.setTypeface(null, Typeface.NORMAL);
        rb5.setTypeface(null, Typeface.NORMAL);
        mRadioGroupText01=null;
        mRadioGroupText02=null;
        mEditTextPhoneNumber.setText("");
        mEditTextLocationName.setText("");
        mSubDistrictText.setText(Constraints.THANA_NAME);
        mDistrictText.setText(Constraints.DISTRICT_NAME);
        mPlaceName="";
        mSelectDate="";
        sliceText=null;
        mSubDistrictText.setTag(null);
        mSnakeNameTV.setTag(null);
        latitude=0 ;
        longitude=0 ;

        if(language.equalsIgnoreCase(Constraints.LAN_ENG)){
            mDateTV.setText(getResources().getString(R.string.q_date_eng));
            mSnakeNameTV.setText(getResources().getString(R.string.q_think_eng));
        }else if(language.equalsIgnoreCase(Constraints.LAN_BAN)){
            mDateTV.setText(getResources().getString(R.string.q_date_ban));
            mSnakeNameTV.setText(getResources().getString(R.string.q_think_ban));
        }

        mCardViewLocationName03.setVisibility(View.GONE);
        mCardViewLocationName04.setVisibility(View.GONE);
        mThanaZillaLinear.setVisibility(View.GONE);
        mClickText.setVisibility(View.VISIBLE);
    }

    private void getLastLocation() {
        if (checkPermissions()) {
            getDirectLocationPick() ;
            if (isLocationEnabled()) {
                locationState = true;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                });
            } else {
                locationState = false;
            }
        } else {
            requestPermissions();
        }

    }

    private void requestNewLocationData() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude() ;
            longitude=mLastLocation.getLongitude() ;
        }
    };
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, Constraints.REQUEST_CODE);
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }




    public void onSubDistrictClick() {
        String zillaText = mDistrictText.getText().toString().trim() ;
        if(zillaText.equalsIgnoreCase(Constraints.DISTRICT_NAME) ){
            Toast.makeText(this, Constraints.SELECT_DISTRICT_BAN, Toast.LENGTH_SHORT).show();
        }else if( zillaText.equalsIgnoreCase(Constraints.DISTRICT) ){
            Toast.makeText(this, Constraints.SELECT_DISTRICT_ENG, Toast.LENGTH_SHORT).show();
        }else{
            CreateDistrictThanaPopUpDialog(Constraints.THANA);
        }
    }
    public void onDistrictClick() {
        CreateDistrictThanaPopUpDialog(Constraints.DISTRICT);
    }

    private void CreateDistrictThanaPopUpDialog(String name) {
        mThanaList.clear();
        mZillaList.clear();
        mThanaIDList.clear();
        View view = getLayoutInflater().inflate(R.layout.layout_name_popup, null);
        mSearchTextLayout = view.findViewById(R.id.text_search_name_popup) ;
        mSearchEditText = view.findViewById(R.id.edit_search_name_popup) ;
        mFullImage = view.findViewById(R.id.image_full_popup) ;
        mSearchListView = view.findViewById(R.id.listview_popup);
        RecyclerView mSearchRecyclerView = view.findViewById(R.id.recycler_popup);

        if(preferLanguage.equals(Constraints.LAN_ENG)){
            mSearchTextLayout.setHint(Constraints.SNAKE_NAME_ENG);
        }else{
            mSearchTextLayout.setHint(Constraints.SNAKE_NAME_BAN);
        }

        switch (name){
            case Constraints.THANA:
                mSearchTextLayout.setVisibility(View.GONE);
                mSearchListView.setVisibility(View.VISIBLE);
                mSearchRecyclerView.setVisibility(View.GONE);
                loadSubDistrictList(mDistrictText.getTag().toString().trim(), preferLanguage);

                mSearchListView.setOnItemClickListener((parent, view1, position, id) -> {
                    mSubDistrictText.setText((String) parent.getItemAtPosition(position));
                    mSubDistrictText.setTag(mThanaIDList.get(position));
                    dialog.cancel();
                });
                break;
            case Constraints.DISTRICT:
                mSearchListView.setVisibility(View.VISIBLE);
                mSearchRecyclerView.setVisibility(View.GONE);
                loadDIstrictList(preferLanguage);

                mSearchListView.setOnItemClickListener((parent, view12, position, id) -> {
                    String  currentSelectItemName = (String) parent.getItemAtPosition(position);
                    if(!currentSelectItemName.equalsIgnoreCase(selcetPrev[0])){
                        selcetPrev[0] =currentSelectItemName;
                        if(preferLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
                            mSubDistrictText.setText(Constraints.THANA_NAME);
                        }else{
                            mSubDistrictText.setText(Constraints.THANA);
                        }
                    }
                    mDistrictText.setText(currentSelectItemName);
                    mDistrictText.setTag(position+1);
                    dialog.cancel();
                });

                mSearchEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        adapter.getFilter().filter(s.toString());
                    }
                });
                break;
            case Constraints.SNAKE_NAME:
                mSearchListView.setVisibility(View.GONE);
                mFullImage.setVisibility(View.GONE);
                mSearchRecyclerView.setVisibility(View.VISIBLE);

                OnClickInt onClickInt = (view13, id, name1, type) -> {
                    if (!type) {
                        mSnakeNameTV.setText(name1);
                        mSnakeNameTV.setTag(Integer.valueOf(id));
                        dialog.cancel();
                    } else {
                        mFullImage.setVisibility(View.VISIBLE);
                        mSearchTextLayout.setVisibility(View.GONE);
                        if (name1.equalsIgnoreCase("")) {
                            mFullImage.setVisibility(View.GONE);
                            mSearchTextLayout.setVisibility(View.VISIBLE);
                        } else {
                            Picasso.get().load(name1).placeholder(R.drawable.stoke_border_both).error(R.drawable.stoke_border_both).into(mFullImage);
                        }
                    }

                };

                mSnakeNameViewAdapter = new SnakeNameViewAdapter(CameraViewActivity.this, mSnakeList, preferLanguage, onClickInt) ;
                mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(CameraViewActivity.this));
                mSearchRecyclerView.setAdapter(mSnakeNameViewAdapter);

                if(mSnakeList.size()==0){
                    loadSnakeNameList(preferLanguage, true);
                }
                AddTextChange();

                mFullImage.setOnClickListener(v -> {
                    mFullImage.setVisibility(View.GONE);
                    mSearchTextLayout.setVisibility(View.VISIBLE);
                });

                break;
            default:
                break;
        }
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
    }



    private void loadSubDistrictList(String district_id, String language) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.GET_SUB_DISTRICT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString(Constraints.ERROR).equalsIgnoreCase(Constraints.FALSE)){
                    JSONArray grpArray = object.getJSONArray(Constraints.DATA) ;
                    for(int i=0; i<grpArray.length(); i++){
                        JSONObject mGrpObject;
                        try {
                            mGrpObject = grpArray.getJSONObject(i);
                            if(language.equalsIgnoreCase(Constraints.LAN_BAN)){
                                mThanaList.add(mGrpObject.getString(Constraints.NAME_BAN)) ;
                            }else{
                                mThanaList.add(mGrpObject.getString(Constraints.NAME_ENG)) ;
                            }
                            mThanaIDList.add(Integer.valueOf(mGrpObject.getString(Constraints.ID))) ;
                        } catch (JSONException ignored) {
                        }
                    }
                    adapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.list_black_text, R.id.list_content, mThanaList);
                    mSearchListView.setAdapter(adapter);
                }

            } catch (JSONException ignored) {
            }
        }, error -> {
            if(language.equalsIgnoreCase(Constraints.LAN_ENG)){
                Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_ENG, Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_BAN , Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                parameters.put(Constraints.AUTH_TOKEN_NAME, Constraints.AUTH_TOKEN);
                parameters.put(Constraints.DISTRICT_ID, district_id);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void loadDIstrictList(String language) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constraints.GET_DISTRICTS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString(Constraints.ERROR).equalsIgnoreCase(Constraints.FALSE)){
                    JSONArray grpArray = object.getJSONArray(Constraints.DATA) ;

                    for(int i=0; i<grpArray.length(); i++){
                        JSONObject mGrpObject;
                        try {
                            mGrpObject = grpArray.getJSONObject(i);
                            if(language.equalsIgnoreCase(Constraints.LAN_BAN)){
                                mZillaList.add(mGrpObject.getString(Constraints.NAME_BAN)) ;
                            }else{
                                mZillaList.add(mGrpObject.getString(Constraints.NAME_ENG)) ;
                            }

                        } catch (JSONException ignored) {
                        }
                    }
                    adapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.list_black_text, R.id.list_content, mZillaList);
                    mSearchListView.setAdapter(adapter);
                }
            } catch (JSONException ignored) {
            }
        }, error -> {
            dialog.cancel();
            if(language.equalsIgnoreCase(Constraints.LAN_ENG)){
                Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_ENG , Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_BAN , Toast.LENGTH_SHORT).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.create_menu, menu);

        languageMenuItem = menu.findItem(R.id.user_language) ;
        if(preferLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
            languageMenuItem.setTitle(Constraints.LAN_ENG) ;
        }else {
            languageMenuItem.setTitle(Constraints.BANGLA) ;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.user_about){
            popupAboutUs();
        }else if(item.getItemId() == R.id.user_rescue){
            Intent intent = new Intent(getApplicationContext(), VolunteerListActivity.class);
            intent.putExtra(Constraints.AUTH_TOKEN_NAME, Constraints.AUTH_TOKEN) ;
            intent.putExtra(Constraints.LANGUAGE, preferLanguage) ;
            startActivity(intent);
        }else if(item.getItemId() == R.id.user_language){
            if(preferLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
                item.setTitle(Constraints.BANGLA) ;
                convertAllTextByLanguage(Constraints.LAN_ENG);
                languageState = false ;
            }else {
                item.setTitle(Constraints.LAN_ENG) ;
                convertAllTextByLanguage(Constraints.LAN_BAN);
                languageState = true ;
            }
            sharedSaved(sharedPreferencesLanguage, stateLanguage, languageState);

        }

        else if (item.getItemId() == R.id.user_share){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Please share my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        else if (item.getItemId() == R.id.user_rate){
            try{
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
            }
            catch (ActivityNotFoundException e){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
            }
        }
        return true;
    }



    private void popupAboutUs() {
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        popupLayoutInitialize(view);
        mMainTextPopup.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dimen_14));

        btnOk.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        mHeadingTextPopup.setText(getResources().getString(R.string.about_us_menu));
        mMainTextPopup.setText(R.string.about_us);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void createPopupDialog( String language) {
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        popupLayoutInitialize(view);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if(language.equalsIgnoreCase(Constraints.LAN_BAN)){
            mHeadingTextPopup.setText(getResources().getString(R.string.rules_ban));
            mMainTextPopup.setText(Html.fromHtml(Constraints.SHC_RULES_BAN));
            btnOk.setText((getResources().getString(R.string.agree_name_ban)));
            btnCancel.setText((getResources().getString(R.string.disagree_name_ban)));
        }else if(language.equalsIgnoreCase(Constraints.LAN_ENG)){
            mHeadingTextPopup.setText(getResources().getString(R.string.rules_eng));
            mMainTextPopup.setText(Html.fromHtml(Constraints.SHC_RULES_ENG));

            btnOk.setText((getResources().getString(R.string.agree_name_eng)));
            btnCancel.setText((getResources().getString(R.string.disagree_name_eng)));
        }

        btnOk.setOnClickListener(v -> {
            confirmState = true;
            dialog.dismiss();
            sharedSaved(sharedPreferencesConfirm, stateConfirm, confirmState);
            if(btnOk.getText().toString().equalsIgnoreCase(getResources().getString(R.string.agree_name_ban))){
                convertAllTextByLanguage(Constraints.LAN_BAN);
            }else if(btnOk.getText().toString().equalsIgnoreCase(getResources().getString(R.string.agree_name_eng))){
                convertAllTextByLanguage(Constraints.LAN_ENG);
            }
        });

        btnCancel.setOnClickListener(v -> finish());

    }

    private void createPopupLanguage() {
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        popupLayoutInitialize(view);

        mPopupRelative.getLayoutParams().height= 400 ;
        mMainTextPopup.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dimen_14));
        btnOk.setText(Constraints.BANGLA);
        btnCancel.setText(Constraints.LAN_ENG);
        btnOk.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);

        mHeadingTextPopup.setText(R.string.language_menu);
        mMainTextPopup.setText(R.string.language_popup);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        btnOk.setOnClickListener(v -> {
            banEngBool = true;
            languageState = true ;
            dialog.dismiss();
            sharedSaved(sharedPreferencesLanguage, stateLanguage, languageState);

            if(!confirmState){
                createPopupDialog(Constraints.LAN_BAN);
            }else{
                convertAllTextByLanguage(Constraints.LAN_BAN);
            }
        });
        btnCancel.setOnClickListener(v -> {
            banEngBool = true;
            languageState = false ;
            dialog.dismiss();
            sharedSaved(sharedPreferencesLanguage, stateLanguage, languageState );

            if(!confirmState){
                createPopupDialog(Constraints.LAN_ENG);
            }else{
                convertAllTextByLanguage(Constraints.LAN_ENG);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStateConfirm = sharedPreferencesConfirm.getString(stateConfirm, Constraints.FALSE);
        confirmState = Boolean.parseBoolean(getStateConfirm);
        String getStateLanguage = sharedPreferencesLanguage.getString(stateLanguage, "");
        if(getStateLanguage.equalsIgnoreCase(Constraints.FALSE)){
            preferLanguage = Constraints.LAN_ENG ;
        }else{
            preferLanguage=Constraints.LAN_BAN ;
        }

        if(confirmState){
            convertAllTextByLanguage(preferLanguage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void convertAllTextByLanguage(String language) {
        if(language.equalsIgnoreCase(Constraints.LAN_ENG)){
            preferLanguage=language;

            mQuestionTV01.setText(getResources().getString(R.string.question_01_eng));
            mQuestionTV02.setText(getResources().getString(R.string.question_02_eng));
            rb1.setText(Constraints.YES);
            rb2.setText(Constraints.NO);
            rb3.setText(Constraints.DEAD);
            rb4.setText(Constraints.ALIVED);
            rb5.setText(Constraints.RESCUED);
            mPhoneNumberTextInput.setHint(getResources().getString(R.string.q_phone_eng));
            mLocationNameTextInput.setHint(getResources().getString(R.string.q_location_eng));
            mDistrictText.setText(Constraints.DISTRICT);
            mSubDistrictText.setText(Constraints.THANA);
            mDateTV.setText(getResources().getString(R.string.q_date_eng));
            mSnakeNameTV.setText(getResources().getString(R.string.q_think_eng));
            mSubmitBtn.setText(getResources().getString(R.string.submit_eng));
        }else if(language.equalsIgnoreCase(Constraints.LAN_BAN)){
            preferLanguage=language;

            mQuestionTV01.setText(getResources().getString(R.string.question_01_ban));
            mQuestionTV02.setText(getResources().getString(R.string.question_02_ban));
            rb1.setText(getResources().getString(R.string.yes_rb));
            rb2.setText(getResources().getString(R.string.no_rb));
            rb3.setText(getResources().getString(R.string.dead_rb));
            rb4.setText(getResources().getString(R.string.live_rb));
            rb5.setText(getResources().getString(R.string.rescued_rb));
            mPhoneNumberTextInput.setHint(getResources().getString(R.string.q_phone_ban));
            mLocationNameTextInput.setHint(getResources().getString(R.string.q_location_ban));
            mDistrictText.setText(Constraints.DISTRICT_NAME);
            mSubDistrictText.setText(Constraints.THANA_NAME);
            mDateTV.setText(getResources().getString(R.string.q_date_ban));
            mSnakeNameTV.setText(getResources().getString(R.string.q_think_ban));
            mSubmitBtn.setText(getResources().getString(R.string.submit_ban));
        }

        if(banEngBool){
            if(preferLanguage.equalsIgnoreCase(Constraints.LAN_BAN) ){
                languageMenuItem.setTitle(Constraints.LAN_ENG) ;
            }else {
                languageMenuItem.setTitle(Constraints.BANGLA) ;
            }
        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name_inside)
                .setMessage(Constraints.EXIT_MSG_BACK)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> finish())
                .setIcon(R.mipmap.image_icon)
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public void onLinearDateFrom(View view) {
        String lan = "EN";
        if(preferLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
            lan="BN" ;
        }else if(preferLanguage.equalsIgnoreCase(Constraints.LAN_ENG)){
            lan="EN" ;
        }

        Locale locale = new Locale(lan);
        Locale.setDefault(locale);
        DatePickerDialog datepick = new DatePickerDialog(CameraViewActivity.this, (view1, year, month, dayOfMonth) -> {
            Calendar selectCalendar1 = Calendar.getInstance();
            selectCalendar1.set(year, month, dayOfMonth);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
            String dateFormat1 = simpleDateFormat1.format(selectCalendar1.getTime());

            mDateTV.setText(dateFormat1);


            Locale locale1 = new Locale("EN");
            Locale.setDefault(locale1);

            Calendar selectCalendar2 = Calendar.getInstance();
            selectCalendar2.set(year, month, dayOfMonth);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

            mSelectDate = simpleDateFormat2.format(selectCalendar2.getTime());

        }, currentYear, currentMonth, currentDay);
        datepick.setTitle("select date");
        datepick.getDatePicker().setMaxDate(System.currentTimeMillis());
        datepick.show();

    }


    public void onLoadAllSnakeName(View view) {
        CreateDistrictThanaPopUpDialog(Constraints.SNAKE_NAME);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void loadSnakeNameList(String language, boolean load) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constraints.GET_SNAKES, response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(Constraints.ERROR).equalsIgnoreCase(Constraints.FALSE)){
                            JSONArray jsonArray = object.getJSONArray(Constraints.DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                mSnakeList.add(new SnakeName(
                                        jsonObject.getString(Constraints.ID),
                                        jsonObject.getString(Constraints.NAME_BAN),
                                        jsonObject.getString(Constraints.NAME_ENG),
                                        jsonObject.getString(Constraints.IMAGE)
                                        ));
                            }
                            if(load){
                                mSnakeNameViewAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> {
                    if(load){
                        if(language.equalsIgnoreCase(Constraints.LAN_ENG))
                            Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_ENG, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), Constraints.CHECK_CONNECTION_BAN, Toast.LENGTH_SHORT).show();
                        dialog.cancel() ;
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void AddTextChange(){
        mSearchEditText.addTextChangedListener(new TextWatcher() {
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
        List<SnakeName> filteredList = new ArrayList<>();
        for (SnakeName item : mSnakeList) {
            if (item.getName_ban().toLowerCase().contains(text.toLowerCase()) && preferLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
                filteredList.add(item);
            }else if (item.getName_eng().toLowerCase().contains(text.toLowerCase()) && preferLanguage.equalsIgnoreCase(Constraints.LAN_ENG)){
                filteredList.add(item);
            }
        }
        mSnakeNameViewAdapter.searchFilterList(filteredList);
    }
}