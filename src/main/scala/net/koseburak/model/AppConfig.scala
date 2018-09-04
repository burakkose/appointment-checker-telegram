package net.koseburak.model

import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
import eu.timepit.refined.string.Uri

final case class AppConfig(
    token: String Refined NonEmpty,
    chatId: Long,
    category: String Refined NonEmpty,
    subCategory: String Refined NonEmpty,
    typ: String Refined NonEmpty,
    frequency: Int Refined Positive,
    reporterFrequency: Int Refined Positive,
    appointmentSystemUri: String Refined Uri
)
