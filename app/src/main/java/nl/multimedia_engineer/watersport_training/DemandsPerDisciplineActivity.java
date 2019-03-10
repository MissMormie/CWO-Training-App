package nl.multimedia_engineer.watersport_training;

import android.os.Bundle;
import android.widget.ExpandableListView;

import com.google.firebase.database.DatabaseError;

import java.util.List;

import nl.multimedia_engineer.watersport_training.model.Diploma;
import nl.multimedia_engineer.watersport_training.persistence.PersistDiploma;
import nl.multimedia_engineer.watersport_training.util.PreferenceUtil;

public class DemandsPerDisciplineActivity extends BaseActivity implements PersistDiploma.ReceiveDiplomas {
    DisciplinesExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demands_per_discipline);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.elv_demands_per_disciplines);

        getListData();
    }

    private void getListData() {
        showProgressDialog();
        String discipline = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_discipline), "");
        PersistDiploma.getDiplomaEisen(discipline, this);
    }

    @Override
    public void onReceiveDiplomas(List<Diploma> diplomas) {
        hideProgressDialog();
        // setting list adapter
        listAdapter = new DisciplinesExpandableListAdapter(this, diplomas);
        expListView.setAdapter(listAdapter);
    }

    @Override
    public void onFailedReceivingDiplomas(DatabaseError databaseError) {
        hideProgressDialog();
        showErrorDialog();
    }
}