package com.antohin.iremember.screen.fragment.main;

import com.antohin.iremember.model.NoteModel;
import com.antohin.iremember.screen.activity.PresenterForContent;
import com.antohin.iremember.screen.activity.details.DetailsNoteActivity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.RealmResults;

import static com.antohin.iremember.screen.activity.main.MainActivity.REQUEST_ADD_NOTE;


public class FragmentMainPresenter extends PresenterForContent<FragmentMainView> {

    void initRecyclerViewAll() {
        mRealm.executeTransaction(realm -> {
            if (realm.where(NoteModel.class).findAll() != null) {
                RecyclerViewAdapter adapter = new RecyclerViewAdapter();
                RealmResults<NoteModel> all = realm.where(NoteModel.class).equalTo("isArchive",false).findAll().sort("mLastEdit");
                List<NoteModel> noteModels = realm.copyFromRealm(all);
                Collections.reverse(noteModels);
                adapter.setNoteList(noteModels);
                mView.setAdapter(adapter);
            }
        });
    }


    void initRecyclerViewReminders() {
        mRealm.executeTransaction(realm -> {
            if (realm.where(NoteModel.class).findAll() != null) {
                RecyclerViewAdapter adapter = new RecyclerViewAdapter();
                RealmResults<NoteModel> all = realm.where(NoteModel.class).equalTo("isArchive",false).isNotNull("mDateTimeAlarm").findAll().sort("mLastEdit");
                List<NoteModel> noteModels = realm.copyFromRealm(all);
                Collections.reverse(noteModels);
                adapter.setNoteList(noteModels);
                mView.setAdapter(adapter);
            }
        });
    }

    void initRecyclerViewArchive() {
        mRealm.executeTransaction(realm -> {
            if (realm.where(NoteModel.class).findAll() != null) {
                RecyclerViewAdapter adapter = new RecyclerViewAdapter();
                RealmResults<NoteModel> all = realm.where(NoteModel.class).equalTo("isArchive",true).findAll().sort("mLastEdit");
                List<NoteModel> noteModels = realm.copyFromRealm(all);
                Collections.reverse(noteModels);
                adapter.setNoteList(noteModels);
                mView.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void whatDoItResult(String noteId) {
        mView.startActivityForResult((
                        DetailsNoteActivity.createIntent(mView.getContext(), noteId)),
                REQUEST_ADD_NOTE);
    }

    @Override
    protected void updateState() {
        mView.setupState(mView.getCurrentState());
    }

    @Override
    protected NoteModel getCurrentNote() {
        mRealm.beginTransaction();
        NoteModel data = mRealm.createObject(NoteModel.class, UUID.randomUUID().toString());
        mRealm.commitTransaction();
        return data;
    }

    void archiveModel(String id) {
        mRealm.executeTransaction(realm -> {
            NoteModel data = realm.where(NoteModel.class).equalTo("mId", id).findFirst();
            if (data == null) return;
            if (data.isArchive()){
                data.setArchive(false);
            }else {
                data.setArchive(true);
            }
        });
    }

    void deleteModel(String id) {
        mRealm.executeTransaction(realm -> {
            NoteModel data = realm.where(NoteModel.class).equalTo("mId", id).findFirst();
            if (data == null) return;
            data.deleteFromRealm();
        });
    }
}
