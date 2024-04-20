package com.thenoughtfox.orasulmeu.utils

import android.app.Service
import android.view.View
import android.view.inputmethod.InputMethodManager
import dev.chrisbanes.insetter.applyInsetter

fun View.applyTopStatusInsetMargin() {
    applyInsetter {
        type(statusBars = true) {
            margin()
        }
    }
}

fun View.applyBottomInsetMargin() {
    applyInsetter {
        type(navigationBars = true) {
            margin()
        }
    }
}

fun View.hideKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(this.windowToken, 0)
}