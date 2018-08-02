package com.dev.portay.macave;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
        private final TextView mWineItemView;
        private final TextView mWineContentView;

        private WineViewHolder(View pItemView)
        {
            super(pItemView);
            mWineItemView =  pItemView.findViewById(R.id.id_text);
            mWineContentView = pItemView.findViewById(R.id.content);
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
            String lNumber = String.format("%d", mCellarItems.get(pPosition).getYear());
            pHolder.mWineContentView.setText(lNumber);
            pHolder.mWineItemView.setText(mWines.get(mCellarItems.get(pPosition).getWineId()).getName());
            pHolder.itemView.setTag(mCellarItems.get(pPosition).getId());

        }
        else
        {
            // Data not ready yet
            pHolder.mWineItemView.setText("");
            pHolder.mWineContentView.setText("No Wine");
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
                arguments.putInt(WineDetailFragment.ARG_ITEM_ID, (int)view.getTag());
                WineDetailFragment fragment = new WineDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.wine_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, WineDetailActivity.class);
                intent.putExtra(WineDetailFragment.ARG_ITEM_ID, (int)view.getTag());

                context.startActivity(intent);
            }
        }
    };

}
