package nl.multimedia_engineer.cwo_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.persistence.BetterPersistCursist;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
import nl.multimedia_engineer.cwo_app.tasks.ImageCompressTask;
import nl.multimedia_engineer.cwo_app.util.KeyboardUtil;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;


/**
 * Fragment that allows editing of a cursist object.
 * To use this fragment the implementing activity MUST implement onActivityResult with the following code:
 * if(requestCode == CroperinoConfig.REQUEST_TAKE_PHOTO || requestCode == CroperinoConfig.REQUEST_CROP_PHOTO) {
 *    cursistFormFragment.onActivityResult(requestCode, resultCode, data);
 * }
 * This is because this library only supports a callback to an activity.
 *
 */
public class CursistFormFragment extends Fragment implements PersistCursist.SavedCursist {
    // bundle info
    final String CURSIST = "cursist";


    private OnFragmentInteractionListener parentActivity;
    private Cursist cursist;

    // UI elements.
    private EditText voornaamEditText;
    private EditText tussenvoegselEditText;
    private EditText achternaamEditText;
    private EditText opmerkingenEditText;
    private CheckBox paspoortCheckbox;
    private ImageView fotoImageView;
    private Button saveButton;

    private boolean takingPhoto = false;


    /**
     */
    interface OnFragmentInteractionListener {
        void onCursistSaved(Cursist cursist);
        void showProgressDialog();
        void hideProgressDialog();
        void showErrorDialog();
    }

    public CursistFormFragment() {

    }

    private void setupFields() {
        voornaamEditText = getActivity().findViewById(R.id.editTextVoornaam);
        tussenvoegselEditText = getActivity().findViewById(R.id.editTextTussenvoegsel);
        achternaamEditText = getActivity().findViewById(R.id.editTextAchternaam);
        opmerkingenEditText = getActivity().findViewById(R.id.editTextOpmerkingen);
        paspoortCheckbox =  getActivity().findViewById(R.id.checkBoxPaspoort);
        fotoImageView =  getActivity().findViewById(R.id.imageViewFoto);
        saveButton =  getActivity().findViewById(R.id.buttonSave);
        ImageButton takeImageButton =  getActivity().findViewById(R.id.imageButtonPhoto);
        takeImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickTakePicture();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cursist_form, container, false);

        // workaround because onClick in xml does not work for fragments, it sends the onClick to the activity instead.
        if(saveButton == null) {
            saveButton = (Button) v.findViewById(R.id.buttonSave);
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickSaveCursist();
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        populateCursist();
        outState.putParcelable(CURSIST, cursist);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        takingPhoto = false;
        if(savedInstanceState != null && savedInstanceState.containsKey(CURSIST)) {
            cursist = savedInstanceState.getParcelable(CURSIST);

        }
        populateFields();
    }

//    todo use the preferred setArguments rather than setCursist.
//    @Override
//    public void setArguments(@Nullable Bundle args) {
//        super.setArguments(args);
//    }

    public void setCursist(Cursist cursist, boolean overWrite) {
        if(this.cursist == null || overWrite) {
            this.cursist = cursist;
            populateFields();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            parentActivity = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
    }

    private void populateFields() {
        if (voornaamEditText == null) {
            setupFields();
        }

        if(cursist == null) {
            return;
        }

        // Check if we're working with an existing cursist or a new empty one.
        voornaamEditText.setText(cursist.getVoornaam());
        tussenvoegselEditText.setText(cursist.getTussenvoegsel());
        achternaamEditText.setText(cursist.getAchternaam());
        opmerkingenEditText.setText(cursist.getOpmerking());
        if (cursist.getPaspoort() == null) {
            paspoortCheckbox.setChecked(false);
        } else {
            paspoortCheckbox.setChecked(true);
        }

        showImg();

    }

    /**
     * Read the entered data and use it to fill the member Cursist instance cursist.
     */
    private void populateCursist() {
        cursist.setVoornaam(voornaamEditText.getText().toString());
        cursist.setTussenvoegsel(tussenvoegselEditText.getText().toString());
        cursist.setAchternaam(achternaamEditText.getText().toString());
        cursist.setOpmerking(opmerkingenEditText.getText().toString());
        if (paspoortCheckbox.isChecked()) {
            // check if it was already checked, if not, set date of today.
            if (cursist.getPaspoort() == null) {
                cursist.setPaspoort(System.currentTimeMillis());
            }
        } else {
            cursist.setPaspoort(null);
        }

    }

    private void showImg(){
        if(cursist.getPhotoFileNormal() != null) {
            fotoImageView.setImageURI(Uri.fromFile(cursist.getPhotoFileNormal()));
        } else if(cursist.getPhotoPathNormal() != null && !cursist.getPhotoPathNormal().isEmpty()){
            Uri uri = Uri.parse(cursist.getPhotoPathNormal());
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.ic_user_image)
                    .into(fotoImageView);
        }
    }

    private void onClickSaveCursist() {
        if(voornaamEditText.getText() == null || voornaamEditText.getText().toString().equals("")) {
            showMinimumFormDemand();
            return;
        }

        saveButton.setEnabled(false);
        parentActivity.showProgressDialog();
        populateCursist();
        saveCursist();
    }

    private void saveCursist() {
        String groupId = PreferenceUtil.getPreferenceString(getContext(), getString(R.string.pref_current_group_id), "");
        BetterPersistCursist persistCursist = new BetterPersistCursist(groupId);
        persistCursist.saveCursistWithPhotos(cursist, this);
    }

    private void showMinimumFormDemand() {
        Toast toast = Toast.makeText(getContext(), getString(R.string.minimum_form_demand), Toast.LENGTH_SHORT);
        toast.show();
    }

    // ------------------------------- Implements PersistCursist.SavedCursist ----------------------

    @Override
    public void onCursistSaved(Cursist cursist) {
        this.cursist = cursist;
        parentActivity.onCursistSaved(cursist);

        // Get preference for showing diploma's after creation of Cursist.
    }

    @Override
    public void onCursistSaveFailed() {
        parentActivity.hideProgressDialog();
        parentActivity.showErrorDialog();
    }

    // ------------------------------------ PHOTO SUPPORT -----------------------------------------------
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);
    private ImageCompressTask imageCompressTask;
    private Uri tempImgUri;

    private boolean isNewImageTaken() {
        return cursist.getPhotoFileNormal() != null && cursist.getPhotoFileLarge() != null && cursist.getPhotoFileThumbnail() != null;
    }

    private void onClickTakePicture(){
        KeyboardUtil.hideKeyboard(getActivity());
        //Initialize on every usage
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/CWO_APP/Pictures", "/sdcard/SWO_APP/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(getActivity());
        CroperinoFileUtil.setupDirectory(getActivity());
        try {
            Croperino.prepareCamera(getActivity());
        } catch(Exception e) {
            e.printStackTrace();
            parentActivity.showErrorDialog();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                    Croperino.runCropImage(CroperinoFileUtil.getTempFile(), getActivity(), true, 1, 1, R.color.gray, R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    tempImgUri = Uri.fromFile(CroperinoFileUtil.getTempFile());
                    fotoImageView.setImageURI(tempImgUri);

                    // Compress Photo
                    compressPhoto(tempImgUri);
                }

                break;
            default:
                break;
        }
    }

    private void compressPhoto(Uri uri) {
        if(uri != null) {
            String path = uri.getPath();

            ImageCompressTask.Size[] sizes = {ImageCompressTask.Size.LARGE, ImageCompressTask.Size.NORMAL, ImageCompressTask.Size.THUMBNAIL};
            //Create ImageCompressTask and execute with Executor.
            imageCompressTask = new ImageCompressTask(getContext(), path, iImageCompressTaskListener, sizes);

            mExecutorService.execute(imageCompressTask);
        }
    }

    private ImageCompressTask.IImageCompressTaskListener iImageCompressTaskListener = new ImageCompressTask.IImageCompressTaskListener() {
        @Override
        public void onComplete(Map<ImageCompressTask.Size, File> compressed) {
            //photo compressed. Yay!
            //prepare for uploads. Use an Http library like Retrofit, Volley or async-http-client (My favourite)

            if(compressed.containsKey(ImageCompressTask.Size.LARGE)) {
                cursist.setPhotoFileLarge(compressed.get(ImageCompressTask.Size.LARGE));
            }

            if(compressed.containsKey(ImageCompressTask.Size.NORMAL)) {
                cursist.setPhotoFileNormal(compressed.get(ImageCompressTask.Size.NORMAL));
            }

            if(compressed.containsKey(ImageCompressTask.Size.THUMBNAIL)) {
                cursist.setPhotoFileThumbnail(compressed.get(ImageCompressTask.Size.THUMBNAIL));
            }
            showImg();
        }


        @Override
        public void onError(Throwable error) {
            //very unlikely, but it might happen on a device with extremely low storage.
            //log it, log.WhatTheFuck?, or show a dialog asking the user to delete some files....etc, etc
            Log.wtf("ImageCompressor", "Error occurred", error);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdown();
        mExecutorService = null;
        imageCompressTask = null;
    }
}