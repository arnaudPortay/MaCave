package com.dev.portay.macave;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class WineListAdapter extends RecyclerView.Adapter<WineListAdapter.WineViewHolder>
{
    /**************  INNER CLASS  **************/
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

    /**************  MEMBERS  **************/
    private List<Wine> mWines; //Cached Copy
    private final WineListActivity mParentActivity;
    private final boolean mTwoPane;

    /************** FUNCTIONS **************/
    WineListAdapter(WineListActivity mParent, boolean mTwoPane)
    {
        this.mParentActivity = mParent;
        this.mTwoPane = mTwoPane;
    }

    @Override
    @NonNull
    public WineViewHolder onCreateViewHolder( @NonNull ViewGroup pViewGroup, int pI)
    {
        View lItemView = LayoutInflater.from(pViewGroup.getContext())
                .inflate(R.layout.wine_list_content, pViewGroup, false);

        return new WineViewHolder(lItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final WineViewHolder pHolder, int pPosition)
    {
        if (mWines != null)
        {
            String lNumber = String.format("%d", mWines.get(pPosition).getId());
            pHolder.mWineItemView.setText(lNumber);
            pHolder.mWineContentView.setText(mWines.get(pPosition).getName());
        }
        else
        {
            // Data not ready yet
            pHolder.mWineItemView.setText("");
            pHolder.mWineContentView.setText("No Wine");
        }
    }

    @Override
    public int getItemCount()
    {
        return (mWines == null) ? 0 : mWines.size();
    }

    void setWines(List<Wine> pWines)
    {
        mWines = pWines;
        notifyDataSetChanged();
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
