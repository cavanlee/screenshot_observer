package com.instapme.screenshot_observer

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import androidx.lifecycle.*
import com.supernovel.screenshot_observer.ScreenshotObserver

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** ScreenshotObserverPlugin */
class ScreenshotObserverPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, LifecycleEventObserver {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var handler: Handler
  private lateinit var context: Context
  private lateinit var screenshotObserver: ScreenshotObserver

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "screenshot_observer")
    channel.setMethodCallHandler(this)
    handler = Handler(Looper.getMainLooper())
    context = flutterPluginBinding.getApplicationContext()
    screenshotObserver = ScreenshotObserver(context.contentResolver, object : ScreenshotObserver.Listener {
      override fun onScreenshot(path: String?) {
        channel.invokeMethod("onScreenshot", null)
      }
    })

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method.equals("initialize")) {
      screenshotObserver?.observe()
      result.success("initialize")
    } else if (call.method.equals("dispose")) {
      screenshotObserver?.unobserve()
      result.success("dispose")
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    ProcessLifecycleOwner.get().lifecycle.addObserver(this)

  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
  }


  override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    if (event == Lifecycle.Event.ON_STOP) {
      screenshotObserver?.unobserve()
    } else if (event == Lifecycle.Event.ON_START) {
      screenshotObserver?.observe()
    }
  }
}
