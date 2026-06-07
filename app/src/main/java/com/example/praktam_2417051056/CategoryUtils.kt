package com.example.praktam_2417051056

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.ui.graphics.vector.ImageVector

fun getCategoryIcon(category: String): ImageVector = when (category) {
    "Kuliah"  -> Icons.Default.MenuBook
    "Pribadi" -> Icons.Default.FitnessCenter
    "Kerja"   -> Icons.Default.Work
    "Penting" -> Icons.Default.Star
    else      -> Icons.Default.Bookmark
}