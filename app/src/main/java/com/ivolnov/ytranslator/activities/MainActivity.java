package com.ivolnov.ytranslator.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ivolnov.ytranslator.R;
import com.ivolnov.ytranslator.YandexApi;
import com.ivolnov.ytranslator.adapters.BookmarksAdapter;
import com.ivolnov.ytranslator.adapters.HistoryAdapter;
import com.ivolnov.ytranslator.db.EventLog;
import com.ivolnov.ytranslator.db.SQLiteEventLogLoader;
import com.ivolnov.ytranslator.dictionary.Dictionary;
import com.ivolnov.ytranslator.dictionary.VolleyDictionary;
import com.ivolnov.ytranslator.fragments.BookmarksFragment;
import com.ivolnov.ytranslator.fragments.FragmentsPropertiesProvider;
import com.ivolnov.ytranslator.fragments.HistoryFragment;
import com.ivolnov.ytranslator.fragments.TranslatorFragment;
import com.ivolnov.ytranslator.languages.Languages;
import com.ivolnov.ytranslator.languages.VolleyLanguages;
import com.ivolnov.ytranslator.translator.Translator;
import com.ivolnov.ytranslator.translator.VolleyTranslator;


/**
 * A root {@link android.app.Activity} for the app.
 *
 * @author ivolnov
 * @version %I%, %G%
 * @since 31.03.17
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Pair<Cursor, Cursor>>,
        FragmentsPropertiesProvider {

    public static final String TAB_POSITION_KEY = "last_active_tab_position";

    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private EventLog mEventLog;
    private RequestQueue mVolleyQueue;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private HistoryAdapter mHistoryAdapter;
    private BookmarksAdapter mBookmarksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        customiseTabs(mTabLayout);

        mVolleyQueue = Volley.newRequestQueue(this);
        mEventLog = (SQLiteEventLogLoader)
                getSupportLoaderManager()
                .initLoader(R.id.history_loader_id, null, this);

        mHistoryAdapter = new HistoryAdapter(mEventLog);
        mHistoryAdapter.withActiveIconDescription(
                getResources().getString(R.string.bookmark_icon_active_description));
        mHistoryAdapter.withIconDescription(
                getResources().getString(R.string.bookmark_icon_description));

        mBookmarksAdapter = new BookmarksAdapter(mEventLog);

        restoreTabPosition();

        YandexApi.init(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveTabPosition();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public Translator getTranslator() {
        return new VolleyTranslator(
                mVolleyQueue,
                new VolleyTranslator.RequestBuilder(
                        YandexApi.TRANSLATE_URL,
                        YandexApi.TRANSLATE_KEY
                ));
    }

    @Override
    public Dictionary getDictionary() {
        return new VolleyDictionary(
                mVolleyQueue,
                new VolleyDictionary.RequestBuilder(
                        YandexApi.DICTIONARY_URL,
                        YandexApi.DICTIONARY_KEY
                ));
    }

    @Override
    public Languages getLanguages() {
        final String ui = "ru";//TODO: hardcode!!!

        return new VolleyLanguages(
                mVolleyQueue,
                new VolleyLanguages.RequestBuilder(
                        YandexApi.LANGUAGES_URL,
                        YandexApi.TRANSLATE_KEY
                ),
                ui);
    }

    @Override
    public HistoryAdapter getHistoryAdapter() {
        return mHistoryAdapter;
    }

    @Override
    public BookmarksAdapter getBookmarksAdapter() {
        return mBookmarksAdapter;
    }

    @Override
    public Loader<Pair<Cursor, Cursor>> onCreateLoader(int id, Bundle args) {
        return new SQLiteEventLogLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<Pair<Cursor, Cursor>> loader, Pair<Cursor, Cursor> data) {
        mHistoryAdapter.swapCursor(data.first);
        mBookmarksAdapter.swapCursor(data.second);
    }

    @Override
    public void onLoaderReset(Loader<Pair<Cursor, Cursor>> loader) {
        mHistoryAdapter = null;
    }

    public SectionsPagerAdapter getSectionsPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    public EventLog getEventLog() {
        return mEventLog;
    }

    public MainActivity withHistoryAdapter(HistoryAdapter adapter) {
        this.mHistoryAdapter = adapter;
        return this;
    }

    public MainActivity withBookmarksAdapter(BookmarksAdapter adapter) {
        this.mBookmarksAdapter = adapter;
        return this;
    }

    public MainActivity withEventLog(EventLog eventLog) {
        this.mEventLog = eventLog;
        return this;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final SparseArray<Fragment> mInstantiatedFragments = new SparseArray<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mInstantiatedFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return TranslatorFragment.newInstance();
                case 1:
                    return HistoryFragment.newInstance();
                case 2:
                    return BookmarksFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        public Fragment getFragmentOnPosition(int position) {
            return mInstantiatedFragments.get(position);
        }
    }

    private void saveTabPosition() {
        final int position = mTabLayout.getSelectedTabPosition();

        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putInt(TAB_POSITION_KEY, position)
                .apply();
    }

    private void restoreTabPosition() {
        final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        final int position = preferences.getInt(TAB_POSITION_KEY, 0);
        mTabLayout.getTabAt(position).select();
    }

    private void customiseTabs(TabLayout tabLayout) {
        final TabLayout.Tab translate = tabLayout.getTabAt(0);
        final TabLayout.Tab history = tabLayout.getTabAt(1);
        final TabLayout.Tab favourites = tabLayout.getTabAt(2);

        translate.setIcon(R.drawable.ic_translate_black_24dp);
        history.setIcon(R.drawable.ic_access_time_grey_24dp);
        favourites.setIcon(R.drawable.ic_bookmark_grey_24dp);

        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            final String translatorTitle = getResources().getString(R.string.translator_tab_name);
            final String historyTitle = getResources().getString(R.string.history_tab_name);
            final String bookmarksTitle = getResources().getString(R.string.bookmarks_tab_name);

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        translate.setIcon(R.drawable.ic_translate_black_24dp);
                        history.setIcon(R.drawable.ic_access_time_grey_24dp);
                        favourites.setIcon(R.drawable.ic_bookmark_grey_24dp);
                        getSupportActionBar().setTitle(translatorTitle);
                        break;
                    case 1:
                        translate.setIcon(R.drawable.ic_translate_grey_24dp);
                        history.setIcon(R.drawable.ic_access_time_black_24dp);
                        favourites.setIcon(R.drawable.ic_bookmark_grey_24dp);
                        getSupportActionBar().setTitle(historyTitle);
                        break;
                    case 2:
                        translate.setIcon(R.drawable.ic_translate_grey_24dp);
                        history.setIcon(R.drawable.ic_access_time_grey_24dp);
                        favourites.setIcon(R.drawable.ic_bookmark_black_24dp);
                        getSupportActionBar().setTitle(bookmarksTitle);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
