package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.CursistPartial;
import nl.multimedia_engineer.cwo_app.persistence.PersistCursist;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class CursistListActivity extends BaseActivity implements CursistListAdapater.CursistListAdapterOnClickHandler, PersistCursist.ReceiveCursistPartialList {
    private static final String TAG = CursistListActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private CursistListAdapater cursistListAdapater;
    private MenuItem searchItem;
    private static final int CURSIST_DETAIL = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursist_list);

        /*
         * Get references to the elements in the layout we need.
         * */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_cursist_lijst);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        // All items in list have the same size
        mRecyclerView.setHasFixedSize(true);
        cursistListAdapater = new CursistListAdapater(this, this);
        mRecyclerView.setAdapter(cursistListAdapater);

        loadCursistListData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cursist_lijst_menu, menu);
        //searchItem = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int itemThatWasClickedId = item.getItemId();
        /*if (itemThatWasClickedId == R.id.action_search) {
            // TODO do something searchy ;)
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }*/
    }

    private void loadCursistListData() {
        showProgressDialog();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String groupId = sharedPreferences.getString(getResources().getString(R.string.pref_current_group_id), "");

        PersistCursist.getCursistPartialList(groupId, this);

    }

    @Override
    public void onClick(CursistPartial cursist) {
        Context context = this;

        Class destinationClass = CursistDetailActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra("cursistId", cursist.getId());
        intent.putExtra("cursist", cursist);
        startActivityForResult(intent, CURSIST_DETAIL);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CURSIST_DETAIL)

            if (resultCode == RESULT_OK)
                cursistListAdapater.updateCursistInList((Cursist) data.getExtras().getParcelable("cursist"));
                // update cursist
            else if (resultCode == RESULT_CANCELED) {
                if (data.hasExtra("cursist")) {
                    Cursist cursist = data.getExtras().getParcelable("cursist");
                    cursistListAdapater.deleteCursistFromList(cursist);
                }
            }

    }


    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        showErrorDialog();
    }

    public void showCursistListData() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void receiveCursistPartialList(List<CursistPartial> cursistPartialList) {
        hideProgressDialog();
        cursistListAdapater.setCursistListData(cursistPartialList);
    }

    @Override
    public void receiveCursistPartialListFailed() {
        hideProgressDialog();
    }
}