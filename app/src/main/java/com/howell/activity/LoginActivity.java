package com.howell.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.howell.action.FingerprintUiHelper;
import com.howell.action.LoginAction;
import com.howell.activity.fragment.FingerPrintFragment;
import com.howell.bean.Custom;
import com.android.howell.webcam.R;
import com.howell.utils.AlerDialogUtils;
import com.howell.utils.IConst;
import com.howell.utils.ServerConfigSp;
import com.howell.utils.Util;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,OnClickListener,LoginAction.IloginRes,IConst {

    private static final int MSG_LOGIN_SUCCESS = 0x01;
    private static final int MSG_LOGIN_CUSTOM_SERVER = 0x02;
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
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;

    private View mProgressView;
    private View mLoginFormView;
    private Button mloginBtn,mRegBtn;
    LinearLayout mTryLl,mCustomLl;
    private FloatingActionButton mFab;
    private ImageView mCustomIv;
    private TextView mCustomTv;
    private Switch mCustomSw;

    private boolean mIsGuest;
    private boolean mIsCustom = false;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LoginActivity.MSG_LOGIN_SUCCESS:
                    break;
                case LoginActivity.MSG_LOGIN_CUSTOM_SERVER:
                    Intent intent = new Intent(LoginActivity.this,ServerSetActivity.class);
                    startActivity(intent);
                    break;
                case MSG_FINGER_OK:
                    Bundle bundle = msg.getData();
                    String userName = bundle.getString("userName");
                    String userPassword = bundle.getString("userPassword");
                    Custom c = (Custom) bundle.getSerializable("custom");

                    Log.i("123","userName="+userName+"  pwd="+userPassword+" custom="+c.isCustom()
                            +"  ip="+c.getCustomIP()+"  port="+c.getCustomPort()+"   ssl="+c.isSSL());


                    mIsGuest = userName.equals(GUEST_NAME)?true:false;
                    mProgressView.setVisibility(View.VISIBLE);
                    LoginAction.getInstance().setContext(LoginActivity.this).regLoginResCallback(LoginActivity.this).Login(userName,userPassword,c);

                    break;
                case FingerPrintFragment.MSG_FINGERPRINT_ERROR:
                    Snackbar.make(mLoginFormView,getString(R.string.fingerprint_no_support),Snackbar.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.login_et_username);

//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.login_et_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(false);
                    return true;
                }
                return false;
            }
        });

        mloginBtn = (Button) findViewById(R.id.login_btn_login);
        mloginBtn.setOnClickListener(this);

        mRegBtn = (Button) findViewById(R.id.login_btn_reg);
        mRegBtn.setOnClickListener(this);

        mTryLl = (LinearLayout) findViewById(R.id.login_ll_try);
        mTryLl.setOnClickListener(this);

        mCustomLl = (LinearLayout) findViewById(R.id.login_ll_custom);
        mCustomLl.setOnClickListener(this);

        mCustomIv = (ImageView) findViewById(R.id.login_iv_custom);
        mCustomTv = (TextView) findViewById(R.id.login_tv_server);

        mCustomSw = (Switch) findViewById(R.id.login_sw_custom);
        mCustomSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsCustom = isChecked;
                if (mIsCustom) {
                    String ip = ServerConfigSp.loadServerIP(LoginActivity.this);
                    int port = ServerConfigSp.loadServerPort(LoginActivity.this);
                    if (ip == null || port == 0) {
                        mHandler.sendEmptyMessage(MSG_LOGIN_CUSTOM_SERVER);
                        return;
                    }
                    mCustomTv.setText(getString(R.string.login_server_select) + ip + ":" + port);

                }else{
                    mCustomTv.setText(getString(R.string.login_server_select)+getString(R.string.login_server));
                }
            }
        });
        mCustomSw.setChecked(false);
        mLoginFormView = findViewById(R.id.login_form);
//        mLoginFormView.setOnClickListener(this);
        mProgressView = findViewById(R.id.login_progress);

        mFab = (FloatingActionButton) findViewById(R.id.login_fab_fingerprint);

//        FontAwesome.Icon

//        mFab.setImageDrawable(new IconicsDrawable(this,  GoogleMaterial.Icon.gmd_print).actionBar().color(Color.RED));
        mFab.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsCustom){
            String ip = ServerConfigSp.loadServerIP(LoginActivity.this);
            int port = ServerConfigSp.loadServerPort(LoginActivity.this);
            if (ip != null && port != 0) {
                mCustomTv.setText(getString(R.string.login_server_select) + ip + ":" + port);
                return;
            }
        }
        mCustomSw.setChecked(false);
        mCustomTv.setText(getString(R.string.login_server_select)+getString(R.string.login_server));
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUserNameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(boolean isGuest) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        if (isGuest){
            username = GUEST_NAME;
            password = GUEST_PASSWORD;
        }
        if (username.equals(GUEST_NAME)){
            mIsGuest = true;
        }


        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.verification),null);
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.verification));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.

            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            //login
            mProgressView.setVisibility(View.VISIBLE);
            Custom c = new Custom();
            c.setCustom(mIsCustom);
            c.setCustomIP(mIsCustom?ServerConfigSp.loadServerIP(this):null);
            c.setCustomPort(ServerConfigSp.loadServerPort(this));
            c.setSSL(ServerConfigSp.loadServerSSL(this));

            LoginAction.getInstance().setContext(this).regLoginResCallback(this).Login(username,password,c);
        }
    }

    private void startRegisterActivity(){
        Activities.getInstance().addActivity(LoginActivity.class.getName(),this);
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUserNameView.setAdapter(adapter);
    }

    private void closeInput(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }

    @Override
    public void onClick(View view) {
        Log.i("123","id: "+view.getId());
        mPasswordView.setCursorVisible(false);
        mUserNameView.setCursorVisible(false);
        closeInput(view);
        switch (view.getId()){
            case R.id.login_btn_login:
                attemptLogin(false);
                break;
            case R.id.login_btn_reg:
                startRegisterActivity();
                break;
            case R.id.login_ll_try:
                attemptLogin(true);
                break;
            case R.id.login_fab_fingerprint:
                fingerprintFun();
                break;
            case R.id.login_ll_custom:
                customFun();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoginSuccess() {
        mProgressView.setVisibility(View.INVISIBLE);
        LoginAction.getInstance().setmIsGuest(mIsGuest).unRegLoginResCallback();
        mHandler.sendEmptyMessage(LoginActivity.MSG_LOGIN_SUCCESS);
        //TODO:show camLIST activity
        Intent intent = new Intent(this,HomeExActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginError(int e) {
        mProgressView.setVisibility(View.INVISIBLE);
        switch (e){
            case LoginAction.ERROR_LOGIN_ACCOUNT:
                mUserNameView.requestFocus();
                mUserNameView.post(new Runnable() {
                    @Override
                    public void run() {
                        mUserNameView.setError(getString(R.string.account_error));
                    }
                });
                break;
            case LoginAction.ERROR_LOGIN_PWD:
                mPasswordView.requestFocus();
                mPasswordView.post(new Runnable() {
                    @Override
                    public void run() {
                        mPasswordView.setError(getString(R.string.password_error),null);
                    }
                });
                break;
            case LoginAction.ERROR_LOGIN_OTHER:
                Snackbar.make(mLoginFormView,getString(R.string.login_fail),Snackbar.LENGTH_LONG).show();
                break;
            case LoginAction.ERROR_LINK_ERROR:
                Snackbar.make(mLoginFormView,getString(R.string.login_error),Snackbar.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void fingerprintFun(){
        if(!Util.isNewApi() || !FingerprintUiHelper.isFingerAvailable(this)){
            AlerDialogUtils.postDialogMsg(this,getString(R.string.login_other_fingerprint),getString(R.string.login_other_fingerprint_no_support),null);
            return;
        }
        FingerPrintFragment fingerFragment = new FingerPrintFragment();
        fingerFragment.setHandler(mHandler);
        fingerFragment.show(getFragmentManager(), "fingerLogin");
    }

    private void customFun(){
        Intent intent = new Intent(LoginActivity.this,ServerSetActivity.class);
        startActivity(intent);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

