package com.dev.portay.macave;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.portay.macave.db.entity.Wine;

import java.util.List;


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
    private List<Wine> mWines; //Cached Copy
    private final CellarListActivity mParentActivity;
    private final boolean mTwoPane;
    private static int mCurrentId = -1;

    /* ************* FUNCTIONS ************* */
    public CellarAdapter(CellarListActivity pParentActivity, boolean pTwoPane)
    {
        this.mParentActivity = pParentActivity;
        this.mTwoPane = pTwoPane;
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
        if (mWines != null && mWines.get(pPosition) != null)
        {
            // Set Year
            String lNumber = String.format("%d", mWines.get(pPosition).getYear());
            pHolder.mWineYearView.setText(lNumber);

            // Set name or hide
            String lName = mWines.get(pPosition).getName();
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
            String lRegion = mWines.get(pPosition).getOrigin();
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
            pHolder.itemView.setTag(mWines.get(pPosition).getId());
        }

        pHolder.itemView.setOnClickListener(mOnClickListener);
    }

    void setWines(List<Wine> pWines)
    {
        mWines = pWines;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return (mWines == null) ? 0 : mWines.size();
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mCurrentId = (int)view.getTag();

            if (mTwoPane)
            {
                if (mParentActivity.mIsEditing)
                {
                    mParentActivity.findViewById(R.id.fabCancel).callOnClick();
                }
                updateTabletView();
            }
            else
            {
                Context context = view.getContext();
                Intent intent = new Intent(context, WineDetailActivity.class);
                intent.putExtra(WineDetailFragment.ARG_ITEM_ID, mCurrentId);

                context.startActivity(intent);
            }
        }
    };

    public void updateTabletView()
    {
        if (mCurrentId != -1)
        {
            Bundle arguments = new Bundle();
            arguments.putInt(WineDetailFragment.ARG_ITEM_ID, mCurrentId);
            WineDetailFragment fragment = new WineDetailFragment();
            fragment.setArguments(arguments);
            mParentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.wine_detail_container, fragment)
                    .commit();
        }
    }

    public void clearTabletView()
    {
        if (mTwoPane)
        {
            mParentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.wine_detail_container, new Fragment())
                    .commit();
        }
    }

    public int getCurrentId()
    {
        return mCurrentId;
    }
}