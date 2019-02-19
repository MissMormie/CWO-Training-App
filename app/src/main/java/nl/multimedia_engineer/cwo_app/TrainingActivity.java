package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.persistence.PersistExamenEisen;

public class TrainingActivity extends BaseActivity
        implements
        TrainingsListAdapter.TrainingListAdapterOnClickHandler,
        PersistExamenEisen.ReceivedDiplomaEisen {

    private RecyclerView mRecyclerView;
    private Button volgendeButton;
    private TrainingsListAdapter trainingsListAdapter;
    private final ArrayList<DiplomaEis> selectedDiplomaEisList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        // Link the variables to the view items.
        mRecyclerView = findViewById(R.id.recyclerview_training_lijst);
        volgendeButton = findViewById(R.id.buttonVolgende);

        // Set up of the recycler view and adapter.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        // Not all items in list have the same size
        mRecyclerView.setHasFixedSize(true);
        trainingsListAdapter = new TrainingsListAdapter(this, this);
        mRecyclerView.setAdapter(trainingsListAdapter);


        // Get data
        loadCwoEisData();
    }

    /**
     * On click method voor volgende button.
     *
     */
    public void onClickShowVolgende(View view) {
        if (!selectedDiplomaEisList.isEmpty()) {
            showCursistBehaaldEisenActivity();
        }
    }

    private void showCursistBehaaldEisenActivity() {
        Context context = this;
        Class destinationClass = CursistBehaaldEisActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putParcelableArrayListExtra("selectedDiplomaEisList", selectedDiplomaEisList);

        startActivity(intent);
    }

    private void loadCwoEisData() {
        showProgressDialog();
        // todo make this not hardcoded.
        PersistExamenEisen.requestDiplomaEisen("windsurfen", this);
    }

    @Override
    public void onClick(DiplomaEis diplomaEis, boolean selected) {
        if (selected) {
            selectedDiplomaEisList.add(diplomaEis);
        } else if (selectedDiplomaEisList.contains(diplomaEis)) {
            selectedDiplomaEisList.remove(diplomaEis);
        }
        toggleVolgendeButton();
    }

    @Override
    public boolean isSelectedDiplomaEis(DiplomaEis eis) {
        return selectedDiplomaEisList.contains(eis);
    }

    private void toggleVolgendeButton() {
        volgendeButton.setEnabled(!selectedDiplomaEisList.isEmpty());
    }

    @Override
    public void receiveDiplomaEisen(List<DiplomaEis> diplomaEisList) {
        hideProgressDialog();
        if(diplomaEisList == null || diplomaEisList.isEmpty()) {
            showErrorDialog();
        } else {
            trainingsListAdapter.setCwoData(diplomaEisList);
        }
    }
}