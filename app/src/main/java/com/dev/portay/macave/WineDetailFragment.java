package com.dev.portay.macave;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dev.portay.macave.db.entity.Cepage;
import com.dev.portay.macave.db.entity.Dish;
import com.dev.portay.macave.db.entity.Wine;
import com.dev.portay.macave.util.MySpinnerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A fragment representing a single Wine detail screen.
 * This fragment is either contained in a {@link CellarListActivity}
 * in two-pane mode (on tablets) or a {@link WineDetailActivity}
 * on handsets.
 */
public class WineDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private List<String> mDishesNames;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WineDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDishesNames = new ArrayList<>();

        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            DataRepository.getDataRepository().getAllDishesName().observe(this, new Observer<List<String>>()
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


            ViewModelProviders.of(this).get(WineViewModel.class)
                    .getWineById(getArguments().getInt(ARG_ITEM_ID))
                    .observe(this, new Observer<List<Wine>>()
                    {
                        @Override
                        public void onChanged(@Nullable final List<Wine> wines)
                        {

                            if (wines != null && wines.size() > 0)
                            {
                                enableEdition(false);

                                // Populate year spinner
                                int lCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
                                int lMinYear = wines.get(0).getYear() < lCurrentYear - 50 ? wines.get(0).getYear() : lCurrentYear - 50;
                                MySpinnerAdapter<Integer> lYearAdapter = new MySpinnerAdapter<>(getContext(), android.R.layout.simple_spinner_item);

                                for (int i = lCurrentYear; i >= lMinYear; i--)
                                {
                                    lYearAdapter.add(i);
                                }
                                ((Spinner)getView().findViewById(R.id.spinner_year)).setAdapter(lYearAdapter);
                                ((Spinner)getView().findViewById(R.id.spinner_year)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                                {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                    {
                                        //mYear = (int)adapterView.getItemAtPosition(i);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView)
                                    {
                                        //Nothing to do
                                    }
                                });

                                ((Spinner)getView().findViewById(R.id.spinner_year)).setSelection(lYearAdapter.getPosition(wines.get(0).getYear()));

                                // Set Year
                                ((TextView) getView().findViewById(R.id.year_detail)).
                                        setText(String.format("%d",wines.get(0).getYear()));

                                // Set Bottle Number
                                ((TextView) getView().findViewById(R.id.number_detail)).
                                        setText(String.format("%d",wines.get(0).getBottleNumber()));

                                // Increase button listener
                                getView().findViewById(R.id.increase_button).setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        TextView lView = getView().findViewById(R.id.number_detail);
                                        String lVal = lView.getText().toString();

                                        // update bottle number
                                        DataRepository.getDataRepository().updateBottleNumber(Integer.parseInt(lVal) + 1, wines.get(0).getId());
                                        if (lVal.compareTo("0") == 0)
                                        {
                                            DataRepository.getDataRepository().
                                                    updateRebuy(false, wines.get(0).getId());
                                        }
                                    }
                                });

                                // Decrease button listener
                                getView().findViewById(R.id.decrease_button).setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        TextView lView = getView().findViewById(R.id.number_detail);
                                        int lVal = Integer.parseInt(lView.getText().toString());
                                        if (lVal != 0)
                                        {
                                            // update bottle number
                                            DataRepository.getDataRepository().updateBottleNumber(lVal - 1, wines.get(0).getId());
                                        }

                                        if (lVal == 1)
                                        {
                                            AlertDialog.Builder lBuilder = new AlertDialog.Builder(view.getContext()).setCancelable(false);
                                            lBuilder.setTitle(R.string.add_buy_list_title);
                                            lBuilder.setMessage(R.string.add_buy_list_mess);
                                            lBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i)
                                                {
                                                    DataRepository.getDataRepository().updateBottleNumber(0,wines.get(0).getId());
                                                    DataRepository.getDataRepository().updateRebuy(true, wines.get(0).getId());
                                                }
                                            });

                                            lBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
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
                                    }
                                });

                                // Set Title
                                // if on phone
                                if (getActivity().findViewById(R.id.toolbar_layout) != null)
                                {
                                    // Set title in Toolbar
                                    ((CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout))
                                            .setTitle(wines.get(0).getName());

                                    // Set title in text view
                                    ((TextView)getActivity().findViewById(R.id.name_detail)).setText(wines.get(0).getName());

                                    // Hide Text view
                                    getActivity().findViewById(R.id.name_detail).setVisibility(View.INVISIBLE);
                                }
                                else if (getActivity().findViewById(R.id.name_detail) != null)// on tablet
                                {
                                    // Set title in text view
                                    ((TextView)getActivity().findViewById(R.id.name_detail)).setText(wines.get(0).getName());
                                    // Show text view
                                    getActivity().findViewById(R.id.name_detail).setVisibility(View.VISIBLE);
                                }

                                if (wines.get(0).getLabelPath().compareTo("") != 0)
                                {
                                    File lFile = new File(wines.get(0).getLabelPath());
                                    if (lFile.exists())
                                    {
                                        Bitmap lLabelBitmap = BitmapFactory.decodeFile(wines.get(0).getLabelPath());

                                        if (lLabelBitmap != null && getActivity().findViewById(R.id.LabelImageView) != null)
                                        {
                                            ((ImageView)getActivity().findViewById(R.id.LabelImageView)).setImageBitmap(lLabelBitmap);

                                            getActivity().findViewById(R.id.LabelImageView).setOnClickListener(new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    // Start activity to see image
                                                    Context context = view.getContext();
                                                    Intent intent = new Intent(context, LabelDisplayActivity.class);
                                                    intent.putExtra(LabelDisplayActivity.ARG_LABEL, wines.get(0).getLabelPath());

                                                    context.startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                }

                                // Set Region
                                ((TextView) getView().findViewById(R.id.region_detail)).
                                        setText(wines.get(0).getOrigin());

                                // Set Producer
                                ((TextView) getView().findViewById(R.id.producer_detail)).
                                        setText(wines.get(0).getProducer());

                                // Set Color
                                ((TextView) getView().findViewById(R.id.color_detail))
                                        .setText(Wine.getStringIdFromColor(wines.get(0).getColor()));

                                // Set Consumption Date
                                ((TextView) getView().findViewById(R.id.consumption_date_detail))
                                        .setText(String.format("%d", wines.get(0).getConsumptionDate()));

                                // Set suggested dishes
                                DataRepository.getDataRepository().getDishesByWineId(wines.get(0).getId()).observe(WineDetailFragment.this, new Observer<List<Dish>>()
                                {
                                    @Override
                                    public void onChanged(@Nullable List<Dish> dishes)
                                    {
                                        for (final Dish lDish: dishes) // iterate through dishes
                                        {
                                            // do nothing if the chip already exists
                                            if (!chipGroupHasChip((ChipGroup) getView().findViewById(R.id.dishes_chipgroup), lDish.mDishName))
                                            {
                                                // Create chip
                                                Chip lChip = new Chip(getContext());
                                                lChip.setText(lDish.mDishName);
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
                                                        ((ChipGroup) getView().findViewById(R.id.dishes_chipgroup)).removeView(view);
                                                        // delete dish from db
                                                        DataRepository.getDataRepository().deleteDish(lDish);
                                                    }
                                                });

                                                // Add chip to chipgroup
                                                ((ChipGroup) getView().findViewById(R.id.dishes_chipgroup)).addView(lChip, ((ChipGroup) getView().findViewById(R.id.dishes_chipgroup)).getChildCount() - 1);
                                            }
                                        }
                                    }
                                });

                                // Add dish chip behaviour
                                getView().findViewById(R.id.chip_addDish).setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(final View view)
                                    {
                                        AlertDialog.Builder lBuilder = new AlertDialog.Builder(view.getContext()).setCancelable(false);
                                        lBuilder.setTitle(R.string.add_suggested_dish_title);

                                        ArrayAdapter<String> lAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, mDishesNames);
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
                                                    if (!chipGroupHasChip((ChipGroup) getView().findViewById(R.id.dishes_chipgroup), lEdit.getText().toString()))// do nothing if dish already exists
                                                    {
                                                        DataRepository.getDataRepository().insertDish(new Dish(wines.get(0).getId(), lEdit.getText().toString()));
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

                                // Set suggested dishes
                                DataRepository.getDataRepository().getCepageByWineId(wines.get(0).getId()).observe(WineDetailFragment.this, new Observer<List<Cepage>>()
                                {
                                    @Override
                                    public void onChanged(@Nullable List<Cepage> cepages)
                                    {
                                        for (final Cepage lCepage: cepages) // iterate through cepages
                                        {
                                            // do nothing if the chip already exists
                                            if (!chipGroupHasChip((ChipGroup) getView().findViewById(R.id.cepage_chipgroup), lCepage.mCepageName))
                                            {
                                                // Create chip
                                                Chip lChip = new Chip(getContext());
                                                lChip.setText(lCepage.mCepageName);
                                                lChip.setChipBackgroundColorResource(R.color.colorPrimary);
                                                lChip.setTextColor(getResources().getColor(android.R.color.background_light));

                                                // Add chip to chipgroup
                                                ((ChipGroup) getView().findViewById(R.id.cepage_chipgroup)).addView(lChip, ((ChipGroup) getView().findViewById(R.id.cepage_chipgroup)).getChildCount());
                                            }
                                        }
                                    }
                                });
                            }

                            getView().findViewById(R.id.del_wine_button).setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    AlertDialog.Builder lBuilder = new AlertDialog.Builder(view.getContext()).setCancelable(false);
                                    lBuilder.setTitle(R.string.deletion);
                                    lBuilder.setMessage(R.string.wine_deletion_message);
                                    lBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            DataRepository.getDataRepository().deleteWine(wines.get(0));
                                            if (getActivity().getClass() == WineDetailActivity.class)
                                            {
                                                getActivity().onBackPressed();
                                            }
                                        }
                                    });

                                    lBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
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
                            });
                        }
                    });


        }
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cellar_item_detail, container, false);
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

    public void enableEdition(boolean pEdit)
    {
        KeyListener lListener = pEdit ? TextKeyListener.getInstance() : null;
        ((EditText)getView().findViewById(R.id.name_detail)).setKeyListener(lListener);
        getView().findViewById(R.id.name_detail).setFocusable(pEdit);
        getView().findViewById(R.id.name_detail).setFocusableInTouchMode(pEdit);
        getView().findViewById(R.id.name_detail).setClickable(pEdit);

        ((EditText)getView().findViewById(R.id.region_detail)).setKeyListener(lListener);
        getView().findViewById(R.id.region_detail).setFocusable(pEdit);
        getView().findViewById(R.id.region_detail).setFocusableInTouchMode(pEdit);
        getView().findViewById(R.id.region_detail).setClickable(pEdit);

        ((EditText)getView().findViewById(R.id.producer_detail)).setKeyListener(lListener);
        getView().findViewById(R.id.producer_detail).setFocusable(pEdit);
        getView().findViewById(R.id.producer_detail).setFocusableInTouchMode(pEdit);
        getView().findViewById(R.id.producer_detail).setClickable(pEdit);

        getView().findViewById(R.id.spinner_year).setVisibility(pEdit ? View.VISIBLE : View.INVISIBLE);
        getView().findViewById(R.id.year_detail).setVisibility(pEdit ? View.INVISIBLE : View.VISIBLE);

        getView().findViewById(R.id.del_wine_button).setVisibility(pEdit ? View.VISIBLE : View.GONE);

        getView().findViewById(R.id.name_detail).setVisibility(pEdit ? View.VISIBLE : getView().findViewById(R.id.toolbar_layout) == null ? View.VISIBLE : View.INVISIBLE);
    }
}