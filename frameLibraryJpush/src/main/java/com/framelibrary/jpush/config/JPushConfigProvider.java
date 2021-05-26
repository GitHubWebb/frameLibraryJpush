package com.framelibrary.jpush.config;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2021/05/25 15:01:55
 *      description:    使用ContentProvider机制配置SDK初始化
 *      version:        v1.0
 * </pre>
 */
public class JPushConfigProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        Context application = getContext().getApplicationContext();
        if (application == null)
            return true;


        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
