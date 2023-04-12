package com.example.fypnewversion

import android.content.Context
import android.graphics.Typeface

public class FontManager {
    companion object {
        val ROOT = "fonts/"
        val FONTAWESOME = ROOT + "fa-regular-400.ttf"

        fun getTypeface(context: Context, font: String?): Any {
            return Typeface.createFromAsset(context.getAssets(), font)
        }
    }
}