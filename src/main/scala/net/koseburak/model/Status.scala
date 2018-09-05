package net.koseburak.model

final case class Status(
    success: Long = 0L,
    empty: Long = 0L,
    error: Long = 0L,
    lastAvailableSlots: List[Appointment] = List.empty
)
