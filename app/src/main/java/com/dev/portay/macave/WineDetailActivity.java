package com.dev.portay.macave;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dev.portay.macave.db.CellarDatabase;

/**
 * An activity representing a single Wine detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CellarListActivity}.
 */
public class WineDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cellar_item_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get relevant data
                int lNumber = Integer.parseInt(((TextView)findViewById(R.id.number_detail)).getText().toString());
                int lId = getIntent().getIntExtra(WineDetailFragment.ARG_ITEM_ID,-1);

                // update bottle number
                new updateDBAsync(lNumber, lId).execute();

                // Navigate back to CellarList Activity
                NavUtils.navigateUpTo((Activity)view.getContext(), new Intent(view.getContext(), CellarListActivity.class));
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        //TODO: Check if it's better to add wine id as an extra rather than getting it again from the cellar...
        // TODO: If so then do the same when creating fragment in CellarAdapter
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            arguments.putInt(WineDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(WineDetailFragment.ARG_ITEM_ID,-1));

            arguments.putInt(WineDetailFragment.ARG_WINE_ID,
                    getIntent().getIntExtra(WineDetailFragment.ARG_WINE_ID,-1));

            WineDetailFragment fragment = new WineDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.wine_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, CellarListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

// Utility class
class updateDBAsync extends AsyncTask<Void, Void, Void>
{
    private final int mNumber;
    private final int mId;

    updateDBAsync(int pNumber, int pId)
    {
        mNumber = pNumber;
        mId = pId;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        CellarDatabase.getInstance().mCellarDao().updateBottleNumber(mNumber, mId);
        return null;
    }
}

