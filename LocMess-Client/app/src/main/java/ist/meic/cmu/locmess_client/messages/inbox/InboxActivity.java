package ist.meic.cmu.locmess_client.messages.inbox;

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
import ist.meic.cmu.locmess_client.messages.inbox.available.AvailableTabFragment;
import ist.meic.cmu.locmess_client.messages.inbox.opened.OpenedTabFragment;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;

public class InboxActivity extends BaseNavigationActivity {

    private FloatingActionButton fab;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_inbox, frameLayout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InboxActivity.this, NewMessageActivity.class);
                startActivity(intent);
            }
        });

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        if (savedInstanceState != null) {
            AvailableTabFragment atf = (AvailableTabFragment)getSupportFragmentManager()
                    .getFragment(savedInstanceState, "available");
            OpenedTabFragment otf = (OpenedTabFragment)getSupportFragmentManager()
                    .getFragment(savedInstanceState, "opened");
            pagerAdapter = new PagerAdapter(getSupportFragmentManager(), InboxActivity.this, atf, otf);
        } else {
            pagerAdapter = new PagerAdapter(getSupportFragmentManager(), InboxActivity.this);
        }
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_inbox);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "available", pagerAdapter.mTabAvailable);
        getSupportFragmentManager().putFragment(outState, "opened", pagerAdapter.mTabOpened);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        AvailableTabFragment mTabAvailable;
        OpenedTabFragment mTabOpened;

        String[] TAB_TITLES = {getString(R.string.tab_available_messages), getString(R.string.tab_opened_messages)};
        Context mContext;

        public PagerAdapter(FragmentManager fm,  Context context) {
            super(fm);
            mContext = context;
        }

        PagerAdapter(FragmentManager fm, Context context, AvailableTabFragment atf, OpenedTabFragment otf) {
            super(fm);
            mContext = context;
            mTabAvailable = atf;
            mTabOpened = otf;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if (mTabAvailable == null) {
                        mTabAvailable = new AvailableTabFragment();
                    }
                    return mTabAvailable;
                case 1:
                    if (mTabOpened == null ) {
                        mTabOpened = new OpenedTabFragment();
                    }
                    return mTabOpened;
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
