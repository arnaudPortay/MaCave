package com.dev.portay.macave;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.dev.portay.macave.db.entity.Wine;
import com.dev.portay.macave.db.entity.WineColorConverter;
import com.dev.portay.macave.util.MySpinnerAdapter;

import java.util.Calendar;

public class AddWineActivity extends AppCompatActivity
{
    public static final String WINE_REPLY = "com.dev.portay.macave.WINE_REPLY";

    private EditText mEditWineNameView;
    private EditText mEditRegionView;
    private EditText mEditProducerView;
    private Spinner mYearSpinner;
    private Spinner mColorSpinner;
    private EditText mEditBottleNumberView;
    private int mYear;
    private Wine.WineColor mColor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wine);

        // Set Members
        mEditWineNameView = findViewById(R.id.editName);
        mEditRegionView = findViewById(R.id.editOrigin);
        mEditProducerView = findViewById(R.id.editProducer);
        mYearSpinner = findViewById(R.id.yearSpinner);
        mColorSpinner = findViewById(R.id.colorSpinner);
        mEditBottleNumberView = findViewById(R.id.editBottleNumber);

        // Populate year spinner
        int lCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        MySpinnerAdapter<Integer> lYearAdapter = new MySpinnerAdapter<>(this, android.R.layout.simple_spinner_item);

        for (int i = lCurrentYear; i > lCurrentYear - 50; i--)
        {
            lYearAdapter.add(i);
        }
        mYearSpinner.setAdapter(lYearAdapter);
        mYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                mYear = (int)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                //Nothing to do
            }
        });


        // Populate color spinner
        MySpinnerAdapter<String> lColorAdapter = new MySpinnerAdapter<>(this, android.R.layout.simple_spinner_item);
        for (Wine.WineColor lColor : Wine.WineColor.values())
        {
            lColorAdapter.add(getResources().getString(Wine.getStringIdFromColor(lColor)));
        }
        mColorSpinner.setAdapter(lColorAdapter);
        mColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                mColor = WineColorConverter.toWineColor(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                //Nothing to do
            }
        });



        final Button lButton = findViewById(R.id.buttonSave);
        lButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent lReplyIntent = new Intent();
                if (TextUtils.isEmpty(mEditWineNameView.getText()) && TextUtils.isEmpty(mEditProducerView.getText()))
                {
                    AlertDialog.Builder lBuilder = new AlertDialog.Builder(view.getContext());
                    lBuilder.setTitle(R.string.add_wine_error_title);
                    lBuilder.setMessage(R.string.add_wine_error_message);
                    lBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            // Nothing to do
                        }
                    });
                    AlertDialog lDialog = lBuilder.create();
                    lDialog.show();
                }
                else
                {
                    String lName = mEditWineNameView.getText().toString();
                    String lOrigin = mEditRegionView.getText().toString();
                    String lProducer = mEditProducerView.getText().toString();
                    int lBottleNumber = Integer.parseInt(mEditBottleNumberView.getText().toString());

                    lReplyIntent.putExtra(WINE_REPLY,
                            new Wine(lName, lOrigin, mColor, lProducer, mYear, lBottleNumber));
                    setResult(RESULT_OK, lReplyIntent);
                    finish();
                }
            }
        });
    }
}
