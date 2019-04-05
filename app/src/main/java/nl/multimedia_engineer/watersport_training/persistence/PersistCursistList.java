package nl.multimedia_engineer.watersport_training.persistence;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.util.DatabaseRefUtil;

/**
 * User beware, normal rules for firebase filtering still apply. Ie. you can't combine many things.
 */
public class PersistCursistList {

    private Integer limit = null;
    private boolean finishedList = false;
    private boolean currentlyLoading = false;
    private String startAtId = null;
    private String orderBy = null;
    private String groupId;

    private PersistCursist.ReceiveCursistList receiver;

    public PersistCursistList(PersistCursist.ReceiveCursistList receiver, String groupId) {
        this.receiver = receiver;
        this.groupId = groupId;
    }

    public PersistCursistList filterByVoornaam(String value) {
        orderBy = "voornaam";
        return this;
    }

    public PersistCursistList filterByVerborgen() {
        orderBy = "verborgen";
        return this;
    }

    /**
     * This also orders elements by key, because it's the only way it works. You need unique values here.
     * @param limit
     * @return
     */
    public PersistCursistList setLimit(int limit) {
        this.limit =  limit;
        orderBy = "id";
        return this;
    }

    public PersistCursistList startAt(String id) {
        startAtId = id;
        return this;
    }


    public PersistCursistList execute() {
        if(currentlyLoading) {
            return this;
        }
        DatabaseReference databaseReference = DatabaseRefUtil.getCursistenPerGroep(groupId);
        Query query = databaseReference;
        switch(orderBy) {
            case "verborgen":
            case "voornaam": query = query.orderByChild(orderBy); finishedList = true; break;
            case "id" :
                query = query.orderByChild(orderBy);
                if (limit != null) {
                    query = databaseReference.orderByChild("id").startAt(startAtId).limitToFirst(limit);
                }
                break;
            default:
                break;

        }

        currentlyLoading = true;
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentlyLoading = false;
                List<Cursist> cursistList = new ArrayList<>();
                for(DataSnapshot cursistSnapshot : dataSnapshot.getChildren()) {
                    Cursist cursist = PersistCursist.getCursist(cursistSnapshot);
                    if(cursist != null) {
                        cursistList.add(cursist);
                    }
                }

                if(cursistList.isEmpty() || cursistList.size() < limit) {
                    finishedList = true;
                } else {
                    increaseStartAtId(cursistList.get(cursistList.size() -1 ).getId());
                }
                receiver.onReceiveCursistList(cursistList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                currentlyLoading = false;
                receiver.onReceiveCursistListFailed();
            }
        });
        return this;
    }

    public boolean isEndOfListReached() {
        return finishedList;
    }

    private void increaseStartAtId(String id) {
        char[] chars = id.toCharArray();
        int recursions = 0;
        boolean loop;
        do {
            loop = false;
            char newChar = (char) (chars[chars.length - recursions -1 ] + 1);
            if(newChar == '{') {
                newChar = '0';
                loop = true;
            }
            chars[chars.length - recursions -1] = newChar;
            recursions ++;
        } while (loop);

        StringBuilder sb = new StringBuilder();
        for(char c : chars) {
            sb.append(c);
        }

        startAtId = sb.toString();
    }

    /**
     *
     * @return false if there are no more cursisten to return.
     */
    public boolean requestNextCursisten(String startAt) {
        if(finishedList) {
            return false;
        }
        this.startAtId = startAt;
        execute();
        return true;
    }

    public boolean requestNextCursisten() {
        if(finishedList) {
            return false;
        }
        execute();
        return true;
    }

}
