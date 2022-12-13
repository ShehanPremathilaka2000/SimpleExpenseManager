package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.myDataBase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private myDataBase myDB;

    private static final String TRANSACTION_TABLE = "transactions";
    private static final String DATE = "date";
    private static final String ACCOUNT_NO = "accountno";
    private static final String TYPE = "expenseType";
    private static final String AMOUNT = "amount";

    public PersistentTransactionDAO(myDataBase db){
        myDB = db;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        if(expenseType == ExpenseType.EXPENSE){
            PersistentAccountDAO pa = new PersistentAccountDAO(myDB);
            try {
                Account user = pa.getAccount(accountNo);
                if(user.getBalance() < amount){
                    return;
                }
            }catch (Exception e){
                System.out.println("Invalid Account");
            }
        }

        ContentValues transContent = new ContentValues();

        transContent.put(ACCOUNT_NO, accountNo);
        transContent.put(DATE, date.toString());
        transContent.put(TYPE, typeToStr(expenseType));
        transContent.put(AMOUNT, amount);

        myDB.insertToDB(TRANSACTION_TABLE, transContent);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        Cursor result = myDB.getData(TRANSACTION_TABLE,new String[] {"*"},new String[][] {});
        List<Transaction> transactions = new ArrayList<>();

        if(result.getCount() > 0) {
            while (result.moveToNext()) {
                Transaction transaction = new Transaction(stringToDate(result.getString(2)),result.getString(1), findType(result.getString(3)),result.getDouble(4) );
                transactions.add(transaction);
            }
        }

        result.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        Cursor cursor = myDB.getDataWithLimit(TRANSACTION_TABLE,new String[] {"*"},new String[][] {},limit);
        List<Transaction> transactions = new ArrayList<>();

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                    Transaction transaction = new Transaction(stringToDate(cursor.getString(2)),cursor.getString(1), findType(cursor.getString(3)),cursor.getDouble(4) );
                    transactions.add(transaction);
            }
        }

        cursor.close();
        return transactions;
    }

    public Date stringToDate(String strDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date date = new Date();

        try{
            date = dateFormat.parse(strDate);
        }catch(Exception e){
            System.out.println(e);
        }

        return date;
    }

    public ExpenseType findType(String expense){
        if(expense.equals("Expense")){
            return ExpenseType.EXPENSE;
        }
        return ExpenseType.INCOME;
    }

    public String typeToStr(ExpenseType expense){
        if(expense == ExpenseType.EXPENSE){
            return "Expense";
        }
        return "Income";
    }
}
