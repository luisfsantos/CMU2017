package ist.meic.cmu.locmess_client.messages.posted;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.messages.create.NewMessageActivity;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;

public class PostedActivity extends BaseNavigationActivity {

    private static final String KEY_ACTIVE_FRAG = "active";
    private static final String KEY_ARCHIVED_FRAG = "archived";

    private static final int ACTIVE_MESSAGES_LOADER_ID = R.id.active_messages_loader_id;
    private static final int ARCHIVED_MESSAGES_LOADER_ID = R.id.archived_messages_loader_id;

    private static final String ACTIVE_MESSAGES_SELECTION_QUERY =
            "date('now') BETWEEN " +
            LocMessDBContract.PostedMessages.COLUMN_DATE_FROM +
            " AND " + LocMessDBContract.PostedMessages.COLUMN_DATE_TO;
    private static final String ARCHIVED_MESSAGES_SELECTION_QUERY =
            "date('now') NOT BETWEEN " +
            LocMessDBContract.PostedMessages.COLUMN_DATE_FROM +
            " AND " + LocMessDBContract.PostedMessages.COLUMN_DATE_TO;

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
                Intent intent = new Intent(PostedActivity.this, NewMessageActivity.class);
                startActivity(intent);
            }
        });

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        if (savedInstanceState != null) {
            PostedTabFragment atf = (PostedTabFragment)getSupportFragmentManager()
                    .getFragment(savedInstanceState, KEY_ACTIVE_FRAG);
            PostedTabFragment artf = (PostedTabFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, KEY_ARCHIVED_FRAG);
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
        getSupportFragmentManager().putFragment(outState, KEY_ACTIVE_FRAG, pagerAdapter.mTabActive);
        getSupportFragmentManager().putFragment(outState, KEY_ARCHIVED_FRAG, pagerAdapter.mTabArchived);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        PostedTabFragment mTabActive;
        PostedTabFragment mTabArchived;

        String[] TAB_TITLES = {getString(R.string.tab_active_messages), getString(R.string.tab_archived_messages)};
        Context mContext;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        PagerAdapter(FragmentManager fm, Context context, PostedTabFragment atf, PostedTabFragment artf) {
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
                        mTabActive = PostedTabFragment.newInstance(ACTIVE_MESSAGES_LOADER_ID, ACTIVE_MESSAGES_SELECTION_QUERY);
                    }
                    return mTabActive;
                case 1:
                    if (mTabArchived == null ) {
                        mTabArchived = PostedTabFragment.newInstance(ARCHIVED_MESSAGES_LOADER_ID, ARCHIVED_MESSAGES_SELECTION_QUERY);
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
