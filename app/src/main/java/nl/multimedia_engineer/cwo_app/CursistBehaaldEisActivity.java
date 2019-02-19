package nl.multimedia_engineer.cwo_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.databinding.ActivityCursistChecklistBinding;
import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;

public class CursistBehaaldEisActivity extends BaseActivity implements  PersistCursist.ReceiveCursistList{
    // Parcelable
    final String CURSIST_LIST = "cursistList";

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

        if(savedInstanceState != null && savedInstanceState.containsKey(CURSIST_LIST)) {
            cursistList = savedInstanceState.getParcelableArrayList(CURSIST_LIST);
            onReceiveCursistList(cursistList);
        } else {
            loadCursistListData();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        cursistList.add(0, currentCursist);
        if(!(cursistList instanceof ArrayList)) {
            // should never happen.
            ArrayList<Cursist> list = new ArrayList<>();
            for(Cursist cursist : cursistList) {
                list.add(cursist);
            }
            outState.putParcelableArrayList(CURSIST_LIST, list);
        } else {
            outState.putParcelableArrayList(CURSIST_LIST, (ArrayList) cursistList);
        }
    }


    private void loadCursistListData() {
        showProgressDialog();
        String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");
        PersistCursist.getCursistList(groupId, this, false);
    }

    private void showFirstCursist() {
        cursistBehaaldEisAdapter.setCwoListData(diplomaEisList);
        // effectively first
        showNextCursist();
    }

    private void showNextCursist() {
        if (cursistList.isEmpty()) {
            backToMainActivity();
        } else {
            currentCursist = cursistList.remove(0);
            setCursistData();
            if(cursistList.isEmpty()) {
                dataBinding.buttonVolgende.setText(R.string.btn_finish);
            }
        }
    }

    private void setCursistData() {
        cursistBehaaldEisAdapter.setCursist(currentCursist);
        dataBinding.textViewNaam.setText(currentCursist.nameToString());
        dataBinding.textViewOpmerking.setText(currentCursist.opmerking);
        if (currentCursist.getPaspoort() == null || currentCursist.getPaspoort() == 0L)
            dataBinding.textViewPaspoort.setText(getString(R.string.paspoort) +": " + getString(R.string.nee));
        else
            dataBinding.textViewPaspoort.setText(getString(R.string.paspoort) +": " + getString(R.string.ja));

        // Set photo if available, else set user mockup.
        if (currentCursist.getFotoFileBase64() != null) {
            byte[] imgByteArray = Base64.decode(currentCursist.getFotoFileBase64(), Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
            dataBinding.imageViewFoto.setImageBitmap(bitmap);
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
    public void onReceiveCursistList(List<Cursist> cursistList) {
        if (cursistList == null) {
            showErrorDialog();
            return;
        }

        hideProgressDialog();

        if(showAlreadyCompleted) {
            this.cursistList = cursistList;
        } else {
            // filter cursistList. Verborgen is filtered out serverside, but can only filter once, so
            // filtering for already met demands here.
            this.cursistList = new ArrayList<>();

            for (Cursist cursist : cursistList) {
                if (!cursist.isAlleEisenBehaald(diplomaEisList)) {
                    this.cursistList.add(cursist);
                }
            }
        }

        this.cursistList = cursistList;
        showFirstCursist();
    }

    @Override
    public void onReceiveCursistListFailed() {
        showErrorDialog();
    }



}