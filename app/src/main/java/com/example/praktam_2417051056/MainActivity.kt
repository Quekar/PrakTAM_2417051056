package com.example.praktam_2417051056

import com.example.praktam_2417051056.model.EventDummy
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.praktam_2417051056.ui.theme.PrakTAM_2417051056Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051056Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val event1 = EventDummy.eventList[0]
    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {
        Text(text = "Judul: ${event1.title}")
        Text(text = "Deskripsi: ${event1.description}")
        Text(text = "Tanggal: ${event1.date}")
        Text(text = "Jam Mulai: ${event1.startTime}")
        Text(text = "Jam Selesai: ${event1.endTime}")
        Text(text = "Durasi: ${event1.durationMinutes} menit")
        Text(text = "Kategori: ${event1.category}")
    }
    val event2 = EventDummy.eventList[1]
    Column(modifier = modifier.fillMaxSize().padding(vertical = 240.dp, horizontal = 24.dp)) {
        Text(text = "Judul: ${event2.title}")
        Text(text = "Deskripsi: ${event2.description}")
        Text(text = "Tanggal: ${event2.date}")
        Text(text = "Jam Mulai: ${event2.startTime}")
        Text(text = "Jam Selesai: ${event2.endTime}")
        Text(text = "Durasi: ${event2.durationMinutes} menit")
        Text(text = "Kategori: ${event2.category}")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakTAM_2417051056Theme {
        Greeting()
    }
}
