package com.framelibrary.jpush.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.framelibrary.jpush.JPushClient;
import com.framelibrary.util.logutil.LoggerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 如果不定义这个Receiver：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JpushReceiver extends BroadcastReceiver {

    // 打印所有的 intent extra 数据
    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    LoggerUtils.I("This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LoggerUtils.E("Get message extra JSON error!");
                }
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        LoggerUtils.D("[JpushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            /**
             * 收到REGISTRATION_ID事件
             * 每个用户独一的注册ID，可以发到自己的服务器和用户绑定
             * 一般不处理这个action，使用极光的标签和别名比较好
             */
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LoggerUtils.D("接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LoggerUtils.D("接收到自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            /**
             * 收到自定义消息事件（附加字段）
             */
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            /**
             * 收到消息事件
             */
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LoggerUtils.D("接收到通知: " + notifactionId);

            //拿到携带的参数
            String extraMessage = bundle.getString(JPushInterface.EXTRA_EXTRA);

            if (JPushClient.getInstance().getiNoticeReceiverUtil() != null)
                JPushClient.getInstance().getiNoticeReceiverUtil().dealWithFlowByMsgType(extraMessage, false);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LoggerUtils.D("用户点击打开了通知");
            /**
             * 用户打开通知的事件！
             */
            //拿到携带的参数
            String extraMessage = bundle.getString(JPushInterface.EXTRA_EXTRA);

            if (JPushClient.getInstance().getiNoticeReceiverUtil() != null)
                JPushClient.getInstance().getiNoticeReceiverUtil().dealWithFlowByMsgType(extraMessage, true);

//			//判定是否是更新事件
//			if (mUpdate.equals("1")) {
//				//需要更新
//				Beta.checkUpgrade();
//			}


        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            /**
             * 收到富文本消息
             */
            LoggerUtils.D("用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            LoggerUtils.D("[JpushReceiver]" + intent.getAction() + " connected state change to " + connected);
            /**
             * 极光推送服务连接上的事件
             */
        } else {
            LoggerUtils.D("[JpushReceiver] Unhandled intent - " + intent.getAction());
        }
    }

}
