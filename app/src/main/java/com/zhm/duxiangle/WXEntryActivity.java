package com.zhm.duxiangle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.utils.ToastUtils;

/** 微信客户端回调activity示例 */
public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private String TAG = WXEntryActivity.class.getSimpleName();
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID_WECHAT, false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wxentry);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                Log.i(TAG, "分享成功");
//                ToastUtils.showToast();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                Log.i(TAG, "分享取消");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Log.i(TAG, "分享拒绝");
                //分享拒绝
                break;
        }
    }
}
