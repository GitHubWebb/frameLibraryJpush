# frameLibraryJpush
极光推送Module集成

## 使用说明
  该框架为AndroidX环境

## 使用方式
1. 必须先在项目初始化的时候先调用 JPushClient#init(Context context, JPushConfigBean configBean)  配置实现类
2. 需要在主module(通常是app-module)中 
                        defaultConfig{
                                manifestPlaceholders = [
                                                            JPUSH_PKGNAME : applicationId,
                                                            JPUSH_APPKEY  : "你的 Appkey ", //JPush 上注册的包名对应的 Appkey.
                                                            JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.
                                                    ]}
                                                    
3.                                                     