package com.atmcorp.caraccountant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.util.ArrayList;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


public class incomeReport extends AppCompatActivity {

    Spinner spinnerYears;

    SQLiteDatabase db;
    String DB_NAME;

    String stringSelectedYear;


    private final String[] monthNumbers ={"00","01","02","03","04","05","06","07","08","09","10","11","12"};

    ArrayList<String[]> al_incomeYearly = new ArrayList<String[]>();
    ArrayList<String[]> al_incomeMonthly = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_report);


        updateSpinnerYears();

        FetchIncomeYearlyFromDB();
        //FetchIncomeMonthlyFromDB();


    }



    public void updateSpinnerYears() {
        spinnerYears = (Spinner) findViewById(R.id.spinner_Year);
        ArrayList<String> yearsFromDB = FetchIncomeYearsFromDB();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearsFromDB);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYears.setAdapter(dataAdapter);
    }

    public ArrayList<String> FetchIncomeYearsFromDB()
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

        clearTableLayout();

        al_incomeYearly.clear();


        FetchIncomeYearlyFromDB();

        CreateMonthlyIncomeSummary();


    }
    public void clearTableLayout()
    {
        TableLayout masterTable = findViewById(R.id.tableIncomeReport);
        int count = masterTable.getChildCount();
        for (int i = 0; i <= count; i++) {
            View child = masterTable.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
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

        CreateIncomeReportChart(Float.valueOf(currNetIncome),Float.valueOf(currHST));
    }

    public int FetchIncomeMonthlyFromDB(String month) {
        al_incomeMonthly.clear();

        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT sum(netIncome)," +
                "sum(approxHST) " +
                "FROM " + getString(R.string.TABLE_INCOME) + " WHERE substr(dateField,7) LIKE '" + stringSelectedYear +
                "' AND substr(dateField,4,2) LIKE '" + month +
                "'",null);

        String monthlyNetEarning,monthlyHST;


        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do{
                    monthlyNetEarning = c.getString(0);
                    if(monthlyNetEarning ==null)
                    {
                        db.close();
                        return 0;
                    }
                    monthlyHST = c.getString(1);
                    String[] curEntry = {monthlyNetEarning,monthlyHST};
                    al_incomeMonthly.add(curEntry);
                }while(c.moveToNext());
            }
        }
        db.close();
        return c.getCount();
    }

    public void CreateMonthlyIncomeSummary()
    {
        TableLayout masterTable = findViewById(R.id.tableIncomeReport);
        TextView tv;
        TableRow tr;
        String titleMonthSummary="";


        for(int i = 1;i<=12;i++)
        {


            int recordFound = FetchIncomeMonthlyFromDB(monthNumbers[i]);

            if(recordFound >0)
            {

                switch (monthNumbers[i])
                {
                    case "01":
                    {
                        titleMonthSummary = "January Summary";
                        break;
                    }
                    case "02":
                    {
                        titleMonthSummary = "February Summary";
                        break;
                    }
                    case "03":
                    {
                        titleMonthSummary = "March Summary";
                        break;
                    }
                    case "04":
                    {
                        titleMonthSummary = "April Summary";
                        break;
                    }
                    case "05":
                    {
                        titleMonthSummary = "May Summary";
                        break;
                    }
                    case "06":
                    {
                        titleMonthSummary = "June Summary";
                        break;
                    }
                    case "07":
                    {
                        titleMonthSummary = "July Summary";
                        break;
                    }
                    case "08":
                    {
                        titleMonthSummary = "August Summary";
                        break;
                    }
                    case "09":
                    {
                        titleMonthSummary = "September Summary";
                        break;
                    }
                    case "10":
                    {
                        titleMonthSummary = "October Summary";
                        break;
                    }
                    case "11":
                    {
                        titleMonthSummary = "November Summary";
                        break;
                    }
                    case "12":
                    {
                        titleMonthSummary = "December Summary";
                        break;
                    }

                }//switch
                //To add the Month Summary Title
                tv = new TextView(this);
                tv.setText(titleMonthSummary);
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                tv.setTextSize(18);
                tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tr.addView(tv);
                masterTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                //Month Summary Title Ends here

                ArrayList<String> tempValue= new ArrayList<>();

                for(int j=0;j<recordFound;j++)
                {
                    String[] cat_1_Items = al_incomeMonthly.get(j);
                    tempValue.add("Net Income : $"+ cat_1_Items[0] +"\n" +"HST : $"+ cat_1_Items[1]);
                }

                ArrayAdapter<String> clients_adapter = new ArrayAdapter<>(
                        this,android.R.layout.simple_list_item_1,tempValue);

                tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                ListView lv = new ListView(this);
                lv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                lv.setAdapter(clients_adapter);

                tr.addView(lv);

                masterTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            } //if (recordFound>0)
        } //for loop

    }//GenerateMontlyIncomeSummary()


    public void CreateIncomeReportChart(float currYearNetIncome, float currYearHST)
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
