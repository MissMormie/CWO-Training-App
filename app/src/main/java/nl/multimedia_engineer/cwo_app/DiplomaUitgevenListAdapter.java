package nl.multimedia_engineer.cwo_app;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.List;

import nl.multimedia_engineer.cwo_app.model.Diploma;

/**
 * Created by sonja on 4/1/2017.
 * Uses diploma_list_item to show entered list of Diploma's, including checkbox behaviour.
 */

class DiplomaUitgevenListAdapter extends RecyclerView.Adapter<DiplomaUitgevenListAdapter.DiplomaListAdapterViewHolder> {
    private List<Diploma> diplomaList;
    final private DiplomaListAdapterOnClickHandler clickHandler;
    private Context context;

    DiplomaUitgevenListAdapter(DiplomaListAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public DiplomaListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.diploma_radio_button_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new DiplomaUitgevenListAdapter.DiplomaListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiplomaListAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (diplomaList == null)
            return 0;
        return diplomaList.size();
    }

    public void setDiplomaList(List<Diploma> diplomaList) {
        this.diplomaList = diplomaList;
        notifyDataSetChanged();
    }

    class DiplomaListAdapterViewHolder extends RecyclerView.ViewHolder /* implements View.OnClickListener */ {
        private final RadioButton diplomaRadioButton;

        DiplomaListAdapterViewHolder(View itemView) {
            super(itemView);
            diplomaRadioButton = (RadioButton) itemView.findViewById(R.id.radioButtonDiploma);

            setListeners(itemView);
        }

        private void setListeners(View itemView) {
            View.OnClickListener clickListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    Diploma diploma = diplomaList.get(adapterPosition);

                    clickHandler.onClick(diploma, true);
                    notifyItemRangeChanged(0, getItemCount());

                }
            };
            diplomaRadioButton.setOnClickListener(clickListener);


            /*
            diplomaRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int adapterPosition = getAdapterPosition();
                    Diploma diploma = diplomaList.get(adapterPosition);
                    clickHandler.onClick(diploma, isChecked);
                }
            });
            */
        }

        void bind(int position) {
            Diploma diploma = diplomaList.get(position);
            diplomaRadioButton.setChecked(clickHandler.isSelectedDiploma(diploma));
            diplomaRadioButton.setText(diploma.getTitel() + " " + diploma.getNivo());
        }
    }

    interface DiplomaListAdapterOnClickHandler {
        void onClick(Diploma diploma, boolean selected);

        boolean isSelectedDiploma(Diploma diploma);
    }
}