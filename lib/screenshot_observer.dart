/*
 * @Author: Cavan.liyongwang
 * @Date: 2022-05-12 10:23:01
 * @Email: cavanvip@gmail.com
 * @Github: https://github.com/cavanlee
 * @LastEditors: Cavan
 * @LastEditTime: 2022-05-12 11:09:23
 * @Description: 
 */
import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

class ScreenshotObserver {
  static const MethodChannel _channel = MethodChannel('screenshot_observer');

  static final _listeners = <VoidCallback>[];

  static Future<void> initialize() async {
    if (Platform.isAndroid) {
      final isGranted = await Permission.storage.isGranted;

      if (!isGranted) {
        await Permission.storage.request();
      }
    }
    _channel.setMethodCallHandler(_handleMethod);
    await _channel.invokeMethod('initialize');
  }

  static void addListener(VoidCallback listener) {
    _listeners.add(listener);
  }

  static Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case 'onScreenshot':
        for (final listener in _listeners) {
          listener();
        }
        break;
      default:
        throw ('method not defined');
    }
  }

  /// Remove listeners.
  static Future<void> dispose() async {
    _listeners.clear();
    await _channel.invokeMethod('dispose');
  }
}
