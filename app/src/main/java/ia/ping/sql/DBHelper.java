package ia.ping.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ia.ping.util.PingConstants;

/**
 * Created by parath on 7/16/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_PHONE = "phone";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text)"
        );

        db.execSQL(
                "create table config " +
                        "(name text primary key, value text)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact(String name, String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "phone = ? ",
                new String[] { phone });
    }

    public ArrayList<String> getAllContacts()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select * from contacts", null);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_PHONE)));
                res.moveToNext();
            }
            return array_list;
        }
        finally
        {
            if (res != null)
                res.close();
        }
    }

    public boolean isRegisteredContact(String phone)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            if (phone.length() > 10)
            {
                phone = phone.substring(phone.length() - 10);
            }
            res = db.rawQuery("select * from contacts where phone like ?", new String[]{ "%" + phone });
            return res.getCount() > 0;
        }
        finally
        {
           if (res != null)
               res.close();
        }
    }

    public void insertOrUpdateConfig(String configName, String value)
    {
        if(configName == null)
            return;
        String sql = "insert or replace into config ( name, value) values(?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(sql, new String[]{configName, value});
        }
        finally {
            db.close();
        }
    }

    public String getConfigValue(String configName)
    {
        String value = null;
        if(configName != null)
        {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = null;
            try {
                res = db.rawQuery("select value from config where name = ?",
                        new String[]{configName });
                if (res != null && res.getCount() > 0) {
                    res.moveToFirst();
                    value = res.getString(res.getColumnIndex("value"));
                }
                return value;
            }
            finally
            {
                if (res != null)
                    res.close();
            }
        }
        return value;
    }

}
