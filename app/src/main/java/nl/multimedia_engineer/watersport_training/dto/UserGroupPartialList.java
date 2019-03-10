package nl.multimedia_engineer.watersport_training.dto;

import android.support.annotation.Keep;

import java.util.Map;

import nl.multimedia_engineer.watersport_training.model.GroupPartial;

@Keep
public class UserGroupPartialList {
    Map<String, String> groepen;
    private Map<String, GroupPartial> groupList = null;
//
//    @NonNull
//    public List<GroupPartial> getGroepen() {
//        if(groupList != null) {
//            return groupList;
//        }
//
//        groupList = new ArrayList<GroupPartial>();
//        if(groepen == null || groepen.isEmpty()) {
//            return groupList;
//        }
//        for(Map.Entry<String, String> entry : groepen.entrySet()) {
//            GroupPartial g = new GroupPartial(entry.getKey(), entry.getValue());
//            groupList.add(g);
//        }
//        return groupList;
//    }

    public void setGroepen(Map<String, String> groepen) {
        this.groepen = groepen;
    }
}
