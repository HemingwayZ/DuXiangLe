package com.zhm.duxiangle.utils;

import android.content.Context;

import com.lidroid.xutils.DbUtils;

/**
 * Created by zhm on 2015/10/23.
 */
public class DXLDbUtils {
    public static String DB_BOOK="db_books";
    private DXLDbUtils() {
    }

    ;
    private static DXLDbUtils dxlDbUtils;
    private static Context mContext;
    private DbUtils dbUtils;

    /**
     * 单例模式获取数据库对象
     */
    public static DXLDbUtils getInstance(Context context) {
        mContext = context;
        return dxlDbUtils == null ? new DXLDbUtils() : dxlDbUtils;
    }

    /**
     * 根据数据库的名称获取数据库
     * @param dbName
     * @return
     */
    public DbUtils getDbByName(String dbName) {
        if (null == dbUtils) {
            dbUtils = DbUtils.create(mContext, dbName);
        }
        return dbUtils;
    }

    /**
     * 创建一个数据库
     * @param dbName
     * @return
     */
    public DbUtils createDb(String dbName){
        return getDbByName(dbName);
    }

}
