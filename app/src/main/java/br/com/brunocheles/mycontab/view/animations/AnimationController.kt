package br.com.brunocheles.mycontab.view.animations

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

class AnimationController {

    @Composable
    fun animateFloatWithTransition(
        transition: Transition<Boolean>,
        valueForTrue: Float,
        duration: Int = 350
    ): Float {
        val animationValue by transition.animateFloat(
            label = "floatTransition",
            transitionSpec = { tween(durationMillis = duration) }
        ) { state ->
            if (state) valueForTrue else 0f
        }
        return animationValue
    }
}