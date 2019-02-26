package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
import nl.multimedia_engineer.cwo_app.persistence.PersistDiploma;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;

public class CreateCursistActivity
        extends BaseActivity
        implements CursistFormFragment.OnFragmentInteractionListener,
                   PersistCursist.SavedCursist,
                   PersistDiploma.ReceiveDiplomas {
    private CursistFormFragment cursistFormFragment;
    private Cursist cursist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_cursist);
        cursistFormFragment = (CursistFormFragment) getSupportFragmentManager().findFragmentById(R.id.cursist_form_fragment);
        Cursist cursist = new Cursist();
        cursistFormFragment.setCursist(cursist);
    }


    @Override
    public void saveCursist(Cursist cursist) {
        String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");

        PersistCursist.saveCursist(groupId, cursist, this);
    }

    // ------------------------------- Implements PersistCursist.SavedCursist ----------------------

    @Override
    public void onCursistSaved(Cursist cursist) {
        this.cursist = cursist;
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.cursist_opgeslagen), Toast.LENGTH_SHORT);
        toast.show();

        // Get preference for showing diploma's after creation of Cursist.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean showDiploma = sharedPreferences.getBoolean(getString(R.string.pref_show_diploma_after_create_key),
                getResources().getBoolean(R.bool.pref_show_diploma_after_create_cursist));

        if (showDiploma) {
            String discipline = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_discipline), "");
            PersistDiploma.getDiplomaEisen(discipline, this);
        } else {
            finish();
        }
    }

    @Override
    public void onCursistSaveFailed() {
        hideProgressDialog();
        showErrorDialog();
    }


    // ------------------------------- Implements PersistDiploma.ReceiveDiplomas ------------------------
    @Override
    public void onReceiveDiplomas(List<Diploma> diplomas) {
        ArrayList<Diploma> diplomaArrayList;
        if(diplomas instanceof ArrayList) {
            diplomaArrayList = (ArrayList<Diploma>) diplomas;
        } else {
            diplomaArrayList = new ArrayList<>();
            diplomaArrayList.addAll(diplomas);
        }

        Intent intent = new Intent(this, CursistBehaaldDiplomaActivity.class);

        intent.putParcelableArrayListExtra("selectedDiplomaList", diplomaArrayList);
        intent.putExtra(CursistBehaaldDiplomaActivity.EXTRA_CURSIST, cursist);

        startActivity(intent);
    }

    @Override
    public void onFailedReceivingDiplomas(DatabaseError databaseError) {
        showErrorDialog();
    }
}