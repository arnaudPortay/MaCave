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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dev.portay.macave.db.entity.Wine;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Wines. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WineDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CellarListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    public static final int ADD_WINE_ACTIVITY_REQUEST_CODE = 1;
    private boolean mTwoPane;
    private WineViewModel mWineViewModel;
    private List<Wine> mWines; // Cached Data for searchview. Kinda ugly, find another way to do this ?

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_WINE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            mWineViewModel.insert(
                    (Wine)data.getParcelableExtra(AddWineActivity.WINE_REPLY)
            );

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
                mWines = pWines;
                lListAdapter.setWines(pWines);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String s)
    {
        final List<Wine> lFilteredWineList = filter(mWines,s);
        RecyclerView lView = findViewById(R.id.wine_list);
        ((CellarAdapter)lView.getAdapter()).setWines(lFilteredWineList);
        lView.scrollToPosition(0);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s)
    {
        // Nothing to do
        return false;
    }

    private List<Wine> filter(List<Wine> pWines, String pQuery)
    {
        String lLowerCaseQuery = pQuery.toLowerCase();
        final  List<Wine> lFilteredList = new ArrayList<>();

        // Get rid of accents
        lLowerCaseQuery = lLowerCaseQuery.replaceAll("[àâ]", "a");
        lLowerCaseQuery = lLowerCaseQuery.replaceAll("[éèêë]", "e");
        lLowerCaseQuery = lLowerCaseQuery.replaceAll("[îï]", "i");
        lLowerCaseQuery = lLowerCaseQuery.replaceAll("[ôö]", "o");
        lLowerCaseQuery = lLowerCaseQuery.replaceAll("[ùûü]", "u");

        for (Wine lWine : pWines)
        {
            // Replace Name accents
            String lName = lWine.getName().toLowerCase();

            lName = lName.replaceAll("[àâ]", "a");
            lName = lName.replaceAll("[éèêë]", "e");
            lName = lName.replaceAll("[îï]", "i");
            lName = lName.replaceAll("[ôö]", "o");
            lName = lName.replaceAll("[ùûü]", "u");

            // Replace Origin accents
            String lOrigin = lWine.getOrigin().toLowerCase();

            lOrigin = lOrigin.replaceAll("[àâ]", "a");
            lOrigin = lOrigin.replaceAll("[éèêë]", "e");
            lOrigin = lOrigin.replaceAll("[îï]", "i");
            lOrigin = lOrigin.replaceAll("[ôö]", "o");
            lOrigin = lOrigin.replaceAll("[ùûü]", "u");

            // Replace Producer accents
            String lProducer = lWine.getProducer().toLowerCase();

            lProducer = lProducer.replaceAll("[àâ]", "a");
            lProducer = lProducer.replaceAll("[éèêë]", "e");
            lProducer = lProducer.replaceAll("[îï]", "i");
            lProducer = lProducer.replaceAll("[ôö]", "o");
            lProducer = lProducer.replaceAll("[ùûü]", "u");

            //Replace Color accents
            String lColor = getResources().getString(Wine.getStringIdFromColor(lWine.getColor())).toLowerCase();

            lColor = lColor.replaceAll("[àâ]", "a");
            lColor = lColor.replaceAll("[éèêë]", "e");
            lColor = lColor.replaceAll("[îï]", "i");
            lColor = lColor.replaceAll("[ôö]", "o");
            lColor = lColor.replaceAll("[ùûü]", "u");

            // Check if query matches any field
            if (    lName.contains(lLowerCaseQuery) && !lWine.getName().isEmpty()
                    || lOrigin.contains(lLowerCaseQuery) && !lWine.getOrigin().isEmpty()
                    || lProducer.contains(lLowerCaseQuery) && !lWine.getProducer().isEmpty()
                    || String.format("%d",lWine.getYear()).contains(lLowerCaseQuery)
                    || lColor.contains(lLowerCaseQuery))
            {
                lFilteredList.add(lWine);
            }
        }
        return lFilteredList;
    }
}
