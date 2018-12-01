package com.dev.portay.macave;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 * An activity representing a single Wine detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CellarListActivity}.
 */
public class WineDetailActivity extends AppCompatActivity {

    private static boolean msIsEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cellar_item_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        final FloatingActionButton fabCancel = findViewById(R.id.fab_close);
        final FloatingActionButton fabValid = findViewById(R.id.fab_validate);
        findViewById(R.id.fab).setVisibility(msIsEditing ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.fab_close).setVisibility(msIsEditing ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.fab_validate).setVisibility(msIsEditing ? View.VISIBLE : View.INVISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msIsEditing = true;
                ((WineDetailFragment)getSupportFragmentManager().getFragments().get(0)).enableEdition(true);
                view.setVisibility(View.INVISIBLE);
                fabCancel.show();
                fabValid.show();

            }
        });

        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msIsEditing = false;
                view.setVisibility(View.INVISIBLE);
                findViewById(R.id.fab_validate).setVisibility(View.INVISIBLE);
                fab.show();
                ((WineDetailFragment)getSupportFragmentManager().getFragments().get(0)).restoreView();
                ((WineDetailFragment)getSupportFragmentManager().getFragments().get(0)).enableEdition(false);
            }
        });

        fabValid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                msIsEditing = false;
                view.setVisibility(View.INVISIBLE);
                findViewById(R.id.fab_close).setVisibility(View.INVISIBLE);
                fab.show();

                ((WineDetailFragment)getSupportFragmentManager().getFragments().get(0)).updateWine();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity.

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            arguments.putInt(WineDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(WineDetailFragment.ARG_ITEM_ID,-1));

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
        if (id == android.R.id.home || id == android.R.id.closeButton) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown.
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (msIsEditing)
        {
            findViewById(R.id.fab_close).callOnClick();
        }
        super.onBackPressed();
    }
}

