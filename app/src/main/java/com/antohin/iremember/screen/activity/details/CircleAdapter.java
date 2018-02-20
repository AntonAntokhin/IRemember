package com.antohin.iremember.screen.activity.details;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.antohin.iremember.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.ViewHolder> {

    private int[] arrayColor = {
            R.color.default_color_item,
            R.color.red,
            R.color.pink,
            R.color.purple,
            R.color.deep_purple,
            R.color.indigo,
            R.color.blue,
            R.color.cyan,
            R.color.Teal,
            R.color.green,
            R.color.light_green,
            R.color.lime,
            R.color.amber,
            R.color.orange,
            R.color.deep_orange,
            R.color.brown
    };

    private OnClickItemColor mOnClickItemColor;

    CircleAdapter setOnClickItemColor(OnClickItemColor onClickItemColor) {
        mOnClickItemColor = onClickItemColor;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(parent));
    }

    private View inflate(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_circle_adapter, parent, false);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(arrayColor[position]);
    }

    @Override
    public int getItemCount() {
        return arrayColor.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img) ImageView mImageColor;
        private ColorDrawable mColorDrawable;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int idColor) {
            mColorDrawable = new ColorDrawable(ContextCompat.getColor(mImageColor.getContext(), idColor));
            Glide.with(mImageColor.getContext())
                    .load(mColorDrawable)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageColor);
        }

        @OnClick(R.id.img)
        public void onClickItem() {
            if (mOnClickItemColor!= null) mOnClickItemColor.onClickColor(mColorDrawable);
        }
    }

    public interface OnClickItemColor {
        void onClickColor(ColorDrawable colorDrawable);
    }
}
