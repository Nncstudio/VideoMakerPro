package com.video.maker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.video.maker.R;
import com.video.maker.model.FilterItem;
import com.video.maker.util.KSUtil;
import com.video.maker.view.RoundRectView;

import java.util.ArrayList;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolderFilter> {
    ArrayList<FilterItem> filterArrayList;
    FilterAdapterListener listener;
    Context mContext;
    int row_selected = 0;

    public interface FilterAdapterListener {
        void onFilterSelected(FilterItem filterItem, int position);
    }

    public FilterAdapter(ArrayList<FilterItem> arrayList, Context context, FilterAdapterListener filterAdapterListener) {
        this.filterArrayList = arrayList;
        this.mContext = context;
        this.listener = filterAdapterListener;
    }

    public ViewHolderFilter onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolderFilter(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filter, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolderFilter viewHolderFilter, int i) {


        if ((i + 1) >= 4 && !KSUtil.Filterposs.contains(i)) {
            viewHolderFilter.premium.setVisibility(View.VISIBLE);
        } else {
            viewHolderFilter.premium.setVisibility(View.GONE);
        }


        if (this.row_selected == i) {
            Glide.with(this.mContext).load(Integer.valueOf(this.filterArrayList.get(i).getImgRes())).thumbnail(0.1f).into(viewHolderFilter.filter_section);
            viewHolderFilter.filterContainer.setBorderWidth(this.mContext.getResources().getDimension(R.dimen.border_width));
            viewHolderFilter.filterContainer.setBorderColor(this.mContext.getResources().getColor(R.color.bg_purpal));
            viewHolderFilter.filter_name.setText(this.filterArrayList.get(i).getName());
            viewHolderFilter.filter_name.setTextColor(this.mContext.getResources().getColor(R.color.bg_purpal));
            return;
        }
        Glide.with(this.mContext).load(Integer.valueOf(this.filterArrayList.get(i).getImgRes())).thumbnail(0.1f).into(viewHolderFilter.filter_section);
        viewHolderFilter.filterContainer.setBorderWidth(0.0f);
        viewHolderFilter.filterContainer.setBorderColor(this.mContext.getResources().getColor(R.color.gray));
        viewHolderFilter.filter_name.setText(this.filterArrayList.get(i).getName());
        viewHolderFilter.filter_name.setTextColor(this.mContext.getResources().getColor(R.color.gray));
    }

    public int getItemCount() {
        return this.filterArrayList.size();
    }

    public class ViewHolderFilter extends RecyclerView.ViewHolder {
        RoundRectView filterContainer;
        TextView filter_name;
        ImageView filter_section;

        ImageView premium;

        public ViewHolderFilter(View view) {
            super(view);
            this.filterContainer = (RoundRectView) view.findViewById(R.id.filterContainer);
            this.filter_section = (ImageView) view.findViewById(R.id.filter_img);
            this.filter_name = (TextView) view.findViewById(R.id.filter_txt);
            this.premium = view.findViewById(R.id.premium);
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {


                    if (FilterAdapter.this.filterArrayList.size() > ViewHolderFilter.this.getAdapterPosition() && ViewHolderFilter.this.getAdapterPosition() >= 0) {
                        if (FilterAdapter.this.listener != null) {
                            FilterAdapter.this.listener.onFilterSelected(FilterAdapter.this.filterArrayList.get(ViewHolderFilter.this.getAdapterPosition()), getAdapterPosition());
                        }
                        FilterAdapter.this.row_selected = ViewHolderFilter.this.getAdapterPosition();
                        FilterAdapter.this.notifyDataSetChanged();

                    }
                }
            });
        }
    }
}
