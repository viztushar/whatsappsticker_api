import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

enum StickerPackResult {
  SUCCESS,
  ADDSUCCESSFUL,
  ALREADYADDED,
  CANCELLED,
  ERROR,
  UNKNOWN,
}

class WhatsappstickerApi {
  static const MethodChannel _channel = const MethodChannel(
      'com.viztushar.whatsappstickerapi.whatsappstickerapi/whatsappstickerapi');

  static Future<dynamic> addToJson(
      {@required String identiFier,
      @required String name,
      @required String publisher,
      @required String trayimagefile,
      @required String publisheremail,
      @required String publisherwebsite,
      @required String privacypolicywebsite,
      @required String licenseagreementwebsite,
      @required String imagedataversion,
      bool avoidcache = false,
      @required List<String> stickerImages}) async {
        print(imagedataversion);
        print(avoidcache);
    try {
      var result = await _channel.invokeMapMethod("addTOJson", {
        "identiFier": identiFier,
        "name": name,
        "publisher": publisher,
        "trayimagefile": trayimagefile,
        "publisheremail": publisheremail,
        "publisherwebsite": publisherwebsite,
        "privacypolicywebsite": privacypolicywebsite,
        "licenseagreementwebsite": licenseagreementwebsite,
        "image_data_version": imagedataversion,
        "avoid_cache": avoidcache,
        "sticker_image": stickerImages,
      });
      print(result);
      return result;
    } on PlatformException catch (e) {
      FlutterError(e.toString());
    }
  }

  static Future<void> addStickerPackToWhatsApp(
      {@required String identifier, @required String name}) async {
    try {
      await _channel.invokeMapMethod(
          "addStickerPackToWhatsApp", {"identifier": identifier, "name": name});
    } on PlatformException catch (e) {
      FlutterError(e.toString());
    }
  }
}
