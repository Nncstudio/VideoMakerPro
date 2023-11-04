package com.video.maker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.video.maker.R;
import com.video.maker.model.TransferItem;
import com.video.maker.util.KSUtil;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransitionsAdapter extends RecyclerView.Adapter<TransitionsAdapter.ViewHolderTransfer> {
    TransferAdapterListener listener;
    Context mContext;
    int row_selected = 0;
    ArrayList<TransferItem> transferItemArrayList;

    public interface TransferAdapterListener {
        void onTransferSelected(TransferItem transferItem, int position);
    }

    public TransitionsAdapter(ArrayList<TransferItem> arrayList, Context context, TransferAdapterListener transferAdapterListener) {
        this.transferItemArrayList = arrayList;
        this.mContext = context;
        this.listener = transferAdapterListener;
    }

    public ViewHolderTransfer onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolderTransfer(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_transition, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolderTransfer viewHolderTransfer, int i) {


        if ((i + 1) >= 3 && !KSUtil.Transitionposs.contains(i)) {
            viewHolderTransfer.premium.setVisibility(View.VISIBLE);
        } else {
            viewHolderTransfer.premium.setVisibility(View.GONE);
        }

        if (this.row_selected == i) {
            viewHolderTransfer.imgTransfer.setImageResource(this.transferItemArrayList.get(i).getImgRes());
            viewHolderTransfer.imgTransfer.setBorderWidth(4);
            viewHolderTransfer.imgTransfer.setBorderColor(this.mContext.getResources().getColor(R.color.bg_purpal));
            viewHolderTransfer.nameTransfer.setText(this.transferItemArrayList.get(i).getName());
            viewHolderTransfer.nameTransfer.setTextColor(this.mContext.getResources().getColor(R.color.bg_purpal));
            return;
        }
        viewHolderTransfer.imgTransfer.setImageResource(this.transferItemArrayList.get(i).getImgRes());
        viewHolderTransfer.imgTransfer.setBorderWidth(2);
        viewHolderTransfer.imgTransfer.setBorderColor(this.mContext.getResources().getColor(R.color.gray));
        viewHolderTransfer.nameTransfer.setText(this.transferItemArrayList.get(i).getName());
        viewHolderTransfer.nameTransfer.setTextColor(this.mContext.getResources().getColor(R.color.gray));
    }

    public int getItemCount() {
        return this.transferItemArrayList.size();
    }

    public class ViewHolderTransfer extends RecyclerView.ViewHolder {
        CircleImageView imgTransfer;
        TextView nameTransfer;

        ImageView premium;

        public ViewHolderTransfer(View view) {
            super(view);
            this.imgTransfer = (CircleImageView) view.findViewById(R.id.transfer_img);
            this.nameTransfer = (TextView) view.findViewById(R.id.transfer_txt);
            this.premium = view.findViewById(R.id.premium);
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {


                    if (ViewHolderTransfer.this.getAdapterPosition() < TransitionsAdapter.this.transferItemArrayList.size()) {
                        TransitionsAdapter.this.listener.onTransferSelected(TransitionsAdapter.this.transferItemArrayList.get(ViewHolderTransfer.this.getAdapterPosition()), getAdapterPosition());
                        TransitionsAdapter.this.row_selected = ViewHolderTransfer.this.getAdapterPosition();
                        TransitionsAdapter.this.notifyDataSetChanged();
                    }

                }
            });
        }
    }
}
