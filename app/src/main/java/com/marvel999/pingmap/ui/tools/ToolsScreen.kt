package com.marvel999.pingmap.ui.tools

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ToolsScreen(onNavigate: (String) -> Unit = {}) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Tools", style = MaterialTheme.typography.headlineMedium)
    }
}
