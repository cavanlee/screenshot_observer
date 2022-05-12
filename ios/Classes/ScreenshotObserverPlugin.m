#import "ScreenshotObserverPlugin.h"
#if __has_include(<screenshot_observer/screenshot_observer-Swift.h>)
#import <screenshot_observer/screenshot_observer-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "screenshot_observer-Swift.h"
#endif

@implementation ScreenshotObserverPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftScreenshotObserverPlugin registerWithRegistrar:registrar];
}
@end
