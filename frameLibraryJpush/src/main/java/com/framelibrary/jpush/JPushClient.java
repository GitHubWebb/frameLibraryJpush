package com.framelibrary.jpush;

import android.content.Context;

import com.framelibrary.jpush.bean.TagAliasBean;
import com.framelibrary.jpush.receiver.INoticeReceiverUtil;
import com.framelibrary.util.DeviceUtils;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;


/**
 * @Author: wangweixu
 * @Date: 2021/04/25 14:23:00
 * @Description: 极光推送配置类
 * @Version: v1.0
 */
public class JPushClient {

    //使用volatile修饰 目的是为了在JVM层编译顺序一致
    private static volatile JPushClient jPushClient = null;
    private INoticeReceiverUtil iNoticeReceiverUtil; // 有实现类实现具体方法逻辑

    private JPushClient() {
    }

    public static JPushClient getInstance() {

        //第一次校验
        if (jPushClient == null) {
            synchronized (JPushClient.class) {

                //第二次校验
                if (jPushClient == null) {
                    jPushClient = new JPushClient();
                }
            }
        }
        return jPushClient;
    }

    public INoticeReceiverUtil getiNoticeReceiverUtil() {
        return iNoticeReceiverUtil;
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
    public TagAliasBean registerJPush(String alias) {
        //将设备IDuserid设置为别名
        // JPushInterface.setAlias(mContext, StringUtil.deleteCharString(DeviceUtils.getDeviceId(SenyintApplication.getInstance().getContext()), '-') + SPUtil.getUserInfo().getId(), StringUtil.tagAlias);
        return registerJPush(getTagAliasBean(alias));
    }

    //注册极光推送
    public TagAliasBean registerJPush(TagAliasBean tagAliasBean) {
        //将设备IDuserid设置为别名
        // JPushInterface.setAlias(mContext, StringUtil.deleteCharString(DeviceUtils.getDeviceId(SenyintApplication.getInstance().getContext()), '-') + SPUtil.getUserInfo().getId(), StringUtil.tagAlias);
        JPushTagAliasOperatorHelper.getInstance().handleAction(
                tagAliasBean);
        return tagAliasBean;
    }

    /**
     * 通知权限是否开启
     *
     * @return 0 未开启
     */
    public int isNotificationEnabled(Context context) {
        return JPushInterface.isNotificationEnabled(context);
    }

    /**
     * 跳转到应用设置 用于设置通知权限
     */
    public void goToAppNotificationSettings(Context context) {
        JPushInterface.goToAppNotificationSettings(context);
    }

    /**
     * 申请定位、存储权限
     */
    public void requestPermission(Context context) {
        JPushInterface.requestPermission(context);
    }

    /**
     * 获取极光注册成功后,极光生成的唯一设备ID
     *
     * @return
     */
    public String getRegistrationID(Context context) {
        return JPushInterface.getRegistrationID(context);
    }

    public TagAliasBean getTagAliasBean(String alias) {
        return getTagAliasBean(true, alias);
    }

    public TagAliasBean getTagAliasBean(boolean isJoinDeviceId, String alias) {
        return new TagAliasBean()
                .setAliasAction(true)
                .setAction(JPushTagAliasOperatorHelper.ACTION_SET)
                .setAlias(isJoinDeviceId, alias);
    }

    //退出极光推送
    public void logOutJPush() {
        //将空设置为别名
        JPushTagAliasOperatorHelper.getInstance().handleActionClearAll();
        JPushTagAliasOperatorHelper.getInstance().handleAction(
                getTagAliasBean(""));

    }

    /**
     * @Author:         wangweixu
     * @Date:           2021/05/25 13:59:28
     * @Description:    标签运算结果
     * @Version:        v1.0
     */
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        JPushTagAliasOperatorHelper.getInstance().onTagOperatorResult(context, jPushMessage);
    }


    /**
     * @Author:         wangweixu
     * @Date:           2021/05/25 14:00:20
     * @Description:    检查标签运算符结果
     * @Version:        v1.0
     */
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        JPushTagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
    }

    /**
     * @Author:         wangweixu
     * @Date:           2021/05/25 14:01:28
     * @Description:    别名运算符结果
     * @Version:        v1.0
     */
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        JPushTagAliasOperatorHelper.getInstance().onAliasOperatorResult(context, jPushMessage);
    }

    /**
     * @Author:         wangweixu
     * @Date:           2021/05/25 14:02:35
     * @Description:    手机号码运营商结果
     * @Version:        v1.0
     */
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        JPushTagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context, jPushMessage);
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
