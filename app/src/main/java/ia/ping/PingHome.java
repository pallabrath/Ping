package ia.ping;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ia.ping.sql.DBHelper;
import ia.ping.util.PingConstants;

public class PingHome extends AppCompatActivity {
    private Button add,save;
    private EditText phone, replyOn;
    private DBHelper mydb ;
    private ListView listview;
    private Switch autoReplySwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_home);
        phone = (EditText) findViewById(R.id.editText);
        replyOn = (EditText) findViewById(R.id.keyText);
        add = (Button) findViewById(R.id.button);
        save = (Button) findViewById(R.id.save);
        mydb = new DBHelper(this);
        listview = (ListView) findViewById(R.id.listView);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString() == null || "".equals((phone.getText().toString())))
                {
                    phone.setError("Phone no required.");

                }else {
                    boolean inserted = mydb.insertContact(phone.getText().toString(), phone.getText().toString());
                    //Toast.makeText(getApplicationContext(), "Adding "+inserted, Toast.LENGTH_SHORT).show();
                    ((StableArrayAdapter) listview.getAdapter()).add(phone.getText().toString());
                    ((StableArrayAdapter) listview.getAdapter()).notifyDataSetChanged();
                }
            }
        });
        String keyText = mydb.getConfigValue(PingConstants.REPLY_ON_CONFIG);
        if (keyText != null && !"" .equals(keyText))
        {
            replyOn.setText(keyText);
        }
        else
        {
            mydb.insertOrUpdateConfig(PingConstants.REPLY_ON_CONFIG, "where are you");
            replyOn.setText("where are you");
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyOntxt = replyOn.getText().toString();
                if (replyOntxt == null || "".equals((replyOntxt.toString())))
                {
                    replyOn.setError("Can not be empty");

                }else {
                    mydb.insertOrUpdateConfig(PingConstants.REPLY_ON_CONFIG, replyOntxt);
                }
            }
        });

        ArrayList<String> list = mydb.getAllContacts();
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = listview.getPositionForView(view);
                String toRemove = adapter.getItem(position);
                //Toast.makeText(getApplicationContext(), "removing "+toRemove, Toast.LENGTH_SHORT).show();
                mydb.deleteContact(toRemove);
                adapter.remove(toRemove);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        autoReplySwitch = (Switch) findViewById(R.id.switch1);
        String autoReplyOption = mydb.getConfigValue(PingConstants.AUTO_REPLY_CONFIG);
        if (autoReplyOption == null)
        {
            mydb.insertOrUpdateConfig(PingConstants.AUTO_REPLY_CONFIG, "true");
            autoReplySwitch.setChecked(true);
        }
        else {
            autoReplySwitch.setChecked(Boolean.parseBoolean(autoReplyOption));
        }
        autoReplySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mydb.insertOrUpdateConfig(PingConstants.AUTO_REPLY_CONFIG, "" + isChecked);
            }
        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
        private int i = 0;
        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public void add(String newItem)
        {
            super.add(newItem);
            mIdMap.put(newItem, ++i);
        }


    }

}



