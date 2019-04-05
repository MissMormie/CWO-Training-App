package nl.multimedia_engineer.watersport_training;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import java.util.List;

import nl.multimedia_engineer.watersport_training.model.DiplomaEis;

/**
 * Created by sonja on 3/15/2017.
 * Shows list of diploma Eisen.
 */

class TrainingsListAdapter extends RecyclerView.Adapter<TrainingsListAdapter.TrainingsListAdapterViewHolder> {
    private static final String TAG = TrainingsListAdapter.class.getSimpleName();

    private List<DiplomaEis> diplomaEisList = null;
    private final TrainingListAdapterOnClickHandler clickHandler;
    private Context context;

    TrainingsListAdapter(TrainingListAdapterOnClickHandler clickHandler, Context context) {
        this.clickHandler = clickHandler;
        this.context = context;
    }

    @Override
    public TrainingsListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.training_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TrainingsListAdapter.TrainingsListAdapterViewHolder(view);
    }

    void setCwoData(List<DiplomaEis> diplomaEis) {
        this.diplomaEisList = diplomaEis;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(TrainingsListAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (diplomaEisList == null)
            return 0;
        return diplomaEisList.size();
    }

    class TrainingsListAdapterViewHolder extends RecyclerView.ViewHolder /* implements View.OnClickListener */ {
        final CheckBox cbCwoEis;
        final ImageButton imgButtonInfo;

        TrainingsListAdapterViewHolder(View itemView) {
            super(itemView);
            cbCwoEis = (CheckBox) itemView.findViewById(R.id.checkBoxTrainingsEis);
            imgButtonInfo = (ImageButton) itemView.findViewById(R.id.imageButtonInfo);

            setListeners();

        }

        private void setListeners() {
            cbCwoEis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int adapterPosition = getAdapterPosition();
                    DiplomaEis diplomaEis = diplomaEisList.get(adapterPosition);

                    clickHandler.onClick(diplomaEis, isChecked);
                }
            });


            imgButtonInfo.setOnClickListener(new ImageButton.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    DiplomaEis diplomaEis = diplomaEisList.get(adapterPosition);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(diplomaEis.getTekst())
                            .setTitle(diplomaEis.getTitel())
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // We doen niets, maar je moet deze hebben om eruit te komen.
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

        }


        void bind(int position) {
            DiplomaEis diplomaEis = diplomaEisList.get(position);
            cbCwoEis.setChecked(clickHandler.isSelectedDiplomaEis(diplomaEis));
            String text = diplomaEis.getDiploma().getTitel() + " " + diplomaEis.getDiploma().getNivo() + " " + diplomaEis.getTitel();
            cbCwoEis.setText(text);
        }
    }

    interface TrainingListAdapterOnClickHandler {
        void onClick(DiplomaEis diplomaEis, boolean selected);

        boolean isSelectedDiplomaEis(DiplomaEis eis);
    }
}