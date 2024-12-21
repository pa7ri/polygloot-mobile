package com.polygloot.mobile.android.ui.theme

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.polygloot.mobile.android.R

@Composable
fun PolyglootAnimation(
    modifier: Modifier = Modifier,
    @RawRes animation: Int = R.raw.anim_loading_dots,
    iterations: Int = LottieConstants.IterateForever,
    onAnimationEnd: () -> Unit = {}
) {
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animation)
    )

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = iterations,
        isPlaying = true
    )

    if (preloaderProgress == 1f) {
        onAnimationEnd()
    }

    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = preloaderProgress,
        modifier = modifier
    )
}