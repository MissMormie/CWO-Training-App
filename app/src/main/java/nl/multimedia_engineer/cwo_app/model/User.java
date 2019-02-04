package nl.multimedia_engineer.cwo_app.model;

import java.util.Map;

public class User {
    private String userUid;
    private Map<String, String> groups; // group id, group name

    public User(String userUid, Map<String,String> groups) {
        this.userUid = userUid;
        this.groups = groups;
    }

}
