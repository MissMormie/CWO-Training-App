package nl.multimedia_engineer.watersport_training;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.watersport_training.databinding.ActivityCursistChecklistBinding;
import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.model.DiplomaEis;
import nl.multimedia_engineer.watersport_training.persistence.PersistCursist;
import nl.multimedia_engineer.watersport_training.persistence.PersistCursistList;
import nl.multimedia_engineer.watersport_training.util.PreferenceUtil;

public class CursistBehaaldEisActivity extends BaseActivity implements  PersistCursist.ReceiveCursistList {
    // Parcelable
    final String CURSIST = "cursistList";

    // Lijst met diploma eisen die getraind zijn.
    private List<DiplomaEis> diplomaEisList;
    private List<Cursist> cursistList;
    private CursistBehaaldEisAdapter cursistBehaaldEisAdapter;
    private Cursist currentCursist;
    private ActivityCursistChecklistBinding dataBinding;
    private Boolean showAlreadyCompleted = false;

    private Button btnNext;

    // data used for keeping track of loading cursist
    private final int limit = 5;
    private final int reloadAt = 3;
    private boolean moveToNextCursistAfterLoading = true;

    private PersistCursistList persistCursistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_cursist_checklist);

        Intent intent = getIntent();
        diplomaEisList = intent.getParcelableArrayListExtra("selectedDiplomaEisList");
        btnNext = findViewById(R.id.buttonVolgende);
        btnNext.setEnabled(false);

        // Set up of the recycler view and adapter.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        dataBinding.recyclerviewTrainingLijst.setLayoutManager(layoutManager);
        // All items in list have the same size
        dataBinding.recyclerviewTrainingLijst.setHasFixedSize(true);
        cursistBehaaldEisAdapter = new CursistBehaaldEisAdapter();
        dataBinding.recyclerviewTrainingLijst.setAdapter(cursistBehaaldEisAdapter);

        // Get preference for showing cursisten who already met all eisen.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showAlreadyCompleted = sharedPreferences.getBoolean(getString(R.string.pref_show_already_completed_key),
                getResources().getBoolean(R.bool.pref_show_already_completed_default));

        if(savedInstanceState != null && savedInstanceState.containsKey(CURSIST)) {
            moveToNextCursistAfterLoading = false;
            Cursist cursist = savedInstanceState.getParcelable(CURSIST);
            cursistList = new ArrayList<>();
            cursistList.add(cursist);
            showFirstCursist();
        } else {
            showProgressDialog();
            loadCursistListData();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURSIST, currentCursist);
    }

    private void loadCursistListData() {
        String startAt = null;
        if(cursistList != null && !cursistList.isEmpty()) {
            startAt = cursistList.get(cursistList.size() -1).getId();
        } else {
            if(currentCursist != null) {
                startAt = currentCursist.getId();
            }
        }

        if(persistCursistList == null) {
            String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");
            persistCursistList = new PersistCursistList(this, groupId)
                    .setLimit(limit)
                    .startAt(startAt)
                    .execute();
        } else {
            persistCursistList.requestNextCursisten();
        }
    }

    private void showFirstCursist() {
        cursistBehaaldEisAdapter.setCwoListData(diplomaEisList);
        // effectively first
        showNextCursist();
    }

    private void showNextCursist() {
        if(cursistList == null) {
            return;
        }
        if (cursistList.isEmpty() && persistCursistList.isEndOfListReached()) {
            backToMainActivity();
        } else if (cursistList.isEmpty() && !persistCursistList.isEndOfListReached()){
            showProgressDialog();
        } else {
            currentCursist = cursistList.remove(0);
            setCursistData();
            if(cursistList.isEmpty() && persistCursistList.isEndOfListReached()) {
                dataBinding.buttonVolgende.setText(R.string.btn_finish);
            } else if (cursistList.size() <= reloadAt && !persistCursistList.isEndOfListReached()) {
                loadCursistListData();
            }
        }
    }

    private void setCursistData() {
        cursistBehaaldEisAdapter.setCursist(currentCursist);
        ((CursistHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.cursist_header_fragment)).setCursist(currentCursist);
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onClickButton(View view) {
        moveToNextCursistAfterLoading = true;
        showNextCursist();
    }

    @Override
    public void onReceiveCursistList(List<Cursist> cursistList) {
        hideProgressDialog();
        btnNext.setEnabled(true);

        if (cursistList == null && this.cursistList == null) {
            showErrorDialog();
            return;
        }

        // Er zijn nog geen cursisten.
        if(cursistList.isEmpty() && this.cursistList == null) {
            showGeenCursisten();
            return;
        }

        if(!showAlreadyCompleted) {
            // filter cursistList. Verborgen is filtered out serverside, but can only filter once, so
            // filtering for already met demands here.
            List<Cursist> tempList = new ArrayList<>();
            for (Cursist cursist : cursistList) {
                if (!cursist.isAlleEisenBehaald(diplomaEisList)) {
                    tempList.add(cursist);
                }
            }
            cursistList = tempList;
        }

        if(this.cursistList == null || this.cursistList.isEmpty()) {
            this.cursistList = cursistList;

            // When retreivingSavedInstanceState there is an empty list, but the currently shown cursist should not change.
            if(!moveToNextCursistAfterLoading) {
                moveToNextCursistAfterLoading = true;
            } else {
                showFirstCursist();
            }
        } else {
            this.cursistList.addAll(cursistList);
        }
    }

    @Override
    public void onReceiveCursistListFailed() {
        showErrorDialog();
    }

}