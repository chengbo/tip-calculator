package net.chengbo.tipcalculator;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText m_txtAmountAfterTax;
    private EditText m_txtAmountBeforeTax;
    private EditText m_txtNoOfPeople;
    private EditText m_txtTaxRate;
    private EditText m_txtTipPercentage;

    private Button m_btn1People;
    private Button m_btn2People;
    private Button m_btn3People;
    private Button m_btn10Percent;
    private Button m_btn15Percent;
    private Button m_btn20Percent;

    private TextView m_lblTipAmount;
    private TextView m_lblTotalPerPeople;
    private TextView m_lblTotalToPay;

    private float m_tipAmount;
    private float m_totalToPay;
    private float m_totalPerPeople;

    private float m_amountBeforeTax;
    private float m_amountAfterTax;
    private float m_tipPercentage;
    private float m_taxRate;
    private int m_numOfPeople = 3;

    private static final String KEY_TIP_PERCENTAGE = "KEY_TIP_PERCENTAGE";
    private static final String KEY_TAX_RATE = "KEY_TAX_RATE";
    private static final String KEY_TIME_FIRST_INSTALL = "KEY_TIME_FIRST_INSTALL";

    private boolean m_preventTextChangedEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initDataFromPreferences();

        initControls();

        setListeners();

        tryToPromptUserToRateApp();
    }

    @Override
    protected void onPause() {
        saveDataToPreferences();
        super.onPause();
    }

    private void initDataFromPreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        try {
            m_tipPercentage = preferences.getFloat(KEY_TIP_PERCENTAGE, 0.1f);
        } catch (ClassCastException ex) {
            m_tipPercentage = 0.1f;
        }
        try {
            m_taxRate = preferences.getInt(KEY_TAX_RATE, 9) / 100f;
        } catch (ClassCastException ex) {
            m_taxRate = 0.09f;
        }
    }

    private void saveDataToPreferences() {
        Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putFloat(KEY_TIP_PERCENTAGE, m_tipPercentage).putInt(KEY_TAX_RATE, (int) (m_taxRate * 100)).commit();
    }

    private void setListeners() {
        m_btn1People.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_numOfPeople = 1;
                m_txtNoOfPeople.setText("1");
                tryCalculate();
            }
        });
        m_btn2People.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_numOfPeople = 2;
                m_txtNoOfPeople.setText("2");
                tryCalculate();
            }
        });
        m_btn3People.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_numOfPeople = 3;
                m_txtNoOfPeople.setText("3");
                tryCalculate();
            }
        });
        m_btn10Percent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_tipPercentage = 0.1f;
                m_txtTipPercentage.setText("10");
                tryCalculate();
            }
        });
        m_btn15Percent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_tipPercentage = 0.15f;
                m_txtTipPercentage.setText("15");
                tryCalculate();
            }
        });
        m_btn20Percent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_tipPercentage = 0.2f;
                m_txtTipPercentage.setText("20");
                tryCalculate();
            }
        });
        m_txtAmountAfterTax.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (m_preventTextChangedEvent) {
                    return;
                }
                m_preventTextChangedEvent = true;
                float amount = 0;
                try {
                    amount = Float.parseFloat(m_txtAmountAfterTax.getText().toString());
                } catch (NumberFormatException ex) {
                    m_txtAmountAfterTax.setError("Amount format error");
                }
                m_amountAfterTax = amount;
                if (m_taxRate > 0) {
                    m_amountBeforeTax = m_amountAfterTax / (1 + m_taxRate);
                } else {
                    m_amountBeforeTax = m_amountAfterTax;
                }
                m_txtAmountBeforeTax.setText(String.format("%.2f", m_amountBeforeTax));
                if (amount <= 0) {
                    m_txtAmountAfterTax.setError("Amount should be positive");
                }
                m_preventTextChangedEvent = false;

                tryCalculate();
            }
        });
        m_txtAmountBeforeTax.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (m_preventTextChangedEvent) {
                    return;
                }
                m_preventTextChangedEvent = true;
                float amount = 0;
                try {
                    amount = Float.parseFloat(m_txtAmountBeforeTax.getText().toString());
                } catch (NumberFormatException ex) {
                    m_txtAmountBeforeTax.setError("Amount format error");
                }
                m_amountBeforeTax = amount;
                if (m_taxRate > 0) {
                    m_amountAfterTax = m_amountBeforeTax * (1 + m_taxRate);
                } else {
                    m_amountAfterTax = m_amountBeforeTax;
                }
                m_txtAmountAfterTax.setText(String.format("%.2f", m_amountAfterTax));
                if (amount <= 0) {
                    m_txtAmountBeforeTax.setError("Amount should be positive");
                }
                m_preventTextChangedEvent = false;

                tryCalculate();
            }
        });
        m_txtTaxRate.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (m_preventTextChangedEvent) {
                    return;
                }
                m_preventTextChangedEvent = true;
                float rate = 0;
                try {
                    rate = Float.parseFloat(m_txtTaxRate.getText().toString()) / 100;
                } catch (NumberFormatException ex) {
                    m_txtTaxRate.setError("Rate format error");
                }
                m_taxRate = rate;
                if (m_taxRate > 0) {
                    m_amountAfterTax = m_amountBeforeTax * (1 + m_taxRate);
                } else {
                    m_amountAfterTax = m_amountBeforeTax;
                }
                m_txtAmountAfterTax.setText(String.format("%.2f", m_amountAfterTax));
                if (rate < 0) {
                    m_txtTaxRate.setError("Rate should be positive or 0");
                }
                m_preventTextChangedEvent = false;

                tryCalculate();
            }
        });
        m_txtTipPercentage.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (m_preventTextChangedEvent) {
                    return;
                }
                m_preventTextChangedEvent = true;
                float percentage = 0;
                try {
                    percentage = Float.parseFloat(m_txtTipPercentage.getText().toString()) / 100;
                } catch (NumberFormatException ex) {
                    m_txtTipPercentage.setError("Percentage format error");
                }
                m_tipPercentage = percentage;
                if (percentage < 0) {
                    m_txtTaxRate.setError("Percentage should be positive or 0");
                }
                m_preventTextChangedEvent = false;

                tryCalculate();
            }
        });
        m_txtNoOfPeople.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (m_preventTextChangedEvent) {
                    return;
                }
                m_preventTextChangedEvent = true;
                int no = 1;
                try {
                    no = Integer.parseInt(m_txtNoOfPeople.getText().toString());
                } catch (NumberFormatException ex) {
                    m_txtNoOfPeople.setError("Number should be positive");
                }
                m_numOfPeople = no;
                if (m_numOfPeople <= 0) {
                    m_txtNoOfPeople.setError("Number should be positive");
                }
                m_preventTextChangedEvent = false;

                tryCalculate();
            }
        });
    }

    private void tryCalculate() {
        calculate();
        displayResult();
    }

    private void calculate() {
        m_tipAmount = m_amountBeforeTax * m_tipPercentage;
        m_totalToPay = m_amountAfterTax + m_tipAmount;
        m_totalPerPeople = m_totalToPay / m_numOfPeople;
    }

    private void displayResult() {
        m_lblTipAmount.setText(String.format("%.2f", m_tipAmount));
        m_lblTotalPerPeople.setText(String.format("%.2f", m_totalPerPeople));
        m_lblTotalToPay.setText(String.format("%.2f", m_totalToPay));
    }

    private void initControls() {
        m_txtAmountAfterTax = (EditText) findViewById(R.id.txt_amount_after_tax);
        m_txtAmountBeforeTax = (EditText) findViewById(R.id.txt_amount_before_tax);
        m_txtNoOfPeople = (EditText) findViewById(R.id.txt_no_of_people);
        m_txtTaxRate = (EditText) findViewById(R.id.txt_tax_rate);
        m_txtTipPercentage = (EditText) findViewById(R.id.txt_tip_percentage);
        m_btn1People = (Button) findViewById(R.id.btn_1_people);
        m_btn2People = (Button) findViewById(R.id.btn_2_people);
        m_btn3People = (Button) findViewById(R.id.btn_3_people);
        m_btn10Percent = (Button) findViewById(R.id.btn_10_percent);
        m_btn15Percent = (Button) findViewById(R.id.btn_15_percent);
        m_btn20Percent = (Button) findViewById(R.id.btn_20_percent);
        m_lblTipAmount = (TextView) findViewById(R.id.lbl_tip_amount);
        m_lblTotalPerPeople = (TextView) findViewById(R.id.lbl_total_per_people);
        m_lblTotalToPay = (TextView) findViewById(R.id.lbl_total_to_pay);

        m_txtTaxRate.setText(String.valueOf((int) (m_taxRate * 100)));
        m_txtTipPercentage.setText(String.valueOf((int) (m_tipPercentage * 100)));
    }

    private void tryToPromptUserToRateApp() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        long now = new Date().getTime();
        long firstInstallTime = now;
        try {
            firstInstallTime = preferences.getLong(KEY_TIME_FIRST_INSTALL, now);
        } catch (ClassCastException ex) {
        }

        // firstInstallTime equals now means no value found in preferences
        if (firstInstallTime == now) {
            preferences.edit().putLong(KEY_TIME_FIRST_INSTALL, now).commit();
        }

        if (sevenDaysLater(firstInstallTime)) {
            showPromptDialog();
        }
    }

    private void showPromptDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate me").setIcon(R.drawable.ic_launcher).setMessage("Do you like this App? Rate it!")
                .setPositiveButton("Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

                        if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
                            dialog.cancel();
                            startActivity(rateAppIntent);
                        } else {
                            Toast.makeText(MainActivity.this, "Couldn't launch the market.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNeutralButton("Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                        // prompt again 7 days later
                        preferences.edit().putLong(KEY_TIME_FIRST_INSTALL, addDays(new Date(), 7).getTime()).commit();
                        dialog.cancel();

                    }
                }).setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                        // I think no one will use this app until 01/01/2050
                        preferences.edit().putLong(KEY_TIME_FIRST_INSTALL, new Date(2050, 1, 1).getTime()).commit();
                        dialog.cancel();

                    }
                }).create().show();
    }

    private static boolean sevenDaysLater(long time) {
        return addDays(new Date(time), 7).before(new Date());
    }

    private static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

}
