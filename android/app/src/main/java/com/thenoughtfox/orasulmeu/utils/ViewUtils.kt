package com.thenoughtfox.orasulmeu.utils

import android.view.View
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
