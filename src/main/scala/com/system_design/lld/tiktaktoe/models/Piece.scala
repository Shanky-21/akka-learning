package com.system_design.lld.tiktaktoe.models

final case class Piece(
  piece_type: String
)

object Piece {
  def createPiece(piece_type: String): Piece = {
    Piece(piece_type)
  }
}
