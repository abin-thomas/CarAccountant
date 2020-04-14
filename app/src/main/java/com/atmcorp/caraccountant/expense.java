package com.atmcorp.caraccountant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


public class expense extends AppCompatActivity {

    SQLiteDatabase db;

    String DB_NAME;

    EditText et_ExpDate;
    EditText et_ExpCategory;
    EditText et_ExpOdometer;
    EditText et_ExpAmount;


    String stringExpDate;
    String stringExpCategory;
    String stringExpAmount;
    int intExpOdometer;

    float currGasExpense;
    float currInsuranceExpense;
    float currServiceExpense;
    float currOtherExpense;

    NumberFormat nf = new DecimalFormat("##.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        fetchControls();
        showCalendarPopUp();
        createExpenseChart();
    }

    public void fetchControls()
    {
        et_ExpDate = findViewById(R.id.txt_date);
        et_ExpOdometer = findViewById(R.id.txt_odometer);
        et_ExpCategory = findViewById(R.id.txt_expenseCategory);
        et_ExpAmount = findViewById(R.id.txt_amount);

    }

    public void fetchValues()
    {
        stringExpDate = et_ExpDate.getText().toString();

        stringExpCategory =  et_ExpCategory.getText().toString();

        intExpOdometer = Integer.parseInt(et_ExpOdometer.getText().toString());

        float tempUserExpenseAmount = Float.parseFloat(et_ExpAmount.getText().toString());
        stringExpAmount = nf.format(tempUserExpenseAmount);
    }

    public void showCalendarPopUp()
    {
        et_ExpDate.setInputType(InputType.TYPE_NULL);
        et_ExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // show datePicker dialog

                DatePickerDialog datePicker = new DatePickerDialog(expense.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                DecimalFormat mFormat = new DecimalFormat("00");
                                String tempDate = mFormat.format(Double.valueOf(dayOfMonth));
                                String tempMonth = mFormat.format(Double.valueOf(monthOfYear + 1));

                                et_ExpDate.setText(tempDate + "/" + tempMonth + "/" + year);
                            }
                        }, year, month, day);

                datePicker.show();
            }
        });
    } // showCalender
    public void callShowExpenseCategory(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");

// add a list
        String[] expenseSubs = {"Gas", "Insurance", "Service", "Other"};
        builder.setItems(expenseSubs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {

                switch(selected)
                {
                    case 0:
                    {
                        et_ExpCategory.setText("Gas");
                        break;
                    }
                    case 1:
                    {
                        et_ExpCategory.setText("Insurance");
                        break;
                    }
                    case 2:
                    {
                        et_ExpCategory.setText("Service");
                        break;
                    }
                    case 3:
                    {
                        et_ExpCategory.setText("Other");
                        break;
                    }
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void callAddRecord(View view) {
        fetchValues();

        DB_NAME = getString(R.string.DB_NAME);

        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);


        ContentValues newValues = new ContentValues();
        newValues.put("dateField", stringExpDate);
        newValues.put("expenseCategory", stringExpCategory);
        newValues.put("odometer", intExpOdometer);
        newValues.put("expenseAmount", stringExpAmount);

        //insert details
        long result = db.insert(getString(R.string.TABLE_EXPENSE),null,newValues);

        if(result != -1)
            Toast.makeText(this, "New Record added ...", Toast.LENGTH_SHORT).show();

        else
            Toast.makeText(this, "Alert....Something wrong", Toast.LENGTH_SHORT).show();


        db.close();

        createExpenseChart();
    }

    public void createExpenseChart()
    {
        fetchExpenseData();

        PieChartView pieChartView = findViewById(R.id.chart);
        ArrayList<SliceValue> pieData = new ArrayList<SliceValue>();

        pieData.add(new SliceValue(currGasExpense, Color.GREEN).setLabel("Gas : "+currGasExpense));
        pieData.add(new SliceValue(currInsuranceExpense, Color.GRAY).setLabel("Insurance : "+currInsuranceExpense));
        pieData.add(new SliceValue(currServiceExpense, Color.BLUE).setLabel("Service : "+currServiceExpense));
        pieData.add(new SliceValue(currOtherExpense, Color.MAGENTA).setLabel("Other : "+currOtherExpense));


        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);

        pieChartView.setPieChartData(pieChartData);

        pieChartView.setChartRotation(270,true);


    }

    private void fetchExpenseData() {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        DB_NAME = getString(R.string.DB_NAME);
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        Cursor c = db.rawQuery("select " +
                "expenseCategory," +
                "sum(expenseAmount)" +
                " from "+ getString(R.string.TABLE_EXPENSE) +
                " where substr(dateField,7)like '"+ currentYear +"' " +
                "group by expenseCategory",null);

        if(c != null)
        {

            if(c.moveToFirst())
            {
                do {
                    String category = c.getString(0);
                    switch(category)
                    {
                        case "Gas":
                        {
                            currGasExpense = c.getFloat(1);
                            break;
                        }
                        case "Insurance":
                        {
                            currInsuranceExpense = c.getFloat(1);
                            break;
                        }
                        case "Other":
                        {
                            currOtherExpense = c.getFloat(1);
                            break;
                        }
                        case "Service":
                        {
                            currServiceExpense = c.getFloat(1);
                            break;
                        }

                    }//Switch


                }while(c.moveToNext());

            } //cursor if
        } //null if

        db.close();
    }
}
