package app.institute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import app.institute.adapter.InboxRecyclerAdapter;
import app.institute.model.Message;
import app.institute.sqlite.SQLiteDB;

import java.util.List;

import static app.institute.app.Global.TABLE_INBOX;
import static app.institute.services.DownstreamMessageBroadcast.DISPLAY_MESSAGE_ACTION;
import static app.institute.services.DownstreamMessageBroadcast.EXTRA_MESSAGE;

public class InboxActivity extends AppCompatActivity
{
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int pageCount = 1;

    private InboxRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Message> list;

    private SQLiteDB helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Notification's");

        this.helper = new SQLiteDB(this);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        list = new SQLiteDB(this).getAllMessage(0, 20);

        if (adapter == null)
        {
            adapter = new InboxRecyclerAdapter(this, list);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(0);
        }

        adapter.SetOnItemClickListener(new InboxRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

            }
        });

        this.registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

        /**
         * Mark as Read
         */
        new SQLiteDB(this).setAsRead();
        loadMoreOnScroll();
    }


    private void loadMoreOnScroll()
    {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                loading = true;

                int totalRecord = helper.dbRowCount(TABLE_INBOX);
                int totalPage;

                if (totalRecord % 10 == 0) {
                    totalPage = (totalRecord / 20);
                } else {
                    totalPage = (totalRecord / 20) + 1;
                }

                if (pageCount < totalPage) {

                    if (dy > 0) //check for scroll down
                    {

                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {

                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {

                                loading = false;
                                Log.v("onScrolled: ", "Last Item Wow ! " + pageCount);

                                List<Message> tempList = helper.getAllMessage(pageCount * 20, 20);

                                for (Message message: tempList) {
                                    list.add(list.size(), message);
                                }

                                adapter.notifyDataSetChanged();

                                recyclerView.scrollToPosition(totalItemCount - 1);
                                pageCount++;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_clear_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                finish();
                return true;

            case R.id.action_clear:

                new SQLiteDB(this).deleteAllRow(TABLE_INBOX);
                list.clear();
                adapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called just before the activity is destroyed. */
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

            try
            {
                /**
                 * Add to message list
                 */
                list.add(0, new Message(newMessage, String.valueOf(System.currentTimeMillis())));
                recyclerView.scrollToPosition(0);

                /**
                 * Mark as Read
                 */
                new SQLiteDB(InboxActivity.this).setAsRead();
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            finally
            {
                adapter.notifyDataSetChanged();
            }
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
        }
    };
}