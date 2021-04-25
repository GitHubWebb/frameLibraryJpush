package com.framelibrary.jpush.config;

import android.content.Context;

import com.framelibrary.jpush.event.JPushTagAliasOperatorHelper;
import com.framelibrary.jpush.receiver.INoticeReceiverUtil;
import com.framelibrary.util.DeviceUtils;

import cn.jpush.android.api.JPushInterface;


/**
 * @Author: wangweixu
 * @Date: 2021/04/25 14:23:00
 * @Description: 极光推送配置类
 * @Version: v1.0
 */
public class JPushClient {

    //使用volatile修饰 目的是为了在JVM层编译顺序一致
    private static volatile JPushClient jpushReceiver = null;
    private INoticeReceiverUtil iNoticeReceiverUtil; // 有实现类实现具体方法逻辑

    private JPushClient() {
    }

    public static JPushClient getInstance() {

        //第一次校验
        if (jpushReceiver == null) {
            synchronized (JPushClient.class) {

                //第二次校验
                if (jpushReceiver == null) {
                    jpushReceiver = new JPushClient();
                }
            }
        }
        return jpushReceiver;
    }

    /**
     * 初始化极光配置信息
     *
     * @param context
     * @param configBean
     */
    public void init(Context context, JPushConfigBean configBean) {
        if (context == null || configBean == null)
            return;

        JPushInterface.setDebugMode(configBean.debugMode);
        JPushInterface.init(context);

        this.iNoticeReceiverUtil = configBean.noticeReceiverUtil;

        DeviceUtils.setMetaData(context, configBean.metaKey, configBean.metaData);
    }

    //注册极光推送
    public void registerJPush(String alias) {
        //将设备IDuserid设置为别名
        // JPushInterface.setAlias(mContext, StringUtil.deleteCharString(DeviceUtils.getDeviceId(SenyintApplication.getInstance().getContext()), '-') + SPUtil.getUserInfo().getId(), StringUtil.tagAlias);
        registerJPush(getTagAliasBean(alias));
    }

    //注册极光推送
    public void registerJPush(JPushTagAliasOperatorHelper.TagAliasBean tagAliasBean) {
        //将设备IDuserid设置为别名
        // JPushInterface.setAlias(mContext, StringUtil.deleteCharString(DeviceUtils.getDeviceId(SenyintApplication.getInstance().getContext()), '-') + SPUtil.getUserInfo().getId(), StringUtil.tagAlias);
        JPushTagAliasOperatorHelper.getInstance().handleAction(
                tagAliasBean);
    }

    public JPushTagAliasOperatorHelper.TagAliasBean getTagAliasBean(String alias) {
        return new JPushTagAliasOperatorHelper.TagAliasBean()
                .setAliasAction(true)
                .setAction(JPushTagAliasOperatorHelper.ACTION_SET)
                .setAlias(true, alias);
    }

    //退出极光推送
    public void logOutJPush() {
        //将空设置为别名
        JPushTagAliasOperatorHelper.getInstance().handleActionClearAll();
        JPushTagAliasOperatorHelper.getInstance().handleAction(
                getTagAliasBean(""));

    }

    public INoticeReceiverUtil getiNoticeReceiverUtil() {
        return iNoticeReceiverUtil;
    }

    public class JPushConfigBean {
        INoticeReceiverUtil noticeReceiverUtil;
        String metaKey;
        String metaData;
        boolean debugMode;

        public JPushConfigBean(INoticeReceiverUtil noticeReceiverUtil, String metaKey, String metaData, boolean debugMode) {
            this.noticeReceiverUtil = noticeReceiverUtil;
            this.metaKey = metaKey;
            this.metaData = metaData;
            this.debugMode = debugMode;
        }

    }
}
