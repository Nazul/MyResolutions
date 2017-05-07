/*
 * Copyright 2017 Mario Contreras <marioc@nazul.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mx.iteso.msc.myresolutions;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import mx.iteso.msc.myresolutions.dataaccess.Goal;

/**
 * Created by Mario_Contreras on 5/4/2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList<Goal> goals;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton ibChecked;
        TextView tvName;
        TextView tvCategory;
        TextView tvOptions;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.ibChecked = (ImageButton) itemView.findViewById(R.id.ibCheck);
            this.tvName = (TextView) itemView.findViewById(R.id.tvName);
            this.tvCategory = (TextView) itemView.findViewById(R.id.tvCategory);
            this.tvOptions = (TextView) itemView.findViewById(R.id.tvOptions);
        }
    }

    public CustomAdapter(Context context, ArrayList<Goal> data) {
        this.context = context;
        this.goals = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_element, parent, false);

        view.setOnClickListener(GoalsListFragment.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        ImageButton ibChecked = holder.ibChecked;
        TextView tvName = holder.tvName;
        TextView tvCategory = holder.tvCategory;
        TextView tvOptions = holder.tvOptions;

        if (goals.get(listPosition).isChecked()) {
            ibChecked.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_assignment_done));
        } else {
            ibChecked.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_assignment));
        }
        tvName.setText(goals.get(listPosition).getName());
        tvCategory.setText(goals.get(listPosition).getCategory());
        tvOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.tvOptions);
                popupMenu.inflate(R.menu.menu_goal);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }
}

// EOF
