package org.cuatrovientos.wordle.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.wordle.R;
import org.cuatrovientos.wordle.model.Palabra;

import java.util.List;

import io.realm.RealmResults;

public class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.RecyclerDataHolder>{

    List<Palabra> listPalabras;
    public RecyclerDataAdapter(List<Palabra> listPalabras){
        this.listPalabras = listPalabras;
    }


    @NonNull
    @Override
    public RecyclerDataAdapter.RecyclerDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new RecyclerDataHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerDataAdapter.RecyclerDataHolder holder, int position) {
        holder.asignData(listPalabras.get(position));
    }

    @Override
    public int getItemCount() {
        return listPalabras.size();
    }

    public class RecyclerDataHolder extends RecyclerView.ViewHolder {
        //Elementos del itemview
        TextView l1,l2,l3,l4,l5;

        public RecyclerDataHolder(@NonNull View itemView) {
            super(itemView);
            l1 = itemView.findViewById(R.id.txt1);
            l2 = itemView.findViewById(R.id.txt2);
            l3 = itemView.findViewById(R.id.txt3);
            l4 = itemView.findViewById(R.id.txt4);
            l5 = itemView.findViewById(R.id.txt5);

        }
        public void asignData(Palabra pal){
            Integer tmpNum;
            String[] palabra = pal.getPalabraStr().split("");
            l1.setText(palabra[0]);
            tmpNum = pal.getLetraResList().get(0);
            switch (tmpNum){
                case 0:
                    l1.setBackgroundResource(R.drawable.btnlet_layout_green);
                    break;
                case 1:
                    l1.setBackgroundResource(R.drawable.btnlet_layout_orange);
                    break;
                case 2:
                    l1.setBackgroundResource(R.drawable.btnlet_layout_grey);
                    break;
                default:
                    l1.setBackgroundResource(R.drawable.btnlet_layout);
                    break;
            }
            l2.setText(palabra[1]);
            tmpNum = pal.getLetraResList().get(1);
            switch (tmpNum){
                case 0:
                    l2.setBackgroundResource(R.drawable.btnlet_layout_green);
                    break;
                case 1:
                    l2.setBackgroundResource(R.drawable.btnlet_layout_orange);
                    break;
                case 2:
                    l2.setBackgroundResource(R.drawable.btnlet_layout_grey);
                    break;
                default:
                    l2.setBackgroundResource(R.drawable.btnlet_layout);
                    break;
            }
            l3.setText(palabra[2]);
            tmpNum = pal.getLetraResList().get(2);
            switch (tmpNum){
                case 0:
                    l3.setBackgroundResource(R.drawable.btnlet_layout_green);
                    break;
                case 1:
                    l3.setBackgroundResource(R.drawable.btnlet_layout_orange);
                    break;
                case 2:
                    l3.setBackgroundResource(R.drawable.btnlet_layout_grey);
                    break;
                default:
                    l3.setBackgroundResource(R.drawable.btnlet_layout);
                    break;
            }
            l4.setText(palabra[3]);
            tmpNum = pal.getLetraResList().get(3);
            switch (tmpNum){
                case 0:
                    l4.setBackgroundResource(R.drawable.btnlet_layout_green);
                    break;
                case 1:
                    l4.setBackgroundResource(R.drawable.btnlet_layout_orange);
                    break;
                case 2:
                    l4.setBackgroundResource(R.drawable.btnlet_layout_grey);
                    break;
                default:
                    l4.setBackgroundResource(R.drawable.btnlet_layout);
                    break;
            }
            l5.setText(palabra[4]);
            tmpNum = pal.getLetraResList().get(4);
            switch (tmpNum){
                case 0:
                    l5.setBackgroundResource(R.drawable.btnlet_layout_green);
                    break;
                case 1:
                    l5.setBackgroundResource(R.drawable.btnlet_layout_orange);
                    break;
                case 2:
                    l5.setBackgroundResource(R.drawable.btnlet_layout_grey);
                    break;
                default:
                    l5.setBackgroundResource(R.drawable.btnlet_layout);
                    break;
            }


        }

    }
    /*public interface OnItemClickListener{
        void onItemClick(Tweet tw);
    }*/
}
