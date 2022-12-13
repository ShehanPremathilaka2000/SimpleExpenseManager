package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.*;
import android.database.Cursor;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.myDataBase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final myDataBase myDB;

    private static final String ACCOUNT_TABLE = "account";

    private static final String ACCOUNT_NO = "accountno";
    private static final String BANK = "bankname";
    private static final String HOLDER_NAME = "accountHolderName";
    private static final String BALANCE = "balance";

    public PersistentAccountDAO(myDataBase db){
        myDB = db;
    }

    @Override
    public List<String> getAccountNumbersList() {
        Cursor cursor = myDB.getData(ACCOUNT_TABLE,new String[] {ACCOUNT_NO}, new String[][] {});
        List<String> accountNumbers = new ArrayList<>();

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                accountNumbers.add(cursor.getString(0));
            }
        }

        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        Cursor cursor = myDB.getData(ACCOUNT_TABLE,new String[] {"*"}, new String[][] {});
        List<Account> accounts = new ArrayList<>();

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
                accounts.add(account);
            }
        }

        cursor.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = myDB.getData(ACCOUNT_TABLE,new String[] {"*"}, new String[][] {{ACCOUNT_NO, "=",accountNo}});

        if(cursor.getCount() == 0){
            throw new InvalidAccountException("Account number is wrong!");
        }

        Account account = null;

        while(cursor.moveToNext()){
            account = new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
        }

        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ACCOUNT_NO, account.getAccountNo());
        contentValues.put(BANK, account.getBankName());
        contentValues.put(HOLDER_NAME, account.getAccountHolderName());
        contentValues.put(BALANCE, account.getBalance());

        myDB.insertToDB(ACCOUNT_TABLE, contentValues);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        int result = myDB.deleteFromDB(ACCOUNT_TABLE,ACCOUNT_NO,accountNo);
        if(result == 0){
            throw new InvalidAccountException("Account number is wrong");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        double balance = 0, total=0;

        try{
            Account acc = getAccount(accountNo);
            balance = acc.getBalance();
        }catch(Exception e){
            throw new InvalidAccountException("Account number is wrong");
        }

        if (expenseType == ExpenseType.EXPENSE){
            if(balance < amount){
                throw new InvalidAccountException("Balance is not sufficient");
            }
            total = balance - amount;
        }else{
            total = balance + amount;
        }

        ContentValues accContent = new ContentValues();

        accContent.put(BALANCE, total);

        long result = myDB.updateDB(ACCOUNT_TABLE,accContent,new String[] {ACCOUNT_NO,"=",accountNo});

        if(result == -1){
            throw new InvalidAccountException("Account number is wrong");
        }
    }


}
