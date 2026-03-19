package com.example.praktam_2417051056.model

object TaskDummy {
    val taskList: List<Task> = listOf(

        Task(
            id = 1,
            title = "TAM R LKP 1",
            description = "Tugas LKP 1 TAM Responsi.",
            category = "Kuliah",
            priority = 1,
            deadline = "2026-02-26 23:50",
            isDone = true,
            createdAt = "2026-02-19 10:00"
        ),

        Task(
            id = 2,
            title = "TAM R LKP 2",
            description = "Tugas LKP 2 TAM Responsi.",
            category = "Kuliah",
            priority = 1,
            deadline = "2026-03-5 23:50",
            isDone = true,
            createdAt = "2026-02-27 11:00"
        ),

        Task(
            id = 3,
            title = "Beli Busa Peredam Suara",
            description = "Cari di toko online busa peredam suara 2m x 1m 1, 1m x 50cm 1, dan 50cm x 50cm 2",
            category = "Pribadi",
            priority = 3,
            deadline = "-",
            isDone = false,
            createdAt = "2026-03-15 12:00"
        ),

        Task(
            id = 4,
            title = "TAM R LKP 3",
            description = "Tugas LKP 3 TAM Responsi.",
            category = "Kuliah",
            priority = 1,
            deadline = "2026-03-12 23:59",
            isDone = true,
            createdAt = "2026-03-6 11:00"
        ),

        Task(
            id = 5,
            title = "TAM R LKP 4",
            description = "Tugas LKP 4 TAM Responsi.",
            category = "Kuliah",
            priority = 1,
            deadline = "2026-03-19 23:50",
            isDone = false,
            createdAt = "2026-03-13 13:00"
        ),
    )
}