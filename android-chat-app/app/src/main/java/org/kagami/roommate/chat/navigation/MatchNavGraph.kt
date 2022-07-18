package org.kagami.roommate.chat.navigation

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph
@NavGraph
annotation class MatchNavGraph(
    val start: Boolean = false
)