package app.institute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import app.institute.adapter.DailyPracticePapersRecyclerAdapter;
import app.institute.alert.CustomAlertDialog;
import app.institute.app.MyApplication;
import app.institute.helper.OnAlertButtonClick;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.Unit;
import app.institute.mysql.receive.ReceiveDailyPracticePapers;

public class DailyPracticePaperActivity extends AppCompatActivity implements OnTaskCompleted, OnAlertButtonClick
{

    private DailyPracticePapersRecyclerAdapter mAdapter;
    private ProgressBar progress;
    private MyApplication aController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dpp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.aController = (MyApplication) getApplicationContext();

        final Unit _unit = (Unit) getIntent().getSerializableExtra("UNIT");

        setTitle("Daily Practice Papers");

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(DailyPracticePaperActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        progress = (ProgressBar) findViewById(R.id.pbLoading);
        progress.setVisibility(View.VISIBLE);

        aController.dppList.clear();
        new ReceiveDailyPracticePapers(getApplicationContext(), this).execute(_unit);

        mAdapter = new DailyPracticePapersRecyclerAdapter(getApplicationContext(), this, aController.dppList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.SetOnItemClickListener(new DailyPracticePapersRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(DailyPracticePaperActivity.this, OnlinePDFViewerActivity.class);
                intent.putExtra("FILE_NAME", aController.dppList.get(position).daily_practice_paper);
                startActivity(intent);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_in);
        final RelativeLayout dpp_layout = (RelativeLayout) findViewById(R.id.dpp_layout);
        dpp_layout.startAnimation(animation);
    }

    @Override
    public void onAlertButtonClick(boolean flag, int code)
    {
        if (flag && code == 200)
        {
            finish();
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {
        try
        {
            if (flag && code == 200)
            {
                mAdapter.notifyDataSetChanged();
            }

            else
            {
                new CustomAlertDialog(DailyPracticePaperActivity.this, this).showOKDialog("Sorry", message, "CLOSE");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}