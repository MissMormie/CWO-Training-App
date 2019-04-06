package nl.multimedia_engineer.watersport_training.model;

import android.support.annotation.Keep;

import java.util.Map;

@Keep
public class User {
    private String userUid;
    private Map<String, String> groups; // group id, group name

    public User(String userUid) {
        this.userUid = userUid;
    }

    public User(String userUid, Map<String,String> groups) {
        this.userUid = userUid;
        this.groups = groups;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public Map<String, String> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, String> groups) {
        this.groups = groups;
    }
}
