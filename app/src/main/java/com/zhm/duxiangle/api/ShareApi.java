package com.zhm.duxiangle.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhm.duxiangle.R;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.utils.BitmapUtils;

/**
 * Created by Administrator on 2015/11/5.
 */
public class ShareApi {
    static private ShareApi shareApi;
    private static Context mContext;

    public static ShareApi getInstance(Context _mContext) {
        mContext = _mContext;
        return shareApi == null ? new ShareApi() : shareApi;
    }
    public static IWXAPI api ;
    public void regToWx(){
        //通过工厂模式获取微信的实例
        api = WXAPIFactory.createWXAPI(mContext, Constant.APP_ID_WECHAT, true);
        //将应用的appID注册到微信
        api.registerApp(Constant.APP_ID_WECHAT);
    }
    /**
     * 分享微信图片到个人
     *
     * @return
     */
    public void share2WeChatWithImage(int flag) {
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.book_cover_default);
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
        bmp.recycle();
        msg.thumbData = BitmapUtils.getInstance(mContext).bmpToByteArray(thumbBmp, true);  // 设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene =flag==0? SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;//分享图片到个人
        api.sendReq(req);
    }

    public void share2WeChatWithWebUrl(int flag,String url){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = "WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
//        msg.description = "WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description Very Long Very Long Very Long Very Long Very Long Very Long Very Long";

        msg.title="title";
        msg.description="description";
        Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.book_cover_default);
        msg.thumbData = BitmapUtils.getInstance(mContext).bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene =flag==0? SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;//分享图片到个人
        api.sendReq(req);
    }

    public void wechatShare(int flag,String url) {
        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = "http://120.25.201.60/ZL/ZL.html";
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "Be Happy";
        msg.description = "Be Happy everyday";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        ShareApi.api.sendReq(req);
    }

    public void wechatShareToBook(int flag,String url,String title,String imageUrl) {
        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = "http://120.25.201.60/ZL/ZL.html";
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = "Be Happy everyday";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
//        Bitmap thumb = BitmapUtils.getInstance(mContext)
        msg.setThumbImage(thumb);


        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        ShareApi.api.sendReq(req);
    }
//http://120.25.201.60/ZL/ZL.html
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
