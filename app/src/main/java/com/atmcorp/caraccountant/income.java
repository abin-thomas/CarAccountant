package com.atmcorp.caraccountant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class income extends AppCompatActivity {

    /*******User Defined Variables**********/
    SQLiteDatabase db;
    String DB_NAME;

    DatePickerDialog datePicker;
    EditText et_date;
    EditText et_startKms;
    EditText et_endKms;
    EditText et_GrossIncome;
    EditText et_NoHstIncome;
    TextView tv_ApproxHST;
    TextView tv_NetIncome;


    float tempNoHstEarning;
    float tempGrossEarning;
    float tempHstAmount;
    float currYearNetIncome = 0;
    float currYearHST = 0;

    String stringDate;
    int intStartKms;
    int intEndKms;
    String stringEarningAmount;
    String stringHstAmount;
    String stringNetIncome;

    public NumberFormat nf = new DecimalFormat("##.##");
    /******************************************/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        fetchControls();
        showCalendarPopUp();
        earningsCalculation();
        createIncomeChart();
    }

    public void fetchControls()
    {
        et_date = findViewById(R.id.txt_date);
        et_startKms = findViewById(R.id.txt_startKms);
        et_endKms = findViewById(R.id.txt_endKms);
        et_GrossIncome = findViewById(R.id.txt_totalIncome);
        et_NoHstIncome = findViewById(R.id.txt_noHstIncome);
        tv_ApproxHST = findViewById(R.id.txt_approxHst);
        tv_NetIncome = findViewById(R.id.txt_netIncome);

    }

    public void showCalendarPopUp()
    {

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // show datePicker dialog

                datePicker = new DatePickerDialog(income.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                DecimalFormat mFormat= new DecimalFormat("00");
                                String tempDate = mFormat.format(Double.valueOf(dayOfMonth));
                                String tempMonth = mFormat.format(Double.valueOf(monthOfYear+1));

                                et_date.setText(tempDate + "/" + tempMonth + "/" + year);
                            }
                        }, year, month, day);

                datePicker.show();
            }
        });
    }//showCalendarPopUp

    public void earningsCalculation()
    {
        et_GrossIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {}

            @Override
            public void afterTextChanged(Editable s)
            {
                if(s.length()!=0)
                {
                    if(s.charAt(0)=='.')
                        et_GrossIncome.setText("0.");
                    showNetIncome_HST();
                }
                else
                {
                    clearNetIncome_HST();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {}


        });
        et_NoHstIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {}

            @Override
            public void afterTextChanged(Editable s)
            {
                showNetIncome_HST();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {}


        });
    } //earningsCalculation

    public void showNetIncome_HST()
    {


        if(et_NoHstIncome.getText().length()!=0)
        {
            tempNoHstEarning = Float.parseFloat(et_NoHstIncome.getText().toString());
        }
        else
        {
            tempNoHstEarning =0;
        }


        tempGrossEarning = Float.parseFloat(et_GrossIncome.getText().toString());
        stringEarningAmount = nf.format(tempGrossEarning);
        tempHstAmount = (tempGrossEarning - tempNoHstEarning) *18/100;
        stringHstAmount = nf.format(tempHstAmount);
        stringNetIncome = nf.format(tempGrossEarning - tempHstAmount);


        tv_ApproxHST.setText(stringHstAmount);

        tv_NetIncome.setText(stringNetIncome);
    }

    public void clearNetIncome_HST()
    {
        tv_ApproxHST.setText("");
        tv_NetIncome.setText("");
    }

    public void createIncomeChart()
    {
        fetchIncomeData(); //This function populates the 'currYearNetIncome' and 'currYearHST' from table

        PieChartView pieChartView = findViewById(R.id.chart);
        ArrayList<SliceValue> pieData = new ArrayList<SliceValue>();

        pieData.add(new SliceValue(currYearNetIncome, Color.GREEN).setLabel("Net Income : "+currYearNetIncome));
        pieData.add(new SliceValue(currYearHST, Color.RED).setLabel("HST : "+currYearHST));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);

        pieChartView.setPieChartData(pieChartData);

        pieChartView.setChartRotation(270,true);


    }

    public void fetchIncomeData()
    {
        DB_NAME = getString(R.string.DB_NAME); //getting the value from strings.xml

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        Cursor c = db.rawQuery("select " +
                "sum(netIncome)," +
                "sum(approxHST)" +
                " from "+ getString(R.string.TABLE_INCOME) +
                " where substr(dateField,7)like '"+ currentYear +"'",null);

        if(c != null)
        {
            if(c.moveToFirst())
            {
                do{
                    currYearNetIncome = c.getFloat(0);
                    currYearHST = c.getFloat(1);
                }while(c.moveToNext());

            } //cursor if
        } //null if

        db.close();
    }


    public void fetchValues()
    {
        stringDate = et_date.getText().toString();

        intStartKms =  Integer.parseInt(et_startKms.getText().toString());

        intEndKms = Integer.parseInt(et_endKms.getText().toString());

    }

   public void callAddRecord(View view)
    {
        fetchValues(); // This is the function to read convert user values to the dbstyle data types.

        DB_NAME = getString(R.string.DB_NAME);
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);


        ContentValues newValues = new ContentValues();
        newValues.put("dateField", stringDate);
        newValues.put("startOdometer", intStartKms);
        newValues.put("endOdometer", intEndKms);
        newValues.put("totalOdometer", (intEndKms - intStartKms));
        newValues.put("totalIncome", stringEarningAmount);
        newValues.put("incomeNoHST",tempNoHstEarning);
        newValues.put("approxHST", stringHstAmount);
        newValues.put("netIncome", stringNetIncome);

        //insert details
        long result = db.insert(getString(R.string.TABLE_INCOME),null,newValues);

        if(result != -1)
            Toast.makeText(this, "New Record added ...", Toast.LENGTH_SHORT).show();

        else
            Toast.makeText(this, "Alert....Something wrong", Toast.LENGTH_SHORT).show();


        db.close();

        createIncomeChart();

    }
} // class
