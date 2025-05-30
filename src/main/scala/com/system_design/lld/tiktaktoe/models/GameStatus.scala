package com.system_design.lld.tiktaktoe.models

import java.time.LocalDateTime

enum GameStatus {
  case IN_PROGRESS
  case COMPLETED( completed_at: LocalDateTime)
  case CANCELLED( cancelled_at: LocalDateTime)
  case DRAW
}

object GameStatus {

  def fromString(status: String): GameStatus = status match {
    case "IN_PROGRESS" => IN_PROGRESS
    case "COMPLETED" => COMPLETED(LocalDateTime.now())
    case "CANCELLED" => CANCELLED(LocalDateTime.now())
    case "DRAW" => DRAW
  }
}
