package com.viztushar.whatsappstickerapi

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.fxn.stash.Stash
import com.viztushar.whatsappstickerapi.TestActivity.createIntentToAddStickerPack
import com.viztushar.whatsappstickerapi.mode.Sticker
import com.viztushar.whatsappstickerapi.mode.StickerPack
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.util.*

/** WhatsappstickerApiPlugin */
class WhatsappstickerApiPlugin() : FlutterPlugin, MethodCallHandler, ActivityAware {
    private var methodChannel: MethodChannel? = null
    private lateinit var context: Context
    private var activity: Activity? = null
    private lateinit var result: Result
    private var stickerPacks = ArrayList<StickerPack>()
    private val TAG = WhatsappstickerApiPlugin::class.java.simpleName
    private var pluginBinding: FlutterPluginBinding? = null
    private var activityBinding: ActivityPluginBinding? = null


    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            if (registrar.activity() == null) {
                // If a background flutter view tries to register the plugin, there will be no activity from the registrar,
                // we stop the registering process immediately because the ImagePicker requires an activity.
                return
            }
            val plugin = WhatsappstickerApiPlugin()
            plugin.activity = registrar.activity()
            plugin.setupChannels(registrar.messenger(), registrar.context())
            MethodChannel(registrar.messenger(), "com.viztushar.whatsappstickerapi.whatsappstickerapi/whatsappstickerapi").setMethodCallHandler(plugin)
        }

        const val EXTRA_STICKER_PACK_ID = "sticker_pack_id"
        const val EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority"
        const val EXTRA_STICKER_PACK_NAME = "sticker_pack_name"
        const val ADD_PACK = 200

        @JvmStatic
        fun getContentProviderAuthority(context: Context): String? {
            return context.packageName + ".stickercontentprovider"
        }
    }

//    private fun setup(
//            messenger: BinaryMessenger,
//            activity: Activity,
//            registrar: Registrar?,
//            activityBinding: ActivityPluginBinding?) {
//        this.activity = activity
//        context = registrar?.context()!!
//     methodChannel = MethodChannel(messenger, "com.viztushar.whatsappstickerapi.whatsappstickerapi/whatsappstickerapi")
//      methodChannel!!.setMethodCallHandler(this);
//        if (registrar != null) {
//            // V1 embedding setup for activity listeners.
//            registrar.addActivityResultListener(this)
//           } else {
//            // V2 embedding setup for activity listeners.
//            activityBinding?.addActivityResultListener(this)
//           }
//    }

    private fun setupChannels(messenger: BinaryMessenger, context: Context) {
        this.context = context
        methodChannel = MethodChannel(messenger, "com.viztushar.whatsappstickerapi.whatsappstickerapi/whatsappstickerapi")
        methodChannel?.setMethodCallHandler(this)

    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        this.result = result
        when (call.method) {
            "addTOJson" -> {
                addToJson(call.argument("identiFier"), call.argument("name"),
                        call.argument("publisher"), call.argument("trayimagefile"),
                        call.argument("publisheremail"), call.argument("publisherwebsite"),
                        call.argument("privacypolicywebsite"), call.argument("licenseagreementwebsite"),
                        Objects.requireNonNull(call.argument("sticker_image")),Objects.requireNonNull(call.argument("image_data_version")),Objects.requireNonNull(call.argument("avoid_cache")), result)
            }
            "addStickerPackToWhatsApp" -> {
                addStickerPackToWhatsApp(call.argument("identifier"), call.argument("name"))
            }
            else -> {
                result.notImplemented()
            }
        }
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
//        if (requestCode == ADD_PACK) {
//            if (resultCode == Activity.RESULT_CANCELED && data != null) {
//                val validationError = data.getStringExtra("validation_error")
//                if (validationError != null) {
//                    if (result != null) {
//                        result.error("100", "failed", validationError)
//                    }
//                    Log.e(TAG, "Validation failed:$validationError")
//                }
//            } else if (requestCode == Activity.RESULT_OK && data != null) {
//                val bundle: Bundle = data.extras
//                if (bundle.containsKey("add_successful")) {
//                    result.success("101")
//                } else if (bundle.containsKey("already_added")) {
//                    result.success("102")
//                } else {
//                    result.success("105")
//                }
//            }
//        }
//        return true
//    }

    private fun addToJson(identifier: String?, name: String?, publisher: String?,
                          tray_image_file: String?, publisher_email: String?, publisher_website: String?, privacy_policy_website: String?,
                          license_agreement_website: String?,
                          sticker: ArrayList<*>,imagedataversion: String?,avoidcache: Boolean, @NonNull result: Result) {
        Log.d(TAG, "addToJson: $tray_image_file")
        val stickers = ArrayList<Sticker>()
        for (i in sticker.indices) {
            stickers.add(Sticker(sticker[i].toString(), Arrays.asList(*"🙂,🙂".split(",".toRegex()).toTypedArray())))
        }
        val stickerPack = StickerPack(
                identifier,
                name,
                publisher,
                tray_image_file,
                publisher_email,
                publisher_website,
                privacy_policy_website,
                license_agreement_website, imagedataversion, avoidcache)
        stickerPack.setAndroidPlayStoreLink("https://play.google.com/store/apps/details?id=" + context.packageName.toString())
        stickerPack.setIosAppStoreLink("")
        stickerPack.setStickers(stickers)
        stickerPacks.add(stickerPack)
        Log.d(TAG, "addToJson: " + stickerPacks.get(0).stickers + " " + sticker.size)
        Stash.put("sticker_pack", stickerPacks)
        if (sticker.size == 3 || sticker.size > 3) {
            val intent = Intent()
            intent.action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
            intent.putExtra(EXTRA_STICKER_PACK_ID, identifier)
            intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, getContentProviderAuthority(context))
            intent.putExtra(EXTRA_STICKER_PACK_NAME, name)
            try {
                //activityBinding?.activity?.startActivityForResult(intent, ADD_PACK)
              val t = TestActivity(result,context)
              activityBinding?.addActivityResultListener(t);
              activity?.startActivityForResult(intent, ADD_PACK)
              result.success("success")
            } catch (e: java.lang.Exception) {

                Log.d(TAG, "addToJson: error" + e.message)
                val errorMessage = "Sticker pack not added. If you'd like to add it, make sure you update to the latest version of WhatsApp."
                result.error(errorMessage, "failed", e)


            }
        } else {
            Log.d(TAG, "addToJson: error")
            val toast = Toast.makeText(context, "You need 3 or more sticker per pack", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            result.error("need3stickerormore", "need 3 or more sticker", "You need 3 or more sticker per pack")
        }
    }

    fun addStickerPackToWhatsApp(identifier: String?, stickerPackName: String?) {
        try {
            //if neither WhatsApp Consumer or WhatsApp Business is installed, then tell user to install the apps.
            var p = activity?.packageManager
            activity?.packageManager?.let {
                if (!WhitelistCheck.isWhatsAppConsumerAppInstalled(it) && WhitelistCheck.isWhatsAppSmbAppInstalled(it)) {
                    Toast.makeText(context, "Sticker pack not added. If you\\'d like to add it, make sure you update to the latest version of WhatsApp.", Toast.LENGTH_LONG).show()
                    return
                }
                val stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(context, identifier!!)
                val stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(context, identifier)
                if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                    //ask users which app to add the pack to.
                    launchIntentToAddPackToChooser(identifier, stickerPackName)
                } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
                    launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME)
                } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                    launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME)
                } else {
                    Toast.makeText(context, "Sticker pack not added. If you\\'d like to add it, make sure you update to the latest version of WhatsApp.", Toast.LENGTH_LONG).show()

                    if(WhitelistCheck.isWhitelisted(context,identifier)){
                        launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME)
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "error adding sticker pack to WhatsApp", e)
            Toast.makeText(context, "Sticker pack not added. If you\\'d like to add it, make sure you update to the latest version of WhatsApp.", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchIntentToAddPackToSpecificPackage(identifier: String?, stickerPackName: String?, whatsappPackageName: String) {
        val intent = createIntentToAddStickerPack(getContentProviderAuthority(context),identifier, stickerPackName)
        intent.setPackage(whatsappPackageName)
        try { val t = TestActivity(result,context)
          activityBinding?.addActivityResultListener(t);
          activity?.startActivityForResult(intent, ADD_PACK)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Sticker pack not added. If you\\'d like to add it, make sure you update to the latest version of WhatsApp.", Toast.LENGTH_LONG).show()
        }
    }

    //Handle cases either of WhatsApp are set as default app to handle this intent. We still want users to see both options.
    private fun launchIntentToAddPackToChooser(identifier: String?, stickerPackName: String?) {
        val intent = createIntentToAddStickerPack( getContentProviderAuthority(context),identifier, stickerPackName)
        try {
          val t = TestActivity(result,context)
          activityBinding?.addActivityResultListener(t);
            activity?.startActivityForResult(Intent.createChooser(intent, "Add to WhatsApp"), ADD_PACK)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Sticker pack not added. If you\\'d like to add it, make sure you update to the latest version of WhatsApp.", Toast.LENGTH_LONG).show()
        }
    }




    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        pluginBinding = flutterPluginBinding

        setupChannels(flutterPluginBinding.binaryMessenger, flutterPluginBinding.applicationContext)

    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        pluginBinding = null
      tearDown()
    }

    override fun onDetachedFromActivity() {
        tearDown()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        context = binding.activity.applicationContext
      activityBinding = binding


    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    private fun tearDown() {

        activityBinding = null
        methodChannel?.setMethodCallHandler(null)
        methodChannel = null
    }

}
