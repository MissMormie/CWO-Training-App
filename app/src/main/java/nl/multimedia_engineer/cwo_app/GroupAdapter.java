package nl.multimedia_engineer.cwo_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

import nl.multimedia_engineer.cwo_app.dto.UserGroupPartialList;
import nl.multimedia_engineer.cwo_app.model.GroupPartial;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupAdapterViewHolder>  {
    private List<GroupPartial> groupList;
    private ItemClickListener listener;


    // Provide a suitable constructor (depends on the kind of dataset)
    public GroupAdapter(UserGroupPartialList userGroupPartialList, ItemClickListener listener) {
        this.listener = listener;
        groupList = userGroupPartialList.getGroepen();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupAdapter.GroupAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.group_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new GroupAdapterViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapterViewHolder holder, int position) {
        holder.bind(position);


        // alternate row colors
        if(position %2 == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
        }
        // Todo if active group turn different color.

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return groupList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class GroupAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tv_groupListName;
        final ImageView iv_edit;
        final ImageView iv_delete;
        private WeakReference<ItemClickListener> listenerRef;

        GroupAdapterViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            tv_groupListName = itemView.findViewById(R.id.tv_group_list_item_name);
            iv_edit = itemView.findViewById(R.id.iv_group_list_item_edit);
            iv_delete = itemView.findViewById(R.id.iv_group_list_item_delete);

            listenerRef = new WeakReference(listener);

            itemView.setOnClickListener(this);
            iv_delete.setOnClickListener(this);
            iv_edit.setOnClickListener(this);

        }

        void bind(int position) {
            GroupPartial group = groupList.get(position);
            tv_groupListName.setText(group.getName());
        }


        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
            if(v.getId() == iv_delete.getId()) {
                deleteItem(v);
            } else if(v.getId() == iv_edit.getId()) {

            } else {
                // click on row. Make this active group.
            }
            Toast.makeText(v.getContext(), "clicked "  + getAdapterPosition(), Toast.LENGTH_LONG);
        }
    }

    private void deleteItem(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
        dialogBuilder.setMessage("message");
        dialogBuilder.setTitle("alert");
        dialogBuilder.setNeutralButton("btn ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialogBuilder.setNegativeButton("btn nee ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
