package app.institute;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.institute.adapter.DashboardRecyclerAdapter;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.Branch;
import app.institute.model.Class;
import app.institute.model.Subject;
import app.institute.mysql.receive.ReceiveDefaultData;
import app.institute.session.SessionManager;
import app.institute.sqlite.SQLiteDB;

import static app.institute.app.Global.MOBILE_NUMBER;
import static app.institute.app.Global.NAME;
import static app.institute.app.Global.PROFILE_DETAILS;
import static app.institute.app.Global.TABLE_BRANCH;
import static app.institute.app.MyApplication.getInstance;
import static app.institute.app.MyApplication.prefs;

import static app.institute.services.DownstreamMessageBroadcast.DISPLAY_MESSAGE_ACTION;
import static app.institute.services.DownstreamMessageBroadcast.EXTRA_MESSAGE;


public class DashboardActivity extends AppCompatActivity implements OnTaskCompleted,
             View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{

    private SessionManager session;

    private static Branch branch_object = new Branch();
    private static Subject subject_object = new Subject();

    private static RecyclerView mRecyclerView;
    private ProgressBar progress;
    private SQLiteDB helper;

    private Menu menu;
    private static FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        helper = new SQLiteDB(this);

        progress = (ProgressBar) findViewById(R.id.pbLoading);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        if(helper.dbRowCount(TABLE_BRANCH) != 0)
        {
            onTaskCompleted(true, 200, "branch");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.session = new SessionManager(this);

        this.registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fab:

                startActivity(new Intent(DashboardActivity.this, ScoresActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed()
    {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        try
        {
            menu.clear();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {

            getMenuInflater().inflate(R.menu.dashboard, menu);

            this.menu = menu;

            MenuItem menuItemBidders = menu.findItem(R.id.action_inbox);
            menuItemBidders.setIcon(buildCounterDrawable(new SQLiteDB(this).unreadMessageCount(), R.drawable.ic_bell_white_24dp));
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case R.id.action_logout:

                session.logoutUser();
                finish();
                break;

            case R.id.action_inbox:

                startActivity(new Intent(DashboardActivity.this, InboxActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case R.id.nav_my_profile:

                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                break;

            case R.id.nav_notification:

                startActivity(new Intent(DashboardActivity.this, InboxActivity.class));
                break;

            case R.id.nav_score:

                startActivity(new Intent(DashboardActivity.this, ScoresActivity.class));
                break;

            case R.id.nav_share:

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Spectrum Eduventures App https://play.google.com/store/apps/details?id=" + getInstance().getPackageName();
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Spectrum Eduventures App");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

                startActivity(Intent.createChooser(sharingIntent, "Share Via"));

                break;

            case R.id.nav_rate:

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getInstance().getPackageName())));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onDestroy()
    {

        try
        {
            unregisterReceiver(mHandleMessageReceiver);
        }

        catch (Exception e)
        {
            Log.e("UnRegister Error", "> " + e.getMessage());
        }

        super.onDestroy();
    }


    @Override
    public void onResume()
    {

        try
        {

            if(helper.dbRowCount(TABLE_BRANCH) == 0)
            {
                progress.setVisibility(View.VISIBLE);
                new ReceiveDefaultData(getApplicationContext(), this).execute();
            }

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View header = navigationView.getHeaderView(0);
            TextView nav_user_name = (TextView) header.findViewById(R.id.nav_user_name);
            TextView nav_mobile_number = (TextView) header.findViewById(R.id.nav_mobile_number);

            try
            {
                JSONObject jsonObj = new JSONObject(prefs.getString(PROFILE_DETAILS, ""));

                if(jsonObj.has(NAME))
                {
                    nav_user_name.setText(jsonObj.getString(NAME).toUpperCase());
                }

                if (jsonObj.has(MOBILE_NUMBER))
                {
                    nav_mobile_number.setText(String.valueOf("M# " + jsonObj.getString(MOBILE_NUMBER)));
                }
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }

            onCreateOptionsMenu(menu);

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        super.onResume();
    }


    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent) {

        try
        {

            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            // lblMessage.append(newMessage + "\n");

            if(newMessage == null)
            {
                return;
            }

            Log.v("message: ", newMessage);
            onCreateOptionsMenu(menu);
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
        }
    };


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            if( flag && code == 200)
            {

                helper.getAllBranch();

                final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

                viewPager.setOffscreenPageLimit(Branch.list.size());
                setupViewPager(viewPager, Branch.list);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);

                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab)
                    {
                        viewPager.setCurrentItem(tab.getPosition());
                        branch_object = Branch.list.get(tab.getPosition());
                    }


                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });


                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position)
                    {
                        branch_object = Branch.list.get(position);
                    }

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            progress.setVisibility(View.GONE);
        }
    }


    private void setupViewPager(ViewPager viewPager, List<Branch> branchList)
    {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (Branch branch: branchList)
        {
            branch_object = Branch.list.get(0);
            List<Subject> list = helper.getAllSubject(branch.branch_code);
            list.add(list.size(), new Subject("1", "Mock Test"));

            adapter.addFrag(new BranchFragment(this, list), branch.branch_name.toUpperCase());
        }

        viewPager.setAdapter(adapter);
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter
    {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        private ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
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


    public static class BranchFragment extends Fragment
    {

        private List<Subject> list;
        private Context context;
        private RecyclerView.LayoutManager mLayoutManager;
        private DashboardRecyclerAdapter mAdapter;


        public BranchFragment()
        {

        }


        @SuppressLint("ValidFragment")
        public BranchFragment(Context context, List<Subject> list)
        {
            this.context = context;
            this.list = list;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {

            View view = inflater.inflate(R.layout.fragment_branch, container, false);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new GridLayoutManager(context, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);

            if (mAdapter == null)
            {
                mAdapter = new DashboardRecyclerAdapter(context, list);
                mRecyclerView.setAdapter(mAdapter);
            }

            mAdapter.SetOnItemClickListener(new DashboardRecyclerAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {

                    subject_object = list.get(position);

                    if(position == list.size()-1)
                    {
                        //Toast.makeText(context, "Select Subject to Start Mock Test", Toast.LENGTH_LONG).show();
                        create_popup_menu(new SQLiteDB(getInstance()).getAllClass(branch_object.branch_code, subject_object.subject_code), true, fab);
                        return;
                    }

                    create_popup_menu(new SQLiteDB(getInstance()).getAllClass(branch_object.branch_code, subject_object.subject_code), false, fab);
                }
            });

            return view;
        }
    }


    private static void create_popup_menu(List<Class> list, final boolean is_mock_test, final View view)
    {

        PopupMenu popup = new PopupMenu(getInstance(), view);
        //Inflating the Popup using xml file
        //popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        for(Class _class: list)
        {
            popup.getMenu().add(0, Integer.valueOf(_class.class_code), 0, String.valueOf(_class.class_name));
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item)
            {

                Subject _subject = new Subject(subject_object.subject_code, subject_object.subject_name);
                Class _class = new Class(String.valueOf(item.getItemId()), String.valueOf(item.getTitle()));

                if(!is_mock_test)
                {
                    Intent intent = new Intent(getInstance(), UnitActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("BRANCH", new Branch(_subject, _class, branch_object.branch_code, branch_object.branch_name));
                    getInstance().startActivity(intent);
                }

                else
                {
                    Intent intent = new Intent(getInstance(), TestPaperActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("BRANCH", new Branch(_subject, _class, branch_object.branch_code, branch_object.branch_name));
                    getInstance().startActivity(intent);
                }

                return true;
            }
        });

        popup.show();//showing popup menu
    }


    private Drawable buildCounterDrawable(int count, int backgroundImageId)
    {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);


        if (count == 0)
        {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        }

        else if(count > 9)
        {
            count = 9;
        }


        TextView textView = (TextView) view.findViewById(R.id.count);
        textView.setText(String.valueOf(count));


        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());


        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }
}