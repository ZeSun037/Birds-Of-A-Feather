package com.example.birdsofafeather.nearby;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.StatusCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.Task;

public class MessageLogger implements MessagesClient {

    private MessagesClient mClient;

    public MessageLogger(MessagesClient mClient) {
        this.mClient = mClient;
    }

    @Override
    @NonNull
    public Task<Void> publish(@NonNull Message message) {
        Log.d("User: ", "Publishing a message.");
        return mClient.publish(message);
    }

    @Override
    @NonNull
    public Task<Void> publish(@NonNull Message message, @NonNull PublishOptions publishOptions) {
        Log.d("User: ", "Publishing a message with option " + publishOptions.toString());
        return mClient.publish(message, publishOptions);
    }

    @Override
    @NonNull
    public Task<Void> unpublish(@NonNull Message message) {
        Log.d("User: ", "Unpublishing a message.");
        return mClient.unpublish(message);
    }

    @Override
    @NonNull
    public Task<Void> subscribe(@NonNull MessageListener messageListener) {
        Log.d("User: ", "Subscribing to " + messageListener.toString());
        return mClient.subscribe(messageListener);
    }

    @Override
    @NonNull
    public Task<Void> subscribe(@NonNull MessageListener messageListener, @NonNull SubscribeOptions subscribeOptions) {
        Log.d("User: ", "Subscribing to " + messageListener.toString() + "with option " + subscribeOptions);
        return mClient.subscribe(messageListener, subscribeOptions);
    }

    @Override
    @NonNull
    public Task<Void> subscribe(@NonNull PendingIntent pendingIntent, @NonNull SubscribeOptions subscribeOptions) {
        Log.d("User: ", "Subscribing at " + pendingIntent + " with option " + subscribeOptions);
        return mClient.subscribe(pendingIntent, subscribeOptions);
    }

    @Override
    @NonNull
    public Task<Void> subscribe(@NonNull PendingIntent pendingIntent) {
        Log.d("User: ", "Subscribing at " + pendingIntent);
        return mClient.subscribe(pendingIntent);
    }

    @Override
    @NonNull
    public Task<Void> unsubscribe(@NonNull MessageListener messageListener) {
        Log.d("User: ", "Unsubscribing " + messageListener);
        return mClient.unsubscribe(messageListener);
    }

    @Override
    @NonNull
    public Task<Void> unsubscribe(@NonNull PendingIntent pendingIntent) {
        Log.d("User ", "Unsubscribing at " + pendingIntent);
        return mClient.unsubscribe(pendingIntent);
    }

    @Override
    @NonNull
    public Task<Void> registerStatusCallback(@NonNull StatusCallback statusCallback) {
        Log.d("MessageClient ", "registers " + statusCallback);
        return mClient.registerStatusCallback(statusCallback);
    }

    @Override
    @NonNull
    public Task<Void> unregisterStatusCallback(@NonNull StatusCallback statusCallback) {
        Log.d("MessageClient ", "unregisters " + statusCallback);
        return mClient.unregisterStatusCallback(statusCallback);
    }

    @Override
    public void handleIntent(@NonNull Intent intent, @NonNull MessageListener messageListener) {
        Log.d("Client ", "handling intent: " + intent + " at " + messageListener);
        mClient.handleIntent(intent, messageListener);
    }

    @NonNull
    @Override
    public ApiKey<MessagesOptions> getApiKey() {
        Log.d("User: ", "Retrieving API Key.");
        return mClient.getApiKey();
    }
}