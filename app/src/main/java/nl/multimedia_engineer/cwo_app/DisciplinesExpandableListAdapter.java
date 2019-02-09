package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;

public class DisciplinesExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Diploma> diplomaList;

    public DisciplinesExpandableListAdapter(Context context, List<Diploma> diplomas) {
        this.context = context;
        diplomaList = diplomas;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return diplomaList.get(groupPosition).getDiplomaEis().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final DiplomaEis diplomaEis = (DiplomaEis) getChild(groupPosition, childPosition);
        final String childText = diplomaEis.getTitel();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_view_item_exam_demands, null);
        }

        TextView tvExamDemandsTitle = convertView.findViewById(R.id.lblListItem);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tvExamDemandsText = (TextView) view.findViewById(R.id.tv_exam_demands_text);
                if(tvExamDemandsText.getVisibility() == View.GONE) {
                    tvExamDemandsText.setText(diplomaEis.getTekst());
                    tvExamDemandsText.setVisibility(View.VISIBLE);
                } else {
                    tvExamDemandsText.setText("");
                    tvExamDemandsText.setVisibility(View.GONE);
                }
            }
        });

        tvExamDemandsTitle.setText(diplomaEis.getTitel());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(diplomaList.get(groupPosition).getDiplomaEis() == null) {
            return 0;
        }
        return diplomaList.get(groupPosition).getDiplomaEis().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return diplomaList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return diplomaList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Diploma diploma = (Diploma) getGroup(groupPosition);
        String headerTitle = diploma.getId();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_view_header_sub_disciplines, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}