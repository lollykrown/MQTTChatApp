package com.lollykrown.mqttchatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public final static String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;

    // Views
    private RecyclerView mMessageRecyclerView;
    private MessageAdapter mMessageAdapter;
    private EditText mMessageEditText;
    private Button mSendButton;

    // LocalBroadcastManager for the Activity
    private LocalBroadcastManager mLocalBroadcastManager;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessageEditText = findViewById(R.id.messageEditText);
        mMessageRecyclerView = findViewById(R.id.messageRv);
        mSendButton = findViewById(R.id.sendButton);

        // Initialize message RecyclerView and its adapter
        final ArrayList<ChatMessage> friendlyMessages = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager (this);
        mMessageRecyclerView.setLayoutManager(mLayoutManager);
        mMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
                mMessageRecyclerView.setAdapter(mMessageAdapter);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                mMessageRecyclerView.setAdapter(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
