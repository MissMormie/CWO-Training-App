package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.databinding.ActivityCursistDetailBinding;
import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.CursistPartial;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
import nl.multimedia_engineer.cwo_app.persistence.PersistDiploma;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;

public class CursistDetailActivity
        extends BaseActivity
        implements PersistCursist.ReceiveCursist,
                   PersistDiploma.ReceiveDiplomas
            {
    private static final String TAG = CursistDetailActivity.class.getSimpleName();

    private ActivityCursistDetailBinding activityCursistDetailBinding;
    private Cursist cursist;
    private CursistBehaaldEisAdapter cursistBehaaldEisAdapter;
    private static final int EDIT_CURSIST = 1;
    private List<Diploma> diplomaList;
    private MenuItem verbergenMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCursistDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_cursist_detail);

        // Set up of the recycler view and adapter.
        RecyclerView recyclerView =  findViewById(R.id.recyclerview_training_lijst);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // Not all items in list have the same size
        recyclerView.setHasFixedSize(true);
        cursistBehaaldEisAdapter = new CursistBehaaldEisAdapter();
        recyclerView.setAdapter(cursistBehaaldEisAdapter);

        // Get info from parcel to fill part of cursist.
        CursistPartial cursistPartial = getIntent().getExtras().getParcelable("cursist");
        cursist = new Cursist(cursistPartial);

        // Displaying data we got from the parcel, this does not contain complete cursist data but shows something while the rest is loading.
        displayCursistInfo();
        showProgressDialog();
        String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");

        PersistCursist.getCursist(groupId, cursist.getId(), this);
    }


    /// ---------------------------- MENU Functions ------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cursist_detail_menu, menu);
        verbergenMenu = menu.findItem(R.id.action_verbergen);
        setMenuTitle();
        return true;
    }

    private void setMenuTitle() {
        if(!cursist.isVerborgen()) {
            verbergenMenu.setTitle(getString(R.string.verbergen));
        } else{
            verbergenMenu.setTitle(getString(R.string.niet_verbergen));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                showEditCursist();
                break;
            case R.id.action_verbergen:
                hideCursist();
                break;
            case R.id.action_delete:
                deleteCursist();
                break;
            case R.id.action_diploma:
                diplomaUitgeven();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void showEditCursist() {
        Context context = this;
        Class destinationClass = EditCursistActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra("cursist", cursist);
        startActivityForResult(intent, EDIT_CURSIST);
    }

    private void diplomaUitgeven() {
        Class destinationClass = CursistBehaaldDiplomaActivity.class;
        Intent intent = new Intent(this, destinationClass);

        ArrayList<Diploma> diplomaArrayList = (ArrayList<Diploma>) diplomaList;
        intent.putParcelableArrayListExtra("selectedDiplomaList", diplomaArrayList);
        intent.putExtra("cursist", cursist);

        startActivity(intent);
    }

    private void hideCursist() {
        toggleLoading(true);
        cursist.toggleVerborgen();
        // todo
//        new SaveCursistAsyncTask(this).execute(cursist);
        setMenuTitle();
    }

    private void deleteCursist() {
        // todo
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(R.string.echt_verwijderen)
//                .setPositiveButton(R.string.ja, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        new DeleteCursistTask().execute();
//                    }
//                })
//                .setNegativeButton(R.string.nee, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                    // Create the AlertDialog object and return it
//                });
//
//        Dialog dialog = builder.create();
//        dialog.show();
    }

    private void cursistDeleted() {
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.cursist_verwijderd), Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent();
        intent.putExtra("cursist", cursist);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    // ---------------------------------------------------------------------------------------------
    // BACK BEHAVIOUR

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("cursist", cursist);
        setResult(RESULT_OK, intent);
        finish();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CURSIST)
            if (resultCode == RESULT_OK && data.hasExtra("cursist")) {
                this.cursist = data.getExtras().getParcelable("cursist");
                displayCursistInfo();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ---------------------------------------------------------------------------------------------

    private void toggleLoading(boolean currentlyLoading) {
        if (activityCursistDetailBinding.loadingProgressBar == null)
            return;
        if (currentlyLoading)
            activityCursistDetailBinding.loadingProgressBar.setVisibility(View.VISIBLE);
        else
            activityCursistDetailBinding.loadingProgressBar.setVisibility(View.GONE);

    }


    private void loadDiplomaData() {
        PersistDiploma.getDiplomaEisen("windsurfen", this);
        //new FetchCursistTask().execute(cursistId);
//        new FetchCwoEisData().execute();
    }

    private void displayCursistInfo() {
        if (cursist == null)
            return;

        activityCursistDetailBinding.textviewNaam.setText(cursist.nameToString());
        activityCursistDetailBinding.textViewOpmerking.setText(cursist.opmerking);
        if (cursist.paspoortDate == null)
            activityCursistDetailBinding.textViewPaspoort.setText(getString(R.string.paspoort) + ": " + getString(R.string.nee));
        else
            activityCursistDetailBinding.textViewPaspoort.setText(getString(R.string.paspoort) + ": " + getString(R.string.ja));


        if (cursist.getFotoFileBase64() != null && !cursist.getFotoFileBase64().isEmpty()) {
            String imgData = cursist.getFotoFileBase64();
            byte[] imgByteArray = Base64.decode(imgData, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
            activityCursistDetailBinding.imageViewFoto.setImageBitmap(bitmap);
        } else {
            // todo?

//                URL fotoUrl = NetworkUtils.buildUrl("foto", cursist.getCursistFoto().getId().toString());
//                new DownloadAndSetImageTask(activityCursistDetailBinding.imageViewFoto, getApplicationContext())
//                        .execute(fotoUrl.toString());

        }
        loadDiplomaData();
//            activityCursistDetailBinding.imageViewFoto.setImageBitmap();
        // Pass information to adapter for eisen met.
//        cursistBehaaldEisAdapter.setCursist(cursist);
    }

    private void displayDiplomaEisInfo(List<DiplomaEis> diplomaEisList) {
        cursistBehaaldEisAdapter.setCwoListData(diplomaEisList);
        cursistBehaaldEisAdapter.setCursist(cursist);
    }

////    @Override
//    public void cursistSaved(Cursist cursist) {
//        toggleLoading(false);
//        if (cursist != null) {
//            String tekst;
//            if (cursist.isVerborgen())
//                tekst = getString(R.string.cursist_verborgen);
//            else
//                tekst = getString(R.string.cursist_niet_verborgen);
//
//            Toast toast = Toast.makeText(getApplicationContext(), tekst, Toast.LENGTH_SHORT);
//            toast.show();
//        } else {
//            showErrorDialog();
//        }
//    }


    // ------------------------------------- Persist cursist implementation ------------------------
    @Override
    public void receiveCursist(Cursist cursist) {
        hideProgressDialog();
        this.cursist = cursist;
        displayCursistInfo();
    }

    @Override
    public void receiveCursistFailed() {
        Log.d(TAG, "failed receiving cursist");
        showErrorDialog();
    }


    // ------------------------------------- Persist Diploma eisen implementation ------------------------


    @Override
    public void onReceiveDiplomas(List<Diploma> diplomas) {
        diplomaList = diplomas;
        toggleLoading(false);
        if (diplomas != null) {
            List<DiplomaEis> diplomaEisenLijst = new ArrayList<>();
            for (int i = 0; i < diplomas.size(); i++) {
                diplomaEisenLijst.addAll(diplomas.get(i).getDiplomaEisList());
            }

            displayDiplomaEisInfo(diplomaEisenLijst);
        } else {
            showErrorDialog();
            Log.d(TAG, "failed getting list of diploma's");
        }

    }



    @Override
    public void onFailedReceivingDiplomas(DatabaseError databaseError) {
        Log.d(TAG, "failed receiving diplomas");
    }

//
//    private class DeleteCursistTask extends AsyncTask<Long, Void, Integer> {
//
//        @Override
//        protected void onPreExecute() {
//            showProgressDialog();
//
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Integer doInBackground(Long... params) {
////            URL url = NetworkUtils.buildUrl("cursist", cursist.id.toString());
////            int resultCode = 0;
////            try {
////                resultCode = NetworkUtils.sendToServer(url);
////                return resultCode;
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////            return resultCode;
//            return 404;
//        }
//
//        @Override
//        protected void onPostExecute(Integer resultCode) {
//            hideProgressDialog();
//            toggleLoading(false);
//            if (resultCode == HttpURLConnection.HTTP_OK) {
//                cursistDeleted();
//            } else {
//                Log.d(TAG, "failed deleting cursist");
//                showErrorDialog();
//            }
//        }
//    }
//
//    private class FetchCwoEisData extends AsyncTask<String, Void, List<Diploma>> {
//
//        @Override
//        protected void onPreExecute() {
//            toggleLoading(true);
//            super.onPreExecute();
//        }
//
//        @Override
//        protected List<Diploma> doInBackground(String... params) {
////            URL diplomaListUrl = NetworkUtils.buildUrl("diplomas");
////
////            try {
////                String jsonDiplomaLijstResponse = NetworkUtils.getResponseFromHttpUrl(diplomaListUrl);
////                return OpenJsonUtils.getDiplomaLijst(jsonDiplomaLijstResponse);
////            } catch (Exception e) {
////                e.printStackTrace();
////                return null;
////            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(List<Diploma> diplomaListResult) {
//            diplomaList = diplomaListResult;
//            toggleLoading(false);
//            if (diplomaListResult != null) {
//                List<DiplomaEis> diplomaEisenLijst = new ArrayList<>();
//                for (int i = 0; i < diplomaListResult.size(); i++) {
//                    diplomaEisenLijst.addAll(diplomaListResult.get(i).getDiplomaEisList());
//                }
//
//                displayDiplomaEisInfo(diplomaEisenLijst);
//            } else {
//                showErrorDialog();
//                Log.d(TAG, "failed getting list of diploma's");
//            }
//        }
//    }
////
//    class FetchCursistTask extends AsyncTask<Long, Void, Cursist> {
//
//        @Override
//        protected void onPreExecute() {
//            toggleLoading(true);
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Cursist doInBackground(Long... id) {
////            URL curistUrl = NetworkUtils.buildUrl("cursist", id[0].toString());
////
////            try {
////                String jsonCursistResponse = NetworkUtils.getResponseFromHttpUrl(curistUrl);
////                Cursist cursist = OpenJsonUtils.getCursist(jsonCursistResponse);
////                return cursist;
////
////            } catch (Exception e) {
////                e.printStackTrace();
////                return null;
////            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Cursist cursistObject) {
//            toggleLoading(false);
//            cursist = cursistObject;
//            displayCursistInfo();
//        }
//    }
}