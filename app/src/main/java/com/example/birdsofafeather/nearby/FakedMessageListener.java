package com.example.birdsofafeather.nearby;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FakedMessageListener extends MessageListener {
    private MessageListener realMessageListener;

    public FakedMessageListener(MessageListener realMessageListener) {
        this.realMessageListener = realMessageListener;
    }

    @Override
    public void onFound(Message message) {
        this.realMessageListener.onFound(message);
    }

    @Override
    public void onLost(@NonNull Message message) {
        this.realMessageListener.onLost(message);
    }
}