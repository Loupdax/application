package com.example.applicationlogin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PoubelleAdapter extends RecyclerView.Adapter<PoubelleAdapter.PoubelleViewHolder> {

    private List<Poubelle> poubelles;

    public PoubelleAdapter(List<Poubelle> poubelles) {
        this.poubelles = poubelles;
    }

    @NonNull
    @Override
    public PoubelleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poubelle, parent, false);
        return new PoubelleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PoubelleViewHolder holder, int position) {
        Poubelle poubelle = poubelles.get(position);


        holder.tvAdresse.setText(poubelle.getAdresse() + " - " + poubelle.getVille());

        holder.tvType.setText("ID Poubelle : " + poubelle.getId());


        holder.itemView.setOnClickListener(v -> {

            if (poubelle.getDateVisu() == null || poubelle.getDateVisu().isEmpty()) {
                Toast.makeText(v.getContext(), "Veuillez choisir une date d'abord", Toast.LENGTH_SHORT).show();
                return;
            }


            Intent intent = new Intent(v.getContext(), GraphActivity.class);
            intent.putExtra("ID_POUBELLE", poubelle.getId());


            intent.putExtra("DATE_MESURE", poubelle.getDateVisu());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return poubelles.size();
    }

    static class PoubelleViewHolder extends RecyclerView.ViewHolder {
        TextView tvAdresse, tvType;

        public PoubelleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdresse = itemView.findViewById(R.id.tv_adresse);
            tvType = itemView.findViewById(R.id.tv_type);
        }
    }
}