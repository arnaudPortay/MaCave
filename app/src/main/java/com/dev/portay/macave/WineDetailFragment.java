package com.dev.portay.macave;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.portay.macave.db.entity.CellarItem;
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
    public static final String ARG_WINE_ID = "wine_id";


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

        if (getArguments().containsKey(ARG_ITEM_ID) && getArguments().containsKey(ARG_WINE_ID))
        {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //TODO: Try and follow the above piece of advice

            ViewModelProviders.of(this).get(CellarViewModel.class)
                    .getCellarItemById(getArguments().getInt(ARG_ITEM_ID))
                    .observe(this, new Observer<List<CellarItem>>()
                    {
                        @Override
                        public void onChanged(@Nullable final List<CellarItem> cellarItems)
                        {

                            if (cellarItems != null && cellarItems.size() > 0)
                            {
                                // Set Year
                                ((TextView) getView().findViewById(R.id.year_detail)).
                                        setText(String.format("%d",cellarItems.get(0).getYear()));

                                // Set Bottle Number
                                ((TextView) getView().findViewById(R.id.number_detail)).
                                        setText(String.format("%d",cellarItems.get(0).getBottleNumber()));

                                // Increase button listener
                                getView().findViewById(R.id.increase_button).setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        TextView lView = getView().findViewById(R.id.number_detail);
                                        String lVal = lView.getText().toString();
                                        lView.setText(String.format("%d", Integer.parseInt(lVal) + 1));
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
                                    }
                                });
                            }
                        }
                    });

            ViewModelProviders.of(this).get(WineViewModel.class)
                    .getWineById(getArguments().getInt(ARG_WINE_ID))
                    .observe(this, new Observer<List<Wine>>()
                    {
                        @Override
                        public void onChanged(@Nullable List<Wine> wines)
                        {
                            if (wines != null && wines.size() > 0)
                            {
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
                                int lId = R.string.error;

                                switch (wines.get(0).getColor())
                                {
                                    case eRed:
                                        lId = R.string.wine_red;
                                        break;
                                    case eWhite:
                                        lId = R.string.wine_white;
                                        break;
                                    case eRose:
                                        lId = R.string.wine_rose;
                                        break;
                                    case ePaille:
                                        lId = R.string.wine_paille;
                                        break;
                                    case eSparkling:
                                        lId = R.string.wine_sparkling;
                                        break;
                                    case eCremant:
                                        lId = R.string.wine_cremant;
                                        break;
                                    case eChampagne:
                                        lId = R.string.wine_champagne;
                                        break;
                                    case eChampagneRose:
                                        lId = R.string.wine_champagne_rose;
                                        break;
                                    default:
                                        Log.e("MACAVE", "Wrong number for Wine Color enum");
                                        break;
                                }
                                ((TextView) getView().findViewById(R.id.color_detail)).setText(lId);
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

}