package com.example.caraccountant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

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
