package com.system_design

import com.system_design.lld.tiktaktoe.models.Game
import com.system_design.lld.tiktaktoe.models.Player
import com.system_design.lld.tiktaktoe.models.Board
import com.system_design.lld.tiktaktoe.models.GameStatus
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class GameService {
  
  def createGame(player_1: Player, player_2: Player): Game = {
    Game.createGame(player_1, player_2)
  }

  def getGameStatus(game: Game): GameStatus = {
    Game.getGameStatus(game)
  }

  def getBoard(game: Game): Board = {
    Game.getBoard(game)
  }

  def getPlayer1(game: Game): Player = {
    Game.getPlayer1(game)
  }

  def getPlayer2(game: Game): Player = {
    Game.getPlayer2(game)
  }

  def getGameId(game: Game): String = {
    Game.getGameId(game)
  }

  def makeMove(game: Game, player: Player, cell_name: String): Try[Game] = {
    Board.getRowColFromCellName(game.board, cell_name) match {
      case Failure(exception) => Failure(new Exception("Invalid cell name"))
      case Success(value) => 

        val (row, col) = value

        if (game.board.board_cells(row)(col) != "_") {
          Failure(new Exception("Cell already occupied, Please retry"))
        } else {
          Success(Game.makeMove(game, player, row, col))
        }
        
    }
  }
  
  
}
