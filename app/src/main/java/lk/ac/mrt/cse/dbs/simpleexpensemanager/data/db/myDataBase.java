package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.database.Cursor;

public class myDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME =  "200482F.db";

    private static final String TABLE_NAME_ACCOUNT = "account";
    private static final String ACCOUNT_NO = "accountno";
    private static final String BANK = "bankname";
    private static final String ACC_HOLDER_NAME = "accountHolderName";
    private static final String BALANCE = "balance";

    private static final String TABLE_NAME_TRANSACTION = "transactions";
    private static final String TRANSACTION_ID = "transaction_no";
    private static final String DATE = "date";
    private static final String TYPE = "expenseType";
    private static final String AMOUNT = "amount";

    public myDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_NAME_ACCOUNT +
                "(" + ACCOUNT_NO+ " TEXT PRIMARY KEY,"
                +BANK + " TEXT,"
                +ACC_HOLDER_NAME + " TEXT,"
                +BALANCE + " REAL"
                +")");

        db.execSQL("CREATE TABLE "+ TABLE_NAME_TRANSACTION +
                " (" + TRANSACTION_ID + "INTEGER  PRIMARY KEY AUTOINCREMENT,"+
                ACCOUNT_NO + "TEXT  ,"+
                DATE + "TEXT, "+
                TYPE + "TEXT ,"+
                AMOUNT + "REAL," +
                "FOREIGN KEY (" + ACCOUNT_NO + ")" + "REFERENCES " + TABLE_NAME_ACCOUNT + " (" + ACCOUNT_NO + ")"
                +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_TRANSACTION);

        onCreate(db);
    }

    public void insertToDB(String tb_name, ContentValues content){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try{
           sqLiteDatabase.insertOrThrow(tb_name, null,content);
        }catch(Exception e){
            System.out.println(e);
        }

        sqLiteDatabase.close();
    }

    public Cursor getDataWithLimit(String tb_name, String [] columns, String [][] conditions,int limit){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String cols = "";
        String condition = "";
        String[] args = null;
        String lim = "";

        if (columns.length != 0){
            for (int i = 0;i < columns.length ;i++){
                cols += columns[i]+" , ";
            }
            cols = cols.substring(0,cols.length()-2);
        }

        if(conditions.length != 0){
            args = new String[conditions.length];
            condition += " WHERE ";
            for (int i = 0;i < conditions.length ;i++){
                if(conditions[i].length == 3){
                    String[] temp = conditions[i];
                    condition += temp[0] + " "+temp[1]+" ? AND ";
                    args[i] = temp[2];
                }

            }
            condition = condition.substring(0,condition.length()-4);
        }else{
            condition = "";
        }

        if(limit != 0){
            lim = " LIMIT " + String.valueOf(limit);
        }

        String sql = "select " + cols + " from " + tb_name + condition+lim;
        Cursor cursor = sqLiteDatabase.rawQuery(sql,args);

        return cursor;
    }

    public Cursor getData(String tb_name, String [] columns, String [][] conditions){
        return getDataWithLimit(tb_name, columns, conditions,0);
    }

    public long updateDB(String table_name, ContentValues content, String[ ] condition){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String cond = condition[0] + " " + condition[1] + " ? ";
        String[] args = {condition[2]};

        long result;

        try{
            result = sqLiteDatabase.update(table_name, content,cond,args);
        }catch (Exception e){
            result = -1;
            System.out.println(e);
        }

        return result;
    }

    public Integer deleteFromDB(String tb_name, String column, String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Integer res = sqLiteDatabase.delete(tb_name, column+" = ?", new String[] {id});

        sqLiteDatabase.close();
        return res;
    }

}
