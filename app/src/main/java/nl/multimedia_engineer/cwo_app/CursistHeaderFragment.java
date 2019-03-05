package nl.multimedia_engineer.cwo_app;


import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import nl.multimedia_engineer.cwo_app.databinding.FragmentCursistHeaderBinding;
import nl.multimedia_engineer.cwo_app.model.Cursist;


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

        if(cursist.getPhotoPathNormal() != null) {
            Glide.with(this)
                .load(Uri.parse(cursist.getPhotoPathNormal()))
                .placeholder(R.drawable.ic_user_image)
                .into(databinding.imageViewFoto);
        }
    }
}