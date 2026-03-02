package com.example.praktam_2417051056.model

object EventDummy {
    val eventList: List<Event> = listOf(

        // Contoh event — tambahkan event lainnya di sini mengikuti format yang sama
        Event(
            title = "Matkul Pemrograman Web Responsi",
            description = "Pertemuan rutin mata kuliah Pemweb R di Lab R1",
            date = "2026-03-02",
            startTime = "09:15",
            endTime = "10:55",
            durationMinutes = 100,
            category = "Kuliah",
        ),

        Event(
            title = "Matkul Pemrograman Web Teori",
            description = "Pertemuan rutin mata kuliah Pemweb T di GIK L2",
            date = "2026-03-02",
            startTime = "11:00",
            endTime = "12:40",
            durationMinutes = 100,
            category = "Kuliah",
        ),


        Event(
            title = "Matkul Pembelajaran Mesin Teori",
            description = "Pertemuan rutin mata kuliah ML T di GIK L1 C",
            date = "2026-03-03",
            startTime = "09:15",
            endTime = "10:55",
            durationMinutes = 100,
            category = "Kuliah",
        )

    )
}