package com.lollykrown.mqttchatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public final static String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;

    // Views
    private ListView listView;
    private MessageAdapter mMessageAdapter;
    private EditText mMessageEditText;
    private Button mSendButton;

    // LocalBroadcastManager for the Activity
    private LocalBroadcastManager mLocalBroadcastManager;

    EditText editText;
    Button button1;
    Button button2;
    final ArrayList<ChatMessage> friendlyMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subcribeToATopic();

        mMessageEditText = findViewById(R.id.messageEditText);
        listView = findViewById(R.id.messageRv);

        mSendButton = findViewById(R.id.sendButton);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        // Register with local action
        mLocalBroadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "Received message with action: " + intent.getAction());

                String msg = intent.getStringExtra(IntentExtras.MESSAGE);
                if (msg == null) {
                    Log.e(TAG, "Received null message from "
                            + MqttService.class.getSimpleName());
                    return;
                }

                ChatMessage friendlyMessage = new ChatMessage(msg);
                friendlyMessages.add(friendlyMessage);
                mMessageAdapter = new MessageAdapter(getApplicationContext(), friendlyMessages);
                listView.setAdapter(mMessageAdapter);
            }

        }, new IntentFilter(Actions.ACTION_RECEIVE_MESSAGE));


        // Send message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mMessageEditText.getText().toString().trim();

                // don't send if msg is empty
                if (msg.isEmpty()) return;

                mLocalBroadcastManager
                        .sendBroadcast(new Intent(Actions.ACTION_SEND_MESSAGE)
                                .putExtra(IntentExtras.MESSAGE, msg));

                //clear message after sent
                mMessageEditText.setText("");
            }

        });

        // Start service
        startService(new Intent(this, MqttService.class));

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Toast.makeText(MainActivity.this, "message Deleted", Toast.LENGTH_LONG).show();
                friendlyMessages.remove(position);
                mMessageAdapter.notifyDataSetChanged();
                return true;
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

//    protected void onSaveInstanceState(Bundle state) {
//        super.onSaveInstanceState(state);
//        // Save list state
//        listState = mLayoutManager.onSaveInstanceState();
//        state.putParcelable(LIST_STATE_KEY, listState);
//    }
//
//    protected void onRestoreInstanceState(Bundle state) {
//        super.onRestoreInstanceState(state);
//        // Retrieve list state and list/item positions
//        if(state != null)
//            listState = state.getParcelable(LIST_STATE_KEY);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (listState != null) {
//            mLayoutManager.onRestoreInstanceState(listState);
//        }
//    }

    public void subcribeToATopic(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog2, null);

        editText = dialogView.findViewById(R.id.edt_comment);
        button1 = dialogView.findViewById(R.id.buttonSubmit);
        button2 = dialogView.findViewById(R.id.buttonCancel);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                String topicSub = editText.getText().toString().trim();

                SharedPreferences sp = getApplicationContext().getSharedPreferences("topic_sp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("topic", topicSub);
                editor.apply();
                dialogBuilder.dismiss();

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void changeClientId(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        editText = dialogView.findViewById(R.id.edt_comment);
        button1 = dialogView.findViewById(R.id.buttonSubmit);
        button2 = dialogView.findViewById(R.id.buttonCancel);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                String clientID = editText.getText().toString().trim();

                SharedPreferences sp = getApplicationContext().getSharedPreferences("clientid_sp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("clientid", clientID);
                editor.apply();
                dialogBuilder.dismiss();

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clientid:
                changeClientId();
                return true;
            case R.id.clear:
                mMessageAdapter.clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
