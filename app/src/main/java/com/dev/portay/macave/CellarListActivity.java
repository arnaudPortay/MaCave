package com.dev.portay.macave;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dev.portay.macave.db.entity.Wine;

import java.util.List;

/**
 * An activity representing a list of Wines. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WineDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CellarListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    public static final int ADD_WINE_ACTIVITY_REQUEST_CODE = 1;
    private boolean mTwoPane;
    private WineViewModel mWineViewModel;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_WINE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Wine lWine = data.getParcelableExtra(AddWineActivity.WINE_REPLY);
            lWine.setId(mWineViewModel.getAllWines().getValue().size());
            mWineViewModel.insert(lWine);

            Snackbar.make(findViewById(R.id.fab), "done", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cellar_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lIntent = new Intent(CellarListActivity.this, AddWineActivity.class);
                startActivityForResult(lIntent, ADD_WINE_ACTIVITY_REQUEST_CODE);
            }
        });

        if (findViewById(R.id.wine_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        final RecyclerView recyclerView = findViewById(R.id.wine_list);
        assert recyclerView != null;
        final CellarAdapter lListAdapter = new CellarAdapter(this, mTwoPane);
        recyclerView.setAdapter(lListAdapter);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mWineViewModel = ViewModelProviders.of(this).get(WineViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.

        mWineViewModel.getAllWines().observe(this, new Observer<List<Wine>>()
        {
            @Override
            public void onChanged(@Nullable final List<Wine> pWines)
            {
                lListAdapter.setWines(pWines);
            }
        });
    }
}
