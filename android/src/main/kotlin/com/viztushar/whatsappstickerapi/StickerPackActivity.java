package com.viztushar.whatsappstickerapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

import static com.viztushar.whatsappstickerapi.WhatsappstickerApiPlugin.ADD_PACK;
import static com.viztushar.whatsappstickerapi.WhatsappstickerApiPlugin.EXTRA_STICKER_PACK_AUTHORITY;
import static com.viztushar.whatsappstickerapi.WhatsappstickerApiPlugin.EXTRA_STICKER_PACK_ID;
import static com.viztushar.whatsappstickerapi.WhatsappstickerApiPlugin.EXTRA_STICKER_PACK_NAME;

public class StickerPackActivity implements PluginRegistry.ActivityResultListener {
    Result result;
    String TAG = StickerPackActivity.class.getSimpleName();
    private Context context;

    StickerPackActivity(Result result, @NonNull Context context) {
        this.result = result;
        this.context = context;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PACK) {
            if (resultCode == Activity.RESULT_CANCELED && data != null) {
                String validationError = data.getStringExtra("validation_error");
                if (validationError != null) {
                    if (result != null) {
                        result.error("100", "failed", validationError);
                    }
                    Log.e(TAG, "Validation failed:$validationError");
                }
            } else if (requestCode == Activity.RESULT_OK && data != null) {
                Bundle bundle = data.getExtras();
                if (bundle.containsKey("add_successful")) {
                    result.success("101");
                } else if (bundle.containsKey("already_added")) {
                    result.success("102");
                } else {
                    result.success("105");
                }
            }
        }
        return true;
    }


    static Intent createIntentToAddStickerPack(String authority, String identifier, String stickerPackName) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra(EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, authority);
        intent.putExtra(EXTRA_STICKER_PACK_NAME, stickerPackName);
        return intent;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ADD_PACK) {
//            if (resultCode == Activity.RESULT_CANCELED && data != null) {
//                String validationError = data.getStringExtra("validation_error");
//                if (validationError != null) {
//                    if (result != null) {
//                        result.error("100", "failed", validationError);
//                    }
//                    Log.e(TAG, "Validation failed:$validationError");
//                }
//            } else if (requestCode == Activity.RESULT_OK && data != null) {
//                Bundle bundle = data.getExtras();
//                if (bundle.containsKey("add_successful")) {
//                    result.success("101");
//                } else if (bundle.containsKey("already_added")) {
//                    result.success("102");
//                } else {
//                    result.success("105");
//                }
//            }
//        }
//    }
}
