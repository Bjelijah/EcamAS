package com.howell.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.howell.webcam.R;
import com.howell.modules.regist.IRegistContract;
import com.howell.modules.regist.bean.Type;
import com.howell.modules.regist.presenter.RegistPresenter;

import java.util.List;

/**
 * Created by howell on 2016/11/10.
 */

public class RegisterActivity extends AppCompatActivity implements IRegistContract.IVew,View.OnClickListener{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    // UI references.
    private AutoCompleteTextView mUserNameView, mEmailView;
    private EditText mPasswordView, mConfirmPasswordView;
    private View mProgressView,mRegFormView,mRoot;
    private Button mRegBtn;
//    private ToggleButton mBindTbn;
    private Switch mBindTbn;
    private TextView mTitle;

    private boolean mIsBindFinger;
    private IRegistContract.IPresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindPresenter();

        // Set up the login form.
        mTitle = (TextView) findViewById(R.id.reg_title);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.reg_et_email);
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.reg_et_username);
        mPasswordView = (EditText) findViewById(R.id.reg_et_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.reg_et_confirm_password);
//        mBindTbn = (ToggleButton) findViewById(R.id.reg_tb_bind);
        mBindTbn = (Switch) findViewById(R.id.reg_tb_bind);
        mBindTbn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mIsBindFinger = b;
            }
        });

        mRegBtn = (Button) findViewById(R.id.reg_btn_reg);
        mRegBtn.setOnClickListener(this);

        mProgressView = findViewById(R.id.reg_progress);
        mRegFormView = findViewById(R.id.reg_reg_form);
        mRoot = findViewById(R.id.root_ll);
        mRegFormView.setAlpha(0f);
        mRegFormView.setVisibility(View.VISIBLE);
        mRegFormView.animate().alpha(1f).setDuration(1000).setListener(null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPresenter();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
            mTitle.setText("");
            mBindTbn.setVisibility(View.GONE);
            mRegBtn.setVisibility(View.GONE);
            mRoot.setBackgroundColor(getResources().getColor(R.color.transparent));
            mEmailView.setError(null);
            mUserNameView.setError(null);
            mPasswordView.setError(null);
            mConfirmPasswordView.setError(null);


            mRegFormView.animate().alpha(0f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    finish();
                }
            });

        }
        return true;
    }

    private boolean isFingerAccess(){
        return false;
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mIsBindFinger && !isFingerAccess()){
            Snackbar.make(mRegBtn,getString(R.string.reg_finger_support_error),Snackbar.LENGTH_LONG).show();
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String username = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirm = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)){
            mEmailView.setError(getString(R.string.reg_field_empty));
            focusView = mEmailView;
            cancel = true;
        }else if(!isEmailValid(email)){
            mEmailView.setError(getString(R.string.reg_email_not_valid));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.verification));
            focusView = mUserNameView;
            cancel = true;
        }else if(!isUserNameValid(username)){
            mUserNameView.setError(getString(R.string.reg_usename_not_valid));
            focusView = mUserNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.verification), null);
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirm)){
            mConfirmPasswordView.setError(getString(R.string.reg_field_empty),null);
            focusView = mConfirmPasswordView;
            cancel= true;
        }else if(!isPasswordConfirm(password,confirm)){
            mConfirmPasswordView.setError(getString(R.string.register_activity_password_differ),null);
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.

            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            //register
            mProgressView.setVisibility(View.VISIBLE);
//            RegisterAction.getInstance().registerCallback(this).register(username,password,email);
            mPresenter.register(username,password,email);
        }
    }

    private boolean isUserNameValid(String name){
        return name.length()>5&&name.length()<33;
    }

    private boolean isEmailValid(String email){
        return email.contains("@");
    }

    private boolean isPasswordConfirm(String psw,String confirm){
        return psw.equals(confirm);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUserNameView.setAdapter(adapter);
    }

    private void closeInput(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        Log.i("123", "id: " + view.getId());
        mPasswordView.setCursorVisible(false);
        mUserNameView.setCursorVisible(false);
        mEmailView.setCursorVisible(false);
        mConfirmPasswordView.setCursorVisible(false);
        closeInput(view);
        switch (view.getId()) {
            case R.id.reg_btn_reg:
                attemptRegister();
                break;

            default:
                break;
        }
    }





    @Override
    public void bindPresenter() {
        if (mPresenter==null){
            mPresenter = new RegistPresenter();
        }
        mPresenter.bindView(this);
    }

    @Override
    public void unbindPresenter() {
        if (mPresenter!=null){
            mPresenter.unbindView();
        }
    }

    @Override
    public void onError() {
        mProgressView.setVisibility(View.GONE);
        Snackbar.make(mRegFormView,getString(R.string.login_error),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRegistResult(Type type) {
        mProgressView.setVisibility(View.GONE);
        switch (type){
            case OK:
                mProgressView.setVisibility(View.GONE);
                finish();
                break;
            case ERROR_REG_ACCOUNT:
                mUserNameView.requestFocus();
                mUserNameView.post(new Runnable() {
                    @Override
                    public void run() {
                        mUserNameView.setError(getString(R.string.register_activity_fail_account_exist));
                    }
                });
                break;
            case ERROR_REG_EMAIL:
                mEmailView.requestFocus();
                mEmailView.post(new Runnable() {
                    @Override
                    public void run() {
                        mEmailView.setError(getString(R.string.register_activity_fail_email_exist));
                    }
                });
                break;
            case ERROR_REG_FORMAT:
                mUserNameView.requestFocus();
                mUserNameView.post(new Runnable() {
                    @Override
                    public void run() {
                        mUserNameView.setError(getString(R.string.register_activity_fail_account_format));
                    }
                });
                break;
            case ERROR_REG_OTHER:
                Snackbar.make(mRegFormView,getString(R.string.register_activity_fail),Snackbar.LENGTH_LONG).show();
                break;
            default:break;
        }
    }


}
