package ia.ping.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import ia.ping.sql.DBHelper;
import ia.ping.util.LocationUtil;
import ia.ping.util.PingConstants;

/**
 * Created by parath on 7/16/2016.
 */
public class SmsReceiver extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    private DBHelper mydb;
    private Context m_context;
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        mydb = new DBHelper(context);
        m_context = context;
        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    //String format = bundle.getString("format");
                    SmsMessage currentMessage = SmsMessage
                            .createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage
                            .getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: " + senderNum
                            + "; message: " + message);
                    // Show Alert
                    /*int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, "Test :senderNum: "
                            + senderNum + ", message: " + message, duration);
                    toast.show();*/

					/* parse and reply in another thread */
                    Thread parseThread = new SmsParseTask(senderNum,message,mydb);
                    parseThread.start();

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

    private class SmsParseTask extends Thread {
        private String sender;
        private String text;
        private DBHelper dw;
        public SmsParseTask(String sender, String text, DBHelper dw) {
            super();
            this.sender = sender;
            this.text = text;
            this.dw = dw;
        }

        @Override
        public void run() {
            if (dw.isRegisteredContact(sender) &&
                    "true".equals(dw.getConfigValue(PingConstants.AUTO_REPLY_CONFIG))
                    && text.toLowerCase().contains(dw.getConfigValue(PingConstants.REPLY_ON_CONFIG).toLowerCase()))
            {
                Log.i("SmsReceiver", "registered sender "+ sender);
                LocationManager locationManager = (LocationManager)
                        m_context.getSystemService(Context.LOCATION_SERVICE);
                Location location = LocationUtil.getLastKnownLocation(locationManager);
                Log.i("SmsReceiver","location = "+location);
                if (location != null ) {
                    String message = "Hey, My current location:"
                            + LocationUtil.getGoogleMapLink(location);
                    sms.sendTextMessage(sender, null, message, null, null);
                }
            }
            else
            {
                Log.i("SmsReceiver", "not registered sender "+ sender);;
            }
        }

    }

}
