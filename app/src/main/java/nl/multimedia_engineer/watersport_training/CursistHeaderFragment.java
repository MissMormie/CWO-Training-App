package nl.multimedia_engineer.watersport_training;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import nl.multimedia_engineer.watersport_training.databinding.FragmentCursistHeaderBinding;
import nl.multimedia_engineer.watersport_training.model.Cursist;


/**
 * A simple {@link Fragment} subclass.
 */
public class CursistHeaderFragment extends Fragment {
    private FragmentCursistHeaderBinding databinding;

    public CursistHeaderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cursist_header, container, false);
        return databinding.getRoot(); // View
    }

    public void setCursist(Cursist cursist) {
        if (cursist == null)
            return;
        databinding.textViewNaam.setText(cursist.nameToString());
        databinding.textViewOpmerking.setText(cursist.getOpmerking());
        String text;
        if (cursist.getPaspoort() == null) {
            text = getString(R.string.paspoort) + ": " + getString(R.string.nee);
        } else {
            text = getString(R.string.paspoort) + ": " + getString(R.string.ja);
        }
        databinding.textViewPaspoort.setText(text);

        // In some cases not all photopaths are saved.
        // Find photo
        String photoPath = cursist.getPhotoPathNormal();
        if(photoPath == null && cursist.getPhotoPathThumbnail() != null) {
            photoPath = cursist.getPhotoPathThumbnail();
        } else if(photoPath == null && cursist.getPhotoPathLarge() != null) {
            photoPath = cursist.getPhotoPathLarge();
        }

        if(photoPath != null) {
            Glide.with(this)
                .load(Uri.parse(photoPath))
                .placeholder(R.drawable.ic_user_image)
                .into(databinding.imageViewFoto);
        }
    }
}