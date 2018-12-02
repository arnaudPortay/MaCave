package com.dev.portay.macave;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
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
import com.dev.portay.macave.db.entity.WineColorConverter;
import com.dev.portay.macave.util.MySpinnerAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

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
    private List<String> mCepagesNames;
    private static boolean msIsEditing = false;

    private static int msYearPos = -1;
    private static int msConsumptionPos = -1;
    private static int msColorPos = -1;
    private static String msLabelPath = "";
    private static String msPreviousLabelPath = "";
    private static boolean msDeletePicture = false;

    private String mName;
    private String mRegion;
    private String mProducer;
    private int mWineYearPos;
    private int mWineConsumptionPos;
    private int mWineColorPos;
    private boolean mWineRebuy;
    private String mWineLabelPath;
    private boolean mHasCamera;

    private int mWineId;


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
        mCepagesNames = new ArrayList<>();

        mHasCamera = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

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

            DataRepository.getDataRepository().getAllCepageNames().observe(this, new Observer<List<String>>()
            {
                @Override
                public void onChanged(@Nullable List<String> strings)
                {
                    // Getting rid of duplicates
                    Set<String> lSet = new HashSet<>();
                    lSet.addAll(strings);
                    mCepagesNames.clear();
                    mCepagesNames.addAll(lSet);
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
                                enableEdition(msIsEditing);

                                mWineId = wines.get(0).getId();
                                mWineRebuy = wines.get(0).getRebuy();
                                mWineLabelPath = wines.get(0).getLabelPath();

                                // Change label picture behaviour
                                getView().findViewById(R.id.updatePictureButton).setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Intent lTakePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        // Check if there is an app that can handle the intent
                                        if (lTakePictureIntent.resolveActivity(view.getContext().getPackageManager()) != null) {

                                            File lPhotoFile = null;
                                            try
                                            {
                                                // Create file
                                                lPhotoFile = createImageFile();
                                            }
                                            catch (IOException lErr)
                                            {

                                                Log.e("APY", lErr.getMessage());
                                            }

                                            if (lPhotoFile != null)
                                            {
                                                // Define where the image is going to be saved
                                                Uri lPhotoUri = FileProvider.getUriForFile(getContext(), "com.dev.portay.macave.fileprovider", lPhotoFile);
                                                // Redirect output to our path
                                                lTakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, lPhotoUri);

                                                // Grant permission to all relevant apps. Default should be the classic camera app
                                                List<ResolveInfo> resInfoList = view.getContext().getPackageManager().queryIntentActivities(lTakePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                                                for (ResolveInfo resolveInfo : resInfoList) {
                                                    String packageName = resolveInfo.activityInfo.packageName;
                                                    view.getContext().grantUriPermission(packageName, lPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                }

                                                // Start the default app for taking pictures
                                                startActivityForResult(lTakePictureIntent, AddWineActivity.REQUEST_IMAGE_CAPTURE);
                                            }
                                        }
                                    }
                                });


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
                                        msYearPos = i;
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView)
                                    {
                                        //Nothing to do
                                    }
                                });

                                if (!msIsEditing || msYearPos == -1 )
                                {
                                    ((Spinner)getView().findViewById(R.id.spinner_year)).setSelection(lYearAdapter.getPosition(wines.get(0).getYear()));
                                }
                                else
                                {
                                    ((Spinner)getView().findViewById(R.id.spinner_year)).setSelection(msYearPos);
                                }

                                mWineYearPos = lYearAdapter.getPosition(wines.get(0).getYear());

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
                                    if (!msIsEditing)
                                    {
                                        ((TextView)getActivity().findViewById(R.id.name_detail)).setText(wines.get(0).getName());
                                    }

                                    // Hide Text view
                                    getActivity().findViewById(R.id.name_detail).setVisibility(msIsEditing ? View.VISIBLE : View.INVISIBLE);
                                }
                                else if (getActivity().findViewById(R.id.name_detail) != null)// on tablet
                                {
                                    // Set title in text view
                                    ((TextView)getActivity().findViewById(R.id.name_detail)).setText(wines.get(0).getName());
                                    // Show text view
                                    if (!msIsEditing)
                                    {
                                        getActivity().findViewById(R.id.name_detail).setVisibility(View.VISIBLE);
                                    }
                                }

                                mName = wines.get(0).getName();

                                // Set Label
                                String lTempPath = "";
                                if (msIsEditing )
                                {
                                    if (msLabelPath.compareTo("") != 0)
                                    {
                                        lTempPath = msLabelPath;
                                    }
                                }
                                else if (wines.get(0).getLabelPath().compareTo("") != 0)
                                {
                                    lTempPath = wines.get(0).getLabelPath();
                                }

                                if (lTempPath.compareTo("") != 0)
                                {
                                    final String lLabelPath = lTempPath;
                                    File lFile = new File(lLabelPath);
                                    if (lFile.exists() && getActivity().findViewById(R.id.LabelImageView) != null)
                                    {
                                        Picasso.get().load(lFile).placeholder(android.R.drawable.ic_menu_gallery).into((ImageView)getView().findViewById(R.id.LabelImageView));

                                        getActivity().findViewById(R.id.LabelImageView).setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                // Start activity to see image
                                                Context context = view.getContext();
                                                Intent intent = new Intent(context, LabelDisplayActivity.class);
                                                intent.putExtra(LabelDisplayActivity.ARG_LABEL, lLabelPath);

                                                context.startActivity(intent);
                                            }
                                        });
                                    }
                                }

                                // Set Region
                                if (!msIsEditing)
                                {
                                    ((TextView) getView().findViewById(R.id.region_detail)).
                                            setText(wines.get(0).getOrigin());
                                }
                                mRegion = wines.get(0).getOrigin();

                                // Set Producer
                                if (!msIsEditing)
                                {
                                    ((TextView) getView().findViewById(R.id.producer_detail)).
                                            setText(wines.get(0).getProducer());
                                }
                                mProducer = wines.get(0).getProducer();

                                // Set Color
                                ((TextView) getView().findViewById(R.id.color_detail))
                                        .setText(Wine.getStringIdFromColor(wines.get(0).getColor()));

                                // Populate color spinner
                                MySpinnerAdapter<String> lColorAdapter = new MySpinnerAdapter<>(getContext(), android.R.layout.simple_spinner_item);
                                for (Wine.WineColor lColor : Wine.WineColor.values())
                                {
                                    lColorAdapter.add(getResources().getString(Wine.getStringIdFromColor(lColor)));
                                }
                                ((Spinner)getView().findViewById(R.id.spinner_color)).setAdapter(lColorAdapter);
                                ((Spinner)getView().findViewById(R.id.spinner_color)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                                {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                    {
                                        msColorPos = i;
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView)
                                    {
                                        //Nothing to do
                                    }
                                });

                                if (!msIsEditing || msColorPos == -1)
                                {
                                    ((Spinner)getView().findViewById(R.id.spinner_color)).setSelection(lColorAdapter.getPosition(getResources().getString(Wine.getStringIdFromColor(wines.get(0).getColor()))));
                                }
                                else
                                {
                                    ((Spinner)getView().findViewById(R.id.spinner_color)).setSelection(msColorPos);
                                }
                                mWineColorPos = lColorAdapter.getPosition(getResources().getString(Wine.getStringIdFromColor(wines.get(0).getColor())));


                                // Populate consumption date spinner
                                MySpinnerAdapter<Integer> lConsumptionDateAdapter = new MySpinnerAdapter<>(getContext(), android.R.layout.simple_spinner_item);

                                int lMinConsumptionYear = wines.get(0).getConsumptionDate() < lCurrentYear ? wines.get(0).getConsumptionDate() : lCurrentYear;
                                for (int i = lMinConsumptionYear; i < lCurrentYear + 30; i++)
                                {
                                    lConsumptionDateAdapter.add(i);
                                }
                                ((Spinner)getView().findViewById(R.id.spinner_consumption)).setAdapter(lConsumptionDateAdapter);
                                ((Spinner)getView().findViewById(R.id.spinner_consumption)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                                {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                    {
                                        msConsumptionPos = i;
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView)
                                    {
                                        // Nothing to do
                                    }
                                });

                                if (!msIsEditing || msConsumptionPos == -1)
                                {
                                    ((Spinner)getView().findViewById(R.id.spinner_consumption)).setSelection(lConsumptionDateAdapter.getPosition(wines.get(0).getConsumptionDate()));
                                }
                                else
                                {
                                    ((Spinner)getView().findViewById(R.id.spinner_consumption)).setSelection(msConsumptionPos);
                                }
                                mWineConsumptionPos = lConsumptionDateAdapter.getPosition(wines.get(0).getConsumptionDate());

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
                                        getView().findViewById(R.id.name_detail).clearFocus();
                                        getView().findViewById(R.id.region_detail).clearFocus();
                                        getView().findViewById(R.id.producer_detail).clearFocus();
                                        view.requestFocusFromTouch();

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

                                // Set cepages
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
                                                lChip.setCloseIconTintResource(android.R.color.background_light);
                                                lChip.setCloseIconResource(R.drawable.ic_clear_black_24dp);
                                                lChip.setCloseIconEnabled(msIsEditing);
                                                lChip.setOnCloseIconClickListener(new View.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(View view)
                                                    {
                                                        // delete chip
                                                        ((ChipGroup) getView().findViewById(R.id.cepage_chipgroup)).removeView(view);

                                                        // Delete Cepage from DB
                                                        DataRepository.getDataRepository().deleteCepage(lCepage);
                                                    }
                                                });

                                                // Add chip to chipgroup
                                                ((ChipGroup) getView().findViewById(R.id.cepage_chipgroup)).addView(lChip, ((ChipGroup) getView().findViewById(R.id.cepage_chipgroup)).getChildCount() - 1);
                                            }
                                        }
                                    }
                                });
                            }

                            getView().findViewById(R.id.chip_addCepage).setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(final View view)
                                {
                                    getView().findViewById(R.id.name_detail).clearFocus();
                                    getView().findViewById(R.id.region_detail).clearFocus();
                                    getView().findViewById(R.id.producer_detail).clearFocus();
                                    view.requestFocusFromTouch();

                                    AlertDialog.Builder lBuilder = new AlertDialog.Builder(view.getContext()).setCancelable(false);
                                    lBuilder.setTitle(R.string.add_cepage);

                                    ArrayAdapter<String> lAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_dropdown_item_1line, mCepagesNames);
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
                                                if (!chipGroupHasChip((ChipGroup)getView().findViewById(R.id.cepage_chipgroup), lEdit.getText().toString()))// do nothing if cepage already exists
                                                {
                                                    DataRepository.getDataRepository().insertCepage(new Cepage(wines.get(0).getId(), lEdit.getText().toString()));
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



                            // Delete Wine button
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

                            // Delete Label Picture Button
                            getView().findViewById(R.id.deleteLabelButton).setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    msDeletePicture = true;
                                    ((ImageView)getView().findViewById(R.id.LabelImageView)).setImageResource(android.R.drawable.ic_menu_gallery);
                                    getView().findViewById(R.id.LabelImageView).setOnClickListener(null);
                                    deletePicture();
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
        msIsEditing = pEdit;

        KeyListener lListener = pEdit ? TextKeyListener.getInstance() : null;
        try
        {
            ((EditText) getView().findViewById(R.id.name_detail)).setKeyListener(lListener);
            getView().findViewById(R.id.name_detail).setFocusable(pEdit);
            getView().findViewById(R.id.name_detail).setFocusableInTouchMode(pEdit);
            getView().findViewById(R.id.name_detail).setClickable(pEdit);

            ((EditText) getView().findViewById(R.id.region_detail)).setKeyListener(lListener);
            getView().findViewById(R.id.region_detail).setFocusable(pEdit);
            getView().findViewById(R.id.region_detail).setFocusableInTouchMode(pEdit);
            getView().findViewById(R.id.region_detail).setClickable(pEdit);

            ((EditText) getView().findViewById(R.id.producer_detail)).setKeyListener(lListener);
            getView().findViewById(R.id.producer_detail).setFocusable(pEdit);
            getView().findViewById(R.id.producer_detail).setFocusableInTouchMode(pEdit);
            getView().findViewById(R.id.producer_detail).setClickable(pEdit);

            getView().findViewById(R.id.spinner_year).setVisibility(pEdit ? View.VISIBLE : View.INVISIBLE);
            getView().findViewById(R.id.year_detail).setVisibility(pEdit ? View.INVISIBLE : View.VISIBLE);

            getView().findViewById(R.id.spinner_consumption).setVisibility(pEdit ? View.VISIBLE : View.INVISIBLE);
            getView().findViewById(R.id.consumption_date_detail).setVisibility(pEdit ? View.INVISIBLE : View.VISIBLE);

            getView().findViewById(R.id.spinner_color).setVisibility(pEdit ? View.VISIBLE : View.INVISIBLE);
            getView().findViewById(R.id.color_detail).setVisibility(pEdit ? View.INVISIBLE : View.VISIBLE);

            getView().findViewById(R.id.del_wine_button).setVisibility(pEdit ? View.VISIBLE : View.GONE);

            getView().findViewById(R.id.chip_addCepage).setVisibility(pEdit ? View.VISIBLE : View.INVISIBLE);

            getView().findViewById(R.id.updatePictureButton).setVisibility(pEdit && mHasCamera ? View.VISIBLE : View.GONE);
            getView().findViewById(R.id.deleteLabelButton).setVisibility(pEdit && mHasCamera ? View.VISIBLE : View.GONE);

            setCepagesCloseIconVisibility(pEdit);

            getView().findViewById(R.id.name_detail).setVisibility(pEdit ? View.VISIBLE : getView().findViewById(R.id.toolbar_layout) == null ? View.VISIBLE : View.INVISIBLE);
        }
        catch (NullPointerException lErr)
        {
        }
    }

    private void setCepagesCloseIconVisibility(boolean pVisibility)
    {
        for (int i = 0; i < ((ChipGroup)getView().findViewById(R.id.cepage_chipgroup)).getChildCount() - 1; i++)
        {
            ((Chip)((ChipGroup)getView().findViewById(R.id.cepage_chipgroup)).getChildAt(i)).setCloseIconEnabled(pVisibility);
        }
    }

    public void restoreView()
    {
        msColorPos = -1;
        msYearPos = -1;
        msConsumptionPos = -1;
        msDeletePicture = false;

        deletePicture();

        msLabelPath = mWineLabelPath;
        updateLabelPreview();
        msLabelPath="";

        if (getView() != null)
        {
            ((Spinner) getView().findViewById(R.id.spinner_year)).setSelection(mWineYearPos);
            ((Spinner) getView().findViewById(R.id.spinner_color)).setSelection(mWineColorPos);
            ((Spinner) getView().findViewById(R.id.spinner_consumption)).setSelection(mWineConsumptionPos);
        }

        if (getActivity().findViewById(R.id.toolbar_layout) != null)
        {
            // Set title in Toolbar
            ((CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout))
                    .setTitle(mName);
        }
        try
        {
            ((TextView) getActivity().findViewById(R.id.name_detail)).setText(mName);

            ((TextView) getView().findViewById(R.id.region_detail)).
                    setText(mRegion);

            ((TextView) getView().findViewById(R.id.producer_detail)).
                    setText(mProducer);
        }
        catch (NullPointerException lErr)
        {
            //do nothing
        }

    }

    public void updateWine()
    {
        enableEdition(false);
        int lYear = (int)((Spinner)getView().findViewById(R.id.spinner_year)).getSelectedItem();
        int lBottleNumber = Integer.parseInt(((TextView)getView().findViewById(R.id.number_detail)).getText().toString());
        int lConsumptionDate = (int)((Spinner)getView().findViewById(R.id.spinner_consumption)).getSelectedItem();
        String lNewName = ((TextView)getView().findViewById(R.id.name_detail)).getText().toString();
        String lNewRegion = ((TextView)getView().findViewById(R.id.region_detail)).getText().toString();
        String lNewProducer = ((TextView)getView().findViewById(R.id.producer_detail)).getText().toString();
        String lWineLabelPath = msDeletePicture ? "" : msLabelPath.compareTo("") == 0 ? "" : msLabelPath;
        if (msDeletePicture)
        {
            deletePicture();

            if (mWineLabelPath.compareTo("")!= 0)
            {
                // Delete previous picture
                File lFile = new File(mWineLabelPath);
                lFile.delete();
                mWineLabelPath = "";
            }
        }

        Wine lWine = new Wine(lNewName, lNewRegion, WineColorConverter.toWineColor(((Spinner)getView().findViewById(R.id.spinner_color)).getSelectedItemPosition()), lNewProducer,
                lYear, lBottleNumber, lConsumptionDate, lWineLabelPath);
        lWine.setRebuy(mWineRebuy);
        lWine.setId(mWineId);

        msColorPos = -1;
        msYearPos = -1;
        msConsumptionPos = -1;
        msLabelPath = "";
        msPreviousLabelPath = "";
        msDeletePicture = false;

        DataRepository.getDataRepository().updateWine(lWine);
    }

    private File createImageFile() throws IOException
    {
        String lTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String lImageFileName = "JPEG_" + lTimeStamp;
        File lStorageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File lImage = File.createTempFile(lImageFileName, ".jpg", lStorageDir);

        msPreviousLabelPath = msLabelPath;
        msLabelPath = lImage.getAbsolutePath();

        return lImage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == AddWineActivity.REQUEST_IMAGE_CAPTURE)
        {
            if (resultCode == RESULT_CANCELED)
            {
                msLabelPath = msPreviousLabelPath;
                msPreviousLabelPath = "";
            }
            else if (resultCode == RESULT_OK )
            {
                if (msPreviousLabelPath.compareTo("")!= 0)
                {
                    // Delete previous picture
                    File lFile = new File(msPreviousLabelPath);
                    lFile.delete();
                    msPreviousLabelPath = "";
                }

                msDeletePicture = false;
                updateLabelPreview();
            }
        }
    }

    private void updateLabelPreview()
    {
        //Update label picture
        if (msLabelPath != null)
        {
            File lFile = new File(msLabelPath);

            if (lFile.exists() && getView().findViewById(R.id.LabelImageView) != null)
            {
                Picasso.get().load(lFile).placeholder(android.R.drawable.ic_menu_gallery).into((ImageView)getView().findViewById(R.id.LabelImageView));

                getView().findViewById(R.id.LabelImageView).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // Start activity to see image
                        Context context = view.getContext();
                        Intent intent = new Intent(context, LabelDisplayActivity.class);
                        intent.putExtra(LabelDisplayActivity.ARG_LABEL, msLabelPath);

                        context.startActivity(intent);
                    }
                });
            }
            else
            {
                getView().findViewById(R.id.LabelImageView).setOnClickListener(null);
            }
        }
    }

    private void deletePicture()
    {
        if (msPreviousLabelPath.compareTo("")!= 0 )
        {
            // Delete previous picture
            File lFile = new File(msPreviousLabelPath);
            lFile.delete();
        }

        if (msLabelPath.compareTo("")!= 0 )
        {
            // Delete current picture
            File lFile = new File(msLabelPath);
            lFile.delete();
        }

        msLabelPath = "";
        msPreviousLabelPath = "";
    }
}