# frameLibraryJpush
极光推送Module集成

## 使用说明
  该框架为AndroidX环境

## 引用方式
[![](https://jitpack.io/v/GitHubWebb/frameLibraryJpush.svg)](https://jitpack.io/#GitHubWebb/frameLibraryJpush)
[![Github Actions JPush-AndroidX](https://github.com/GitHubWebb/frameLibraryJpush/actions/workflows/Build-Release-JPush-AndroidX.yml/badge.svg)](https://github.com/GitHubWebb/frameLibraryJpush/actions/workflows/Build-Release-JPush-AndroidX.yml)

## 使用方式
1. 必须先在项目初始化的时候先调用 JPushClient#init(Context context, JPushConfigBean configBean)  配置实现类
   ```java
    
        JPushClient.getInstance().init(instance,
                JPushClient.getInstance().new JPushConfigBean(
                        new NoticeReceiverUtilImpl(),
                        "", "", true
                ));

    ```

2. 需要在主module(通常是app-module)中 
                        defaultConfig{
                                manifestPlaceholders = [
                                                            JPUSH_PKGNAME : applicationId,
                                                            JPUSH_APPKEY  : "你的 Appkey ", //JPush 上注册的包名对应的 Appkey.
                                                            JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.
                                                    ]}
                                                    
                            
3. 注册极光
    ```java
    JPushTagAliasOperatorHelper.TagAliasBean tagAliasBean = JPushClient.getInstance().getTagAliasBean(baseBean.data.getId());
                JPushClient.getInstance().registerJPush(tagAliasBean);
    ```                                     
4. 退出极光
    ```java
    JPushClient.getInstance().logOutJPush();
    ``` 