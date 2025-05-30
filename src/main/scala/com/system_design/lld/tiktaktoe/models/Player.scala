package com.system_design.lld.tiktaktoe.models

import java.util.UUID

final case class Player(
  player_id: String, 
  player_name: String,
  player_peice: Piece
)

object Player {
  def createPlayer(player_name: String, player_peice: Piece): Player = {
    Player(UUID.randomUUID().toString, player_name, player_peice)
  }
}
