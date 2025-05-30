package com.system_design.lld.tiktaktoe.models

import java.util.UUID
import java.time.LocalDateTime

final case class  Game(
  game_id: String,
  player_1: Player,
  player_2: Player,
  board: Board,
  game_status: GameStatus,
  winner: Option[Player]
)

object Game {
  def createGame(player_1: Player, player_2: Player): Game = {
    Game(
      UUID.randomUUID().toString,
       player_1,
       player_2, 
       Board.createBoard(3),
       GameStatus.IN_PROGRESS,
       None
      )
  }

  def getGameStatus(game: Game): GameStatus = {
    game.game_status
  }

  def getBoard(game: Game): Board = {
    game.board
  }

  def getPlayer1(game: Game): Player = {
    game.player_1
  }

  def getPlayer2(game: Game): Player = {
    game.player_2
  }

  def getGameId(game: Game): String = {
    game.game_id
  }

  def makeMove(game: Game, player: Player, row: Int, col: Int): Game = {

    game.board.board_cells(row)(col) = player.player_peice.piece_type

    if (checkWinner(game, player.player_peice)) {

      game.copy(game_status = GameStatus.COMPLETED(LocalDateTime.now()), winner = Some(player))

    } else if (checkDraw(game)) {

      game.copy(game_status = GameStatus.DRAW)

    } else game

  }

  def checkWinner(game: Game, piece: Piece): Boolean = {

    val board = game.board

    // check rows
    for (row <- board.board_cells) {
      if (row.mkString("") == piece.piece_type * 3) {
        return true
      }
    }

    // check columns
    for (col <- board.board_cells.transpose) {
      if (col.mkString("") == piece.piece_type * 3) {
        return true
      }
    }

    // check diagonals
    if (
      board.board_cells(0)(0) == piece.piece_type && board.board_cells(1)(1) == piece.piece_type && board.board_cells(2)(2) == piece.piece_type
      || board.board_cells(0)(2) == piece.piece_type && board.board_cells(1)(1) == piece.piece_type && board.board_cells(2)(0) == piece.piece_type
    ) {
      return true
    } else {
      return false
    }

  }

  def checkDraw(game: Game): Boolean = {
    game.board.board_cells.flatten.count(_ != " ") == 0
  }

}
