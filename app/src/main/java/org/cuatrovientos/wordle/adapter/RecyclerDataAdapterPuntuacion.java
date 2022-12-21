package org.cuatrovientos.wordle.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.wordle.R;
import org.cuatrovientos.wordle.activities.MainActivity;
import org.cuatrovientos.wordle.model.Palabra;
import org.cuatrovientos.wordle.model.Puntuacion;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerDataAdapterPuntuacion extends RecyclerView.Adapter<RecyclerDataAdapterPuntuacion.RecyclerDataHolder>{

    List<Puntuacion> listPuntuacion;
    public RecyclerDataAdapterPuntuacion(List<Puntuacion> listPuntuacion){
        this.listPuntuacion = listPuntuacion;
    }


    @NonNull
    @Override
    public RecyclerDataAdapterPuntuacion.RecyclerDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_puntuaciones,parent,false);
        return new RecyclerDataHolder(view);

    }

    Integer puesto = 0;
    @Override
    public void onBindViewHolder(@NonNull RecyclerDataAdapterPuntuacion.RecyclerDataHolder holder, int position) {
        this.puesto+=1;
        holder.asignData(listPuntuacion.get(position),puesto);
    }

    @Override
    public int getItemCount() {
        return listPuntuacion.size();
    }

    public class RecyclerDataHolder extends RecyclerView.ViewHolder {
        //Elementos del itemview
        TextView txtPuesto,txtJugador,txtPuntos,txtFecha;


        public RecyclerDataHolder(@NonNull View itemView) {
            super(itemView);
            txtPuesto = itemView.findViewById(R.id.txtPuesto);
            txtJugador = itemView.findViewById(R.id.txtJugador);
            txtPuntos = itemView.findViewById(R.id.txtPuntos);
            txtFecha = itemView.findViewById(R.id.txtFecha);

        }
        public void asignData(Puntuacion pun, Integer pue){
            txtPuesto.setText(pue.toString()+"º");
            txtJugador.setText(pun.getJugador());
            Integer puntos = pun.getPuntos();
            txtPuntos.setText(puntos.toString());
            Date fecha = pun.getFecha();
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            txtFecha.setText(df.format("yyyy-MM-dd hh:mm:ss", fecha));
        }

    }
    /*public interface OnItemClickListener{
        void onItemClick(Tweet tw);
    }*/
}
