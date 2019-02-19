package nl.multimedia_engineer.cwo_app;


import android.content.Context;
// todo
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.multimedia_engineer.cwo_app.databinding.FragmentCursistHeaderBinding;
import nl.multimedia_engineer.cwo_app.model.Cursist;


/**
 * A simple {@link Fragment} subclass.
 */
public class CursistHeaderFragment extends Fragment {
    private Cursist cursist;
    private FragmentCursistHeaderBinding databinding;


    public CursistHeaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cursist_header, container, false);
        return databinding.getRoot(); // View
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setCursist(Cursist cursist) {
        this.cursist = cursist;


        if (cursist == null)
            return;
        databinding.textViewNaam.setText(cursist.nameToString());
        databinding.textViewOpmerking.setText(cursist.opmerking);
        if (cursist.paspoortDate == null)
            databinding.textViewPaspoort.setText(getString(R.string.paspoort) +": " + getString(R.string.nee));
        else
            databinding.textViewPaspoort.setText(getString(R.string.paspoort) +": " + getString(R.string.ja));

        if (cursist.getFotoFileBase64() != null && !cursist.getFotoFileBase64().isEmpty()) {
            // Check if photo is included in cursist object
                String imgData = cursist.getFotoFileBase64();
                byte[] imgByteArray = Base64.decode(imgData, Base64.NO_WRAP);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
                databinding.imageViewFoto.setImageBitmap(bitmap);
        } else {

            // todo
//                URL fotoUrl = NetworkUtils.buildUrl("foto", cursist.getCursistFoto().getId().toString());
//                new DownloadAndSetImageTask(databinding.imageViewFoto, getContext())
//                        .execute(fotoUrl.toString());
        }
    }

}