package route

import org.apache.camel.Exchange
import rules._
import rules.Expression
import rules.RuleDefinition
import javax.script.ScriptEngineManager

class RuleEvaluator
{
  val engine =
  {
    val highNet = Expression("cause", Eq, "high net")
    val platcust = Expression("platcust", Eq, "plat cust")
    val itemtype = Expression("itemtype", Eq, "item1")
    val subtype = Expression("subtype", Eq, "foobar")
    val causeExp = RuleDefinition(null, Or).addChild(highNet).addChild(platcust)
    val rule = RuleDefinition(null, And).addChild(itemtype).addChild(subtype).addChild(causeExp)

    val ruleText = rule.buildJSRule("recordType1A")

    val factory = new ScriptEngineManager();
    val engine = factory.getEngineByName("JavaScript")

    engine.eval(ruleText)

    engine
  }

  def evaluateRule(exchange: Exchange): String =
  {
    val item = exchange.getIn.getBody[String](classOf[String])
    engine.eval(String.format("var item = %s", item))
    val evaluated =engine.eval("recordType1A(item)")
    String.format("%s matched rule: %s", item, evaluated)
  }
}