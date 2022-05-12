import Flutter
import UIKit

public class SwiftScreenshotObserverPlugin: NSObject, FlutterPlugin {
    
    static var channel: FlutterMethodChannel?
    static var observer: NSObjectProtocol?
    
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        channel  = FlutterMethodChannel(name: "screenshot_observer", binaryMessenger: registrar.messenger())
        
        let instance = SwiftScreenshotObserverPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel!)
        
        NotificationCenter.default.addObserver(
            forName: UIApplication.userDidTakeScreenshotNotification,
            object: nil,
            queue: .main) { notification in
                SwiftScreenshotObserverPlugin.channel?.invokeMethod("onScreenshot", arguments: nil)
            }
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if(call.method == "initialize"){
            
            result("initialize")
        }else if(call.method == "dispose"){
            NotificationCenter.default.removeObserver(self);
            result("dispose")
        }else{
            result("")
        }
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self);
    }
}
