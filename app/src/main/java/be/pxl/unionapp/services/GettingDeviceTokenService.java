package be.pxl.unionapp.services;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class GettingDeviceTokenService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

    }
}
