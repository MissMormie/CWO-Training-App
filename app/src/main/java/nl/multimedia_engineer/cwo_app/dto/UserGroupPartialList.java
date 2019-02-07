package nl.multimedia_engineer.cwo_app.dto;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.model.GroupPartial;

public class UserGroupPartialList {
    Map<String, String> groepen;

    @NonNull
    public List<GroupPartial> getGroepen() {

        List<GroupPartial> groupList = new ArrayList<GroupPartial>();
        if(groepen == null || groepen.isEmpty()) {
            return groupList;
        }
        for(Map.Entry<String, String> entry : groepen.entrySet()) {
            GroupPartial g = new GroupPartial(entry.getKey(), entry.getValue());
            groupList.add(g);
        }
        return groupList;
    }

    public void setGroepen(Map<String, String> groepen) {
        this.groepen = groepen;
    }
}
