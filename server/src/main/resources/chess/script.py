from StockfishPythonLibrary.models import Stockfish
from Chess import *

# receive a position encoded as FEN, communicates with Stockfish
# and outputs the best move as String
#
def getBestMove(fenPosition):

    stockfish = Stockfish("src/main/resources/chess/stockfish.exe")
    stockfish.set_fen_position(fenPosition)
    print(stockfish.get_board_visual())
    bestMove = stockfish.get_best_move_time(300)
    board = Board(fenPosition)
    coordmove = str(board.parse_san(bestMove))
    writeOutput(coordmove)
    print(coordmove)
    

def readInput():
    f = open("src/main/resources/chess/input.txt", "r")
    fen = f.read()
    f.close()
    return fen
def writeOutput(output):
    f = open("src/main/resources/chess/output.txt", "w")
    f.write(output)
    f.close()

print("hey")
getBestMove(readInput())