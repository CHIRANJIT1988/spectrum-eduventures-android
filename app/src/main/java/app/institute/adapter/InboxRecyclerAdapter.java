package app.institute.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.institute.R;
import app.institute.helper.Helper;
import app.institute.model.Message;

import java.text.SimpleDateFormat;
import java.util.List;

public class InboxRecyclerAdapter extends RecyclerView.Adapter<InboxRecyclerAdapter.VersionViewHolder>
{
    private List<Message> list;
    private Context context;
    private OnItemClickListener clickListener;
    private String[] bgColors;

    public InboxRecyclerAdapter(Context context, List<Message> list)
    {
        this.context = context;
        this.list  = list;
        bgColors = context.getApplicationContext().getResources().getStringArray(R.array.background_color);
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_inbox, viewGroup, false);
        return new VersionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VersionViewHolder holder, int i)
    {
        holder.bindData(list.get(i), i);
    }

    @Override
    public int getItemCount()
    {
        return list == null ? 0 : list.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView message;
        TextView timestamp;
        TextView thumbnail;

        private VersionViewHolder(View itemView)
        {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            thumbnail = (TextView) itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }

        private void bindData(Message msg, int i)
        {
            ShapeDrawable background = new ShapeDrawable();
            background.setShape(new OvalShape()); // or RoundRectShape()

            message.setText(msg.message);

            try
            {
                String date = new SimpleDateFormat("yyyy-MM-dd").format(Long.parseLong(msg.timestamp));
                String time = new SimpleDateFormat("hh:mm a").format(Long.parseLong(msg.timestamp));
                timestamp.setText(String.valueOf(Helper.dateTimeFormat(date) + " " + time));
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            thumbnail.setText("S");

            String color = bgColors[i % bgColors.length];
            background.getPaint().setColor(Color.parseColor(color));
            thumbnail.setBackground(background);
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