package nl.multimedia_engineer.watersport_training;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.model.CursistPartial;

/**
 * Created by Sonja on 3/9/2017.
 * Shows listitems of cursisten
 */

class CursistListAdapater extends RecyclerView.Adapter<CursistListAdapater.CursistListAdapterViewHolder> {
    // For logging:
    private static final String TAG = CursistListAdapater.class.getSimpleName();
    private List<CursistPartial> cursistList;
    private final CursistListAdapterOnClickHandler clickHandler;
    private final Context context;


    CursistListAdapater(CursistListAdapterOnClickHandler clickHandler, Context context) {
        this.clickHandler = clickHandler;
        this.context = context;
    }

    // ---------------------------------------- Modify data -------------------------------------------


    void setCursistListData(List<CursistPartial> cursistList) {
        this.cursistList = cursistList;
        notifyDataSetChanged();
    }

    void deleteCursistFromList(Cursist cursist) {
        // Since the object is recreated I have to check every ID.

        int deleteThis = -1;
        for (int i = 0; i < cursistList.size(); i++) {
            if (cursist.getId().equals(cursistList.get(i).getId())) {
                deleteThis = i;
                break;
            }
        }
        if (deleteThis != -1) {
            cursistList.remove(deleteThis);
            notifyDataSetChanged();
        }
    }


    public void updateCursistInList(Cursist cursist) {
        // Since the object is recreated I have to check every ID.
        int updateThis = -1;
        for (int i = 0; i < cursistList.size(); i++) {
            if (cursist.getId().equals(cursistList.get(i).getId())) {
                updateThis = i;
                break;
            }
        }
        if (updateThis != -1) {
            cursistList.set(updateThis, cursist);
            notifyDataSetChanged();
        }
    }

    // ---------------------------------------- Overridden functions ---------------------------------


    @Override
    public CursistListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.cursist_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new CursistListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CursistListAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (cursistList == null)
            return 0;
        return cursistList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    // -------------------------------- Viewholder class --------------------------------------------

    class CursistListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView cursistItemTextView;
        final TextView diplomaTextView;
        final ImageView fotoImageView;
        final View view;

        CursistListAdapterViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            cursistItemTextView = (TextView) itemView.findViewById(R.id.tv_cursist_data);
            fotoImageView = (ImageView) itemView.findViewById(R.id.imageViewFoto);
            diplomaTextView = (TextView) itemView.findViewById(R.id.diplomaTextView);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            CursistPartial cursist = cursistList.get(position);
            cursistItemTextView.setText(cursist.nameToString());
            diplomaTextView.setText(cursist.getHoogsteDiploma());

            // Set foto
            if(cursist.getPhotoPathThumbnail() != null) {
                Glide.with(context)
                        .load(Uri.parse(cursist.getPhotoPathThumbnail()))
                        .placeholder(R.drawable.ic_user_image)
                        .into(fotoImageView);
            }

            // Set background color in case cursist is hidden.
            if (cursist.isVerborgen())
                this.view.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorGray, null));
            else
                this.view.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorWhite, null));

        }



        @Override
        public void onClick(View v) {
            System.out.println("onClick " + TAG);
            int adapterPosition = getAdapterPosition();
            CursistPartial cursist = cursistList.get(adapterPosition);
            clickHandler.onClick(cursist);
        }
    }

    // ----------------------------- Interface --------------------------------------------------

    interface CursistListAdapterOnClickHandler {
        void onClick(CursistPartial cursist);
    }

}