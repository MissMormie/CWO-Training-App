package nl.multimedia_engineer.cwo_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import nl.multimedia_engineer.cwo_app.databinding.ActivityCursistChecklistBinding;
import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;

public class CursistBehaaldEisActivity extends BaseActivity implements  PersistCursist.ReceiveCursistList{

    // Lijst met diploma eisen die getraind zijn.
    private List<DiplomaEis> diplomaEisList;
    private List<Cursist> cursistList;
    private CursistBehaaldEisAdapter cursistBehaaldEisAdapter;
    private Cursist currentCursist;
    private ActivityCursistChecklistBinding dataBinding;
    private Boolean showAlreadyCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_cursist_checklist);

        Intent intent = getIntent();
        diplomaEisList = intent.getParcelableArrayListExtra("selectedDiplomaEisList");



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

        loadCursistListData();

    }

    private void loadCursistListData() {
        // todo remove hardcoded group
        PersistCursist.getCursistList("groepsnummer1", this);

        // new FetchCursistListAsyncTask(this).execute(false);
    }

    private void showFirstCursist() {
        cursistBehaaldEisAdapter.setCwoListData(diplomaEisList);
        // effectively first
        showNextCursist();
    }

    private void showNextCursist() {
        if (cursistList.size() == 0) {
            backToMainActivity();
        } else {
            currentCursist = cursistList.remove(0);
            // Als deze cursist alle eisen behaald heeft en deze preference is aangegeven, sla deze cursist dan over.
            if (currentCursist.isAlleEisenBehaald(diplomaEisList) && !showAlreadyCompleted) {
                showNextCursist();
            } else {
                setCursistData();

            }
        }
    }


    private void setCursistData() {
        cursistBehaaldEisAdapter.setCursist(currentCursist);
        dataBinding.textViewNaam.setText(currentCursist.nameToString());
        dataBinding.textViewOpmerking.setText(currentCursist.opmerking);
        if (currentCursist.paspoortDate == null)
            dataBinding.textViewPaspoort.setText(getString(R.string.paspoort) +": " + getString(R.string.nee));
        else
            dataBinding.textViewPaspoort.setText(getString(R.string.paspoort) +": " + getString(R.string.ja));

        // Set photo if available, else set user mockup.
        if (currentCursist.getCursistFoto() != null) {
//            URL fotoUrl = NetworkUtils.buildUrl("foto", currentCursist.getCursistFoto().getId().toString());
//            new DownloadAndSetImageTask(dataBinding.imageViewFoto, getApplicationContext())
//                    .execute(fotoUrl.toString());
        } else {
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_user_image);
            dataBinding.imageViewFoto.setImageDrawable(drawable);
        }
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onClickShowVolgendeCursist(View view) {
        showNextCursist();
    }


    @Override
    public void receiveCursistList(List<Cursist> cursistList) {
        if (cursistList == null) {
            showErrorMessage();
            return;
        }

        // todo remove this use loading from baseactivity
        dataBinding.pbLoadingIndicator.setVisibility(View.GONE);
        this.cursistList = cursistList;
        showFirstCursist();
    }

    @Override
    public void receiveCursistListFailed() {
        showErrorMessage();
    }


    private void showErrorMessage() {
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error_message), Toast.LENGTH_LONG);
        toast.show();
    }

}