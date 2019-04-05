package nl.multimedia_engineer.watersport_training.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group extends GroupPartial {

    private List<Cursist> cursisten;
    private Map<String, String> users;

    public Group(String discipline, String name) {
        super();
        setDiscipline(discipline);
        setName(name);
    }

    public Group(GroupPartial groupPartial) {
        setDiscipline(groupPartial.getDiscipline());
        setId(groupPartial.getId());
        setName(groupPartial.getName());
    }


    public List<Cursist> getCursisten() {
        return cursisten;
    }

    public void setCursisten(List<Cursist> cursisten) {
        this.cursisten = cursisten;
    }

    public void addUser(User user) {
        if(users == null) {
            users = new HashMap<>();
        }

        if(!users.containsKey(user.getUserUid())) {
            users.put(user.getUserUid(), user.getUserUid());
        }
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }
}
