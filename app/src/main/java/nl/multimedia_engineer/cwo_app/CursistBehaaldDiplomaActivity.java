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

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.databinding.ActivityCursistChecklistBinding;
import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;


public class CursistBehaaldDiplomaActivity extends BaseActivity implements PersistCursist.ReceiveCursistList {
    private List<Diploma> diplomaList;
    private List<Cursist> cursistList;
    private CursistBehaaldDiplomaAdapter cursistBehaaldDiplomaAdapter;
    private Cursist currentCursist;
    private Boolean showAlreadyCompleted = false;
    private ActivityCursistChecklistBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get activity xml
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_cursist_checklist);

        // Get parceled info
        Intent intent = getIntent();
        diplomaList = intent.getParcelableArrayListExtra("selectedDiplomaList");
        if (intent.hasExtra("cursist")) {
            cursistList = new ArrayList<>();
            cursistList.add((Cursist) intent.getExtras().getParcelable("cursist"));
        }


        // Set up of the recycler view and adapter.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        dataBinding.recyclerviewTrainingLijst.setLayoutManager(layoutManager);
        // Not all items in list have the same size
        dataBinding.recyclerviewTrainingLijst.setHasFixedSize(true);
        cursistBehaaldDiplomaAdapter = new CursistBehaaldDiplomaAdapter(diplomaList);
        dataBinding.recyclerviewTrainingLijst.setAdapter(cursistBehaaldDiplomaAdapter);

        // Get preference for showing cursisten who already met all eisen.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showAlreadyCompleted = sharedPreferences.getBoolean(getString(R.string.pref_show_already_completed_key),
                getResources().getBoolean(R.bool.pref_show_already_completed_default));


        loadCursistListData();

    }

    private void loadCursistListData() {
        showProgressDialog();
        // Check if info needs to be loaded, if not go straight to showing it.
        String groupId = "groepsnummer1";
        if (cursistList == null) {
            PersistCursist.getCursistList(groupId, this);
//            new FetchCursistListAsyncTask(this).execute(showAlreadyCompleted);
        } else {
            hideProgressDialog();
            showNextCursist();
        }
    }


    private void showNextCursist() {
        if (cursistList.isEmpty()) {
            // todo show page that all cursisten are shown.
            backToMainActivity();
        } else {
            currentCursist = cursistList.remove(0);
            // Als deze cursist alle eisen behaald heeft en deze preference is aangegeven, sla deze cursist dan over.
            if (currentCursist.isAlleDiplomasBehaald(diplomaList)) {
                showNextCursist();
            } else {
                displayCursistInfo();
            }
        }
    }

    private void displayCursistInfo() {
        cursistBehaaldDiplomaAdapter.setCursist(currentCursist);
        dataBinding.textViewNaam.setText(currentCursist.nameToString());
        dataBinding.textViewOpmerking.setText(currentCursist.opmerking);
        String paspoortText;
        if (currentCursist.paspoortDate == null) {
            paspoortText = getString(R.string.paspoort) +": " + getString(R.string.nee);
        } else {
            paspoortText = getString(R.string.paspoort) + ": " + getString(R.string.ja);
        }

        dataBinding.textViewPaspoort.setText(paspoortText);
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
            showErrorDialog();
            return;
        }

        hideProgressDialog();
        this.cursistList = cursistList;
        showNextCursist();
    }

    @Override
    public void receiveCursistListFailed() {

    }
}