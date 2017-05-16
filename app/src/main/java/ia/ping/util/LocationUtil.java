package ia.ping.util;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.List;

/**
 * Created by parath on 7/23/2016.
 */
public class LocationUtil {

    public static Location getLastKnownLocation(LocationManager mLocationManager) {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            Log.d("LocationUtil","last known location, provider: " + provider + "location: " + l);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                Log.d("LocationUtil","found best last known location: "+  l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    public static String getGoogleMapLink(Location loc)
    {
      // url format http://maps.google.com/?q=<lat>,<lng>
      String url = "http://maps.google.com/?q=";
      if (loc != null)
      {
          url +=loc.getLatitude() + "," + loc.getLongitude();
      }
      return url;
    }

}
