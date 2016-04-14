package com.quanliren.quan_one.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.CounterBean;
import com.quanliren.quan_one.bean.CustomFilterBean;
import com.quanliren.quan_one.bean.CustomFilterQuanBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.MoreLoginUser;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;
import com.quanliren.quan_one.util.LogUtil;
import com.quanliren.quan_one.util.StaticFactory;

import java.sql.SQLException;

public class DBHelper extends OrmLiteSqliteOpenHelper {

    public static ChatListBeanDao chatListBeanDao;
    public static CustomFilterBeanDao customFilterBeanDao;
    public static CustomFilterBeanQuanDao customFilterBeanQuanDao;
    public static DfMessageDao dfMessageDao;
    public static LoginUserDao loginUserDao;
    public static MoreLoginUserDao moreLoginUserDao;
    public static UserTableDao userTableDao;
    public static EmoticonZipDao emoticonZipDao;
    public static CacheDao cacheDao;
    public static CounterDao counterDao;
    public static BadgeDao badgeDao;

    private static Context context;

    public DBHelper(Context context) {
        super(context, StaticFactory.DBName, null, StaticFactory.DBVersion);
        this.context = context;
    }

    public static void init() {
        chatListBeanDao = ChatListBeanDao.getInstance(context);
        customFilterBeanDao = CustomFilterBeanDao.getInstance(context);
        dfMessageDao = DfMessageDao.getInstance(context);
        loginUserDao = LoginUserDao.getInstance(context);
        moreLoginUserDao = MoreLoginUserDao.getInstance(context);
        userTableDao = UserTableDao.getInstance(context);
        emoticonZipDao = EmoticonZipDao.getInstance(context);
        customFilterBeanQuanDao = CustomFilterBeanQuanDao.getInstance(context);
        cacheDao = CacheDao.getInstance(context);
        counterDao = CounterDao.getInstance(context);
        badgeDao = BadgeDao.getInstance(context);
    }

    /**
     * 创建SQLite数据库
     */
    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, CacheBean.class);
            TableUtils.createTable(connectionSource, LoginUser.class);
            TableUtils.createTable(connectionSource, UserTable.class);
            TableUtils.createTable(connectionSource, ChatListBean.class);
            TableUtils.createTable(connectionSource, EmoticonActivityListBean.EmoticonZip.class);
            TableUtils.createTable(connectionSource, DfMessage.class);
            TableUtils.createTable(connectionSource, CustomFilterBean.class);
            TableUtils.createTable(connectionSource, CustomFilterQuanBean.class);
            TableUtils.createTable(connectionSource, MoreLoginUser.class);
            TableUtils.createTable(connectionSource, CounterBean.class);
            TableUtils.createTable(connectionSource, BadgeBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新SQLite数据库
     */
    @Override
    public void onUpgrade(
            SQLiteDatabase sqliteDatabase,
            ConnectionSource connectionSource,
            int oldVer,
            int newVer) {
        try {
            switch (oldVer) {
                case 1:
                    updateDatabase_2();
                case 2:
                    updateDatabase_3();
                case 3:
                    updateDatebase_4(sqliteDatabase);
                    break;
            }
        } catch (Exception e) {
            LogUtil.e(DBHelper.class.getName(),
                    "Unable to upgrade database from version " + oldVer + " to new "
                            + newVer);
        }
    }

    public void updateDatabase_2() throws SQLException {
        TableUtils.dropTable(connectionSource, EmoticonActivityListBean.EmoticonZip.class, true);
        TableUtils.dropTable(connectionSource, EmoticonActivityListBean.EmoticonZip.EmoticonImageBean.class, true);
        TableUtils.createTableIfNotExists(connectionSource, CacheBean.class);
        TableUtils.createTableIfNotExists(connectionSource, LoginUser.class);
        TableUtils.createTableIfNotExists(connectionSource, UserTable.class);
        TableUtils.createTableIfNotExists(connectionSource, ChatListBean.class);
        TableUtils.createTableIfNotExists(connectionSource, EmoticonActivityListBean.EmoticonZip.class);
        TableUtils.createTableIfNotExists(connectionSource, EmoticonActivityListBean.EmoticonZip.EmoticonImageBean.class);
        TableUtils.createTableIfNotExists(connectionSource, DfMessage.class);
        TableUtils.createTableIfNotExists(connectionSource, CustomFilterBean.class);
        TableUtils.createTableIfNotExists(connectionSource, CustomFilterQuanBean.class);
        TableUtils.createTableIfNotExists(connectionSource, MoreLoginUser.class);
    }

    public void updateDatabase_3() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, CounterBean.class);
        TableUtils.createTableIfNotExists(connectionSource, BadgeBean.class);
    }

    public void updateDatebase_4(SQLiteDatabase db) throws SQLException {
        db.beginTransaction();
        try {
            //消息
            db.execSQL("ALTER TABLE DfMessage RENAME TO __temp__DfMessage;");

            db.execSQL("CREATE TABLE DfMessage (content VARCHAR , ctime VARCHAR , friendDetail VARCHAR , msgid VARCHAR , nickname VARCHAR , receiverUid VARCHAR , sendUid VARCHAR , userid VARCHAR , userlogo VARCHAR , download INTEGER , id INTEGER PRIMARY KEY AUTOINCREMENT , isRead INTEGER , msgtype INTEGER , timel INTEGER )");

            db.execSQL("INSERT INTO DfMessage SELECT content , ctime , '' , msgid , nickname , receiverUid , sendUid , userid , userlogo , download , id , isRead , msgtype , timel FROM __temp__DfMessage;");

            db.execSQL("DROP TABLE __temp__DfMessage;");

            db.execSQL("CREATE INDEX DfMessage_userid_idx ON DfMessage ( userid )");

            db.execSQL("CREATE INDEX DfMessage_isRead_idx ON DfMessage ( isRead )");

            db.execSQL("CREATE INDEX DfMessage_download_idx ON DfMessage ( download )");

            db.execSQL("CREATE INDEX DfMessage_sendUid_idx ON DfMessage ( sendUid )");

            db.execSQL("CREATE INDEX DfMessage_msgtype_idx ON DfMessage ( msgtype )");

            db.execSQL("CREATE INDEX DfMessage_receiverUid_idx ON DfMessage ( receiverUid )");

            db.execSQL("CREATE INDEX DfMessage_ctime_idx ON DfMessage ( ctime )");

            db.execSQL("CREATE INDEX DfMessage_msgid_idx ON DfMessage ( msgid )");

            //消息列表
            db.execSQL("ALTER TABLE ChatListBean RENAME TO __temp__ChatListBean;");

            db.execSQL("CREATE TABLE ChatListBean (content VARCHAR , ctime VARCHAR , friendid VARCHAR , nickname VARCHAR , userid VARCHAR , userlogo VARCHAR , id INTEGER PRIMARY KEY AUTOINCREMENT , type INTEGER )");

            db.execSQL("INSERT INTO ChatListBean SELECT content , ctime , friendid , nickname , userid , userlogo , id , 0  FROM __temp__ChatListBean;");

            db.execSQL("DROP TABLE __temp__ChatListBean;");

            db.execSQL("CREATE INDEX ChatListBean_friendid_idx ON ChatListBean ( friendid )");
            
            db.execSQL("CREATE INDEX ChatListBean_userid_idx ON ChatListBean ( userid )");

            //表情包
            /*db.execSQL("ALTER TABLE EmoticonZip RENAME TO __temp__EmoticonZip;");

            db.execSQL("CREATE TABLE EmoticonZip (bannerUrl VARCHAR , downUrl VARCHAR , icoUrl VARCHAR , iconfile VARCHAR , imglist BLOB , name VARCHAR , remark VARCHAR , size VARCHAR , title VARCHAR , userId VARCHAR , price DOUBLE PRECISION , generatedId INTEGER PRIMARY KEY AUTOINCREMENT , have SMALLINT , id INTEGER , isBuy INTEGER , type INTEGER )");

            db.execSQL("INSERT INTO EmoticonZip SELECT bannerUrl , downUrl , icoUrl , iconfile , imglist , name , remark , size , title , userId , price ,null , have , id , isBuy , type   FROM __temp__EmoticonZip;");

            db.execSQL("DROP TABLE __temp__EmoticonZip;");

            db.execSQL("CREATE INDEX EmoticonZip_id_idx ON EmoticonZip ( id )");

            db.execSQL("CREATE INDEX EmoticonZip_userId_idx ON EmoticonZip ( userId )");

            db.execSQL("CREATE INDEX EmoticonZip_iconfile_idx ON EmoticonZip ( iconfile )");*/

            db.execSQL("DROP TABLE EmoticonZip;");

            db.execSQL("CREATE TABLE EmoticonZip (bannerUrl VARCHAR , downUrl VARCHAR , icoUrl VARCHAR , iconfile VARCHAR , imglist BLOB , name VARCHAR , remark VARCHAR , size VARCHAR , title VARCHAR , userId VARCHAR , price DOUBLE PRECISION , generatedId INTEGER PRIMARY KEY AUTOINCREMENT , have SMALLINT , id INTEGER , isBuy INTEGER , type INTEGER )");

            db.execSQL("CREATE INDEX EmoticonZip_id_idx ON EmoticonZip ( id )");

            db.execSQL("CREATE INDEX EmoticonZip_userId_idx ON EmoticonZip ( userId )");

            db.execSQL("CREATE INDEX EmoticonZip_iconfile_idx ON EmoticonZip ( iconfile )");

            
            //表情
            
            db.execSQL("DROP TABLE EmoticonImageBean;");

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }

    public static void clearTable(Context context, Class clazz) {
        try {
            ConnectionSource source = OpenHelperManager.getHelper(context, DBHelper.class).getConnectionSource();
            TableUtils.clearTable(source, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <D extends RuntimeExceptionDao<T, ?>, T> D getDao_(Context context, Class<T> clazz) {
        return OpenHelperManager.getHelper(context, DBHelper.class).getRuntimeExceptionDao(clazz);
    }
}
