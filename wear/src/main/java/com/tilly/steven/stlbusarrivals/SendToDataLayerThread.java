package com.tilly.steven.stlbusarrivals;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Steven on 2016-02-26.
 */
public class SendToDataLayerThread extends Thread {
    private String path;
    private String message;
    private GoogleApiClient googleClient;

    // Constructor to send a message to the data layer
    SendToDataLayerThread(GoogleApiClient googleClient, String p, String msg) {
        path = p;
        message = msg;
        this.googleClient = googleClient;
    }

    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
        Log.v("wearThread", "inside SendData ");
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
            if (result.getStatus().isSuccess()) {
                Log.v("wearThread", "Message: {" + message + "} sent to: " + node.getDisplayName());
            } else {
                // Log an error
                Log.v("wearThread", "ERROR: failed to send Message");
            }
        }
    }
}
