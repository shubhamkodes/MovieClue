package com.itsthetom.movieclue.utils;

import android.graphics.Color;
import android.text.Layout
import android.view.View
import android.view.Window

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView;

import com.itsthetom.movieclue.R;
import com.jama.carouselview.CarouselView;

object  TempUtil {


    fun hideSystemUI(window: Window) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor= Color.TRANSPARENT
    }

    fun showSystemUI(window:Window,view: View) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, view).show(
            WindowInsetsCompat.Type.systemBars())
    }

}
