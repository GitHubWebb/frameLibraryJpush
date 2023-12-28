package com.framelibrary.jpush;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.framelibrary.config.FrameLibBaseApplication;
import com.framelibrary.jpush.bean.TagAliasBean;
import com.framelibrary.util.ApplicationUtil;
import com.framelibrary.util.ToastUtils;
import com.framelibrary.util.logutil.LoggerUtils;

import java.util.Locale;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;

import static cn.jpush.android.api.JPushInterface.deleteAlias;

/**
 * @Author : wangweixu
 * @Date : 2021/04/25 10:06:33
 * @Description: 处理极光别名相关  \n  处理tagalias相关的逻辑
 * @Version: v1.0
 */
class JPushTagAliasOperatorHelper {
    /**
     * 增加
     */
    public static final int ACTION_ADD = 1;
    /**
     * 覆盖
     */
    public static final int ACTION_SET = 2;
    /**
     * 删除部分
     */
    public static final int ACTION_DELETE = 3;
    /**
     * 删除所有
     */
    public static final int ACTION_CLEAN = 4;
    /**
     * 查询
     */
    public static final int ACTION_GET = 5;
    public static final int ACTION_CHECK = 6;
    public static final int DELAY_SEND_ACTION = 1;
    public static final int DELAY_SET_MOBILE_NUMBER_ACTION = 2;
    public static int sequence = 1;
    private static JPushTagAliasOperatorHelper mInstance;
    private Context context;
    private SparseArray<Object> setActionCache = new SparseArray<Object>();
    private Handler delaySendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_SEND_ACTION:
                    if (msg.obj != null && msg.obj instanceof TagAliasBean) {
                        LoggerUtils.I("on delay time");
//                        sequence++;
                        TagAliasBean tagAliasBean = (TagAliasBean) msg.obj;
                        setActionCache.put(sequence, tagAliasBean);
                        if (context != null) {
                            handleAction(tagAliasBean);
                        } else {
                            LoggerUtils.E("#unexcepted - context was null");
                        }
                    } else {
                        LoggerUtils.D("#unexcepted - msg obj was incorrect");
                    }
                    break;
                case DELAY_SET_MOBILE_NUMBER_ACTION:
                    if (msg.obj != null && msg.obj instanceof String) {
                        LoggerUtils.I("retry set mobile number");

                        String mobileNumber = (String) msg.obj;
                        setActionCache.put(sequence, mobileNumber);
                        if (context != null) {
                            handleAction(mobileNumber);
                        } else {
                            LoggerUtils.E("#unexcepted - context was null");
                        }
                    } else {
                        LoggerUtils.D("#unexcepted - msg obj was incorrect");
                    }
                    break;
            }
        }
    };

    private JPushTagAliasOperatorHelper() {
    }

    public static JPushTagAliasOperatorHelper getInstance() {
        if (mInstance == null) {
            synchronized (JPushTagAliasOperatorHelper.class) {
                if (mInstance == null) {
                    mInstance = new JPushTagAliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    public Object get(int sequence) {
        return setActionCache.get(sequence);
    }

    public Object remove(int sequence) {
        return setActionCache.get(sequence);
    }

    public void put(int sequence, Object tagAliasBean) {
        setActionCache.put(sequence, tagAliasBean);
    }

    public void handleAction(String mobileNumber) {
        Context context = ApplicationUtil.getInstance().getApplication();
        // sequence 代表一个序号，这个是和老方法的主要区别，目的是区分设置的序号，本类静态成员变量，需要注意的是在调用之前一定要先把它+1，不然会有问题
        sequence++;

        put(sequence, mobileNumber);
        LoggerUtils.D("sequence:" + sequence + ",mobileNumber:" + mobileNumber);
        JPushInterface.setMobileNumber(context, sequence, mobileNumber);
    }

    /**
     * 处理清除所有别名
     */
    public void handleActionClearAll() {
        TagAliasBean tagAliasBean = new TagAliasBean();
        tagAliasBean
                .setAliasAction(true)
                .setAction(JPushTagAliasOperatorHelper.ACTION_CLEAN)
                .setAlias("");
        handleAction(tagAliasBean);

        // TODO: wangweixu 2021/04/25 10:09:59 清除所有别名后续操作由调用者执行
//        new RxPresenter().getNoticeAppRmAliase();
    }

    /**
     * 处理设置tag
     */
    public void handleAction(TagAliasBean tagAliasBean) {
        Context context = ApplicationUtil.getInstance().getApplication();

        init(context);

        // sequence 代表一个序号，这个是和老方法的主要区别，目的是区分设置的序号，本类静态成员变量，需要注意的是在调用之前一定要先把它+1，不然会有问题
        sequence++;

        if (tagAliasBean == null) {
            LoggerUtils.D("tagAliasBean was null");
            return;
        }
        put(sequence, tagAliasBean);
        if (tagAliasBean.isAliasAction()) {
            switch (tagAliasBean.getAction()) {
                case ACTION_GET:
                    JPushInterface.getAlias(context, sequence);
                    break;
                case ACTION_DELETE:
                case ACTION_CLEAN:
                    deleteAlias(context, sequence);
                    break;
                case ACTION_SET:
                    JPushInterface.setAlias(context, sequence, tagAliasBean.getAlias());
                    break;
                default:
                    LoggerUtils.D("unsupport alias action type");
                    return;
            }
        } else {
            switch (tagAliasBean.getAction()) {
                case ACTION_ADD:
                    JPushInterface.addTags(context, sequence, tagAliasBean.getTags());
                    break;
                case ACTION_SET:
                    JPushInterface.setTags(context, sequence, tagAliasBean.getTags());
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteTags(context, sequence, tagAliasBean.getTags());
                    break;
                case ACTION_CHECK:
                    //一次只能check一个tag
                    String tag = (String) tagAliasBean.getTags().toArray()[0];
                    JPushInterface.checkTagBindState(context, sequence, tag);
                    break;
                case ACTION_GET:
                    JPushInterface.getAllTags(context, sequence);
                    break;
                case ACTION_CLEAN:
                    JPushInterface.cleanTags(context, sequence);
                    break;
                default:
                    LoggerUtils.D("unsupport tag action type");
                    return;
            }
        }

    }

    private boolean RetryActionIfNeeded(int errorCode, TagAliasBean tagAliasBean) {

        //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
        if (errorCode == 6002 || errorCode == 6014) {
            LoggerUtils.D("need retry");
            if (tagAliasBean != null) {
                Message message = new Message();
                message.what = DELAY_SEND_ACTION;
                message.obj = tagAliasBean;
                delaySendHandler.sendMessageDelayed(message, 1000 * 60);
                String logs = getRetryStr(tagAliasBean.isAliasAction(), tagAliasBean.getAction(), errorCode);
                ToastUtils.showToast(logs);
                return true;
            }
        }
        return false;
    }

    private boolean RetrySetMObileNumberActionIfNeeded(int errorCode, String mobileNumber) {


        //返回的错误码为6002 超时,6024 服务器内部错误,建议稍后重试
        if (errorCode == 6002 || errorCode == 6024) {
            LoggerUtils.D("need retry");
            Message message = new Message();
            message.what = DELAY_SET_MOBILE_NUMBER_ACTION;
            message.obj = mobileNumber;
            delaySendHandler.sendMessageDelayed(message, 1000 * 60);
            String str = "Failed to set mobile number due to %s. Try again after 60s.";
            str = String.format(Locale.ENGLISH, str, (errorCode == 6002 ? "timeout" : "server internal error”"));
            ToastUtils.showToast(str);
            return true;
        }
        return false;

    }

    private String getRetryStr(boolean isAliasAction, int actionType, int errorCode) {
        String str = "Failed to %s %s due to %s. Try again after 60s.";
        str = String.format(Locale.ENGLISH, str, getActionStr(actionType), (isAliasAction ? "alias" : " tags"), (errorCode == 6002 ? "timeout" : "server too busy"));
        return str;
    }

    private String getActionStr(int actionType) {
        switch (actionType) {
            case ACTION_ADD:
                return "add";
            case ACTION_SET:
                return "set";
            case ACTION_DELETE:
                return "delete";
            case ACTION_GET:
                return "get";
            case ACTION_CLEAN:
                return "clean";
            case ACTION_CHECK:
                return "check";
        }
        return "unkonw operation";
    }

    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        if (context == null)
            return;

        int sequence = jPushMessage.getSequence();
        LoggerUtils.I("action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags());
        LoggerUtils.I("tags size:" + jPushMessage.getTags().size());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            ToastUtils.showToast("获取缓存记录失败");
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            LoggerUtils.I("action - modify tag Success,sequence:" + sequence);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.getAction()) + " tags success";
            LoggerUtils.I(logs);
            ToastUtils.showToast(logs);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.getAction()) + " tags";
            if (jPushMessage.getErrorCode() == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            LoggerUtils.E(logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                ToastUtils.showToast(logs);
            }
        }
    }

    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        LoggerUtils.I("action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            ToastUtils.showToast("获取缓存记录失败");
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            LoggerUtils.I("tagBean:" + tagAliasBean);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.getAction()) + " tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
            LoggerUtils.I(logs);
            ToastUtils.showToast(logs);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.getAction()) + " tags, errorCode:" + jPushMessage.getErrorCode();
            LoggerUtils.E(logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                ToastUtils.showToast(logs);
            }
        }
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        LoggerUtils.I("action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            ToastUtils.showToast("获取缓存记录失败");
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            LoggerUtils.I("action - modify alias Success,sequence:" + sequence);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.getAction()) + " alias success";
            LoggerUtils.I(logs);
            ToastUtils.showToast(logs);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.getAction()) + " alias, errorCode:" + jPushMessage.getErrorCode();
            LoggerUtils.E(logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                ToastUtils.showToast(logs);
            }
        }
    }

    //设置手机号码回调
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        LoggerUtils.I("action - onMobileNumberOperatorResult, sequence:" + sequence + ",mobileNumber:" + jPushMessage.getMobileNumber());
        init(context);
        if (jPushMessage.getErrorCode() == 0) {
            LoggerUtils.I("action - set mobile number Success,sequence:" + sequence);
            setActionCache.remove(sequence);
        } else {
            String logs = "Failed to set mobile number, errorCode:" + jPushMessage.getErrorCode();
            LoggerUtils.E(logs);
            if (!RetrySetMObileNumberActionIfNeeded(jPushMessage.getErrorCode(), jPushMessage.getMobileNumber())) {
                ToastUtils.showToast(logs);
            }
        }
    }
}
