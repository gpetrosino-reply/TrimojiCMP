package it.reply.open.trimoji.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints

/**
 * Replicates the "invisible" visibility of android views (as opposed to "gone"), making composables take their space
 * without actually laying them out on the screen.
 */
fun Modifier.invisible() = this then object: LayoutModifier {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            //Do not lay out
        }
    }
}


fun Modifier.thenIf(condition: Boolean, then: Modifier.()-> Modifier): Modifier = if(condition) this.then() else this