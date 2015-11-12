package com.zhm.duxiangle.wxapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhm.duxiangle.R;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.utils.ToastUtils;

/**
 * 微信客户端回调activity示例
 */
//@ContentView(R.layout.activity_wxentry)
public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private String TAG = WXEntryActivity.class.getSimpleName();
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
//    @ViewInject(R.id.tvContent)
//    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID_WECHAT);
        super.onCreate(savedInstanceState);
//        ViewUtils.inject(this);
        api.handleIntent(getIntent(), this);
    }


    @Override
    public void onReq(BaseReq baseReq) {
        Log.i(TAG, "onReq");
//        tvContent.setText(baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.i(TAG, "onResp");
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
//                tvContent.setText(baseResp.errStr + "--translaction" + baseResp.transaction + "--openid" + baseResp.openId + "--- checkargs[]" + baseResp.checkArgs());

                Log.i(TAG, "分享成功");
                ToastUtils.showToast(getApplicationContext(), "OK" + baseResp.errCode);
//                ToastUtils.showToast();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
//                tvContent.setText(baseResp.errStr);
                Log.i(TAG, "分享取消");
                ToastUtils.showToast(getApplicationContext(), "分享取消" + baseResp.errCode);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Log.i(TAG, "分享拒绝");
//                tvContent.setText(baseResp.errStr);
                ToastUtils.showToast(getApplicationContext(), "OK" + baseResp.errCode);
                //分享拒绝
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
//                tvContent.setText(baseResp.errStr);
                Log.i(TAG, "ERR_SENT_FAILED");
                ToastUtils.showToast(getApplicationContext(), "ERR_SENT_FAILED  " + baseResp.errCode);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                Log.i(TAG, "ERR_UNSUPPORT");
                ToastUtils.showToast(getApplicationContext(), "ERR_UNSUPPORT  " + baseResp.errCode);
                break;
            case BaseResp.ErrCode.ERR_COMM:
                Log.i(TAG, "default");
                ToastUtils.showToast(getApplicationContext(), "default" + baseResp.errCode);
                break;
            default:
                Log.i(TAG, "default");
                ToastUtils.showToast(getApplicationContext(), "default" + baseResp.errCode);
                break;
        }
        finish();
    }


}
