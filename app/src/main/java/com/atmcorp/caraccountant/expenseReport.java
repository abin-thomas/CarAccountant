package com.atmcorp.caraccountant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import java.util.Random;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


public class expenseReport extends AppCompatActivity {

    Spinner spinnerYears;

    SQLiteDatabase db;
    String DB_NAME;

    String stringSelectedYear;


    private final String[] monthNumbers ={"00","01","02","03","04","05","06","07","08","09","10","11","12"};



    ArrayList<String[]> al_ExpensesByCatYearly = new ArrayList<String[]>();
    ArrayList<String[]> al_ExpensesByCatMonthly = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        updateSpinnerYears();
        FetchExpenseYearlyFromDB();
    }

    public void updateSpinnerYears() {
        spinnerYears = (Spinner) findViewById(R.id.spinner_Year);
        ArrayList<String> yearsFromDB = FetchExpenseYearsFromDB();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearsFromDB);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYears.setAdapter(dataAdapter);
    }

    public ArrayList<String> FetchExpenseYearsFromDB() {
        DB_NAME = getString(R.string.DB_NAME); //getting the value from strings.xml

        ArrayList<String> tempYears = new ArrayList<>();
        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT DISTINCT SUBSTR(dateField,7) FROM " + getString(R.string.TABLE_EXPENSE),null);

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

    public void callShowYearlyExpenseReport(View view) {
        clearTableLayout();

        al_ExpensesByCatYearly.clear();


        FetchExpenseYearlyFromDB();

        CreateMonthlyExpenseSummary();
    }

    public void CreateMonthlyExpenseSummary() {
        TableLayout masterTable = findViewById(R.id.tableExpenseReport);
        TextView tv;
        TableRow tr;
        String titleMonthSummary="";


        for(int i = 1;i<=12;i++)
        {


            int recordFound = FetchExpenseByCatMonthlyFromDB(monthNumbers[i]);

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
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tr.addView(tv);
                masterTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
                //Month Summary Title Ends here

                ArrayList<String> tempValue= new ArrayList<>();
                String temp="";
                for(int j=0;j<recordFound;j++)
                {
                    String[] cat_1_Items = al_ExpensesByCatMonthly.get(j);
                    temp += cat_1_Items[0] + " : $" + cat_1_Items[1] + "\n";
                }
                tempValue.add(temp);
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

    }//GenerateMontlyExpenseSummary()

    public int FetchExpenseByCatMonthlyFromDB(String month) {

        DB_NAME = getString(R.string.DB_NAME);
        al_ExpensesByCatMonthly.clear();

        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT expenseCategory," +
                "sum(expenseAmount) " +
                "FROM "+ getString(R.string.TABLE_EXPENSE) +" WHERE substr(dateField,7) LIKE '" + stringSelectedYear +
                "' AND substr(dateField,4,2) LIKE '" + month +
                "' GROUP BY expenseCategory",null);

        String curCategory,curAmount;


        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do{
                    curCategory = c.getString(0);
                    curAmount = c.getString(1);
                    String[] curEntry = {curCategory,curAmount};
                    al_ExpensesByCatMonthly.add(curEntry);
                }while(c.moveToNext());
            }
        }
        db.close();
        return c.getCount();

    }

    public void FetchExpenseYearlyFromDB() {

        stringSelectedYear = String.valueOf(spinnerYears.getSelectedItem());

        DB_NAME = getString(R.string.DB_NAME);
        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT expenseCategory," +
                "sum(expenseAmount) " +
                "FROM " + getString(R.string.TABLE_EXPENSE) +" WHERE substr(dateField,7) LIKE '" + stringSelectedYear +
                "' GROUP BY expenseCategory",null);

        String curCategory,curAmount;

        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do{

                    curCategory = c.getString(0);
                    curAmount = c.getString(1);
                    String[] curEntry = {curCategory,curAmount};
                    al_ExpensesByCatYearly.add(curEntry);
                }while(c.moveToNext());
            }
        }
        db.close();

        CreateExpenseReportChart();
    }

    public void CreateExpenseReportChart() {
        Random rnd = new Random();


        PieChartView pieChartView = findViewById(R.id.expenseChart);
        ArrayList<SliceValue> pieData = new ArrayList<SliceValue>();

        for (String[] currEntry :al_ExpensesByCatYearly)
        {
            int randColor =Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            pieData.add(new SliceValue(Float.valueOf(currEntry[1]),randColor).setLabel(currEntry[0] +": $" +Float.valueOf(currEntry[1])));
        }

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);

        pieChartView.setPieChartData(pieChartData);

        pieChartView.setChartRotation(270,true);
    }

    public void clearTableLayout()
    {
        TableLayout masterTable = findViewById(R.id.tableExpenseReport);
        int count = masterTable.getChildCount();
        for (int i = 0; i <= count; i++) {
            View child = masterTable.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
    }
}
