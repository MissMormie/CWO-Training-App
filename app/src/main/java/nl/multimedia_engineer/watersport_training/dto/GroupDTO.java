package nl.multimedia_engineer.watersport_training.dto;

import android.support.annotation.Keep;

import java.util.HashMap;
import java.util.Map;

import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.model.CursistPartial;
import nl.multimedia_engineer.watersport_training.model.Group;
import nl.multimedia_engineer.watersport_training.model.GroupPartial;
import nl.multimedia_engineer.watersport_training.model.User;

/**
 * Use this dto for groups under root/groepen
 */
@Keep
public class GroupDTO {
    public String discipline;
    public Map<String, CursistPartial> cursisten;
    public Map<String, String> users;

    public GroupDTO(Group group) {
        discipline = group.getDiscipline();
        users = group.getUsers();
        if(group.getCursisten() != null && !group.getCursisten().isEmpty()) {
            cursisten = new HashMap<>();
            for (Cursist cursist : group.getCursisten()) {
                cursisten.put(cursist.getId(), cursist);
            }
        }
    }

    public GroupDTO(GroupPartial groupPartial, User... usersArray) {
        discipline = groupPartial.getDiscipline();
        if(usersArray != null && usersArray.length != 0) {
            users = new HashMap<>();
            for (User user : usersArray) {
                users.put(user.getUserUid(), user.getUserUid());
            }
        }
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public Map<String, CursistPartial> getCursisten() {
        return cursisten;
    }

    public void setCursisten(Map<String, CursistPartial> cursisten) {
        this.cursisten = cursisten;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }
}
