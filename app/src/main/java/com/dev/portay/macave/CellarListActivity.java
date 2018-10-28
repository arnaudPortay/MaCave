package com.dev.portay.macave;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dev.portay.macave.db.entity.Cepage;
import com.dev.portay.macave.db.entity.Dish;
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
    private DrawerLayout mDrawerLayout;
    private Observer<List<Wine>> mObserver;
    private SearchView mSearchView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_WINE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            List<String> lCepageNames = data.getStringArrayListExtra(AddWineActivity.CEPAGE_REPLY);
            List<Cepage> lCepageList = new ArrayList<>();
            for (String lCepageName: lCepageNames)
            {
                lCepageList.add(new Cepage(-1, lCepageName)); //wine id is set later
            }

            List<String> lDishNameList = data.getStringArrayListExtra(AddWineActivity.DISHES_REPLY);
            List<Dish> lDishList = new ArrayList<>();
            for (String lDishName:lDishNameList)
            {
                lDishList.add(new Dish(-1, lDishName));
            }

            mWineViewModel.insert(
                    (Wine)data.getParcelableExtra(AddWineActivity.WINE_REPLY),
                    lCepageList, lDishList
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cellar_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setTitle(getTitle());

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

        mObserver = new Observer<List<Wine>>()
        {
            @Override
            public void onChanged(@Nullable List<Wine> pWines)
            {
                mWines = pWines;
                lListAdapter.setWines(pWines);
                onQueryTextChange(mSearchView.getQuery().toString());
            }
        };
        mWineViewModel.getWinesWithBottles().observe(this, mObserver);



        // Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        if (mWineViewModel.getWinesWithBottles().hasObservers())
                        {
                            mWineViewModel.getWinesWithBottles().removeObserver(mObserver);
                        }

                        if (mWineViewModel.getWinesToBuy().hasObservers())
                        {
                            mWineViewModel.getWinesToBuy().removeObserver(mObserver);
                        }

                        if (mWineViewModel.getAllWines().hasObservers())
                        {
                            mWineViewModel.getAllWines().removeObserver(mObserver);
                        }

                        if (menuItem.getItemId() == R.id.nav_cellar)
                        {
                            mWineViewModel.getWinesWithBottles().observe(CellarListActivity.this, mObserver);
                            getSupportActionBar().setTitle(R.string.app_name);
                        }

                        if (menuItem.getItemId() == R.id.nav_buy_list)
                        {
                            mWineViewModel.getWinesToBuy().observe(CellarListActivity.this, mObserver);
                            getSupportActionBar().setTitle(R.string.nav_buy_list);
                        }

                        if (menuItem.getItemId() == R.id.nav_all_wines)
                        {
                            mWineViewModel.getAllWines().observe(CellarListActivity.this, mObserver);
                            getSupportActionBar().setTitle(R.string.nav_all_wines);
                        }

                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                findViewById(R.id.search_scrollview).setVisibility(View.GONE);
                return false;
            }
        });
        mSearchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                findViewById(R.id.search_scrollview).setVisibility(View.VISIBLE);
            }
        });

        ChipGroup lGroup = findViewById(R.id.search_chipgroup);
        for (int i = 0; i < lGroup.getChildCount(); i++)
        {
            lGroup.getChildAt(i).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onQueryTextChange(mSearchView.getQuery().toString());
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String s)
    {
        new FilterAsync(mWines,s).execute();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s)
    {
        // Nothing to do
        return false;
    }

    private String replaceAccents(String pString)
    {
        pString = pString.replaceAll("[àâ]", "a");
        pString = pString.replaceAll("[éèêë]", "e");
        pString = pString.replaceAll("[îï]", "i");
        pString = pString.replaceAll("[ôö]", "o");
        pString = pString.replaceAll("[ùûü]", "u");

        return pString;
    }

    private List<Wine> filter(List<Wine> pWines, String pQuery)
    {
        if (!pQuery.isEmpty())
        {
            String lLowerCaseQuery = replaceAccents(pQuery.toLowerCase());
            final List<Wine> lFilteredList = new ArrayList<>();

            for (Wine lWine : pWines)
            {
                if (((Chip) findViewById(R.id.m_chip_name)).isChecked())
                {
                    // Replace Name accents
                    String lName = replaceAccents(lWine.getName().toLowerCase());

                    if (lName.contains(lLowerCaseQuery) && !lWine.getName().isEmpty())
                    {
                        lFilteredList.add(lWine);
                        continue;
                    }
                }

                if (((Chip) findViewById(R.id.m_chip_origin)).isChecked())
                {
                    // Replace Origin accents
                    String lOrigin = replaceAccents(lWine.getOrigin().toLowerCase());

                    if (lOrigin.contains(lLowerCaseQuery) && !lWine.getOrigin().isEmpty())
                    {
                        lFilteredList.add(lWine);
                        continue;
                    }
                }

                if (((Chip) findViewById(R.id.m_chip_producer)).isChecked())
                {
                    // Replace Producer accents
                    String lProducer = replaceAccents(lWine.getProducer().toLowerCase());

                    if (lProducer.contains(lLowerCaseQuery) && !lWine.getProducer().isEmpty())
                    {
                        lFilteredList.add(lWine);
                        continue;
                    }
                }

                if (((Chip) findViewById(R.id.m_chip_color)).isChecked())
                {
                    //Replace Color accents
                    String lColor = replaceAccents(getResources().getString(Wine.getStringIdFromColor(lWine.getColor())).toLowerCase());

                    if (lColor.contains(lLowerCaseQuery))
                    {
                        lFilteredList.add(lWine);
                        continue;
                    }
                }

                if (((Chip) findViewById(R.id.m_chip_year)).isChecked())
                {
                    if (String.format("%d", lWine.getYear()).contains(lLowerCaseQuery))
                    {
                        lFilteredList.add(lWine);
                        continue;
                    }
                }

                if (((Chip) findViewById(R.id.m_chip_bottles)).isChecked())
                {
                    if (String.format("%d", lWine.getBottleNumber()).contains(lLowerCaseQuery))
                    {
                        lFilteredList.add(lWine);
                        continue;
                    }
                }

                if (((Chip) findViewById(R.id.m_chip_dishes)).isChecked())
                {
                    List<Dish> lDishes = DataRepository.getDataRepository().getDishesByWineIdSync(lWine.getId());
                    boolean lHasQuery = false;

                    for (Dish lDish : lDishes)
                    {
                        String lDishName = replaceAccents(lDish.mDishName.toLowerCase());

                        if (lDishName.contains(lLowerCaseQuery))
                        {
                            lFilteredList.add(lWine);
                            lHasQuery = true;
                            break;
                        }
                    }

                    if (lHasQuery)
                    {
                        continue;
                    }

                }

                if (((Chip) findViewById(R.id.m_chip_cepage)).isChecked())
                {
                    List<Cepage> lCepages = DataRepository.getDataRepository().getCepageByWineIdSync(lWine.getId());

                    for (Cepage lCepage : lCepages)
                    {
                        String lCepageName = replaceAccents(lCepage.mCepageName.toLowerCase());

                        if (lCepageName.contains(lLowerCaseQuery))
                        {
                            lFilteredList.add(lWine);
                            break;
                        }
                    }

                }
            }
            return lFilteredList;
        }
        return pWines;
    }

    private class FilterAsync extends AsyncTask<Void, Void, List<Wine>>
    {
        private List<Wine> mWines;
        private String mQuery;

        FilterAsync(List<Wine> pWines, String pQuery)
        {
            mWines = pWines;
            mQuery = pQuery;
        }

        @Override
        protected List<Wine> doInBackground(Void... voids)
        {
            return filter(mWines, mQuery);
        }

        @Override
        protected void onPostExecute(List<Wine> wines)
        {
            RecyclerView lView = findViewById(R.id.wine_list);
            ((CellarAdapter)lView.getAdapter()).setWines(wines);
            lView.scrollToPosition(0);
        }
    }
}
