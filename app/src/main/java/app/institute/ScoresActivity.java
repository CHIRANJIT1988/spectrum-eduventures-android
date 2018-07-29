package app.institute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import app.institute.adapter.ScoreRecyclerAdapter;
import app.institute.model.MockTest;
import app.institute.sqlite.SQLiteDB;

import static app.institute.app.Global.TABLE_SCORE;

public class ScoresActivity extends AppCompatActivity
{
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int pageCount = 1;

    private ScoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<MockTest> list;

    private SQLiteDB helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("My Score's");

        this.helper = new SQLiteDB(this);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        list = new SQLiteDB(this).getAllScore(0, 20);

        adapter = new ScoreRecyclerAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.SetOnItemClickListener(new ScoreRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {

            }
        });

        loadMoreOnScroll();
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

                new SQLiteDB(this).deleteAllRow(TABLE_SCORE);
                list.clear();
                adapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMoreOnScroll()
    {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                loading = true;

                int totalRecord = helper.dbRowCount(TABLE_SCORE);
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

                                List<MockTest> tempList = helper.getAllScore(pageCount * 20, 20);

                                for (MockTest test: tempList) {
                                    list.add(list.size(), test);
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
}