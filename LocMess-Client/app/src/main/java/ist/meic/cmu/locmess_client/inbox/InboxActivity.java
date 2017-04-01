package ist.meic.cmu.locmess_client.inbox;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.LocMessage;
import ist.meic.cmu.locmess_client.inbox.available.AvailableTabFragment;
import ist.meic.cmu.locmess_client.inbox.opened.OpenedTabFragment;

public class InboxActivity extends AppCompatActivity implements AvailableTabFragment.OnMessageOpened {

    private FloatingActionButton fab;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), InboxActivity.this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public void openMessage(LocMessage message) {
        // put message in OpenedTabFragment's dataset
        pagerAdapter.mTabOpened.notifyMessageRead(message);
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

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    mTabAvailable = new AvailableTabFragment();
                    return mTabAvailable;
                case 1:
                    mTabOpened = new OpenedTabFragment();
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
