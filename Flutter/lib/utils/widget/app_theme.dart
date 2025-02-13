// ignore: file_names
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class AppTheme {
  static ThemeData light() {
    return ThemeData(
        primaryColor: Colors.cyan,
        fontFamily: 'shabnam',
        colorScheme:
            ColorScheme.fromSeed(seedColor: Colors.cyan, primary: Colors.white),
        appBarTheme: AppBarTheme(backgroundColor: Colors.grey.shade900),
        textTheme: TextTheme(
          bodySmall: TextStyle(
              color: Colors.white, fontFamily: 'shabnam', fontSize: 12.sp),
          bodyMedium: TextStyle(
              color: Colors.white, fontFamily: 'shabnam', fontSize: 14.sp),
          bodyLarge: TextStyle(
              color: Colors.white, fontFamily: 'shabnam', fontSize: 16.sp),
          titleSmall: TextStyle(
              color: Colors.grey, fontFamily: 'shabnam', fontSize: 12.sp),
          titleMedium: TextStyle(
              color: Colors.grey, fontFamily: 'shabnam', fontSize: 14.sp),
          titleLarge: TextStyle(
              color: Colors.grey, fontFamily: 'shabnam', fontSize: 16.sp),
        ),
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ButtonStyle(
            side: const WidgetStatePropertyAll<BorderSide>(BorderSide.none),
            textStyle: WidgetStatePropertyAll<TextStyle>(
              TextStyle(
                  fontFamily: 'shabnam', color: Colors.white, fontSize: 12.sp),
            ),
            backgroundColor: const WidgetStatePropertyAll<Color>(Colors.cyan),
          ),
        ),
        inputDecorationTheme: InputDecorationTheme(
          enabledBorder: OutlineInputBorder(
              borderSide: const BorderSide(color: Colors.grey),
              borderRadius: BorderRadius.circular(10.0)),
          focusedBorder: OutlineInputBorder(
              borderSide: const BorderSide(color: Colors.cyan),
              borderRadius: BorderRadius.circular(10.0)),
          errorBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(10.0),
            borderSide: const BorderSide(color: Colors.red),
          ),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(10.0),
          ),
          errorStyle: const TextStyle(color: Colors.red),
        ),
        scaffoldBackgroundColor: Colors.grey.shade900,
        cardColor: Colors.grey.shade700);
  }

  static ThemeData dark() {
    return ThemeData.dark().copyWith(
      appBarTheme: const AppBarTheme(backgroundColor: Colors.black),
      textTheme: TextTheme(
        bodySmall: TextStyle(
            color: Colors.white, fontFamily: 'shabnam', fontSize: 12.sp),
        bodyMedium: TextStyle(
            color: Colors.white, fontFamily: 'shabnam', fontSize: 14.sp),
        bodyLarge: TextStyle(
            color: Colors.white, fontFamily: 'shabnam', fontSize: 16.sp),
        titleSmall: TextStyle(
            color: Colors.grey, fontFamily: 'shabnam', fontSize: 12.sp),
        titleMedium: TextStyle(
            color: Colors.grey, fontFamily: 'shabnam', fontSize: 14.sp),
        titleLarge: TextStyle(
            color: Colors.grey, fontFamily: 'shabnam', fontSize: 16.sp),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ButtonStyle(
          backgroundColor: WidgetStatePropertyAll<Color>(Colors.red.shade300),
          textStyle: WidgetStatePropertyAll<TextStyle>(
            TextStyle(
                fontFamily: 'shabnam', color: Colors.black, fontSize: 12.sp),
          ),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        enabledBorder: OutlineInputBorder(
            borderSide: const BorderSide(color: Colors.grey),
            borderRadius: BorderRadius.circular(10.0)),
        focusedBorder: OutlineInputBorder(
            borderSide: BorderSide(color: Colors.purple.shade100),
            borderRadius: BorderRadius.circular(10.0)),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10.0),
          borderSide: const BorderSide(color: Colors.red),
        ),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10.0),
        ),
      ),
    );
  }
}
