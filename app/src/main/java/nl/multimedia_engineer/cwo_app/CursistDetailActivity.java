package nl.multimedia_engineer.cwo_app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.databinding.ActivityCursistDetailBinding;
import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;

public class CursistDetailActivity extends BaseActivity {

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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_training_lijst);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // Not all items in list have the same size
        recyclerView.setHasFixedSize(true);
        cursistBehaaldEisAdapter = new CursistBehaaldEisAdapter();
        recyclerView.setAdapter(cursistBehaaldEisAdapter);

        cursist = getIntent().getExtras().getParcelable("cursist");
        // TODO Fix parcelable. Atm it doesn't pass eisen info, so reloading the cursist Info.
        //displayCursistInfo();
//        new FetchCursistTask().execute(cursist.id);
//        loadDiplomaData();
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
//        new SaveCursistAsyncTask(this).execute(cursist);
        setMenuTitle();
    }

    private void deleteCursist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.echt_verwijderen)
                .setPositiveButton(R.string.ja, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteCursistTask().execute();
                    }
                })
                .setNegativeButton(R.string.nee, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                    // Create the AlertDialog object and return it
                });

        Dialog dialog = builder.create();
        dialog.show();
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
        //new FetchCursistTask().execute(cursistId);
        new FetchCwoEisData().execute();
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

        if (cursist.getCursistFoto() != null && cursist.getCursistFoto().getThumbnail() != null && !cursist.getCursistFoto().getThumbnail().equals("")) {
            // Check if photo is included in cursist object
            if (cursist.getCursistFoto().getImage() != null && !cursist.getCursistFoto().getImage().equals("")) {
                String imgData = cursist.getCursistFoto().getThumbnail();
                byte[] imgByteArray = Base64.decode(imgData, Base64.NO_WRAP);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
                activityCursistDetailBinding.imageViewFoto.setImageBitmap(bitmap);
            } else {

//                URL fotoUrl = NetworkUtils.buildUrl("foto", cursist.getCursistFoto().getId().toString());
//                new DownloadAndSetImageTask(activityCursistDetailBinding.imageViewFoto, getApplicationContext())
//                        .execute(fotoUrl.toString());
            }
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

//    @Override
    public void cursistSaved(Cursist cursist) {
        toggleLoading(false);
        if (cursist != null) {
            String tekst;
            if (cursist.isVerborgen())
                tekst = getString(R.string.cursist_verborgen);
            else
                tekst = getString(R.string.cursist_niet_verborgen);

            Toast toast = Toast.makeText(getApplicationContext(), tekst, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            showErrorMessage();
        }
    }

    private void showErrorMessage() {
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error_message), Toast.LENGTH_LONG);
        toast.show();
    }


    private class DeleteCursistTask extends AsyncTask<Long, Void, Integer> {

        @Override
        protected void onPreExecute() {
            toggleLoading(true);
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Long... params) {
//            URL url = NetworkUtils.buildUrl("cursist", cursist.id.toString());
//            int resultCode = 0;
//            try {
//                resultCode = NetworkUtils.sendToServer(url);
//                return resultCode;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return resultCode;
            return 404;
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            toggleLoading(false);
            if (resultCode == HttpURLConnection.HTTP_OK) {
                cursistDeleted();
            } else {
                showErrorMessage();
            }
        }
    }

    private class FetchCwoEisData extends AsyncTask<String, Void, List<Diploma>> {

        @Override
        protected void onPreExecute() {
            toggleLoading(true);
            super.onPreExecute();
        }

        @Override
        protected List<Diploma> doInBackground(String... params) {
//            URL diplomaListUrl = NetworkUtils.buildUrl("diplomas");
//
//            try {
//                String jsonDiplomaLijstResponse = NetworkUtils.getResponseFromHttpUrl(diplomaListUrl);
//                return OpenJsonUtils.getDiplomaLijst(jsonDiplomaLijstResponse);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Diploma> diplomaListResult) {
            diplomaList = diplomaListResult;
            toggleLoading(false);
            if (diplomaListResult != null) {
                List<DiplomaEis> diplomaEisenLijst = new ArrayList<>();
                for (int i = 0; i < diplomaListResult.size(); i++) {
                    diplomaEisenLijst.addAll(diplomaListResult.get(i).getDiplomaEis());
                }

                displayDiplomaEisInfo(diplomaEisenLijst);
            } else {
                showErrorMessage();
            }
        }
    }

    class FetchCursistTask extends AsyncTask<Long, Void, Cursist> {

        @Override
        protected void onPreExecute() {
            toggleLoading(true);
            super.onPreExecute();
        }

        @Override
        protected Cursist doInBackground(Long... id) {
//            URL curistUrl = NetworkUtils.buildUrl("cursist", id[0].toString());
//
//            try {
//                String jsonCursistResponse = NetworkUtils.getResponseFromHttpUrl(curistUrl);
//                Cursist cursist = OpenJsonUtils.getCursist(jsonCursistResponse);
//                return cursist;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Cursist cursistObject) {
            toggleLoading(false);
            cursist = cursistObject;
            displayCursistInfo();
        }
    }
}