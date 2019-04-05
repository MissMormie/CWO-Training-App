package nl.multimedia_engineer.watersport_training.persistence;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.watersport_training.model.Diploma;
import nl.multimedia_engineer.watersport_training.model.DiplomaEis;
import nl.multimedia_engineer.watersport_training.util.DatabaseRefUtil;

public class PersistExamenEisen {
    public interface ReceivedDiplomaEisen {
        void onReceiveDiplomaEisen(List<DiplomaEis> diplomaEisList);
        void onReceiveDiplomaEisenFailed();
    }

    public interface ReceivedDiplomas {
        void onReceiveDiplomasWithEisen(List<Diploma> diplomas);
        void onReceiveDiplomasFailedWithEisen();
    }

    public static void requestDiplomaEisen(final String discipline, final ReceivedDiplomaEisen receiver) {
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
                receiver.onReceiveDiplomaEisen(diplomaEisen);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.onReceiveDiplomaEisenFailed();
            }
        });
    }
}
