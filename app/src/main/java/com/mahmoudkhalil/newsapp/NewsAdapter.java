package com.mahmoudkhalil.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> newsList = new ArrayList<>();
    private Context context;
    private onItemClickListener mOnItemClickListener;

    public NewsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        String [] dateTimeArr = getDateFormattedString(newsList.get(position).getDateTime());
        String date = dateTimeArr[0];
        String time = dateTimeArr[1];
        holder.newsTitleTextView.setText(newsList.get(position).getTitle());
        holder.sectionTextView.setText(newsList.get(position).getSection());
        holder.authorTextView.setText(newsList.get(position).getAuthor());
        holder.newsDateTextView.setText(date);
        holder.newsTimeTextView.setText(time);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    public String[] getDateFormattedString(String dateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date == null){
            return null;
        }
        String [] dateTimeArr = new String[2];
        SimpleDateFormat convertDateFormat = new SimpleDateFormat("LLL dd, yyyy",Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        dateTimeArr[0] = convertDateFormat.format(date);
        dateTimeArr[1] = timeFormat.format(date);
        return dateTimeArr;
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTextView, newsTitleTextView, newsDateTextView, newsTimeTextView, authorTextView;
        public NewsViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            sectionTextView = itemView.findViewById(R.id.section_textView);
            newsTitleTextView = itemView.findViewById(R.id.newsTitle_textView);
            authorTextView = itemView.findViewById(R.id.newsAuthor_textView);
            newsDateTextView = itemView.findViewById(R.id.newsDate_textView);
            newsTimeTextView = itemView.findViewById(R.id.newsTime_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
