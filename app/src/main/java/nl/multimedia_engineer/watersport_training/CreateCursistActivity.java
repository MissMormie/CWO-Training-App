package nl.multimedia_engineer.watersport_training;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseError;
import com.mikelau.croperino.CroperinoConfig;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.model.Diploma;
import nl.multimedia_engineer.watersport_training.persistence.PersistDiploma;
import nl.multimedia_engineer.watersport_training.util.PreferenceUtil;

public class CreateCursistActivity
        extends BaseActivity
        implements CursistFormFragment.OnFragmentInteractionListener,
                   PersistDiploma.ReceiveDiplomas {
    private CursistFormFragment cursistFormFragment;
    private Cursist cursist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_cursist);
        cursistFormFragment = (CursistFormFragment) getSupportFragmentManager().findFragmentById(R.id.cursist_form_fragment);
        Cursist cursist = new Cursist();
        cursistFormFragment.setCursist(cursist, false);
    }


    public void onCursistSaved(Cursist cursist) {
        this.cursist = cursist;
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
    public void onEndFragment() {
        onBackPressed();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CroperinoConfig.REQUEST_TAKE_PHOTO || requestCode == CroperinoConfig.REQUEST_CROP_PHOTO) {
            cursistFormFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}