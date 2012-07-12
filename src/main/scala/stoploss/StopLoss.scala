package stoploss

//Half baked version of the Stop Loss Kata, as in currently transitioning from imperative-style
//to functional style.
//
// Note this focuses only on price movement, security symbol, dealing with floating point accuracy,
// and other important things have been omitted.
//
// Stop Loss Kata courtesy of Greg Young https://gist.github.com/1500720

object LossEvaluator {
  def stopLoss(basePrice: Float, lossLimit: Float): Float => Boolean = {
    def stopLossF(price: Float) = {
      if (basePrice - price > lossLimit) {
        true
      }
      else {
        false
      }
    }

    stopLossF
  }
}

class StopLoss(val basePrice: Float, val lossLimit: Float) {
  def stopLossEvaluator = LossEvaluator.stopLoss(basePrice, lossLimit)

  def priceChanged(newPrice: Float): StopLoss = {
    if (newPrice - basePrice > 0) {
      new StopLoss(newPrice, lossLimit)
    }
    else {
      this
    }
  }

  def timeToSell(currentPrice: Float) = stopLossEvaluator(currentPrice)
}

//Fire an event when time to sell
abstract class Event
case class Sell() extends Event

//Define the abstract notion of bus functionality, and a test implementation
trait Bus {
  def raiseEvent(event: Event): Unit
}

trait TestBus extends Bus {
  var sell: Boolean = false

  def raiseEvent(event: Event) = {
    event match {
      case Sell() => sell = true
      case _ => ()
    }
  }
}

//Finished up Udi's SOA training last week, so everything is looking like a saga right now. Next
//step will be functional style saga, which should let me hold less state. Also, instead of explicitly
//asking for timer callbacks to be invoked, a 15s 'pulse' would also simplify things.
class StopLossSaga(initialPrice: Float, lossLimit: Float, currentTime: Long) extends TestBus {
  val timeQuanta = 15 //Assume time in seconds
  var lastTime = currentTime
  var lastPrice = initialPrice
  var stopLoss = new StopLoss(initialPrice, lossLimit)

  def timerCallback(currentTime: Long): Unit = {

    val diff = currentTime - lastTime
    diff match {
      //Price went down
      case _: Long if lastPrice < stopLoss.basePrice && diff >= 2 * timeQuanta =>
        stopLoss = stopLoss.priceChanged(lastPrice)
        if (stopLoss.timeToSell(lastPrice)) {
          raiseEvent(Sell())
        }
      //Price went up
      case _: Long if diff >= timeQuanta => stopLoss = stopLoss.priceChanged(lastPrice)

      //Price is the same
      case _ => ()
    }
  }

  def priceChange(price: Float) {
    lastPrice = price
    Timer.setTimer(if(price > lastPrice) timeQuanta else 2*timeQuanta)
  }

}

object Timer {
  def setTimer(interval: Long): Unit = {
    //Does nothing - assume we would have a timer service we would invoke that
    //would call us back with the current time and maybe some context.
  }
}
