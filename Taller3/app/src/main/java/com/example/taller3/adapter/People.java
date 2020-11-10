package com.example.taller3.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.taller3.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class People extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> nombres;
    private final List<String> apellidos;
    private final List<String> ids;
    private final List<String> emails;
    private final List<Double> latitudes;
    private final List<Double> longitudes;
    private final List<String> imagenes;

    private BtnClickListener mClickListener = null;

    public People(Activity context, List<String> names, List<String> apeds, List<String> idents, List<String> mails, List<Double> latitudes, List<Double> longitudes ,List<String> imagenes, BtnClickListener listener) {
        super(context, R.layout.list_item, mails);

        this.context=context;
        this.nombres=names;
        this.apellidos=apeds;
        this.ids=idents;
        this.emails = mails;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.imagenes = imagenes;
        this.mClickListener = listener;
    }

    public View getView(int position,View view,ViewGroup parent) {

        final ViewHolderPublicacion holder;
        if(view==null){

            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.list_item, parent, false);

            holder = new ViewHolderPublicacion();
            holder.layout = view.findViewById(R.id.layoutPersona);
            holder.nombre = (TextView) view.findViewById(R.id.nombre);
            holder.apellido = (TextView) view.findViewById(R.id.apellido);
            holder.id = (TextView) view.findViewById(R.id.identificacion);
            holder.mail =(TextView) view.findViewById(R.id.mail);
            holder.image = (ImageView) view.findViewById(R.id.image);

            view.setTag(holder);

        }else{
            holder = (ViewHolderPublicacion) view.getTag();
        }

        holder.nombre.setText(nombres.get(position));
        holder.apellido.setText(apellidos.get(position));
        holder.mail.setText(emails.get(position));
        holder.id.setText(ids.get(position));
        //holder.image.setImageResource(imagenes.get(position)); //Cómoooo

        Button map = (Button) view.findViewById(R.id.button);
        map.setTag(position); //For passing the list item index
        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mClickListener != null)
                    mClickListener.onBtnClick((Integer) v.getTag());
            }
        });

        return view;


    };

    static class ViewHolderPublicacion {
        LinearLayout layout;
        TextView nombre;
        TextView apellido;
        TextView id;
        TextView mail;
        ImageView image;
    }
}

