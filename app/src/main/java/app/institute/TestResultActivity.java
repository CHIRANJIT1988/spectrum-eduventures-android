package app.institute;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.institute.model.MockTest;
import app.institute.model.Question;
import app.institute.mysql.send.SyncMockTestScore;
import app.institute.session.SessionManager;
import app.institute.sqlite.SQLiteDB;

import static app.institute.app.MyApplication.getInstance;

public class TestResultActivity extends AppCompatActivity
{
    private ViewPager mViewPager;
    private int count_correct, count_wrong, count_not_attempt;
    private int positive_score, negative_store;
    private int back_press_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //assert getSupportActionBar() != null;
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setTitle("");


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        // Fixes bug for disappearing fragment content
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for(MockTest m: getInstance().testList)
        {
            if(m.question.is_correct_answer == 0)
            {
                count_not_attempt ++;
            }

            else if(m.question.is_correct_answer == 1)
            {
                count_correct ++;
                positive_score += m.question.positive_marks;
            }

            else if(m.question.is_correct_answer == -1)
            {
                count_wrong++;
                negative_store += m.question.negative_marks;
            }
        }

        MockTest m = new MockTest(count_correct, count_wrong, count_not_attempt, new Question(positive_score, negative_store), String.valueOf(System.currentTimeMillis()));
        new SQLiteDB(this).insert((MockTest) getIntent().getSerializableExtra("TEST_PAPER"), m);
        new SyncMockTestScore(getApplicationContext()).execute(new SessionManager(this).getUserId());
    }

    @Override
    public void onBackPressed()
    {
        if(back_press_count == 0)
        {
            back_press_count++;
            Toast.makeText(getApplicationContext(), "Press Back Button again to Exit", Toast.LENGTH_LONG).show();
        }

        else
        {
            finish();
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new ScoreFragment(), "YOUR SCORE");
        adapter.addFrag(new ResultFragment(this), "SUMMERY");

        viewPager.setAdapter(adapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }
}