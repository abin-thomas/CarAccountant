package com.atmcorp.caraccountant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbSetup();

    }

    private void dbSetup()
    {
        String DB_NAME = getString(R.string.DB_NAME); //getting the value from strings.xml
        //create our db
        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);


        //create the income table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + getString(R.string.TABLE_INCOME) +
                "(recordID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "dateField TEXT, startKms INTEGER,endKms INTEGER," +
                "totalKms INTEGER,eatsEarning NUMERIC, grossEarning  NUMERIC,"+
                "hst NUMERIC, netEarning NUMERIC);");

        //create the expense table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + getString(R.string.TABLE_EXPENSE) +
                "(recordID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "dateField TEXT, expenseCategory TEXT,odometer INTEGER," +
                "expenseAmount NUMERIC);");

        db.close();
    } //dbSetup

    public void callIncome(View view) {
        Intent incomeIntent = new Intent(MainActivity.this, income.class);
        startActivity(incomeIntent);
    }

    public void callExpense(View view) {
        Intent expenseIntent = new Intent(MainActivity.this, expense.class);
        startActivity(expenseIntent);
    }

    public void callIncomeReport(View view) {
        Intent incomeReportIntent = new Intent(MainActivity.this, incomeReport.class);
        startActivity(incomeReportIntent);
    }

    public void callExpenseReport(View view) {
        Intent expenseReportIntent = new Intent(MainActivity.this, expenseReport.class);
        startActivity(expenseReportIntent);
    }
}
