package com.antohin.iremember.screen.fragment.main;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.antohin.iremember.R;
import com.antohin.iremember.model.NoteModel;
import com.antohin.iremember.ui.SixImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<NoteModel> mNoteList;
    private OnClickItemRecycler mOnClickItemRecycler;

    void setNoteList(List<NoteModel> noteList) {
        mNoteList = noteList;
        notifyDataSetChanged();
    }

    void setOnClickItemRecycler(OnClickItemRecycler onClickItemRecycler) {
        mOnClickItemRecycler = onClickItemRecycler;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateView(parent));
    }

    private View inflateView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_view, parent, false);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mNoteList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNoteList != null ? mNoteList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card) CardView mRoot;
        @BindView(R.id.title) TextView mTitle;
        @BindView(R.id.images) SixImageView mImages;
        @BindView(R.id.note) TextView mNote;
        @BindView(R.id.audio) ImageView mAudio;
        @BindView(R.id.alarm) ImageView mAlarm;
        @BindView(R.id.archive) ImageView mArchive;
        @BindView(R.id.alarm_date_time) TextView mAlarmDateTime;
        private NoteModel mNoteModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(NoteModel noteModel) {
            mNoteModel = noteModel;
            if (mNoteModel.getTitle().isEmpty()) {
                mTitle.setVisibility(View.GONE);
            } else {
                mTitle.setText(mNoteModel.getTitle());
            }
            if (mNoteModel.getNote().isEmpty()) {
                mNote.setVisibility(View.GONE);
            } else {
                mNote.setText(mNoteModel.getNote());
            }
            if (!mNoteModel.isEmptyImageInfo()){
                mImages.init(mNoteModel.getImageInfo());
                mImages.setVisibility(View.VISIBLE);
            } else {
                mImages.setVisibility(View.GONE);
            }
            if (!mNoteModel.getVoicePath().isEmpty()) mAudio.setVisibility(View.VISIBLE);
            else mAudio.setVisibility(View.GONE);
            if (!mNoteModel.isEmptyDateTimeAlarm()) {
                mAlarm.setVisibility(View.VISIBLE);
                mAlarmDateTime.setText(mNoteModel.getAlarmDateTimeText());
            } else {
                mAlarm.setVisibility(View.GONE);
                mAlarmDateTime.setVisibility(View.GONE);
            }
            if (mNoteModel.isArchive()){
                mArchive.setImageDrawable(ContextCompat.getDrawable(mArchive.getContext(),
                        R.drawable.ic_unarchive));
            }else {
                mArchive.setImageDrawable(ContextCompat.getDrawable(mArchive.getContext(),
                        R.drawable.ic_archive));
            }
            mRoot.setBackgroundColor(mNoteModel.getColor());
        }

        @OnClick(R.id.content)
        void onClickCard(View view) {
            if (mOnClickItemRecycler != null) {
                mOnClickItemRecycler.onClickRecycler(mNoteModel.getId());
            }
            view.setClickable(false);
        }

        @OnClick(R.id.archive)
        void onClickArchive() {
            removeCurrentPos(mNoteModel.getId());
            if (mOnClickItemRecycler != null) mOnClickItemRecycler.onArchive(mNoteModel.getId());
        }

        @OnClick(R.id.delete)
        void onClickDelete() {
            removeCurrentPos(mNoteModel.getId());
            if (mOnClickItemRecycler != null) mOnClickItemRecycler.onDelete(mNoteModel.getId());
        }
    }

    private void  removeCurrentPos(String id){
        for (int i = 0; i < mNoteList.size(); i++) {
            if (mNoteList.get(i).getId().equals(id)) {
                mNoteList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public interface OnClickItemRecycler {
        void onClickRecycler(String id);
        void onDelete(String id);
        void onArchive(String id);
    }
}
