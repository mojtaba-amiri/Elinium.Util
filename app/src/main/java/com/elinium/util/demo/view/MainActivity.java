package com.elinium.util.demo.view;

import android.arch.lifecycle.LifecycleActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.elinium.util.demo.R;
import com.elinium.util.demo.items.SampleItem;
import com.elinium.util.exceptionhandling.ExceptionHandler;
import com.elinium.util.ui.broadcast.OnBroadcastReceived;
import com.elinium.util.ui.broadcast.BroadcastListener;
import com.elinium.util.ui.recyclerview.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends LifecycleActivity implements ExceptionHandler.IExceptionHandler {
    public final String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private BaseAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        loadList();
//        List<Class<? extends ERepository>> repos = new ArrayList<>();
//        repos.add(UserRepository.class);
//        RepositoryManager.init(repos);


//        try {
//            DexFile df = new DexFile(getApplicationContext().getPackageCodePath());
//            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
//                Log.d("MainActivity", "Class:" + iter.nextElement());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Log.d("MainActivity", "INIT START");
        BroadcastListener.initialize(this);
        ExceptionHandler.register(this);
        Log.d("MainActivity", "INIT END");
    }

    private void loadList() {
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        adapter = new BaseAdapter();
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        List<SampleItem> items = new ArrayList();
        for (int i = 0; i < 20000; i++)
            adapter.add(new SampleItem(i, " item #" + i));
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onException(String threadName, Throwable throwable) {

    }

    @OnBroadcastReceived(actionName = BluetoothAdapter.ACTION_STATE_CHANGED)
    public void onBleTurnedOff(Intent intent) {
        Log.d("SampleActivity", "method called. state:" + intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1));
    }
}
