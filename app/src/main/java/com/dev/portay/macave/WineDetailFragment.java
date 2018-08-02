package com.dev.portay.macave;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


    // NEVER EVER REASSIGN THIS DURING FRAGMENT LIFECYCLE ! Dirty Hack...
    private WineDetailFragment mInstance;

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
        mInstance = this;

        if (getArguments().containsKey(ARG_ITEM_ID))
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
                        public void onChanged(@Nullable List<CellarItem> cellarItems)
                        {

                            if (cellarItems != null && cellarItems.size() > 0)
                            {

                                ((TextView) getView().findViewById(R.id.wine_detail)).
                                        setText(String.format("%d",cellarItems.get(0).getWineId()));

                                // The nested observers is probably a bad idea performance wise...
                                // TODO: remove potential previous observers before adding it?
                                ViewModelProviders.of(mInstance).get(WineViewModel.class)
                                        .getWineById(cellarItems.get(0).getWineId())
                                        .observe(mInstance, new Observer<List<Wine>>()
                                        {
                                            @Override
                                            public void onChanged(@Nullable List<Wine> wines)
                                            {
                                                if (wines != null && wines.size() > 0)
                                                {
                                                    ((CollapsingToolbarLayout)getActivity().findViewById(R.id.toolbar_layout))
                                                            .setTitle(wines.get(0).getName());
                                                }
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
}