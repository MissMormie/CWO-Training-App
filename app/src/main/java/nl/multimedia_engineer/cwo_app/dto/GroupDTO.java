package nl.multimedia_engineer.cwo_app.dto;

import android.support.annotation.Keep;

import java.util.HashMap;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.CursistPartial;
import nl.multimedia_engineer.cwo_app.model.Group;
import nl.multimedia_engineer.cwo_app.model.GroupPartial;
import nl.multimedia_engineer.cwo_app.model.User;

/**
 * Use this dto for groups under root/groepen
 */
@Keep
public class GroupDTO {
    String discipline;
    Map<String, CursistPartial> cursisten;
    Map<String, String> users;

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

}
