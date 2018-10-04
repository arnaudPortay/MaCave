package com.dev.portay.macave;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dev.portay.macave.db.entity.Dish;
import com.dev.portay.macave.db.entity.Wine;

import java.util.List;

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

        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            ViewModelProviders.of(this).get(WineViewModel.class)
                    .getWineById(getArguments().getInt(ARG_ITEM_ID))
                    .observe(this, new Observer<List<Wine>>()
                    {
                        @Override
                        public void onChanged(@Nullable final List<Wine> wines)
                        {

                            if (wines != null && wines.size() > 0)
                            {
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
                                        lView.setText(String.format("%d", Integer.parseInt(lVal) + 1));
                                        if (lVal.compareTo("0") == 0)
                                        {
                                            DataRepository.getDataRepository().
                                                    updateBottleNumber(1, wines.get(0).getId());
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
                                            lView.setText(String.format("%d", lVal - 1));
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
                                ((CollapsingToolbarLayout)getActivity().findViewById(R.id.toolbar_layout))
                                        .setTitle(wines.get(0).getName());

                                // Set Region
                                ((TextView) getView().findViewById(R.id.region_detail)).
                                        setText(wines.get(0).getOrigin());

                                //TODO: Handle cases where there is no region or name for good responsive behavior
                                // Does not work....
                                /*ConstraintLayout.LayoutParams lParams =
                                        (ConstraintLayout.LayoutParams)getView().findViewById(R.id.year_detail).getLayoutParams();
                                lParams.setMargins(0,0,0,0);
                                getView().findViewById(R.id.year_detail).setLayoutParams(lParams);*/

                                // Set Producer
                                ((TextView) getView().findViewById(R.id.producer_detail)).
                                        setText(wines.get(0).getProducer());

                                // Set Color
                                ((TextView) getView().findViewById(R.id.color_detail))
                                        .setText(Wine.getStringIdFromColor(wines.get(0).getColor()));

                                // Set suggested dished
                                DataRepository.getDataRepository().getDishesByWineId(wines.get(0).getId()).observe(WineDetailFragment.this, new Observer<List<Dish>>()
                                {
                                    @Override
                                    public void onChanged(@Nullable List<Dish> dishes)
                                    {
                                        for (final Dish lDish: dishes) // iterate through dishes
                                        {
                                            // do nothing if the chip already exists
                                            if (!chipGrouHasDish((ChipGroup) getView().findViewById(R.id.dishes_chipgroup), lDish))
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

                                        final EditText lEdit = new EditText(view.getContext());
                                        lBuilder.setView(lEdit);

                                        lBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i)
                                            {
                                                if (lEdit.getText().toString().compareTo("") != 0) // Do nothing if empty
                                                {
                                                    Dish lDish = new Dish(wines.get(0).getId(), lEdit.getText().toString());
                                                    if (!chipGrouHasDish((ChipGroup) getView().findViewById(R.id.dishes_chipgroup), lDish))// do nothing if dish already exists
                                                    {
                                                        DataRepository.getDataRepository().insertDish(lDish);
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
                                        AlertDialog lDialog = lBuilder.create();
                                        lDialog.show();
                                    }
                                });
                            }
                        }
                    });


        }
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cellar_item_detail, container, false);
    }

    private boolean chipGrouHasDish(ChipGroup pGroup, Dish pDish)
    {
        for (int i = 0; i < pGroup.getChildCount(); i++)
        {
            if (pDish.mDishName.compareTo(((Chip)pGroup.getChildAt(i)).getText().toString()) == 0)
            {
                return true;
            }
        }

        return false;
    }

}