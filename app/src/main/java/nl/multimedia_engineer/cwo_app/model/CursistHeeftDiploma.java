package nl.multimedia_engineer.cwo_app.model;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sonja on 3/14/2017.
 * CursistHeeftDiploma
 */

public class CursistHeeftDiploma {

    private final Long cursist;

    private Diploma diploma;

    private Date diplomaBehaald;

    private boolean isBehaald; // Holder variable.

    public CursistHeeftDiploma(Long id, Long cursist, Diploma diploma) {
        Long id1 = id;
        this.cursist = cursist;
        this.diploma = diploma;
    }

    public CursistHeeftDiploma(Long cursist, Diploma diploma, boolean isBehaald) {
        this.cursist = cursist;
        this.diploma = diploma;
        this.isBehaald = isBehaald;
    }


    public Diploma getDiploma() {
        return diploma;
    }

    public void setDiploma(Diploma diploma) {
        this.diploma = diploma;
    }

    public boolean isBehaald() {
        return isBehaald;
    }

    public void setBehaald(boolean behaald) {
        isBehaald = behaald;
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("diplomaId", diploma.getId());
            jsonObject.put("cursistId", cursist);
            return jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

    }
}