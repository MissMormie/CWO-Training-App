package nl.multimedia_engineer.cwo_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

    public interface GroupItemClickListener {
        void onItemClicked(int position);
        void onItemEditClicked(int position);
        void onItemDeleteClicked(int position);
    }

    private final List<GroupPartial> groupList;
    private final GroupItemClickListener listener;
    private String currentActiveGroup;

    public GroupAdapter(UserGroupPartialList userGroupPartialList, GroupItemClickListener listener, String groupId) {
        this.currentActiveGroup = groupId;
        this.listener = listener;
        groupList = userGroupPartialList.getGroepen();
    }

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


    public void setCurrentActiveGroupId(String id) {
        currentActiveGroup = id;
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
        final ImageView iv_check;
        private WeakReference<GroupItemClickListener> listenerRef;

        GroupAdapterViewHolder(View itemView, GroupItemClickListener listener) {
            super(itemView);
            tv_groupListName = itemView.findViewById(R.id.tv_group_list_item_name);
            iv_edit = itemView.findViewById(R.id.iv_group_list_item_edit);
            iv_delete = itemView.findViewById(R.id.iv_group_list_item_delete);
            iv_check = itemView.findViewById(R.id.iv_group_list_item_check);

            listenerRef = new WeakReference(listener);

            itemView.setOnClickListener(this);
            iv_delete.setOnClickListener(this);
            iv_edit.setOnClickListener(this);
        }

        void bind(int position) {
            GroupPartial group = groupList.get(position);
            tv_groupListName.setText(group.getName());
            if(currentActiveGroup.equals(groupList.get(position).getId())) {
                iv_check.setVisibility(View.VISIBLE);
            } else {
                iv_check.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == iv_delete.getId()) {
                listener.onItemDeleteClicked(getAdapterPosition());
            } else if(v.getId() == iv_edit.getId()) {
                listener.onItemEditClicked(getAdapterPosition());
            } else {
                listener.onItemClicked(getAdapterPosition());
                // click on row. Make this active group.
            }
            Toast.makeText(v.getContext(), "clicked "  + getAdapterPosition(), Toast.LENGTH_LONG);
        }
    }

}
