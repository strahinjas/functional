package mocks

import bloxorz.game.UserInterface

class MockUserInterface extends UserInterface {
    override def run(): Unit = {
        // Do nothing, tests will call Game methods explicitly
    }
}
