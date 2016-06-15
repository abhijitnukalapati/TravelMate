package com.gill.travelmate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gill.travelmate.utils.FontHelper;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//SignUp activity
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView cross;
    private EditText nameEditText, emailEditText, passwordEditText;
    private TextView signUpTextView, title;
    private Animation animationFadeIn;
    private Context mContext;
    private Dialog dialog;
    private ScrollView scrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        FontHelper.applyFont(this, findViewById(R.id.container_signup), "bauhaus.ttf");

        mContext=SignUpActivity.this;
        dialog=Utils.getProgressDialog(mContext);

        initializeViews();
        setListener();

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() > 0 && str.startsWith(" ")) {
                    passwordEditText.setText(str.trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() > 0 && str.startsWith(" ")) {
                    nameEditText.setText(str.trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() > 0 && str.startsWith(" ")) {
                    emailEditText.setText(str.trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SignUpActivity.this.finish();
        overridePendingTransition(R.anim.toright_in, R.anim.toright_out);
    }

    //initialize views
    public void initializeViews(){
        cross=(ImageView)findViewById(R.id.cross);
        nameEditText =(EditText)findViewById(R.id.name_edit_text);
        emailEditText =(EditText)findViewById(R.id.email_edit_text);
        passwordEditText =(EditText)findViewById(R.id.password_edit_text);
        signUpTextView =(TextView)findViewById(R.id.signUp_button);
        title=(TextView)findViewById(R.id.title);
        scrollview=(ScrollView)findViewById(R.id.scrollview);

        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        title.setDrawingCacheEnabled(true);
    }

    //set listener on views
    public void setListener(){
        cross.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        title.startAnimation(animationFadeIn);
    }

    //set functionality on views click
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cross:
                SignUpActivity.this.finish();
                overridePendingTransition(R.anim.toright_in, R.anim.toright_out);
                break;
            case R.id.signUp_button:
                Utils.hideKeyboard(mContext,getCurrentFocus());
                if(checkValidation()){
                    if(Utils.isNetworkConnected(mContext)){
                        putUserSignUpDetails();
                    }else{
                        Utils.showToast(mContext,getString(R.string.no_internet_connection));
                    }
                }
                break;
            default:
                break;
        }
    }

    //validate all the fields
    public boolean checkValidation(){
        if(nameEditText.getText().toString().length()<=0){
            Utils.showToast(mContext,getString(R.string.enter_your_name));
            return false;
        }else if(!(emailEditText.getText().toString().matches(GeneralValues.EMAIL_PATTERN))|| emailEditText.getText().toString().length()<=0){
            Utils.showToast(mContext,getString(R.string.enter_valid_email));
            return false;
        }else if(passwordEditText.getText().toString().length()<=0){
            Utils.showToast(mContext,getString(R.string.enter_your_password));
            return false;
        }else{
            return true;
        }
    }

    //api to signup user
    public void putUserSignUpDetails(){
        try{
            dialog.show();
        }catch (Exception e){

        }
        HashMap<String,String> map=new HashMap<>();
        map.put("password",""+ passwordEditText.getText().toString());
        map.put("username",""+ nameEditText.getText().toString());
        map.put("email",""+ emailEditText.getText().toString());
        Call<ResponseBody> call = Utils.requestApi_Default().requestJson_withValues(GeneralValues.USER_SIGNUP, map);

        //Utils.show_log("url = "+call.request().url());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    try{
                        dialog.dismiss();
                    }catch (Exception e){

                    }
                    String jsonResponse = response.body().string();
                    Log.e("res = ",jsonResponse);

                    JSONObject result = new JSONObject(jsonResponse);
                    String res = result.optString(getString(R.string.j_response));
                    String message = result.optString(getString(R.string.j_message));
                    if (res != null && res.equals("1")) {
                        Utils.showToast(mContext,getString(R.string.register_success));
                        startActivity(new Intent(mContext,SignInActivity.class));
                        SignUpActivity.this.finish();
                        overridePendingTransition(R.anim.to_leftin, R.anim.to_leftout);
                    }else{
                        Utils.showToast(getApplicationContext(), "" + message);
                    }
                } catch (Exception e) {
                    Utils.showToast(getApplicationContext(),getString(R.string.server_error));
                    Log.e("exception", "" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.showToast(getApplicationContext(), getString(R.string.server_not_responding));
                try{
                    dialog.dismiss();
                }catch (Exception e){

                }
            }
        });
    }
}
