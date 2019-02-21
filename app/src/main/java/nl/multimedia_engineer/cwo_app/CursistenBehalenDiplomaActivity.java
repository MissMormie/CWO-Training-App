package nl.multimedia_engineer.cwo_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;

public class CursistenBehalenDiplomaActivity extends BaseActivity implements PersistCursist.ReceiveCursistList {

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


    // Fragment
    private CursistHeaderFragment cursistHeaderFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursisten_behalen_diploma);

        // Get parceled info
        Intent intent = getIntent();
        diploma = intent.getExtras().getParcelable("diploma");
        List<DiplomaEis> diplomaEisList = intent.getExtras().getParcelableArrayList("selectedDiplomaEisList");

        // Get UI elements
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_training_lijst);
        diplomaCheckbox = (CheckBox) findViewById(R.id.diplomaCheckbox);
        paspoortCheckBox = (CheckBox) findViewById(R.id.paspoortCheckbox);

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


        loadCursistListData();
        setListeners();

    }

    private void setListeners() {
        final CursistenBehalenDiplomaActivity outerClass = this;
        diplomaCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!saveData)
                    return;
                String groupId = PreferenceUtil.getPreferenceString(outerClass, getString(R.string.pref_current_group_id), "");
                PersistCursist.saveCursistDiploma(groupId, currentCursist.getId(), diploma.getId(), !isChecked);

            }
        });

        paspoortCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!saveData)
                    return;

                currentCursist.heeftPaspoort(isChecked);
                // todo
//                new SaveCursistAsyncTask(outerClass).execute(currentCursist);

            }
        });
    }

    private void loadCursistListData() {
        showProgressDialog();
        String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");
        PersistCursist.getCursistList(groupId, this);
//        new FetchCursistListAsyncTask(this).execute(false);
    }


    private void showNextCursist() {
        if (cursistList.size() == 0) {
            backToMainActivity();
        } else {
            currentCursist = cursistList.remove(0);
            // Als deze cursist alle eisen behaald heeft en deze preference is aangegeven, sla deze cursist dan over.
            if (currentCursist.hasDiploma(diploma.getId()) && !showAlreadyCompleted) {
                showNextCursist();
            } else {
                setCursistData(currentCursist);
            }
        }
    }

    private void setCursistData(Cursist cursist) {
        saveData = false;
        // Set checkbox
        if(cursist.getPaspoort() != null) {
            paspoortCheckBox.setChecked(true);
        } else {
            paspoortCheckBox.setChecked(false);
        }

        if(cursist.hasDiploma(diploma.getId())) {
            diplomaCheckbox.setChecked(true);
        } else {
            diplomaCheckbox.setChecked(false);
        }
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

        this.cursistList = cursistList;
        showNextCursist();
    }

    @Override
    public void onReceiveCursistListFailed() {
        hideProgressDialog();
        showErrorDialog();
    }
}