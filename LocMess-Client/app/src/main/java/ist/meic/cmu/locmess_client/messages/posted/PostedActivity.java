package ist.meic.cmu.locmess_client.messages.posted;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.messages.posted.active.ActiveTabFragment;
import ist.meic.cmu.locmess_client.messages.posted.archived.ArchivedTabFragment;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;

public class PostedActivity extends BaseNavigationActivity {

    private FloatingActionButton fab;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_posted, frameLayout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        if (savedInstanceState != null) {
            ActiveTabFragment atf = (ActiveTabFragment)getSupportFragmentManager()
                    .getFragment(savedInstanceState, "active");
            ArchivedTabFragment artf = (ArchivedTabFragment)getSupportFragmentManager()
                    .getFragment(savedInstanceState, "archived");
            pagerAdapter = new PagerAdapter(getSupportFragmentManager(), PostedActivity.this, atf, artf);
        } else {
            pagerAdapter = new PagerAdapter(getSupportFragmentManager(), PostedActivity.this);
        }
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_posted);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "active", pagerAdapter.mTabActive);
        getSupportFragmentManager().putFragment(outState, "archived", pagerAdapter.mTabArchived);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        ActiveTabFragment mTabActive;
        ArchivedTabFragment mTabArchived;

        String[] TAB_TITLES = {getString(R.string.tab_active_messages), getString(R.string.tab_archived_messages)};
        Context mContext;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        PagerAdapter(FragmentManager fm, Context context, ActiveTabFragment atf, ArchivedTabFragment artf) {
            super(fm);
            mContext = context;
            mTabActive = atf;
            mTabArchived = artf;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if (mTabActive == null) {
                        mTabActive = new ActiveTabFragment();
                    }
                    return mTabActive;
                case 1:
                    if (mTabArchived == null ) {
                        mTabArchived = new ArchivedTabFragment();
                    }
                    return mTabArchived;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }
    }

}
