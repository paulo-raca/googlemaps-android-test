package example.maptest;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This activity tests some feature of the GoogleMaps Android SDK
 *
 * However, I found a few issues when running it under microG: https://github.com/microg/android_packages_apps_GmsCore/issues/1118
 *
 * Comments starting with `Xxx:` highlight microG bugs
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final double EARTH_RADIUS = 6371000; // meters
    private static final double METER = 360 / (EARTH_RADIUS * 2 * Math.PI);  // ~1m in degress
    CompletableFuture<GoogleMap> mMap = new CompletableFuture<>();
    List<Marker> mMarkers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Add a few markers for testing
        mMarkers = Arrays.asList(
                map.addMarker(new MarkerOptions()
                        .draggable(true)  // XXX: I couldn't make dragging work
                        .zIndex(999)
                        .title("Unicamp")    // XXX: I couldn't make the marker info box show up, so the title/snippet is never shown
                        .snippet("A large university")
                        .position(new LatLng(-22.817104, -47.069740))
                        .icon(BitmapDescriptorFactory.defaultMarker())),    // XXX: Markers are only visible if the icon is set explicitly
                map.addMarker(new MarkerOptions()
                        .zIndex(-999)
                        .title("Castelo")
                        .snippet("A crapy touristic landmark")
                        .position(new LatLng(-22.890070, -47.076887))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))),
                map.addMarker(new MarkerOptions()
                        .title("Capybaras")
                        .snippet("Taquaral Lake is a nice park, however full of capybaras")
                        .position(new LatLng(-22.875244, -47.056825))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))),
                map.addMarker(new MarkerOptions()
                        .title("Even more Capybaras")
                        .snippet("Coffee Lake is a nice, however abandoned park, currently taken by capybaras")
                        .position(new LatLng(-22.871080, -47.047662))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

        // Add a few shapes for testing
        // Xxx: Markers should always be in front of shapes

        float radius1 = 100;
        float radius2 = 150;
        for (Marker marker : mMarkers) {
            map.addCircle(new CircleOptions()
                    .center(marker.getPosition())
                    .radius(75) // XXX microG always draws the cicles with the same radius, despite zoom level
                    .strokeColor(0xFFFF0000)
                    .fillColor(0x22FF0000));


            // Polygon / Polylines works fine :)
            PolygonOptions polygonOptions = new PolygonOptions()
                    .strokeColor(0xFF0000FF)
                    .fillColor(0x220000FF);


            int nsides = 4;
            for (int i=0; i<nsides; i++) {
                LatLng p1 = new LatLng(
                        marker.getPosition().latitude + radius1*METER*Math.cos(2*Math.PI*i/nsides),
                        marker.getPosition().longitude + radius1*METER*Math.sin(2*Math.PI*i/nsides));
                LatLng p2 = new LatLng(
                        marker.getPosition().latitude + radius2*METER*Math.cos(2*Math.PI*i/nsides),
                        marker.getPosition().longitude + radius2*METER*Math.sin(2*Math.PI*i/nsides));

                polygonOptions.add(p2);

                PolylineOptions polylineOptions = new PolylineOptions()
                        .color(0xFFFFFFFF)
                        .add(p1, p2);
                map.addPolyline(polylineOptions);
            }
            map.addPolygon(polygonOptions);

        }


        mMap.complete(map);
    }
}