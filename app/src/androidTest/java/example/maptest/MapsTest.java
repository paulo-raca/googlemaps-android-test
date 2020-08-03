package example.maptest;

import android.os.ConditionVariable;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static example.maptest.Util.runInMainThread;

@RunWith(AndroidJUnit4.class)
public class MapsTest {
    private static final String TAG = MapsTest.class.getSimpleName();
    @Rule
    public ActivityTestRule<MapsActivity> mActivityRule = new ActivityTestRule<>(MapsActivity.class);

    @Test
    public void testMap() throws Throwable {
        MapsActivity mapsActivity = mActivityRule.getActivity();
        GoogleMap map = mapsActivity.mMap.get(50, TimeUnit.SECONDS);

        Assert.assertTrue(waitFullRender(map).block(30000));
        for (Marker marker : mapsActivity.mMarkers) {
            runInMainThread(() -> {
                Log.i(TAG, "Next marker: " + marker.getTitle());
                marker.showInfoWindow();  // Xxx: No window is shown
            });

            Log.i(TAG, "Center marker");
            Assert.assertTrue(animateCamera(map, marker).block(30000));

            Log.i(TAG, "Wait render");
            Assert.assertTrue(waitFullRender(map).block(30000));

            Thread.sleep(1000);

            Log.i(TAG, "Wait render");
            runInMainThread(marker::hideInfoWindow);
        }
    }

    public ConditionVariable waitFullRender(GoogleMap map) throws Throwable {
        ConditionVariable ret = new ConditionVariable();
        runInMainThread(() -> {
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    Log.i(TAG, "render complete");
                    ret.open();
                }
            });
        });
        return ret;
    }

    public ConditionVariable animateCamera(GoogleMap map, Marker marker) throws Throwable {
        ConditionVariable ret = new ConditionVariable();
        runInMainThread(() -> {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15);
            map.animateCamera(update, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    Log.i(TAG, "Animation complete");
                    ret.open();
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, "Animation cancelled");
                    ret.open();
                }
            });
        });
        return ret;
    }
}
