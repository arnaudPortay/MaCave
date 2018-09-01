package com.dev.portay.macave;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.portay.macave.db.entity.CellarItem;
import com.dev.portay.macave.db.entity.Wine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellarAdapter extends RecyclerView.Adapter<CellarAdapter.WineViewHolder>
{
    /* *************  INNER CLASS  ************* */
    class WineViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView mWineNameView;
        private final TextView mWineYearView;
        private final TextView mWineRegionView;

        private WineViewHolder(View pItemView)
        {
            super(pItemView);
            mWineNameView =  pItemView.findViewById(R.id.name_text);
            mWineYearView = pItemView.findViewById(R.id.year_text);
            mWineRegionView = pItemView.findViewById(R.id.region_text);
        }
    }

    /* *************  MEMBERS  ************* */
    private List<CellarItem> mCellarItems; //Cached Copy
    private Map<Integer, Wine> mWines;
    private final CellarListActivity mParentActivity;
    private final boolean mTwoPane;

    /* ************* FUNCTIONS ************* */
    public CellarAdapter(CellarListActivity pParentActivity, boolean pTwoPane)
    {
        this.mParentActivity = pParentActivity;
        this.mTwoPane = pTwoPane;
        mWines = new HashMap<>();
    }

    @NonNull
    @Override
    public WineViewHolder onCreateViewHolder(@NonNull ViewGroup pViewGroup, int i)
    {
        View lItemView = LayoutInflater.from(pViewGroup.getContext())
                .inflate(R.layout.cellar_list_content, pViewGroup, false);

        return new CellarAdapter.WineViewHolder(lItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WineViewHolder pHolder, int pPosition)
    {
        if (mCellarItems != null && mWines.get(mCellarItems.get(pPosition).getWineId()) != null)
        {
            // Set Year
            String lNumber = String.format("%d", mCellarItems.get(pPosition).getYear());
            pHolder.mWineYearView.setText(lNumber);

            // Set name or hide
            String lName = mWines.get(mCellarItems.get(pPosition).getWineId()).getName();
            if (lName == null || lName.isEmpty())
            {
                pHolder.mWineNameView.setVisibility(View.GONE);
            }
            else
            {
                pHolder.mWineNameView.setVisibility(View.VISIBLE);
                pHolder.mWineNameView.setText(lName);
            }

            //Set region or hide
            String lRegion = mWines.get(mCellarItems.get(pPosition).getWineId()).getOrigin();
            if (lRegion == null || lRegion.isEmpty())
            {
                pHolder.mWineRegionView.setVisibility(View.GONE);
            }
            else
            {
                pHolder.mWineRegionView.setVisibility(View.VISIBLE);
                pHolder.mWineRegionView.setText(lRegion);
            }

            // Set Tag
            pHolder.itemView.setTag(new Pair<>(mCellarItems.get(pPosition).getId(),mCellarItems.get(pPosition).getWineId()));
        }

        pHolder.itemView.setOnClickListener(mOnClickListener);
    }

    void setCellarItems(List<CellarItem> pWines)
    {
        mCellarItems = pWines;
        notifyDataSetChanged();
    }

    void setWines(List<Wine> pWines)
    {
        for (Wine wine: pWines )
        {
            mWines.put(wine.getId(),wine);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return (mCellarItems == null) ? 0 : mCellarItems.size();
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(WineDetailFragment.ARG_ITEM_ID, ((Pair<Integer,Integer>)view.getTag()).first);
                arguments.putInt(WineDetailFragment.ARG_WINE_ID, ((Pair<Integer,Integer>)view.getTag()).second);
                WineDetailFragment fragment = new WineDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.wine_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, WineDetailActivity.class);
                intent.putExtra(WineDetailFragment.ARG_ITEM_ID, ((Pair<Integer,Integer>)view.getTag()).first);
                intent.putExtra(WineDetailFragment.ARG_WINE_ID, ((Pair<Integer,Integer>)view.getTag()).second);

                context.startActivity(intent);
            }
        }
    };

}
