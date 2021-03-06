package io.blacklagoonapps.myquotes;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.blacklagoonapps.myquotes.authors.AuthorsFragment;
import io.blacklagoonapps.myquotes.quotes.AddQuoteActivity;
import io.blacklagoonapps.myquotes.quotes.QuotesFragment;
import io.blacklagoonapps.myquotes.settings.SettingsActivity;


public class MainActivity extends ThemedActivity {

    private int currentTab = 0;
    private static final String CURRENT_TAB = "CURRENT_TAB";
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private Fragment[] fragments = {new QuotesFragment(), new AuthorsFragment()};
    private FloatingActionButton floatingAddButton;
    private int[] tabIcons = {R.drawable.quotes, R.drawable.authors};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        floatingAddButton = findViewById(R.id.fab_main);

        setActionBarTitle();
        setUpFloatingAddButton();
        setUpViewPager();
        setUpTabLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().hasExtra(CURRENT_TAB)) {
            TabLayout tabLayout = findViewById(R.id.tablayout_main);
            TabLayout.Tab tab = tabLayout.getTabAt(getIntent().getIntExtra(CURRENT_TAB, 0));
            getIntent().removeExtra(CURRENT_TAB);
            if (tab != null)
                tab.select();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ((QuotesFragment) fragments[0]).restart();
        ((AuthorsFragment) fragments[1]).initFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SettingsActivity.THEME_CHANGED) {
            // Using recreate() leaded to Exception: Performing pause of activity that is not resumed
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(getIntent().putExtra(CURRENT_TAB, currentTab));
        }
    }

    private void setUpFloatingAddButton() {
        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddQuoteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpViewPager() {
        viewPager = findViewById(R.id.viewpager_main);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentTab = position;
                if (position == 0) {
                    floatingAddButton.show();
                } else if (position == 1) {
                    floatingAddButton.hide();
                }
                invalidateOptionsMenu();
                setActionBarTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentTab == 0)
            menu.findItem(R.id.item_main_search).setVisible(true);
        else
            menu.findItem(R.id.item_main_search).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    private void setUpTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tablayout_main);
        final int darkColor = ContextCompat.getColor(this, R.color.color_control_white_dark);

        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab quotesTab = tabLayout.getTabAt(0);
        if (quotesTab != null) {
            quotesTab.setIcon(tabIcons[0]);
            quotesTab.setText("");
        }

        TabLayout.Tab authorsTab = tabLayout.getTabAt(1);
        if (authorsTab != null) {
            Drawable shadowedAuthors = AppCompatResources.getDrawable(this, tabIcons[1]);
            if (shadowedAuthors != null)
                shadowedAuthors.setColorFilter(darkColor, PorterDuff.Mode.SRC_IN);
            authorsTab.setIcon(shadowedAuthors);
            authorsTab.setText("");
        }

        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        if (tab.getIcon() != null)
                            tab.getIcon().clearColorFilter();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        if (tab.getIcon() != null)
                            tab.getIcon().setColorFilter(darkColor, PorterDuff.Mode.SRC_IN);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.item_main_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                ((QuotesFragment) fragments[0]).restart();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ((QuotesFragment) fragments[0]).findQuotesWithAuthorsContainingWords(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_main_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 1);
                break;
            case R.id.item_main_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private String[] fragmentTitles = {getString(R.string.quotes_tab_title), getString(R.string.authors_tab_title)};

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles[position];
        }
    }

    private void setActionBarTitle() {
        String title = sectionsPagerAdapter.fragmentTitles[currentTab];
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }
}
