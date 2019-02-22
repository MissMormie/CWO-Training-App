package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import nl.multimedia_engineer.cwo_app.dto.SubDisciplinesWithDemandsList;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.persistence.PersistDiploma;
import nl.multimedia_engineer.cwo_app.persistence.PersistExamenEisen;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;

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