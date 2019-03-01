package nl.multimedia_engineer.cwo_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.Toast;

import com.mikelau.croperino.CroperinoConfig;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;

public class EditCursistActivity
        extends BaseActivity
        implements CursistFormFragment.OnFragmentInteractionListener
                    {
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
        intent.putExtra(CursistDetailActivity.EXTRA_CURSIST, cursist);
        setResult(RESULT_CANCELED, intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CroperinoConfig.REQUEST_TAKE_PHOTO || requestCode == CroperinoConfig.REQUEST_CROP_PHOTO) {
            cursistFormFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    // ------------------------------- Implements PersistCursist.SavedCursist ----------------------

    @Override
    public void onCursistSaved(Cursist cursist) {
        Intent intent = new Intent();
        intent.putExtra(CursistDetailActivity.EXTRA_CURSIST, cursist);
        setResult(RESULT_OK, intent);
        finish();
    }


}