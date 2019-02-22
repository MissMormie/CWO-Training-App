package nl.multimedia_engineer.cwo_app.persistence;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class PersistDiploma {
    public interface ReceiveDiplomas {
        void onReceiveDiplomas(List<Diploma> diplomas);
        void onFailedReceivingDiplomas(DatabaseError databaseError);
    }

    public static void getDiplomaEisen(final String discipline, final ReceiveDiplomas receiver) {
        DatabaseReference examenEisen = DatabaseRefUtil.getExamenEisen(discipline);
        examenEisen.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Diploma> diplomas = new ArrayList<>();

                for(DataSnapshot subdiscipline : dataSnapshot.getChildren()) {
                    Diploma diploma = new Diploma();
                    diploma.setId(subdiscipline.getKey());
                    diploma.setTitel(discipline);
                    for(DataSnapshot exameneis : subdiscipline.getChildren()) {
                        DiplomaEis diplomaEis = exameneis.getValue(DiplomaEis.class);
                        diplomaEis.setDiploma(diploma);
                        diplomaEis.setId(exameneis.getKey());
                        diploma.addDiplomaEis(diplomaEis);
                    }
                    diplomas.add(diploma);
                }
                receiver.onReceiveDiplomas(diplomas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.onFailedReceivingDiplomas(databaseError);
            }
        });
    }
}
