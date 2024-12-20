package com.example.fivebetserio.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fivebetserio.model.Match;
import com.example.fivebetserio.R;


import java.util.List;

public class MatchesRecyclerAdapter extends RecyclerView.Adapter<MatchesRecyclerAdapter.ViewHolder> {

    private int layout;
    private List<Match> matchesList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewHomeTeam;
        private final TextView textViewAwayTeam;
        private final TextView textViewDate;
        private final TextView textViewUno;
        private final TextView textViewX;
        private final TextView textViewDue;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textViewHomeTeam = view.findViewById(R.id.homeTeam);
            textViewAwayTeam = view.findViewById(R.id.awayTeam);
            textViewDate = view.findViewById(R.id.date);
            textViewUno = view.findViewById(R.id.quota1);
            textViewX = view.findViewById(R.id.quota2);
            textViewDue = view.findViewById(R.id.quota3);
        }

        public TextView getTextViewHomeTeam() {
            return textViewHomeTeam;
        }
        public TextView getTextViewAwayTeam() {
            return textViewAwayTeam;
        }
        public TextView getTextViewDate() {
            return textViewDate;
        }

        public TextView getTextViewUno() {
            return textViewUno;
        }

        public TextView getTextViewX() {
            return textViewX;
        }

        public TextView getTextViewDue() {
            return textViewDue;
        }
    }

    public MatchesRecyclerAdapter(int layout, List<Match> matchesList) {
        this.layout = layout;
        this.matchesList = matchesList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //popolo il layout 'item_game' con i dati
        viewHolder.getTextViewHomeTeam().setText(matchesList.get(position).getHome_team());
        viewHolder.getTextViewAwayTeam().setText(matchesList.get(position).getAway_team());
        viewHolder.getTextViewDate().setText(matchesList.get(position).getCommence_time());
        viewHolder.getTextViewUno().setText("1\n" + matchesList.get(position).getBookmakers().get(0).getMarkets().get(0).getOutcomes().get(1).getPrice());
        viewHolder.getTextViewX().setText("X\n" + matchesList.get(position).getBookmakers().get(0).getMarkets().get(0).getOutcomes().get(2).getPrice());
        viewHolder.getTextViewDue().setText("2\n" + matchesList.get(position).getBookmakers().get(0).getMarkets().get(0).getOutcomes().get(0).getPrice());
    }


    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}

