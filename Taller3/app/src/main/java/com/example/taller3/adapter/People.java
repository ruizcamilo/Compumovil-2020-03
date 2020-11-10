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
    private final List<LatLng> ubicaciones;


    private BtnClickListener mClickListener = null;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;

    public People(Activity context, List<String> names, List<String> apeds, List<String> idents, List<String> mails, List<LatLng> locations, BtnClickListener listener) {
        super(context, R.layout.list_item, mails);

        this.context=context;
        this.nombres=names;
        this.apellidos=apeds;
        this.ids=idents;
        this.emails = mails;
        this.ubicaciones = locations;
        this.mClickListener = listener;
    }

    /*public View getView(int position,View view,ViewGroup parent) {

        final ViewHolderPublicacion holder;
        if(view==null){

            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.list_item, parent, false);

            holder = new ViewHolderPublicacion();
            holder.layout = view.findViewById(R.id.layoutPublicacion);
            holder.nomb = (TextView) view.findViewById(R.id.tittles);
            holder.descrip = (TextView) view.findViewById(R.id.descripcion);
            holder.imgPerf = (ImageView) view.findViewById(R.id.icon);
            holder.imgEv = (ImageView) view.findViewById(R.id.imagevento);

            view.setTag(holder);

        }else{
            holder = (ViewHolderPublicacion) view.getTag();
        }

        holder.nomb.setText(nombre[position]);
        holder.imgPerf.setImageResource(imgperfil[position]);
        holder.descrip.setText(descripcion[position]);
        holder.imgEv.setImageResource(imgevento[position]);

        Button map = (Button) view.findViewById(R.id.meet);
        map.setTag(position); //For passing the list item index
        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mClickListener != null)
                    mClickListener.onBtnClick((Integer) v.getTag());
            }
        });
        final ColorStateList colorViejo = holder.nomb.getTextColors();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[0] < 5000) {
                    Log.i("THEME", "DARK THEME " + event.values[0]);
                    holder.layout.setBackgroundResource(R.color.dark_bg);
                    holder.nomb.setTextColor(context.getResources().getColor(R.color.blanco));
                    holder.descrip.setTextColor(context.getResources().getColor(R.color.blanco));

                } else {
                    Log.i("THEME", "LIGHT THEME " + event.values[0]);
                    holder.layout.setBackgroundResource(R.color.blanco);
                    holder.nomb.setTextColor(colorViejo);
                    holder.descrip.setTextColor(colorViejo);

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        return view;


    };

    static class ViewHolderPublicacion {
        RelativeLayout layout;
        TextView nomb;
        TextView descrip;
        ImageView imgPerf;
        ImageView imgEv;
    }*/
}

