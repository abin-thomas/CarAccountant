package com.atmcorp.caraccountant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;



import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class income extends AppCompatActivity {

    /*******User Defined Variables**********/
    DatePickerDialog datePicker;
    EditText et_date;
    EditText et_GrossIncome;
    EditText et_NoHstIncome;
    TextView tv_ApproxHST;
    TextView tv_NetIncome;


    float tempNoHstEarning;
    float tempGrossEarning;
    float tempHstAmount;

    String stringEarningAmount;
    String stringHstAmount;
    String stringNetIncome;

    public NumberFormat nf = new DecimalFormat("##.##");
    /******************************************/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        showCalendarPopUp();
        earningsCalculation();
    }


    public void clearAll(View view) {
        ViewGroup group = (ViewGroup) findViewById(R.id.cLayoutMaster);
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View viewChild = group.getChildAt(i);
            if (viewChild instanceof EditText) {
                ((EditText)viewChild).setText("");
            }

            if(viewChild instanceof ViewGroup && (((ViewGroup)viewChild).getChildCount() > 0))
                clearAll((ViewGroup)view);
        }
    }

    public void showCalendarPopUp()
    {
        et_date = findViewById(R.id.txt_date);
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
        et_GrossIncome = findViewById(R.id.txt_totalIncome);
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
                    showResult();
                }
                else
                {
                    clearResult();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {}


        });
        et_NoHstIncome = findViewById(R.id.txt_noHstIncome);
        et_NoHstIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {}

            @Override
            public void afterTextChanged(Editable s)
            {
                showResult();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {}


        });
    } //earningsCalculation

    public void showResult()
    {

        et_NoHstIncome = findViewById(R.id.txt_noHstIncome);
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


        tv_ApproxHST = findViewById(R.id.txt_approxHst);
        tv_ApproxHST.setText(stringHstAmount);


        tv_NetIncome = findViewById(R.id.txt_netIncome);
        tv_NetIncome.setText(stringNetIncome);
    }

    public void clearResult()
    {
        tv_ApproxHST.setText("");
        tv_NetIncome.setText("");
    }
} // class
