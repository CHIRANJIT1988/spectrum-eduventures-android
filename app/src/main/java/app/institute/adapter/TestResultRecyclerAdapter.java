package app.institute.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.institute.ImagePreviewActivity;
import app.institute.R;
import app.institute.model.MockTest;
import app.institute.model.Option;

import static app.institute.app.MyApplication.getInstance;


public class TestResultRecyclerAdapter extends RecyclerView.Adapter<TestResultRecyclerAdapter.VersionViewHolder>
{
    private Context context = null;
    private OnItemClickListener clickListener;
    private List<MockTest> testList;
    public TestResultRecyclerAdapter(Context context, List<MockTest> testList)
    {
        this.context = context;
        this.testList = testList;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_test_result, viewGroup, false);
        return new VersionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VersionViewHolder holder, int i)
    {
        holder.btn_view_diagram.setTag(i);
        holder.bindData(testList.get(i));
    }

    @Override
    public int getItemCount()
    {
        return testList == null ? 0 : testList.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView positive_marks;
        private TextView negative_marks;
        private TextView question;
        private TextView correct_answer;
        private ImageView thumbnail;
        private Button btn_view_diagram;

        private VersionViewHolder(View itemView)
        {
            super(itemView);

            positive_marks = (TextView) itemView.findViewById(R.id.positive_marks);
            negative_marks = (TextView) itemView.findViewById(R.id.negative_marks);
            question = (TextView) itemView.findViewById(R.id.question);
            correct_answer = (TextView) itemView.findViewById(R.id.correct_answer);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            btn_view_diagram = (Button) itemView.findViewById(R.id.btn_view_diagram);

            btn_view_diagram.setOnClickListener(onButtonClickListener);
            itemView.setOnClickListener(this);
        }

        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if(v.getId() == R.id.btn_view_diagram)
                {
                    final String URL = context.getResources().getString(R.string.spectrumServerAdminUrl) +  testList.get((int) v.getTag()).question.diagram;

                    Intent intent = new Intent(context, ImagePreviewActivity.class);
                    intent.putExtra("URL", URL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            }
        };

        private void bindData(MockTest mockTest)
        {
            positive_marks.setText(String.valueOf("+" + mockTest.question.positive_marks));
            negative_marks.setText(String.valueOf("-" + mockTest.question.negative_marks));
            question.setText(mockTest.question.question);

            if(mockTest.question.diagram.isEmpty())
            {
                btn_view_diagram.setVisibility(View.GONE);
            }

            else
            {
                btn_view_diagram.setVisibility(View.VISIBLE);
            }

            ShapeDrawable background = new ShapeDrawable();
            background.setShape(new OvalShape()); // or RoundRectShape()
            background.getPaint().setColor(Color.parseColor("#4CAF50"));
            positive_marks.setBackground(background);

            ShapeDrawable background1 = new ShapeDrawable();
            background1.setShape(new OvalShape()); // or RoundRectShape()
            background1.getPaint().setColor(Color.parseColor("#e53935"));
            negative_marks.setBackground(background1);

            for(Option m: mockTest.question.optionList)
            {
                if(m.is_correct == 1)
                {
                    correct_answer.setText(m.option);
                    break;
                }
            }

            if(mockTest.question.is_correct_answer == 0)
            {
                int imgResId = context.getResources().getIdentifier("ic_not_attempt", "drawable", getInstance().getPackageName());
                thumbnail.setImageResource(imgResId);
            }

            else if(mockTest.question.is_correct_answer == 1)
            {
                int imgResId = context.getResources().getIdentifier("ic_correct", "drawable", getInstance().getPackageName());
                thumbnail.setImageResource(imgResId);
            }

            else if(mockTest.question.is_correct_answer == -1)
            {
                int imgResId = context.getResources().getIdentifier("ic_wrong", "drawable", getInstance().getPackageName());
                thumbnail.setImageResource(imgResId);
            }
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