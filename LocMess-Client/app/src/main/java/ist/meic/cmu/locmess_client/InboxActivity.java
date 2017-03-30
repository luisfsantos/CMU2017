package ist.meic.cmu.locmess_client;

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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ist.meic.cmu.locmess_client.data.LocMessage;

public class InboxActivity extends AppCompatActivity {

    private static List<LocMessage> DUMMY_DATASET;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), InboxActivity.this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            tab.setCustomView(pagerAdapter.getTabView(i));
//        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String[] TAB_TITLES = {getString(R.string.tab_available_messages), getString(R.string.tab_opened_messages)};
        Context mContext;

        public PagerAdapter(FragmentManager fm,  Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            return new TabFragment();
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
