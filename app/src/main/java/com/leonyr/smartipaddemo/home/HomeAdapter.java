package com.leonyr.smartipaddemo.home;

import android.support.annotation.NonNull;
import android.view.View;

import com.leonyr.mvvm.vh.AbViewBinder;
import com.leonyr.mvvm.vh.BindViewHolder;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.databinding.ItemHomeRlBinding;

public class HomeAdapter extends AbViewBinder<HomeModel, ItemHomeRlBinding> {

    OnHomeItemFuncClick clickListerner;

    public HomeAdapter(OnHomeItemFuncClick clickListerner) {
        this.clickListerner = clickListerner;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_home_rl;
    }

    @Override
    protected void onBindViewHolder(@NonNull BindViewHolder<ItemHomeRlBinding> holder, @NonNull HomeModel item) {
        holder.Binding().itemName.setText(item.getName());
        if (null != clickListerner) {
            holder.Binding().itemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListerner.onItemFuncClick(holder.Binding().itemName, item);
                }
            });
        }
    }

    public interface OnHomeItemFuncClick {
        void onItemFuncClick(View view, HomeModel item);
    }
}
