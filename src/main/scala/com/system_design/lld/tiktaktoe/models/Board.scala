package com.system_design.lld.tiktaktoe.models

import java.util.UUID
import scala.util.Try

final case class Board(
  board_id: String,
  board_size: Int,
  board_cells: Array[Array[String]],
  cell_name_to_index: Map[String, (Int, Int)]
)

object Board {
  def createBoard(size: Int): Board = {
    Board(
      UUID.randomUUID().toString,
      size,
      Array.ofDim[String](size, size).map(row => row.map(_ => "_")),
      mapCellNameToIndex(size)
    )
  }

  def printBoard(board: Board): Unit = {
    for (row <- board.board_cells) {
      println(row.mkString(" "))
    }
  }

  def mapCellNameToIndex(size: Int): Map[String, (Int, Int)] = {

    val alphabets = ('A' to 'Z').toList.take(size)
    val numbers = (1 to size).toList
    val cell_name_to_index = scala.collection.mutable.Map[String, (Int, Int)]()

    for (i <- numbers) {
      for (j <- alphabets.indices) {
        val cell_name = alphabets(j) + i.toString
        val row = i - 1
        val col = j
        cell_name_to_index(cell_name) = (row, col)
      }
    }

    cell_name_to_index.toMap
  }

  def getRowColFromCellName(board: Board, cell_name: String): Try[(Int, Int)] = Try{
    board.cell_name_to_index(cell_name)
  }
}
