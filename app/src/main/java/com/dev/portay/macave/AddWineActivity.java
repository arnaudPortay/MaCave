package com.dev.portay.macave;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.dev.portay.macave.db.entity.Wine;
import com.dev.portay.macave.db.entity.WineColorConverter;
import com.dev.portay.macave.util.MySpinnerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddWineActivity extends AppCompatActivity
{
    public static final String WINE_REPLY = "com.dev.portay.macave.WINE_REPLY";
    public static final String CEPAGE_REPLY = "com.dev.portay.macave.CEPAGE_REPLY";
    public static final String DISHES_REPLY = "com.dev.portay.macave.DISHES_REPLY";

    private AutoCompleteTextView mEditWineNameView;
    private AutoCompleteTextView mEditRegionView;
    private AutoCompleteTextView mEditProducerView;
    private Spinner mYearSpinner;
    private Spinner mColorSpinner;
    private Spinner mConsumptionDateSpinner;
    private EditText mEditBottleNumberView;
    private int mYear;
    private int mConsumptionDate;
    private Wine.WineColor mColor;

    private List<String> mDishesNames; // for auto completion
    private List<String> mCepageNames; // for auto completion
    private List<String> mWineNames; // for auto completion
    private List<String> mProducerNames; // for auto completion
    private List<String> mOrigins; // for auto completion

    // Statics to keep data when rebuilding due to orientation change
    private static ArrayList<String> mCepageNameList;
    private static ArrayList<String> mDishNameList;


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
        mConsumptionDateSpinner = findViewById(R.id.consumptionDateSpinner);
        mEditBottleNumberView = findViewById(R.id.editBottleNumber);
        if (mCepageNameList == null)
        {
            mCepageNameList = new ArrayList<>();
        }

        if (mDishNameList == null)
        {
            mDishNameList = new ArrayList<>();
        }

        DataRepository lRepo = DataRepository.getDataRepository();
        // Populate autocomplete dishes list
        mDishesNames = new ArrayList<>();
        lRepo.getAllDishesName().observe(this, new Observer<List<String>>()
        {
            @Override
            public void onChanged(@Nullable List<String> strings)
            {
                // Getting rid of duplicates
                Set<String> lSet = new HashSet<>();
                lSet.addAll(strings);
                mDishesNames.clear();
                mDishesNames.addAll(lSet);
            }
        });

        // Populate autocomplete cepage list
        mCepageNames = new ArrayList<>();
        lRepo.getAllCepageNames().observe(this, new Observer<List<String>>()
        {
            @Override
            public void onChanged(@Nullable List<String> strings)
            {
                //Getting rid of duplicates
                Set<String> lSet = new HashSet<>();
                lSet.addAll(strings);
                mCepageNames.clear();
                mCepageNames.addAll(lSet);
            }
        });

        // Populate autocomplete wine name list
        mWineNames = new ArrayList<>();
        lRepo.getAllWineNames().observe(this, new Observer<List<String>>()
        {
            @Override
            public void onChanged(@Nullable List<String> strings)
            {
                //Getting rid of duplicates
                Set<String> lSet = new HashSet<>();
                lSet.addAll(strings);
                mWineNames.clear();
                mWineNames.addAll(lSet);
                mEditWineNameView.setAdapter(new ArrayAdapter<>(mEditWineNameView.getContext(), android.R.layout.simple_dropdown_item_1line, mWineNames));
            }
        });


        // Populate autocomplete producer name list
        mProducerNames = new ArrayList<>();
        lRepo.getAllWineProducers().observe(this, new Observer<List<String>>()
        {
            @Override
            public void onChanged(@Nullable List<String> strings)
            {
                //Getting rid of duplicates
                Set<String> lSet = new HashSet<>();
                lSet.addAll(strings);
                mProducerNames.clear();
                mProducerNames.addAll(lSet);
                mEditProducerView.setAdapter(new ArrayAdapter<>(mEditProducerView.getContext(), android.R.layout.simple_dropdown_item_1line, mProducerNames));
            }
        });

        // Populate autocomplete origins list
        mOrigins = new ArrayList<>();
        lRepo.getAllWineOrigins().observe(this, new Observer<List<String>>()
        {
            @Override
            public void onChanged(@Nullable List<String> strings)
            {
                //Getting rid of duplicates
                Set<String> lSet = new HashSet<>();
                lSet.addAll(strings);
                mOrigins.clear();
                mOrigins.addAll(lSet);
                mEditRegionView.setAdapter(new ArrayAdapter<>(mEditRegionView.getContext(), android.R.layout.simple_dropdown_item_1line, mOrigins));
            }
        });


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

        // Populate consumption date spinner
        MySpinnerAdapter<Integer> lConsumptionDateAdapter = new MySpinnerAdapter<>(this, android.R.layout.simple_spinner_item);

        for (int i = lCurrentYear; i < lCurrentYear + 30; i++)
        {
            lConsumptionDateAdapter.add(i);
        }
        mConsumptionDateSpinner.setAdapter(lConsumptionDateAdapter);
        mConsumptionDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                mConsumptionDate = (int)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                // Nothing to do
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

        // Cepage chips
        for (String lCep: mCepageNameList)
        {
            if (!chipGroupHasChip((ChipGroup)findViewById(R.id.aw_cepage_chipgroup), lCep))// do nothing if dish already exists
            {
                // Create chip
                Chip lChip = new Chip(this);
                lChip.setText(lCep);
                lChip.setCloseIconEnabled(true);
                lChip.setChipBackgroundColorResource(R.color.colorPrimary);
                lChip.setTextColor(getResources().getColor(android.R.color.background_light));
                lChip.setCloseIconTintResource(android.R.color.background_light);
                lChip.setCloseIconResource(R.drawable.ic_clear_black_24dp);
                lChip.setOnCloseIconClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // delete chip
                        mCepageNameList.remove(((Chip) view).getText().toString());
                        ((ChipGroup) findViewById(R.id.aw_cepage_chipgroup)).removeView(view);
                    }
                });

                // Add chip to chipgroup
                ((ChipGroup) findViewById(R.id.aw_cepage_chipgroup)).addView(lChip, ((ChipGroup) findViewById(R.id.aw_cepage_chipgroup)).getChildCount() - 1);
            }

        }

        findViewById(R.id.aw_chip_addCepage).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                mEditBottleNumberView.clearFocus();
                mEditProducerView.clearFocus();
                mEditRegionView.clearFocus();
                mEditWineNameView.clearFocus();
                view.requestFocusFromTouch();

                AlertDialog.Builder lBuilder = new AlertDialog.Builder(view.getContext()).setCancelable(false);
                lBuilder.setTitle(R.string.add_cepage);

                ArrayAdapter<String> lAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_dropdown_item_1line, mDishesNames);
                final AutoCompleteTextView lEdit = new AutoCompleteTextView(view.getContext());
                lEdit.setAdapter(lAdapter);
                lBuilder.setView(lEdit);


                lBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (lEdit.getText().toString().compareTo("") != 0) // Do nothing if empty
                        {
                            if (!chipGroupHasChip((ChipGroup)findViewById(R.id.aw_cepage_chipgroup), lEdit.getText().toString()))// do nothing if dish already exists
                            {
                                // Create chip
                                Chip lChip = new Chip(view.getContext());
                                lChip.setText(lEdit.getText().toString());
                                lChip.setCloseIconEnabled(true);
                                lChip.setChipBackgroundColorResource(R.color.colorPrimary);
                                lChip.setTextColor(getResources().getColor(android.R.color.background_light));
                                lChip.setCloseIconTintResource(android.R.color.background_light);
                                lChip.setCloseIconResource(R.drawable.ic_clear_black_24dp);
                                lChip.setOnCloseIconClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        // delete chip
                                        mCepageNameList.remove(((Chip)view).getText().toString());
                                        ((ChipGroup) findViewById(R.id.aw_cepage_chipgroup)).removeView(view);
                                    }
                                });

                                // Add chip to chipgroup and to list
                                mCepageNameList.add(lEdit.getText().toString());
                                ((ChipGroup) findViewById(R.id.aw_cepage_chipgroup)).addView(lChip, ((ChipGroup) findViewById(R.id.aw_cepage_chipgroup)).getChildCount() - 1);
                            }
                        }
                    }
                });

                lBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // Nothing to do
                    }
                });
                final AlertDialog lDialog = lBuilder.create();
                lEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View view, boolean hasFocus)
                    {
                        if (hasFocus)
                        {
                            lDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
                lDialog.show();
            }
        });

        for (String lDish: mDishNameList)
        {
            if (!chipGroupHasChip((ChipGroup)findViewById(R.id.aw_dishes_chipgroup), lDish))// do nothing if dish already exists
            {
                // Create chip
                Chip lChip = new Chip(this);
                lChip.setText(lDish);
                lChip.setCloseIconEnabled(true);
                lChip.setChipBackgroundColorResource(R.color.colorPrimary);
                lChip.setTextColor(getResources().getColor(android.R.color.background_light));
                lChip.setCloseIconTintResource(android.R.color.background_light);
                lChip.setCloseIconResource(R.drawable.ic_clear_black_24dp);
                lChip.setOnCloseIconClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // delete chip
                        mDishNameList.remove(((Chip) view).getText().toString());
                        ((ChipGroup) findViewById(R.id.aw_dishes_chipgroup)).removeView(view);
                    }
                });

                // Add chip to chipgroup
                ((ChipGroup) findViewById(R.id.aw_dishes_chipgroup)).addView(lChip, ((ChipGroup) findViewById(R.id.aw_dishes_chipgroup)).getChildCount() - 1);
            }

        }
        // Dishes chips
        findViewById(R.id.aw_chip_addDish).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                mEditBottleNumberView.clearFocus();
                mEditProducerView.clearFocus();
                mEditRegionView.clearFocus();
                mEditWineNameView.clearFocus();
                view.requestFocusFromTouch();

                AlertDialog.Builder lBuilder = new AlertDialog.Builder(view.getContext()).setCancelable(false);
                lBuilder.setTitle(R.string.add_suggested_dish_title);

                ArrayAdapter<String> lAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_dropdown_item_1line, mDishesNames);
                final AutoCompleteTextView lEdit = new AutoCompleteTextView(view.getContext());
                lEdit.setAdapter(lAdapter);
                lBuilder.setView(lEdit);

                lBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (lEdit.getText().toString().compareTo("") != 0) // Do nothing if empty
                        {
                            if (!chipGroupHasChip((ChipGroup)findViewById(R.id.aw_dishes_chipgroup), lEdit.getText().toString()))// do nothing if dish already exists
                            {
                                // Create chip
                                Chip lChip = new Chip(view.getContext());
                                lChip.setText(lEdit.getText().toString());
                                lChip.setCloseIconEnabled(true);
                                lChip.setChipBackgroundColorResource(R.color.colorPrimary);
                                lChip.setTextColor(getResources().getColor(android.R.color.background_light));
                                lChip.setCloseIconTintResource(android.R.color.background_light);
                                lChip.setCloseIconResource(R.drawable.ic_clear_black_24dp);
                                lChip.setOnCloseIconClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        // delete chip
                                        mDishNameList.remove(((Chip)view).getText().toString());
                                        ((ChipGroup) findViewById(R.id.aw_dishes_chipgroup)).removeView(view);
                                    }
                                });

                                // Add chip to chipgroup and to list
                                mDishNameList.add(lEdit.getText().toString());
                                ((ChipGroup) findViewById(R.id.aw_dishes_chipgroup)).addView(lChip, ((ChipGroup) findViewById(R.id.aw_dishes_chipgroup)).getChildCount() - 1);
                            }
                        }
                    }
                });

                lBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // Nothing to do
                    }
                });
                final AlertDialog lDialog = lBuilder.create();
                lEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View view, boolean hasFocus)
                    {
                        if (hasFocus)
                        {
                            lDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
                lDialog.show();
            }
        });




        // Validation Button
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
                            new Wine(lName, lOrigin, mColor, lProducer, mYear, lBottleNumber,mConsumptionDate));
                    lReplyIntent.putStringArrayListExtra(CEPAGE_REPLY, mCepageNameList);
                    lReplyIntent.putStringArrayListExtra(DISHES_REPLY, mDishNameList);
                    setResult(RESULT_OK, lReplyIntent);
                    finish();

                    // Clear Lists for next wine
                    mDishNameList.clear();
                    mCepageNameList.clear();
                }
            }
        });
    }

    private boolean chipGroupHasChip(ChipGroup pGroup, String pText)
    {
        for (int i = 0; i < pGroup.getChildCount(); i++)
        {
            if (pText.compareTo(((Chip)pGroup.getChildAt(i)).getText().toString()) == 0)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onBackPressed()
    {
        // Clear Lists for next wine
        mDishNameList.clear();
        mCepageNameList.clear();

        super.onBackPressed();
    }
}
