package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.dto.DisciplinesList;
import nl.multimedia_engineer.cwo_app.dto.SubDisciplinesWithDemandsList;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.model.Discipline;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class DemandsPerDisciplineActivity extends AppCompatActivity {
    DisciplinesExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demands_per_discipline);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.elv_demands_per_disciplines);

        // preparing list data
        prepareListData();

    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        final Context context = this;
        // todo when there is support for multiple disciplines this needs to be not hardcoded.
        final DatabaseReference examDemands = DatabaseRefUtil.getDisciplineExamDemands("windsurfen");
        examDemands.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, HashMap<String, DiplomaEis>>> type = new GenericTypeIndicator<HashMap<String, HashMap<String, DiplomaEis>>>() {};
                HashMap<String, HashMap<String, DiplomaEis>> map = dataSnapshot.getValue(type);

                SubDisciplinesWithDemandsList list = new SubDisciplinesWithDemandsList(map);
//                DisciplinesList disciplinesList = new DisciplinesList(dataSnapshot);

                listAdapter = new DisciplinesExpandableListAdapter(context, list.getDiplomaList());

                // setting list adapter
                expListView.setAdapter(listAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo handle this nicely
                Toast.makeText(context, "Something went wrong.. ", Toast.LENGTH_LONG);
            }
        });

    }
}