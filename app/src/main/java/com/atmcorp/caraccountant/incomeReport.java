package com.atmcorp.caraccountant;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


public class incomeReport extends AppCompatActivity {

    Spinner spinnerYears;

    SQLiteDatabase db;
    String DB_NAME;

    String stringSelectedYear;


    ArrayList<String[]> al_incomeYearly = new ArrayList<String[]>();
    ArrayList<String[]> incomeMonthly = new ArrayList<String[]>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_report);


        updateSpinnerYears();

        FetchIncomeYearlyFromDB();
    }

    public void updateSpinnerYears() {
        spinnerYears = (Spinner) findViewById(R.id.spinner_Year);
        ArrayList<String> yearsFromDB = GetIncomeYearsFromDB();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearsFromDB);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYears.setAdapter(dataAdapter);
    }

    public ArrayList<String> GetIncomeYearsFromDB()
    {
        DB_NAME = getString(R.string.DB_NAME); //getting the value from strings.xml

        ArrayList<String> tempYears = new ArrayList<>();
        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT DISTINCT SUBSTR(dateField,7) FROM " + getString(R.string.TABLE_INCOME),null);

        String curItem = "";

        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do{
                    curItem = c.getString(0);
                    tempYears.add(curItem);
                }while(c.moveToNext());
            }
        }
        db.close();
        return tempYears;

    }

    public void callShowYearlyIncomeReport(View view) {

        al_incomeYearly.clear();


        FetchIncomeYearlyFromDB();


    }

    public void FetchIncomeYearlyFromDB()
    {
        String currNetIncome ="",currHST="";

        stringSelectedYear = String.valueOf(spinnerYears.getSelectedItem());

        DB_NAME = getString(R.string.DB_NAME);
        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);

        Cursor c = db.rawQuery("SELECT sum(netIncome)," +
                "sum(approxHST) " +
                "FROM " + getString(R.string.TABLE_INCOME) + " WHERE substr(dateField,7) LIKE '" + stringSelectedYear +
                "'",null);

        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do{

                    currNetIncome = c.getString(0);
                    currHST = c.getString(1);
                    String[] curEntry = {"Net Income : "+ currNetIncome," Approx HST : " +currHST};
                    al_incomeYearly.add(curEntry);
                }while(c.moveToNext());
            }
        }
        db.close();

        createIncomeReportChart(Float.valueOf(currNetIncome),Float.valueOf(currHST));
    }


    public void createIncomeReportChart(float currYearNetIncome, float currYearHST)
    {
        PieChartView pieChartView = findViewById(R.id.chart);
        ArrayList<SliceValue> pieData = new ArrayList<SliceValue>();

        pieData.add(new SliceValue(currYearNetIncome, Color.GREEN).setLabel("Net Income : "+currYearNetIncome));
        pieData.add(new SliceValue(currYearHST, Color.RED).setLabel("HST : "+currYearHST));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);

        pieChartView.setPieChartData(pieChartData);

        pieChartView.setChartRotation(270,true);


    }
}
