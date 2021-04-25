package com.framelibrary.jpush.receiver;

import android.content.Intent;

/**
 * @Author: wangweixu
 * @Date: 2021/04/25 10:31:57
 * @Description: 通知处理工具接口类
 * @Version: v1.0
 */
public interface INoticeReceiverUtil {
    Intent mIntent = null;
    String mAn = "";
    String mUpdate = "";

    /**
     * 根据消息通知类型做相应处理
     *
     * @param extraMessage 传递的JSON数据
     * @param isClick      当前是否是点击调用
     */
    public void dealWithFlowByMsgType(String extraMessage, boolean isClick);

    // 注销登录
    public void reLogout();

    /**
     * 根据课程小节ID进入课程直播
     *
     * @param sectionId 小节ID
     */
    public void startOpenLive(String sectionId);
}
