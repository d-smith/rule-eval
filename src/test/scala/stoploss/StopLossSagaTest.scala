package stoploss

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers


class StopLossSagaTest extends Spec with MustMatchers {
  describe("given a stop loss") {
    describe("when the price falls below the loss amount for 30s") {
      it("a sell event should be emitted") {
      }
    }
  }


  describe("given a stop loss") {
    describe("when the price falls below the loss amount for less than 30s") {
      it("a sell event should not be emitted") {
      }
    }
  }

  describe("given a stop loss") {
    describe("when the price goes up") {
      it("is not used as the new price baseline until held for 15s") {
      }
    }
  }


  describe("given a stop loss") {
    describe("when the price goes up") {
      it("uses the higher price is used as the new price baseline if held for at least 15s") {
      }
    }
  }


  describe("given a stop loss") {
    describe("when the price ratchets up") {
      it("uses the highest price (assuming 15s 'stickiness' requirement) is use to evaluate loss on price fall") {
      }
    }
  }

  describe("given a stop loss") {
    describe("when the price ratchets down") {
      it("a sell event is emitted when the full loss is realized") {
      }
    }
  }


}