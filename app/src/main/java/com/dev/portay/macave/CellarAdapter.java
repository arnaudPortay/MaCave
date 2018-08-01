package com.dev.portay.macave;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.portay.macave.db.entity.CellarItem;

import java.util.List;

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
    private List<CellarItem> mWines; //Cached Copy
    private final CellarListActivity mParentActivity;
    private final boolean mTwoPane;

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
        if (mWines != null)
        {
            Log.d("ARNAUD", "onBindViewHolder not null");
            String lNumber = String.format("%d", mWines.get(pPosition).getId());
            pHolder.mWineItemView.setText(lNumber);
            String lYear = String.format("%d", mWines.get(pPosition).getYear());
            pHolder.mWineContentView.setText(lYear);
        }
        else
        {
            Log.d("ARNAUD", "onBindViewHolder null");
            // Data not ready yet
            pHolder.mWineItemView.setText("");
            pHolder.mWineContentView.setText("No Wine");
        }
    }

    void setWines(List<CellarItem> pWines)
    {
        Log.d("ARNAUD", "Adapter set wines");
        mWines = pWines;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return (mWines == null) ? 0 : mWines.size();
    }

    //TODO: Adapt this ish
//    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
//            if (mTwoPane) {
//                Bundle arguments = new Bundle();
//                arguments.putString(WineDetailFragment.ARG_ITEM_ID, item.id);
//                WineDetailFragment fragment = new WineDetailFragment();
//                fragment.setArguments(arguments);
//                mParentActivity.getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.wine_detail_container, fragment)
//                        .commit();
//            } else {
//                Context context = view.getContext();
//                Intent intent = new Intent(context, WineDetailActivity.class);
//                intent.putExtra(WineDetailFragment.ARG_ITEM_ID, item.id);
//
//                context.startActivity(intent);
//            }
//        }
//    };

}
