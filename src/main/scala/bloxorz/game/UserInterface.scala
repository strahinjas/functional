package bloxorz.game

abstract class UserInterface(var game: Game = null) {
    def run(): Unit
}
