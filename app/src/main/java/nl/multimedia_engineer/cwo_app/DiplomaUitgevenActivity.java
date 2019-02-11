package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;

/**
 * Shows list of available diploma's from database. Can pick one as radio button.
 */
public class DiplomaUitgevenActivity extends BaseActivity implements  DiplomaUitgevenListAdapter.DiplomaListAdapterOnClickHandler {
    private ProgressBar mLoadingIndicator;
    private DiplomaUitgevenListAdapter diplomaListAdapter;
    private Diploma selectedDiploma;
    private final ArrayList<Diploma> selectedDiplomaList = new ArrayList<>();
    private Button volgendeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diploma_uitgeven);

        // Link the variables to the view items.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_diploma_lijst);
        TextView mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
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
//        new FetchDiplomaAsyncTask(this).execute();
    }

//    @Override
    public void setDiploma(List<Diploma> diplomaList) {
        mLoadingIndicator.setVisibility(View.GONE);
        if (diplomaList == null) {
            showError();
        } else {
            diplomaListAdapter.setDiplomaList(diplomaList);
        }
    }

    private void showError() {
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error_message), Toast.LENGTH_LONG);
        toast.show();
    }

// ---------------------------- Click ----------------------------------------------------------

    public void onClickShowVolgende(View view) {
        if (selectedDiploma != null) {
            showCreateDiplomaActivity();
        }
    }

    private void showCreateDiplomaActivity() {
        // Because we're now only allowing a single diploma we're adding that specific one to the list.
        // TODO make this just pass a diploma as parcelable, not this ugly code ;)
        selectedDiplomaList.add(selectedDiploma);

        Context context = this;
        Class destinationClass = CursistenBehalenDiplomaActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra("diploma", selectedDiplomaList.get(0));
        if(selectedDiplomaList.get(0).getDiplomaEis() instanceof ArrayList) {
            ArrayList<DiplomaEis> diplomaEisArrayList = (ArrayList) selectedDiplomaList.get(0).getDiplomaEis();
            intent.putExtra("selectedDiplomaEisList", diplomaEisArrayList);
            startActivity(intent);
        }

        showError();
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
}