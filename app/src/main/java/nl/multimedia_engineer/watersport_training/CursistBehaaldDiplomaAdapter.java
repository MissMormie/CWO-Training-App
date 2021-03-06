package nl.multimedia_engineer.watersport_training;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.model.Diploma;
import nl.multimedia_engineer.watersport_training.persistence.PersistCursist;
import nl.multimedia_engineer.watersport_training.util.PreferenceUtil;

/**
 * Created by sonja on 4/3/2017.
 * Cursist behaald diploma many to many table.
 */

class CursistBehaaldDiplomaAdapter extends RecyclerView.Adapter<CursistBehaaldDiplomaAdapter.CursistBehaaldDiplomaViewHolder> {
    private final List<Diploma> diplomaList;
    private Cursist cursist;
    private Context context;
    private boolean saveData = true; // OnChangeChecklistener saves data when checkbox is clicked, but also when data is refeshed. Using to as workaround.


    CursistBehaaldDiplomaAdapter(List<Diploma> diplomaList) {
        this.diplomaList = diplomaList;
    }

    public void setCursist(Cursist cursist) {
        this.cursist = cursist;
        notifyDataSetChanged();
    }


    @Override
    public CursistBehaaldDiplomaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.diploma_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new CursistBehaaldDiplomaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CursistBehaaldDiplomaViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (diplomaList == null)
            return 0;
        else
            return diplomaList.size();
    }


    class CursistBehaaldDiplomaViewHolder extends RecyclerView.ViewHolder {
        final CheckBox diplomaCheckBox;

        CursistBehaaldDiplomaViewHolder(View itemView) {
            super(itemView);
            diplomaCheckBox = (CheckBox) itemView.findViewById(R.id.checkBoxDiploma);

            // set on checked listener for checkbox
            diplomaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!saveData)
                        return;

                    Diploma diploma = diplomaList.get(getAdapterPosition());
                    if(isChecked) {
                        cursist.addDiploma(diploma);
                    } else {
                        cursist.removeDiploma(diploma);
                    }
                    String activeGroup = PreferenceUtil.getPreferenceString(context, context.getString(R.string.pref_current_group_id), "");

                    PersistCursist.saveCursistDiploma(activeGroup, cursist, diploma.getId(), !isChecked);
                }
            });
        }

        void bind(int position) {
            Diploma diploma = diplomaList.get(position);
            String text = diploma.getTitel() + " " + diploma.getNivo();
            diplomaCheckBox.setText(text);

            // Save data set to false so changing of checkbox doesn't trigger saving to server.
            saveData = false;
            setCheckboxIfDiplomaBehaald(diploma);

            saveData = true;
        }

        private void setCheckboxIfDiplomaBehaald(Diploma diploma) {
            if (cursist == null)
                return;
            // Reset cbCwoEis so it's standard enabled.
            diplomaCheckBox.setEnabled(true);
            boolean checked = false;
            // als diploma behaald is, is automatisch elke eis voor het diploma ook behaald.
            String diplomaId = diploma.getId();
            if (cursist.hasDiploma(diplomaId)) {
                checked = true;
                // als diploma gehaald is kan deze niet ge unchecked worden.
                diplomaCheckBox.setEnabled(false);
            }

            diplomaCheckBox.setChecked(checked);

        }
    }
}
