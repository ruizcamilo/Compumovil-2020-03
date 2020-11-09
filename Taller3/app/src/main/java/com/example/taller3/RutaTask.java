package com.example.taller3;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RutaTask extends AsyncTask<Void, Integer, Boolean> {
    private static final String TOAST_MSG = "Calculando";
    private static final String TOAST_ERR_MAJ = "Imposible encontrar ruta";
    private Context context;
    private Geocoder mGeocoder;
    private GoogleMap gMap;
    private LatLng editFrom;
    private LatLng editTo;
    private final ArrayList<LatLng> lstLatLng = new ArrayList<LatLng>();

    public RutaTask(final Context context, final GoogleMap gMap, final LatLng editFrom, final LatLng editTo) {
        this.context = context;
        this.gMap = gMap;
        this.editFrom = editFrom;
        this.editTo = editTo;
        this.mGeocoder = new Geocoder(context);
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, TOAST_MSG, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            final StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/xml?sensor=false");
            url.append("&origin=");
            url.append(editFrom.latitude + "," + editFrom.longitude);
            url.append("&destination=");
            url.append(editTo.latitude + "," + editTo.longitude);
            url.append("&key=");
            url.append("@string/google_maps_key");
            System.out.println(url.toString());
            final InputStream stream = new URL(url.toString()).openStream();
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringComments(true);
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(stream);
            document.getDocumentElement().normalize();
            final String status = document.getElementsByTagName("status").item(0).getTextContent();
            if (!"OK".equals(status)) {
                return false;
            }
            final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
            final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
            final int length = nodeListStep.getLength();
            for (int i = 0; i < length; i++) {
                final Node nodeStep = nodeListStep.item(i);
                if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elementStep = (Element) nodeStep;
                    decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
                }
            }
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    private void decodePolylines(final String encodedPoints) {
        int index = 0;
        int lat = 0, lng = 0;
        while (index < encodedPoints.length()) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            lstLatLng.add(new LatLng((double) lat / 1E5, (double) lng / 1E5));
        }
    }

    private String geoCoderSearchLatLang(LatLng latLng) {
        String address = "";
        try{
            List<Address> Results = mGeocoder.getFromLocation(latLng.latitude,latLng.longitude, 2);
            if(Results != null && Results.size() > 0)
            {
                address = Results.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (!result) {
            Toast.makeText(context, TOAST_ERR_MAJ, Toast.LENGTH_SHORT).show();
            gMap.addMarker(new MarkerOptions().position(editTo).title(geoCoderSearchLatLang(editTo)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(editTo, 15));

        } else {
            final PolylineOptions polylines = new PolylineOptions();
            polylines.color(Color.parseColor("#EF6C00"));
            for (final LatLng latLng : lstLatLng) {
                polylines.add(latLng);
            }
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lstLatLng.get(lstLatLng.size() - 1), 15));
            //gMap.addMarker(new MarkerOptions().position(lstLatLng.get(0)).title(geoCoderSearchLatLang(lstLatLng.get(0))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            gMap.addPolyline(polylines);
            //gMap.addMarker(new MarkerOptions().position(lstLatLng.get(lstLatLng.size() - 1)).title(geoCoderSearchLatLang(lstLatLng.get(lstLatLng.size() - 1))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
    }
}
