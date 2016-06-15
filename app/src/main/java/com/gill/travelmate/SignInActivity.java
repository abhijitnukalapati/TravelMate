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

//Signin activity
public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView cross;
    private EditText emailEditText, passwordEditText;
    private TextView signInTextView, title;
    private Intent i;
    private Context mContext;
    private Animation animationFadeIn;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        FontHelper.applyFont(this, findViewById(R.id.container_signin), "bauhaus.ttf");

        mContext=SignInActivity.this;
        dialog=Utils.getProgressDialog(mContext);

        initializeViews();
        setListener();

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SignInActivity.this.finish();
        overridePendingTransition(R.anim.toright_in, R.anim.toright_out);
    }

    //initialize views
    public void initializeViews(){
        cross=(ImageView)findViewById(R.id.cross);
        emailEditText =(EditText)findViewById(R.id.email_edit_text);
        passwordEditText =(EditText)findViewById(R.id.password_edit_text);
        signInTextView =(TextView)findViewById(R.id.signin_button);
        title=(TextView)findViewById(R.id.title);

        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        title.setDrawingCacheEnabled(true);
    }

    //set on click on views
    public void setListener(){
        cross.setOnClickListener(this);
        signInTextView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        title.startAnimation(animationFadeIn);
    }

    //set functionality on click of views
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cross:
                SignInActivity.this.finish();
                overridePendingTransition(R.anim.toright_in, R.anim.toright_out);
                break;
            case R.id.signin_button:
                Utils.hideKeyboard(mContext,getCurrentFocus());
                if(checkValidation()){
                    if(Utils.isNetworkConnected(mContext)){
                        checkUserSignInDetails();
                    }else{
                        Utils.showToast(mContext,getString(R.string.no_internet_connection));
                    }
                }
                break;
            default:
                break;
        }
    }

    //check validation of fields
    public boolean checkValidation(){
        if(!(emailEditText.getText().toString().matches(GeneralValues.EMAIL_PATTERN))|| emailEditText.getText().toString().length()<=0){
            Utils.showToast(mContext,getString(R.string.enter_valid_email));
            return false;
        }else if(passwordEditText.getText().toString().length()<=0){
            Utils.showToast(mContext,getString(R.string.enter_your_password));
            return false;
        }else{
            return true;
        }
    }

    //api to check whether user entered correct info or not
    public void checkUserSignInDetails(){
        try{
            dialog.show();
        }catch (Exception e){

        }
        HashMap<String,String> map=new HashMap<>();
        map.put("password",""+ passwordEditText.getText().toString());
        map.put("email",""+ emailEditText.getText().toString());
        Call<ResponseBody> call = Utils.requestApi_Default().requestJson_withValues(GeneralValues.USER_SIGNIN, map);

        Utils.show_log("url = ");

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
                        JSONObject data=new JSONObject(result.getString("data"));
                        i=new Intent(mContext,SelectDestinationActivity.class);
                        i.putExtra("uid",""+data.getString("id"));
                        i.putExtra("uname",""+data.getString("username"));
                        i.putExtra("email",""+ emailEditText.getText().toString());
                        startActivity(i);
                        SignInActivity.this.finish();
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
                    Utils.show_log("Error : "+t.getMessage());
                }catch (Exception e){

                }
            }
        });
    }
}
