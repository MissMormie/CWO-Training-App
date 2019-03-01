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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
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
    final String IMG_URI = "imgUri";

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
    private ProgressBar loadingProgressBar;

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
        loadingProgressBar =  getActivity().findViewById(R.id.loadingProgressBar);
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
        outState.putParcelable(IMG_URI, tempImgUri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        takingPhoto = false;
        if(savedInstanceState != null && savedInstanceState.containsKey(CURSIST)) {
            cursist = savedInstanceState.getParcelable(CURSIST);
            tempImgUri = savedInstanceState.getParcelable(IMG_URI);
            populateFields();
        }
    }

    private void placePicture(String base64ImgInfo) {
        byte[] imgByteArray = Base64.decode(base64ImgInfo, Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
        fotoImageView.setImageBitmap(bitmap);
    }

    public void setCursist(Cursist cursist) {
        this.cursist = cursist;
        populateFields();
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
//        parentActivity.showProgressDialog();
        if (voornaamEditText == null) {
            setupFields();
        }
        // Check if we're working with an existing cursist or a new empty one.
        if (cursist == null || cursist.getId() != null && !cursist.getId().isEmpty()) {
            voornaamEditText.setText(cursist.getVoornaam());
            tussenvoegselEditText.setText(cursist.getTussenvoegsel());
            achternaamEditText.setText(cursist.getAchternaam());
            opmerkingenEditText.setText(cursist.getOpmerking());
            if (cursist.getPaspoort() == null) {
                paspoortCheckbox.setChecked(false);
            } else {
                paspoortCheckbox.setChecked(true);
            }
            if (cursist.getFotoFileBase64() == null || cursist.getFotoFileBase64().isEmpty()) {
//                URL fotoUrl = NetworkUtils.buildUrl("foto", cursist.getCursistFoto().getId().toString());
//                new DownloadAndSetImageTask(fotoImageView, getContext()).execute(fotoUrl.toString());
            }
        }
        // Checking this because it may be called after rotating the screen. Other fields are filled automatically.
        if (cursist.getFotoFileBase64() != null && !cursist.getFotoFileBase64().isEmpty()) {
            placePicture(cursist.getFotoFileBase64());
        }

        if(tempImgUri != null) {
            fotoImageView.setImageURI(tempImgUri);
        }
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

        if(tempImgUri != null) {
            cursist.setTempImgUri(tempImgUri);
        }

        // Check if picture was taken, if so, path to the photo is not null.
//        if (mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty() && takingPhoto == false) {
//            //Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
//            BitmapDrawable drawable = (BitmapDrawable) fotoImageView.getDrawable();
//            Bitmap bm = drawable.getBitmap();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//            byte[] b = baos.toByteArray();
//
//            String image = Base64.encodeToString(b, Base64.NO_WRAP);
//            cursist.setFotoFileBase64(image);
//        }
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
        PersistCursist.saveCursist(groupId, cursist, this);
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

    private void onClickTakePicture(){
        KeyboardUtil.hideKeyboard(getActivity());
        //Initialize on every usage
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/MikeLau/Pictures", "/sdcard/MikeLau/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(getActivity());
        CroperinoFileUtil.setupDirectory(getActivity());
        try {
            Croperino.prepareCamera(getActivity());
        } catch(Exception e) {
            e.printStackTrace();
            // todo handle error
        }
    }


    private Uri tempImgUri;
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

                    //Do saving / uploading of photo method here.
                    //The image file can always be retrieved via CroperinoFileUtil.getTempFile()
                }
                break;
            default:
                break;
        }
    }
//    private static final int REQUEST_IMAGE_CAPTURE = 1;
//    private static final int REQUEST_TAKE_PHOTO = 1;
//    private String mCurrentPhotoPath;
//
//    private void dispatchTakePictureIntent() {
//        takingPhoto = true;
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(getActivity(),
//                        getString(R.string.file_authority),
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//    }
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
//
//    private void setPic() {
//        // Get the dimensions of the View
//        int targetW = fotoImageView.getWidth();
//        int targetH = fotoImageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        fotoImageView.setImageBitmap(bitmap);
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        takingPhoto = false;
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//
//            setPic();
//        }
//    }
}