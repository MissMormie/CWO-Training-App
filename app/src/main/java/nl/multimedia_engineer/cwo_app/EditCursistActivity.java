package nl.multimedia_engineer.cwo_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;

public class EditCursistActivity
        extends BaseActivity
        implements CursistFormFragment.OnFragmentInteractionListener,
                   PersistCursist.SavedCursist {
    private Cursist cursist;
    private CursistFormFragment cursistFormFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cursist);
        cursist = getIntent().getExtras().getParcelable("cursist");
        cursistFormFragment = (CursistFormFragment) getSupportFragmentManager().findFragmentById(R.id.cursist_form_fragment);
        cursistFormFragment.setCursist(cursist);
    }

    @Override
    public void saveCursist(Cursist cursist) {
        this.cursist = cursist;
        PersistCursist.saveCursist(PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), ""), cursist, this);
    }

//    @Override
    public void cursistSaved(Cursist cursist) {
        if (cursist != null) {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.cursist_opgeslagen), Toast.LENGTH_SHORT);
            toast.show();
            if(cursist.getFotoFileBase64()!= null && !cursist.getFotoFileBase64().isEmpty())
//                cursist.getCursistFoto().setImage(this.cursist.getFotoFileBase64());
//             TODO, fix this work around., check if code is used at all.
            // For some reason the api returns a different date, it's saved correctly
            cursist.setPaspoort(this.cursist.getPaspoort());
        } else {
            showErrorDialog();
        }

        Intent intent = new Intent();
        intent.putExtra("cursist", cursist);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("cursist", cursist);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    // ------------------------------- Implements PersistCursist.SavedCursist ----------------------

    @Override
    public void onCursistSaved() {
        hideProgressDialog();
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.cursist_opgeslagen), Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent();
        intent.putExtra("cursist", cursist);
        setResult(RESULT_OK, intent);
        finish();
    }
}