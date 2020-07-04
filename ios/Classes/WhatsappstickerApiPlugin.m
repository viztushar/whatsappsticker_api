#import "WhatsappstickerApiPlugin.h"
#if __has_include(<whatsappsticker_api/whatsappsticker_api-Swift.h>)
#import <whatsappsticker_api/whatsappsticker_api-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "whatsappsticker_api-Swift.h"
#endif

@implementation WhatsappstickerApiPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftWhatsappstickerApiPlugin registerWithRegistrar:registrar];
}
@end
