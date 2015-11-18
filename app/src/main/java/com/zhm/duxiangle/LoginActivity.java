package com.zhm.duxiangle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.SyncStateContract;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.common.Constants;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhm.duxiangle.QQ.BaseUIListener;
import com.zhm.duxiangle.QQ.Util;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.api.ShareApi;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.bean.QQAuthBean;
import com.zhm.duxiangle.bean.QQUserInfo;
import com.zhm.duxiangle.bean.RongYun;
import com.zhm.duxiangle.bean.SdkHttpResult;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.LogUtils;
import com.zhm.duxiangle.utils.SpUtil;
import com.zhm.duxiangle.utils.ToastUtils;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, OnClickListener {

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
    @ViewInject(R.id.email)
    private AutoCompleteTextView mEmailView;
    @ViewInject(R.id.password)
    private EditText mPasswordView;
    @ViewInject(R.id.login_progress)
    private View mProgressView;
    @ViewInject(R.id.login_form)
    private View mLoginFormView;
    //登录按钮
    @ViewInject(R.id.btnLogin)
    private Button btnLogin;
    @ViewInject(R.id.btnSetIp)
    private Button btnSetIp;
    @ViewInject(R.id.etIp)
    private EditText etIp;
    //注册按钮
    @ViewInject(R.id.tvRegister)
    private TextView tvRegister;
    //第三方登录
    @ViewInject(R.id.btnWBLogin)
    private Button btnWBLogin;
    @ViewInject(R.id.btnQQLogin)
    private Button btnQQLogin;
    //用户头像
//    @ViewInject(R.id.ivUser)
//    private CircleImageView ivUser;
    @ViewInject(R.id.tvForgetPass)
    private TextView tvForgetPass;
    private Oauth2AccessToken mAccessToken;
    private Tencent tencent;
    private IUiListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ViewUtils.inject(this);
//        hideSystemKeyBoard(this);
        // Set up the login form.
        populateAutoComplete();
        // 1.4版本:此处需新增参数，传入应用程序的全局context，可通过activity的getApplicationContext方法获取
        tencent = Tencent.createInstance(Constant.QQ_APP_ID, this.getApplicationContext());//初始化qq对象
        btnSetIp.setOnClickListener(this);
//        ivUser.setOnClickListener(this);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        btnLogin.setOnClickListener(this);
        btnWBLogin.setOnClickListener(this);
        btnQQLogin.setOnClickListener(this);
        tvRegister.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tvRegister.setTextColor(Color.RED);
                        break;
                    case MotionEvent.ACTION_UP:
                        tvRegister.setTextColor(Color.GRAY);
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        tvForgetPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tvForgetPass.setTextColor(Color.RED);
                        break;
                    case MotionEvent.ACTION_UP:
                        tvForgetPass.setTextColor(Color.GRAY);
                        Intent data = new Intent(Intent.ACTION_SENDTO);
//                        data.setData(Uri.parse("183340093@qq.com"));
                        data.putExtra(Intent.EXTRA_EMAIL, Uri.parse("183340093@qq.com"));
                        data.putExtra(Intent.EXTRA_SUBJECT, "忘记密码");
                        data.putExtra(Intent.EXTRA_TEXT, "内容");
                        startActivity(Intent.createChooser(data, "邮箱发送"));
                        break;
                }
                return true;
            }
        });
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
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
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
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
            HttpUtils utils = new HttpUtils();
            utils.configTimeout(1000 * 3);
            RequestParams params = new RequestParams();
            params.addBodyParameter("action", "login");
            params.addBodyParameter("username", email);
            params.addBodyParameter("password", password);
            utils.send(HttpRequest.HttpMethod.POST, DXLApi.getUserApi(), params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String result = responseInfo.result;
                    if ("username is null".equals(result)) {
                        showProgress(false);
                        mEmailView.setError(result);
                        mEmailView.requestFocus();
                        return;
                    }
                    if ("password is null".equals(result)) {
                        showProgress(false);
                        mPasswordView.setError(result);
                        mPasswordView.requestFocus();
                        return;
                    }
                    if ("no found".equals(result)) {
                        showProgress(false);
                        mEmailView.setError(result);
                        mEmailView.requestFocus();
                        return;
                    }
                    if ("error password".equals(result)) {

                        showProgress(false);
                        mPasswordView.setError(result);
                        mPasswordView.requestFocus();
                        return;
                    }
                    User user = GsonUtils.getInstance().json2Bean(result, User.class);
                    getTokenByUserId(user);
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    showProgress(false);
                    ToastUtils.cancelToast();
                    //网络链接超时
                    ToastUtils.showToast(getApplication(), "网络链接失败");
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                attemptLogin();
                break;
            case R.id.btnSetIp:
                String strIp = etIp.getText().toString().trim();
                if (!TextUtils.isEmpty(strIp)) {
                    DXLApi.HOST = strIp;
                    btnSetIp.setText(DXLApi.HOST);
                }
                break;
            case R.id.btnWBLogin:
                wBLogin();
                break;
            case R.id.btnQQLogin:
                qqLogin();
                break;
//            case R.id.ivUser:
//                Intent intent = new Intent(LoginActivity.this, FaceCameraActivity.class);
//                startActivity(intent);
//                break;
        }
    }


    /**
     * http://wiki.open.qq.com/wiki/QQ%E7%99%BB%E5%BD%95%E5%92%8C%E6%B3%A8%E9%94%80
     * qq登录
     */
    private void qqLogin() {
        listener = new BaseUiListener();
        tencent.login(LoginActivity.this, "all", listener);

//        getQQUserInfo();
//        UserInfo info = new UserInfo(getApplicationContext(), tencent.getQQToken());
//        info.getUserInfo(listener);
//        LogUtils.i(LoginActivity.this, "zhm-- " + info.toString());
    }

//    private void getQQUserInfo() {
//        UserInfo mInfo = new UserInfo(getApplicationContext(), tencent.getQQToken());
//        mInfo.getUserInfo(new BaseUIListener(getApplicationContext(), "get_simple_userinfo"));
//        LogUtils.i(LoginActivity.this, "zhm--  " + mInfo.toString());
//    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            LogUtils.i(LoginActivity.this, "zhm--" + o.toString());

            doComplete(o);
        }

        private void doComplete(Object o) {
            JSONObject jsonObject = (JSONObject) o;
            //
            QQAuthBean qqAuthBean = GsonUtils.getInstance().json2Bean(jsonObject.toString(), QQAuthBean.class);
            ToastUtils.showToast(getApplicationContext(), qqAuthBean.getOpenid());
            LogUtils.i(LoginActivity.this, "zhm--openid" + qqAuthBean.getOpenid());
            LogUtils.i(LoginActivity.this, "zhm--token" + qqAuthBean.getAccess_token());
            initOpenidAndToken(jsonObject);

            AuthLoginByQQ(qqAuthBean);

//            JSONObject jsonObject1 = null;
//            try {
//                jsonObject1 = tencent.request(Constants.GRAPH_BASE, null, Constants.HTTP_GET);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (com.tencent.open.utils.HttpUtils.NetworkUnavailableException e) {
//                e.printStackTrace();
//            } catch (com.tencent.open.utils.HttpUtils.HttpStatusException e) {
//                e.printStackTrace();
//            }
//            LogUtils.i(LoginActivity.this, "zhm--  " + jsonObject1.toString());
        }


        private void AuthLoginByQQ(QQAuthBean qqAuthBean) {
            showProgress(true);
            //http://localhost:8080/DuXiangLeServer/AuthServlet?action=auth_by_qq&openid=openid&access_token=token
            RequestParams params = new RequestParams();
            params.addBodyParameter("action", "auth_by_qq");
            params.addBodyParameter("openid", qqAuthBean.getOpenid());
            params.addBodyParameter("access_token", qqAuthBean.getAccess_token());
            DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getAuthApi(), params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String result = responseInfo.result;

                    if ("openid is null".equals(result)) {
                        ToastUtils.showToast(LoginActivity.this, result);
                        return;
                    }

                    if ("access_token is null".equals(result)) {
                        ToastUtils.showToast(LoginActivity.this, result);
                        return;
                    }
                    User user = GsonUtils.getInstance().json2Bean(result, User.class);
                    if (user != null) {
                        getTokenByUserId(user);
                        updateUserInfo(user.getUserId());
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    ToastUtils.showToast(getApplicationContext(), "服务器链接失败");
                }
            });
        }


        @Override
        public void onError(UiError e) {
            ToastUtils.showToast(getApplicationContext(), "onError:" + "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            ToastUtils.showToast(getApplicationContext(), "onCancel" + "");
        }
    }

    /**
     * 微博登录
     */
    private void wBLogin() {
        AuthInfo mAuthInfo = new AuthInfo(LoginActivity.this, Constant.SINA_APP_KEY, Constant.SINA_REDIRECT_URL, Constant.SINA_SCOPE);
        SsoHandler handler = new SsoHandler(LoginActivity.this, mAuthInfo);
        handler.authorize(new AuthListener());
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
            String phoneNum = mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
//                updateTokenView(false);

                // 保存 Token 到 SharedPreferences
//                AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                Toast.makeText(LoginActivity.this,
                        "认证成功:" + phoneNum, Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "认证失败:";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this,
                    "取消", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mEmailView.setAdapter(adapter);
    }

    private void getTokenByUserId(final User user) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userid", String.valueOf(user.getUserId()));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getIoRongTokenApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showProgress(false);
                String result = responseInfo.result;
//                Log.i("LoginActivity", result);
                SdkHttpResult tokenResult = GsonUtils.getInstance().json2Bean(result, SdkHttpResult.class);
//                Log.i("LoginActivity", tokenResult.getResult());
                if (tokenResult != null) {
                    RongYun rong = GsonUtils.getInstance().json2Bean(tokenResult.getResult(), RongYun.class);
                    if ("200".equals(rong.getCode())) {
                        user.setToken(rong.getToken());
                        Log.i("LoginActivity", user.getToken());
                        //数据存储到本地
                        SpUtil.setStringSharedPerference(SpUtil.getSharePerference(getApplicationContext()), "user", GsonUtils.getInstance().bean2Json(user));
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showProgress(false);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
                ToastUtils.showToast(getApplicationContext(), "服务器链接失败");
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("是否退出").setIcon(
                R.drawable.ic_launcher).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    //应用调用Andriod_SDK接口时，如果要成功接收到回调，需要在调用接口的Activity的onActivityResult方法中增加如下代码：


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void hideSystemKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplication().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                tencent.setAccessToken(token, expires);
                tencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }

    private class BaseApiListener implements IRequestListener {
        @Override
        public void onComplete(JSONObject jsonObject) {
            LogUtils.i(LoginActivity.this, "zhm-BaseApiListener-" + jsonObject.toString());
        }

        @Override
        public void onIOException(IOException e) {

        }

        @Override
        public void onMalformedURLException(MalformedURLException e) {

        }

        @Override
        public void onJSONException(JSONException e) {

        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException e) {

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException e) {

        }

        //1.4版本中IRequestListener 新增两个异常
        @Override
        public void onNetworkUnavailableException(com.tencent.open.utils.HttpUtils.NetworkUnavailableException e) {
            // 当前网络不可用时触发此异常
        }

        @Override
        public void onHttpStatusException(com.tencent.open.utils.HttpUtils.HttpStatusException e) {
            // http请求返回码非200时触发此异常
        }

        @Override
        public void onUnknowException(Exception e) {
            // 出现未知错误时会触发此异常
        }
    }

    /**
     * 获取用户信息
     *
     * @param userId
     */
    private void updateUserInfo(final int userId) {
        if (tencent != null) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                    LogUtils.i(LoginActivity.this, "zhm--  updateUserInfo" + e.toString());
                }

                @Override
                public void onComplete(final Object response) {
                    JSONObject json = (JSONObject) response;
                    LogUtils.i(LoginActivity.this, "zhm--  0" + json.toString());
                    if (json != null) {
                        QQUserInfo userInfo = GsonUtils.getInstance().json2Bean(response.toString(), QQUserInfo.class);
                        if (userInfo != null) {
                            userInfo.setUserid(userId);
                            uploadUserInfo(userInfo);
                        }
                        LogUtils.i(LoginActivity.this, "zhm--  1" + userInfo.toString());
                    }
//                    Message msg = new Message();
//                    msg.obj = response;
//                    msg.what = 0;
//                    mHandler.sendMessage(msg);
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            JSONObject json = (JSONObject) response;
//                            if (json.has("figureurl")) {
//                                Bitmap bitmap = null;
//                                try {
//                                    bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
//                                } catch (JSONException e) {
//
//                                }
//                                Message msg = new Message();
//                                msg.obj = bitmap;
//                                msg.what = 1;
//                                mHandler.sendMessage(msg);
//                            }
//                        }
//
//                    }.start();
                }

                @Override
                public void onCancel() {
                    LogUtils.i(LoginActivity.this, "zhm--  cancel");
                }
            };
            UserInfo mInfo = new UserInfo(this, tencent.getQQToken());
            mInfo.getUserInfo(listener);
        } else {
//            设置用户信息
        }
    }

//    Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 0) {
//                JSONObject response = (JSONObject) msg.obj;
//                if (null != response) {
//                    QQUserInfo userInfo = GsonUtils.getInstance().json2Bean(response.toString(), QQUserInfo.class);
//                    if (userInfo != null) {
//                        uploadUserInfo(userInfo);
//                    }
//                    LogUtils.i(LoginActivity.this, "zhm--" + userInfo.toString());
//                }
//                LogUtils.i(LoginActivity.this, "zhm--2 " + response.toString());
//                if (response.has("nickname")) {
//                    try {
////                        mUserInfo.setVisibility(android.view.View.VISIBLE);
//                        tvForgetPass.setText(response.getString("nickname"));
//                        LogUtils.i(LoginActivity.this, "zhm-- " + response.getString("nickname"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else if (msg.what == 1) {
//                Bitmap bitmap = (Bitmap) msg.obj;
////                mUserLogo.setImageBitmap(bitmap);
////                mUserLogo.setVisibility(android.view.View.VISIBLE);
//            }
//        }
//
//    };

    private void uploadUserInfo(QQUserInfo userInfo) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "qq_update_userinfo");
        params.addBodyParameter("qquserinfo", GsonUtils.getInstance().bean2Json(userInfo));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getAuthApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if("action is null".equals(result)){
                    LogUtils.i(LoginActivity.this,result);
                    return;
                }
                if("userinfo is null".equals(result)){
                    LogUtils.i(LoginActivity.this,result);
                    return;
                }
                if("1".equals(result)){
                    ToastUtils.showToast(getApplicationContext(),"更新用户资料成功");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtils.showToast(getApplicationContext(),"服务器链接失败");
            }
        });
    }
}

