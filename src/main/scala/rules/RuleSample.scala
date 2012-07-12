package rules

import javax.script.ScriptEngineManager

object JavascriptSupport {
  //Needed as the embedded JavaScript engine might not have the indexOf function
  //on arrays.
  def isIn =
    """
        Array.prototype.isIn = (
          !Array.indexOf ? function (o)
          {
            var l = this.length + 1;
            while (l -= 1)
            {
              if (this[l - 1] === o)
              {
                return true;
              }
            }
            return false;
          } : function (o)
          {
            return (this.indexOf(o) !== -1);
          }
        );
    """.stripMargin
}

object ClassificationRules {
  def rule1 =
              """
                var rule1 = function(wi) {
                  if(wi.item === "foo" && ["alpha","bravo","charlie"].isIn(wi.bar)) {
                    return true;
                  } else {
                    return false;
                  }
                } ;
              """.stripMargin

  def rule2 =
               """
                 var rule2 = function(wi) {
                   if(wi.item == "foo" && !["alpha","bravo","charlie"].isIn(wi.bar)) {
                     return true;
                   } else {
                     return false;
                   }
                 } ;
               """.stripMargin
}

object RuleSample
{
  import ClassificationRules.{rule1,rule2}
  import JavascriptSupport.isIn

  def main(args: Array[String]): Unit = {
    val factory = new ScriptEngineManager();
    val engine = factory.getEngineByName("JavaScript")



    val item =
      """
        var item = {"item":"foo","bar":"bravo"} ;
      """.stripMargin

    //Set up engine with rules and isIn function. Note we can reintroduce rules on demand
    //as they are created, modified, deleted.
    engine.eval(isIn)
    engine.eval(rule1)
    engine.eval(rule2)

    //Introduce the item to evaluate
    engine.eval(item)

    val rule1Fired = engine.eval("rule1(item)")
    println(String.format("rule1 fired: %s", rule1Fired))

    val rule2Fired = engine.eval("rule2(item)")
    println(String.format("rule2 fired: %s", rule2Fired))
  }
}