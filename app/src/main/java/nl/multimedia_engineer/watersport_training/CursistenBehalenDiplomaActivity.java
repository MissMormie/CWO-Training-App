package nl.multimedia_engineer.watersport_training;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.model.Diploma;
import nl.multimedia_engineer.watersport_training.model.DiplomaEis;
import nl.multimedia_engineer.watersport_training.persistence.PersistCursist;
import nl.multimedia_engineer.watersport_training.persistence.PersistCursistList;
import nl.multimedia_engineer.watersport_training.util.PreferenceUtil;

public class CursistenBehalenDiplomaActivity extends BaseActivity implements PersistCursist.ReceiveCursistList {
    public static final String DIPLOMA_EIS_LIJST = "selectedDiplomaEisList";
    public static final String DIPLOMA = "diploma";
    private static String START_AT = "start_at";
    private final int reloadAt = 3;
    private final int limit = 5;

    // UI elements:
    private CheckBox diplomaCheckbox;
    private CheckBox paspoortCheckBox;

    // Data
    private Diploma diploma;
    private Cursist currentCursist;
    private List<Cursist> cursistList;

    private CursistBehaaldEisAdapter cursistBehaaldEisAdapter;
    private Boolean showAlreadyCompleted;
    private boolean saveData = true; // OnChangeChecklistener saves data when checkbox is clicked, but also when data is refeshed. Using to as workaround.

    // Persistence
    private PersistCursistList persistCursistList;
    private boolean showNextCursistOnLoad = true;

    // Fragment
    private CursistHeaderFragment cursistHeaderFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursisten_behalen_diploma);

        // Get parceled info
        Intent intent = getIntent();
        diploma = intent.getExtras().getParcelable(DIPLOMA);
        List<DiplomaEis> diplomaEisList = intent.getExtras().getParcelableArrayList(DIPLOMA_EIS_LIJST);
        String startAt = null;
        if(savedInstanceState != null && savedInstanceState.containsKey(START_AT)) {
            startAt = (String) savedInstanceState.get(START_AT);
        }

        // Get UI elements
        RecyclerView recyclerView = findViewById(R.id.recyclerview_training_lijst);
        diplomaCheckbox = findViewById(R.id.diplomaCheckbox);
        paspoortCheckBox = findViewById(R.id.paspoortCheckbox);


        // Set Recyclerview adapter for behaaldeEisen
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        cursistBehaaldEisAdapter = new CursistBehaaldEisAdapter();
        cursistBehaaldEisAdapter.setCwoListData(diplomaEisList);
        recyclerView.setAdapter(cursistBehaaldEisAdapter);

        // fragments
        cursistHeaderFragment = (CursistHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.cursist_header_fragment);

        // Set diploma tekst eenmalig.
        diplomaCheckbox.setText(diploma.toString());

        // Get preference for showing cursisten who already met all eisen.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showAlreadyCompleted = sharedPreferences.getBoolean(getString(R.string.pref_show_already_completed_key),
                getResources().getBoolean(R.bool.pref_show_already_completed_default));

        loadCursistListData(startAt);
        setListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<DiplomaEis> diplomaEisList = cursistBehaaldEisAdapter.getCwoListData();
        ArrayList<DiplomaEis> diplomaEisArrayList = new ArrayList<>();
        for(DiplomaEis de: diplomaEisList) {
            diplomaEisArrayList.add(de);
        }
        outState.putParcelableArrayList(DIPLOMA_EIS_LIJST, diplomaEisArrayList);
        outState.putParcelable(DIPLOMA, diploma);
        outState.putString(START_AT, currentCursist.getId());
    }

    private void setListeners() {
        final CursistenBehalenDiplomaActivity context = this;
        diplomaCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!saveData)
                    return;
                String groupId = PreferenceUtil.getPreferenceString(context, getString(R.string.pref_current_group_id), "");
                if(isChecked) {
                    currentCursist.addDiploma(diploma);
                } else {
                    currentCursist.removeDiploma(diploma);
                }
                PersistCursist.saveCursistDiploma(groupId, currentCursist, diploma.getId(), !isChecked);
            }
        });

        paspoortCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!saveData)
                    return;

                if(currentCursist != null) {
                    currentCursist.heeftPaspoort(isChecked);
                    String groupId = PreferenceUtil.getPreferenceString(context, getString(R.string.pref_current_group_id), "");
                    PersistCursist.updateCursistPaspoort(groupId, currentCursist.getId(), currentCursist.getPaspoort());
                }
            }
        });
    }

    private void loadCursistListData(@Nullable String startAt) {
        showProgressDialog();
        String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");
        persistCursistList = new PersistCursistList(this, groupId).setLimit(limit).startAt(startAt).execute();
    }


    private void showNextCursist() {
        if (cursistList == null || cursistList.isEmpty() && persistCursistList.isEndOfListReached()) {
            backToMainActivity();
        } else {
            if(cursistList.size() <= reloadAt) {
                persistCursistList.requestNextCursisten();
            }
            if(cursistList.isEmpty()) {
                showNextCursistOnLoad = true;
                showProgressDialog();
                return;
            }
            currentCursist = cursistList.remove(0);
            setCursistData(currentCursist);
        }

        toggleButton();
    }

    private void toggleButton() {
        if (cursistList.isEmpty() && persistCursistList.isEndOfListReached()) {
            Button btn = findViewById(R.id.btn_volgende);
            btn.setText(getString(R.string.btn_finish));
        }
    }

    private void setCursistData(Cursist cursist) {
        // During the setting of check boxes the listeners are also triggered,
        // so telling it not to save data during this time
        saveData = false;

        // Set checkboxes
        paspoortCheckBox.setChecked(cursist.getPaspoort() != null);
        diplomaCheckbox.setChecked(cursist.hasDiploma(diploma.getId()));
        saveData = true;

        cursistBehaaldEisAdapter.setCursist(cursist);
        cursistHeaderFragment.setCursist(cursist);
    }


    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void onClickShowVolgendeCursist(View view) {
        showNextCursist();
    }

//    @Override
    public void diplomaSaved(boolean success) {
        if(!success) {
            showErrorDialog();
        }
    }

//    @Override
    public void cursistSaved(Cursist cursist) {
        if(cursist == null)
            showErrorDialog();
    }

    @Override
    public void onReceiveCursistList(List<Cursist> cursistList) {
        hideProgressDialog();
        if (cursistList == null) {
            onReceiveCursistListFailed();
            return;
        }
        if (this.cursistList == null) {
            this.cursistList = new ArrayList<>();
        }
        if(!showAlreadyCompleted) {
            for(Cursist c : cursistList) {
                if(!c.hasDiploma(diploma.getId()) && !c.isVerborgen()) {
                    this.cursistList.add(c);
                }
            }
        } else {
            for(Cursist c : cursistList) {
                if(!c.isVerborgen()) {
                    this.cursistList.add(c);
                }
            }
            this.cursistList.addAll(cursistList);
        }

        if(showNextCursistOnLoad) {
            showNextCursistOnLoad = false;
            showNextCursist();
        }
    }

    @Override
    public void onReceiveCursistListFailed() {
        hideProgressDialog();
        showErrorDialog();
    }
}