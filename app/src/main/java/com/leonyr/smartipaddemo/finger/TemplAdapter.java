package com.leonyr.smartipaddemo.finger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonyr.smartipaddemo.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created By pq
 * on 2019/3/21
 */
public class TemplAdapter extends RecyclerView.Adapter<TemplAdapter.ConsoleHolder> {
    private List<String> tipLists;
    private Context mContext;
    //    private int mFlag;
    private TemplAdapter.ItemClick itemClick;
    private String datID;

    public TemplAdapter(Context context) {
        this.mContext = context;
        tipLists = new ArrayList<>();
//        mFlag = flag;
    }

    public void setItemClick(TemplAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    //位置在哪

    public List<String> getData() {
        return tipLists;
    }

    public void addData(String tipStr) {
        if (!TextUtils.isEmpty(tipStr)) {
            tipLists.add(tipStr);
            notifyDataSetChanged();
        }
    }

    public void addData(List<String> newList) {
        if (newList != null && newList.size() > 0) {
            tipLists.addAll(newList);
            notifyDataSetChanged();
            newList.clear();
            Log.d("===TAG===", "--- 添加数据  ");
        }
    }

    public void removeData(String data) {
        tipLists.remove(data);
        notifyDataSetChanged();
    }

    public void clearData() {
//        if (tipLists.size() > 0) {
        tipLists.clear();
        notifyDataSetChanged();
//        }
    }

    @NonNull
    @Override
    public TemplAdapter.ConsoleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TemplAdapter.ConsoleHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_console_tip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TemplAdapter.ConsoleHolder holder, final int position) {
//        if (mFlag == 1) {
        holder.consoleClearIcon.setVisibility(View.VISIBLE);
//        }
        int itemCount = getItemCount();
        holder.consoleTipTv.setText(tipLists.get(position));
        holder.consoleTipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择文件
                if (itemClick != null) {
                    //返回dat文件的名字
                    datID = tipLists.get(position);
                    itemClick.itemSelectFile(datID);
                }
            }
        });
        holder.consoleClearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择文件
                if (itemClick != null) {
                    //返回dat文件的名字
                    datID = tipLists.get(position);
                    itemClick.delTempl(datID);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tipLists.size();
    }

    static class ConsoleHolder extends RecyclerView.ViewHolder {

        private final TextView consoleTipTv;
        private final ImageView consoleClearIcon;

        ConsoleHolder(View itemView) {
            super(itemView);
            consoleTipTv = itemView.findViewById(R.id.consoleTipTv);
            consoleClearIcon = itemView.findViewById(R.id.consoleClearIcon);
        }
    }

    public interface ItemClick {

        void itemSelectFile(String datFileName);

        void delTempl(String datFileName);

    }

}
