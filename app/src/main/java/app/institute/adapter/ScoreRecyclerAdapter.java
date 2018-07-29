package app.institute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import app.institute.R;
import app.institute.helper.Helper;
import app.institute.model.MockTest;

public class ScoreRecyclerAdapter extends RecyclerView.Adapter<ScoreRecyclerAdapter.VersionViewHolder>
{
    private List<MockTest> scores;
    private Context context;
    private OnItemClickListener clickListener;

    public ScoreRecyclerAdapter(Context context, List<MockTest> scores)
    {
        this.context = context;
        this.scores = scores;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_score, viewGroup, false);
        return new VersionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VersionViewHolder viewHolder, int i)
    {
        viewHolder.bindData(scores.get(i));
    }

    @Override
    public int getItemCount()
    {
        return scores == null ? 0 : scores.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView test_name;
        private TextView pos_score;
        private TextView neg_score;
        private TextView tv_score;
        private TextView count_wrong;
        private TextView count_correct;
        private TextView count_not_attempt;
        private TextView total_marks;
        private TextView duration;
        private TextView timestamp;
        private TextView progress;
        private ProgressBar progress_bar;

        private VersionViewHolder(View itemView)
        {
            super(itemView);

            test_name = (TextView) itemView.findViewById(R.id.test_name);
            tv_score = (TextView) itemView.findViewById(R.id.score);
            pos_score = (TextView) itemView.findViewById(R.id.pos_score);
            neg_score = (TextView) itemView.findViewById(R.id.neg_score);
            count_wrong = (TextView) itemView.findViewById(R.id.count_wrong);
            count_correct = (TextView) itemView.findViewById(R.id.count_correct);
            count_not_attempt = (TextView) itemView.findViewById(R.id.count_not_attempt);
            total_marks = (TextView) itemView.findViewById(R.id.total_marks);
            duration = (TextView) itemView.findViewById(R.id.duration);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            progress = (TextView) itemView.findViewById(R.id.tv_progress);
            progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);

            itemView.setOnClickListener(this);
        }

        private void bindData(MockTest score)
        {
            test_name.setText(Helper.toCamelCase(score.test_name));
            pos_score.setText(String.valueOf("+" + score.question.positive_marks));
            neg_score.setText(String.valueOf("-" + score.question.negative_marks));
            count_wrong.setText(String.valueOf(score.count_wrong));
            count_correct.setText(String.valueOf(score.count_correct));
            count_not_attempt.setText(String.valueOf(score.count_not_attempt));
            total_marks.setText(String.valueOf(score.total_marks));
            duration.setText(String.valueOf(score.duration + " min"));

            tv_score.setText(String.valueOf(score.question.positive_marks - score.question.negative_marks));
            int percentage = ((score.question.positive_marks - score.question.negative_marks) * 100) / score.total_marks;
            progress.setText(String.valueOf(percentage + "%"));
            progress_bar.setProgress(percentage);

            String datetime = new SimpleDateFormat("yyyy-MM-dd").format(Long.parseLong(score.attempted_on));
            timestamp.setText(Helper.dateTimeFormat(datetime));
        }

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }
}