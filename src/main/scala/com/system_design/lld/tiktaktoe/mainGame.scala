package com.system_design.lld.tiktaktoe

import com.system_design.GameService
import com.system_design.lld.tiktaktoe.models.Player
import scala.concurrent.java8.FuturesConvertersImpl.P
import com.system_design.lld.tiktaktoe.models.Piece
import com.system_design.lld.tiktaktoe.models.GameStatus
import scala.util.Failure
import scala.util.Success
import com.system_design.lld.tiktaktoe.models.Board

object mainGame extends App {


  def mainGame(): Unit = {
    val gameService = new GameService()

    val player1 = Player.createPlayer("Shashank", Piece.createPiece("X"))
    val player2 = Player.createPlayer("Prajjwal", Piece.createPiece("O"))

    var game = gameService.createGame(player1, player2)

    println("Welcome to Tic Tac Toe!")
    println("Player 1: " + player1.player_name)
    println("Player 2: " + player2.player_name)

    var chance = 0

    while (game.game_status == GameStatus.IN_PROGRESS) {

      Board.printBoard(game.board)

      if (chance % 2 == 0) {
        println("Player 1: " + player1.player_name)
        val cell_name = scala.io.StdIn.readLine()

        gameService.makeMove(game, player1, cell_name) match {
        case Failure(exception) => println(exception.getMessage) 
        case Success(value) => 
          game = value
          chance += 1
      }
      } else {
        println("Player 2: " + player2.player_name)
        val cell_name = scala.io.StdIn.readLine()

        gameService.makeMove(game, player2, cell_name) match {
        case Failure(exception) => println(exception.getMessage) 
        case Success(value) => 
          game = value
          chance += 1
      }
      }
      
    }

    if (game.game_status == GameStatus.DRAW) {
      println("Game is a draw!")
    } else {
      println("Game is over!")

      if (game.game_status.toString.contains("COMPLETED")) {
        println("Player " + game.winner.get.player_name + " wins!")
      } else {
        println("Game is a draw!")
      }

    }
  }

  mainGame()
}
