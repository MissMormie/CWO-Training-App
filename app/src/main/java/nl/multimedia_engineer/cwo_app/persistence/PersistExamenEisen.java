package nl.multimedia_engineer.cwo_app.persistence;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class PersistExamenEisen {
    public interface ReceivedDiplomaEisen {
        void receiveDiplomaEisen(List<DiplomaEis> diplomaEisList);
    }

    public static void requestDiplomaEisen(final String discipline, final ReceivedDiplomaEisen receiver) {
        // todo make this not hardcoded. For now using only 1 discipline.
        DatabaseReference examenEisen = DatabaseRefUtil.getExamenEisen(discipline);
        examenEisen.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DiplomaEis> diplomaEisen = new ArrayList<>();

                for(DataSnapshot subdiscipline : dataSnapshot.getChildren()) {
                    Diploma diploma = new Diploma();
                    diploma.setId(subdiscipline.getKey());
                    diploma.setTitel(discipline);
                    for(DataSnapshot exameneis : subdiscipline.getChildren()) {
                        DiplomaEis diplomaEis = exameneis.getValue(DiplomaEis.class);
                        diplomaEis.setDiploma(diploma);
                        diplomaEis.setId(exameneis.getKey());
                        diplomaEisen.add(diplomaEis);
                    }
                }
                receiver.receiveDiplomaEisen(diplomaEisen);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
