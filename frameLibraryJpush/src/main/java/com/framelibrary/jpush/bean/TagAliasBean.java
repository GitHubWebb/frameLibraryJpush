package com.framelibrary.jpush.bean;

import com.framelibrary.util.DeviceIdUtil;
import com.framelibrary.util.EncryptUtils;
import com.framelibrary.util.StringUtils;
import com.framelibrary.util.gsonconverter.GsonUtil;

import java.util.Map;
import java.util.Set;


/**
 * @Author: wangweixu
 * @Date: 2021/05/25 13:48:45
 * @Description: 极光推送 别名 标签实体信息类
 * @Version: v1.0
 */
public class TagAliasBean {
    private int action;
    private Set<String> tags;
    private String alias;
    private boolean isAliasAction;

    public int getAction() {
        return action;
    }

    public TagAliasBean setAction(int action) {
        this.action = action;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public TagAliasBean setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public TagAliasBean setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    // 是否在别名中加入DeviceId
    public TagAliasBean setAlias(boolean isJoinDeviceId, String alias) {
        if (!StringUtils.isBlank(alias) && isJoinDeviceId)
            setAliasByDeviceID(alias);
        else
            setAlias(alias);
        return this;
    }

    /**
     * 根据设备唯一标识设置极光别名
     *
     * @param alias
     */
    private void setAliasByDeviceID(String alias) {
        // 极端认为 当设备唯一标识获取到空的时候,别名设置没有意义反而传输注册错误的值,
        // 所以当唯一标识为空,则别名为空
        String deviceIdJSON = DeviceIdUtil.getDeviceId();
        Map<String, Object> deviceIdMap = GsonUtil.toMaps(deviceIdJSON);
        String clientId = (String) deviceIdMap.get("clientId");
        if (StringUtils.isBlank(clientId)) {
            setAlias("");
            return;
        }

        String aliasMD5 = alias = EncryptUtils.encryptMD5ToString(clientId + ":" + alias);
        setAlias(aliasMD5);
    }

    public boolean isAliasAction() {
        return isAliasAction;
    }

    public TagAliasBean setAliasAction(boolean aliasAction) {
        isAliasAction = aliasAction;
        return this;
    }

    @Override
    public String toString() {
        return "TagAliasBean{" +
                "action=" + action +
                ", tags=" + tags +
                ", alias='" + alias + '\'' +
                ", isAliasAction=" + isAliasAction +
                '}';
    }
}
