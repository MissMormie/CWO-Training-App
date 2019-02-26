package nl.multimedia_engineer.cwo_app;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.List;

import nl.multimedia_engineer.cwo_app.databinding.ActivityCursistChecklistBinding;
import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;


public class CursistBehaaldDiplomaActivity extends BaseActivity implements PersistCursist.ReceiveCursist {
    // Intent information
    public static final String EXTRA_BACK_AFTER_FINISH = "nl.multimedia_engineer.cwo_app.CursistBehaaldDiplomaActivity.backAfterFinish";
    public static final String EXTRA_CURSIST = "nl.multimedia_engineer.cwo_app.CursistBehaaldDiplomaActivity.cursist";

    private List<Diploma> diplomaList;
    private CursistBehaaldDiplomaAdapter cursistBehaaldDiplomaAdapter;
    private Cursist cursist;
    private ActivityCursistChecklistBinding dataBinding;
    private boolean backAfterFinish = false;

    // Fragment
    private CursistHeaderFragment cursistHeaderFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get activity xml
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_cursist_checklist);

        // Get parceled info
        Intent intent = getIntent();
        diplomaList = intent.getParcelableArrayListExtra("selectedDiplomaList");
        if (intent.hasExtra(EXTRA_CURSIST)) {
            cursist = (Cursist) intent.getExtras().getParcelable(EXTRA_CURSIST);
        } else if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_CURSIST)) {
            cursist = savedInstanceState.getParcelable(EXTRA_CURSIST);
        } else {
            showErrorDialog();
            return;
        }

        backAfterFinish = intent.getBooleanExtra(EXTRA_BACK_AFTER_FINISH, false);

        // Set up of the recycler view and adapter.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        dataBinding.recyclerviewTrainingLijst.setLayoutManager(layoutManager);
        // Not all items in list have the same size
        dataBinding.recyclerviewTrainingLijst.setHasFixedSize(true);
        cursistBehaaldDiplomaAdapter = new CursistBehaaldDiplomaAdapter(diplomaList);
        dataBinding.recyclerviewTrainingLijst.setAdapter(cursistBehaaldDiplomaAdapter);
        cursistBehaaldDiplomaAdapter.setCursist(cursist);
        ((CursistHeaderFragment) getSupportFragmentManager()
                .findFragmentById(R.id.cursist_header_fragment)).setCursist(cursist);

        // Get preference for showing cursisten who already met all eisen.
        Button btnFinish = findViewById(R.id.buttonVolgende);
        btnFinish.setText(getString(R.string.btn_finish));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_CURSIST, cursist);
    }

//    private void loadCursistData() {
//        showProgressDialog();
//
//        String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");
//        PersistCursist.getCursist(groupId, cursist.getId(), this);
//    }



    private void endActivity() {
        if(backAfterFinish) {
            Intent intent = new Intent();
            intent.putExtra(CursistDetailActivity.EXTRA_CURSIST, cursist);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            backToMainActivity();
        }
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onClickButton(View view) {
        // this is using activit
        endActivity();
    }

    @Override
    public void onReceiveCursist(Cursist cursist) {
        this.cursist = cursist;
        hideProgressDialog();
    }

    @Override
    public void onReceiveCursistFailed() {
        hideProgressDialog();
        showErrorDialog();

    }
}