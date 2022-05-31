package mocks

import bloxorz.game.Interface

class MockInterface extends Interface {
    override def run(): Unit = {
        // do nothing, tests will call methods explicitly
    }
}
