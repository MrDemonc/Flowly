package com.demonlab.flowly.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class CardPosition {
    SINGLE,
    FIRST,
    MIDDLE,
    END
}

object ExpressiveShapes {
    fun getGroupedShape(
        position: CardPosition,
        largeCorner: Dp = 24.dp,
        smallCorner: Dp = 6.dp
    ): Shape {
        return when (position) {
            CardPosition.SINGLE -> RoundedCornerShape(largeCorner)
            CardPosition.FIRST -> RoundedCornerShape(
                topStart = largeCorner,
                topEnd = largeCorner,
                bottomStart = smallCorner,
                bottomEnd = smallCorner
            )
            CardPosition.MIDDLE -> RoundedCornerShape(smallCorner)
            CardPosition.END -> RoundedCornerShape(
                topStart = smallCorner,
                topEnd = smallCorner,
                bottomStart = largeCorner,
                bottomEnd = largeCorner
            )
        }
    }

    fun getPositionForIndex(index: Int, totalItems: Int): CardPosition {
        if (totalItems <= 1) return CardPosition.SINGLE
        return when (index) {
            0 -> CardPosition.FIRST
            totalItems - 1 -> CardPosition.END
            else -> CardPosition.MIDDLE
        }
    }
}
