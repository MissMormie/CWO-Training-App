package nl.multimedia_engineer.watersport_training;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.watersport_training.databinding.ActivityCursistDetailBinding;
import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.model.CursistPartial;
import nl.multimedia_engineer.watersport_training.model.Diploma;
import nl.multimedia_engineer.watersport_training.model.DiplomaEis;
import nl.multimedia_engineer.watersport_training.persistence.PersistCursist;
import nl.multimedia_engineer.watersport_training.persistence.PersistDiploma;
import nl.multimedia_engineer.watersport_training.util.PreferenceUtil;

public class CursistDetailActivity
        extends BaseActivity
        implements PersistCursist.ReceiveCursist,
                   PersistDiploma.ReceiveDiplomas,
                   PersistCursist.DeletedCursist
            {
    private static final String TAG = CursistDetailActivity.class.getSimpleName();

    // Intent information
    public static final String EXTRA_CURSIST = "nl.multimedia_engineer.watersport_training.CursistDetailActivity.cursist";


    private ActivityCursistDetailBinding activityCursistDetailBinding;
    private Cursist cursist;
    private CursistBehaaldEisAdapter cursistBehaaldEisAdapter;
    private List<Diploma> diplomaList;
    private MenuItem verbergenMenu;

    private static final int EDIT_CURSIST = 1;
    private static final int GIVE_DIPLOMA = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCursistDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_cursist_detail);

        // Set up of the recycler view and adapter.
        RecyclerView recyclerView =  findViewById(R.id.recyclerview_training_lijst);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // Not all items in list have the same size
        recyclerView.setHasFixedSize(true);
        cursistBehaaldEisAdapter = new CursistBehaaldEisAdapter();
        recyclerView.setAdapter(cursistBehaaldEisAdapter);

        // Get info from parcel to fill part of cursist.
        CursistPartial cursistPartial = getIntent().getExtras().getParcelable(EXTRA_CURSIST);
        cursist = new Cursist(cursistPartial);

        // Displaying data we got from the parcel, this does not contain complete cursist data but shows something while the rest is loading.
        displayCursistInfo();
        showProgressDialog();
        String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");

        PersistCursist.getCursist(groupId, cursist.getId(), this);
    }


    /// ---------------------------- MENU Functions ------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cursist_detail_menu, menu);
        verbergenMenu = menu.findItem(R.id.action_verbergen);
        setMenuTitle();
        return true;
    }

    private void setMenuTitle() {
        if(!cursist.isVerborgen()) {
            verbergenMenu.setTitle(getString(R.string.verbergen));
        } else{
            verbergenMenu.setTitle(getString(R.string.niet_verbergen));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                showEditCursist();
                break;
            case R.id.action_verbergen:
                hideCursist();
                break;
            case R.id.action_delete:
                deleteCursist();
                break;
            case R.id.action_diploma:
                diplomaUitgeven();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void showEditCursist() {
        Context context = this;
        Class destinationClass = EditCursistActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra("cursist", cursist);
        startActivityForResult(intent, EDIT_CURSIST);
    }

    private void diplomaUitgeven() {
        Intent intent = new Intent(this, CursistBehaaldDiplomaActivity.class);

        ArrayList<Diploma> diplomaArrayList = (ArrayList<Diploma>) diplomaList;
        intent.putParcelableArrayListExtra("selectedDiplomaList", diplomaArrayList);
        intent.putExtra(CursistBehaaldDiplomaActivity.EXTRA_CURSIST, cursist);
        intent.putExtra(CursistBehaaldDiplomaActivity.EXTRA_BACK_AFTER_FINISH, true);

        startActivityForResult(intent, GIVE_DIPLOMA);
    }

    private void hideCursist() {
        cursist.toggleVerborgen();
        PersistCursist.updateCursistVerborgen(PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), ""), cursist.getId(), cursist.isVerborgen());
        setMenuTitle();
    }

    private void deleteCursist() {
        final PersistCursist.DeletedCursist deletedCursist = this;
        final String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.echt_verwijderen)
                .setPositiveButton(R.string.ja, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showProgressDialog();
                        PersistCursist.requestDeleteCursist(groupId, cursist, deletedCursist);
                        dialog.cancel();

                    }
                })
                .setNegativeButton(R.string.nee, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        Dialog dialog = builder.create();
        dialog.show();
    }


    // ---------------------------------------------------------------------------------------------
    // BACK BEHAVIOUR

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("cursist", cursist);
        setResult(RESULT_OK, intent);
        finish();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CURSIST || requestCode == GIVE_DIPLOMA)
            if (resultCode == RESULT_OK && data.hasExtra(EXTRA_CURSIST)) {
                this.cursist = data.getExtras().getParcelable(EXTRA_CURSIST);
                displayCursistInfo();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ---------------------------------------------------------------------------------------------

    private void toggleLoading(boolean currentlyLoading) {
        if (currentlyLoading)
            showProgressDialog();
        else
            hideProgressDialog();

    }

    private void loadDiplomaData() {
        if(diplomaList == null || diplomaList.isEmpty()) {
            String discipline = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_discipline), "");
            PersistDiploma.getDiplomaEisen(discipline, this);
        } else {
            onReceiveDiplomas(diplomaList);
        }
    }

    private void displayCursistInfo() {
        if (cursist == null) {
            showErrorDialog();
            return;
        }
        ((CursistHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.cursist_header_fragment)).setCursist(cursist);
        loadDiplomaData();
    }

    private void displayDiplomaEisInfo(List<DiplomaEis> diplomaEisList) {
        cursistBehaaldEisAdapter.setCwoListData(diplomaEisList);
        cursistBehaaldEisAdapter.setCursist(cursist);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

                // ------------------------------------- Persist cursist implementation ------------------------
    @Override
    public void onReceiveCursist(Cursist cursist) {
        hideProgressDialog();
        this.cursist = cursist;
        displayCursistInfo();
    }

    @Override
    public void onReceiveCursistFailed() {
        hideProgressDialog();
        showErrorDialog();
    }


    // ------------------------------------- Persist Diploma eisen implementation ------------------------

    @Override
    public void onReceiveDiplomas(List<Diploma> diplomas) {
        diplomaList = diplomas;
        toggleLoading(false);
        if (diplomas != null) {
            List<DiplomaEis> diplomaEisenLijst = new ArrayList<>();
            for (int i = 0; i < diplomas.size(); i++) {
                diplomaEisenLijst.addAll(diplomas.get(i).getDiplomaEisList());
            }

            displayDiplomaEisInfo(diplomaEisenLijst);
        } else {
            showErrorDialog();
        }
    }


    @Override
    public void onFailedReceivingDiplomas(DatabaseError databaseError) {
    }

    // ------------------------------------- Persist Cursist Deleted implementation ------------------------

    @Override
    public void onCursistDeleted() {
        hideProgressDialog();
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.cursist_verwijderd), Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent();
        intent.putExtra("cursist", cursist);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onCursistDeleteFailed() {
        hideProgressDialog();
        showErrorDialog();
    }

}