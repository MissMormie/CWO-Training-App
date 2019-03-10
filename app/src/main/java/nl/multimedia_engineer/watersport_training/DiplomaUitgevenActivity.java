package nl.multimedia_engineer.watersport_training;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.watersport_training.model.Diploma;
import nl.multimedia_engineer.watersport_training.model.DiplomaEis;
import nl.multimedia_engineer.watersport_training.persistence.PersistDiploma;
import nl.multimedia_engineer.watersport_training.util.PreferenceUtil;

/**
 * Shows list of available diploma's from database. Can pick one as radio button.
 */
public class DiplomaUitgevenActivity extends BaseActivity implements  DiplomaUitgevenListAdapter.DiplomaListAdapterOnClickHandler, PersistDiploma.ReceiveDiplomas {
    private DiplomaUitgevenListAdapter diplomaListAdapter;
    private Diploma selectedDiploma;
    private final ArrayList<Diploma> selectedDiplomaList = new ArrayList<>();
    private Button volgendeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diploma_uitgeven);

        // Link the variables to the view items.
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_diploma_lijst);
        volgendeButton = (Button) findViewById(R.id.buttonVolgende);

        // Set up of the recycler view and adapter.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        // Not all items in list have the same size
        mRecyclerView.setHasFixedSize(true);
        diplomaListAdapter = new DiplomaUitgevenListAdapter(this);
        mRecyclerView.setAdapter(diplomaListAdapter);

        loadCwoEisData();
    }

// ---------------------------- Load data ----------------------------------------------------------

    private void loadCwoEisData() {
        showProgressDialog();
        String discipline = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_discipline), "");
        PersistDiploma.getDiplomaEisen(discipline, this);
    }

// ---------------------------- Click ----------------------------------------------------------

    public void onClickShowVolgende(View view) {
        if (selectedDiploma != null) {
            showCreateDiplomaActivity();
        }
    }

    private void showCreateDiplomaActivity() {
        // Because we're now only allowing a single diploma we're adding that specific one to the list.
        selectedDiplomaList.add(selectedDiploma);

        if(selectedDiplomaList.get(0).getDiplomaEisList() instanceof ArrayList) {
            Intent intent = new Intent(this, CursistenBehalenDiplomaActivity.class);
            intent.putExtra("diploma", selectedDiplomaList.get(0));
            ArrayList<DiplomaEis> diplomaEisArrayList = (ArrayList) selectedDiplomaList.get(0).getDiplomaEisList();
            intent.putExtra("selectedDiplomaEisList", diplomaEisArrayList);
            startActivity(intent);
            return;
        }

        showErrorDialog();
    }




    // ----------------- DiplomaListAdapterOnClickHandler implementation ---------------------------

    @Override
    public void onClick(Diploma diploma, boolean selected) {
        selectedDiploma = diploma;
        toggleVolgendeButton();
    }

    @Override
    public boolean isSelectedDiploma(Diploma diploma) {
        if(selectedDiploma != null)
            return diploma.equals(selectedDiploma);
        return false;
    }

    private void toggleVolgendeButton() {
        if(selectedDiploma != null) {
            volgendeButton.setEnabled(true);
        } else {
            volgendeButton.setEnabled(false);
        }
    }

    // ------------------------------ PersistDiploma.ReceiveDiplomas methods -------------------------------//

    @Override
    public void onReceiveDiplomas(List<Diploma> diplomaList) {
        hideProgressDialog();
        if (diplomaList == null) {
            showErrorDialog( );
        } else {
            diplomaListAdapter.setDiplomaList(diplomaList);
        }
    }

    @Override
    public void onFailedReceivingDiplomas(DatabaseError databaseError) {
        hideProgressDialog();
        showErrorDialog();
    }
}