package com.mwagent.feature.onboarding

import com.mwagent.data.chat.ChatTint
import com.mwagent.ui.avatar.AgentLook
import com.mwagent.ui.avatar.AgentShapes
import kotlin.random.Random

object AgentCluster {
    const val SIZE = 5

    fun random(random: Random = Random): List<AgentLook> {
        val shapes = AgentShapes.faces.shuffled(random).take(SIZE)
        val tints = ChatTint.entries.shuffled(random).take(SIZE)
        return shapes.zip(tints) { shape, tint -> AgentLook(shape, tint) }
    }
}
