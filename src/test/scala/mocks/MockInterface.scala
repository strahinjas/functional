package mocks

import bloxorz.game.Interface

class MockInterface extends Interface {
    override def run(): Unit = {
        // Do nothing, tests will call Game methods explicitly
    }
}
