package rules

import javax.script.ScriptEngineManager


abstract class RuleDefNode

trait LeafNode

abstract class Operator extends RuleDefNode

case object And extends Operator {
  override def toString = " && "
}

case object Or extends Operator {
  override def toString = " || "
}

abstract class ExpOperator extends Operator

case object Eq extends ExpOperator {
  override def toString = " === "
}

case object Neq extends ExpOperator {
  override def toString = " !== "
}

case class Expression(field: String, oper: ExpOperator, value: String)
  extends RuleDefNode with LeafNode {
  override def toString = String.format("wi.%s %s \"%s\"", field, oper.toString, value)
}

abstract class SetOperator

case object In extends SetOperator {
  override def toString = "isIn"
}

case object NotIn extends SetOperator {
  override def toString = "isNotIn"
}

case class SetExpression(field: String, oper: SetOperator, values: String)
  extends RuleDefNode with LeafNode {
  override def toString = String.format("%s.%s(wi.%s)", values, oper.toString, field)
}

object JavaHelper {
  def getEmptyList: List[ConditionTree] = {
    List[ConditionTree]()
  }
}

case class RuleDefinition(name: String, org: String, itemType: String,
                          subtype: Option[String],
                          conditions: Option[ConditionTree]) {
  def buildJSRule(): String = {

    val ruleText = new StringBuilder
    ruleText ++= "var "
    ruleText ++= name
    ruleText ++= " = function(wi) { if("

    ruleText ++= Expression("itemtype", Eq, itemType).toString

    subtype match {
      case Some(s) => {
        ruleText ++= " && "
        ruleText ++= Expression("subtype", Eq, s).toString
      }
      case None => ()
    }

    conditions match {
      case Some(ct) => {
        ruleText ++= " && "
        val clause = ct.visit()
        ruleText ++= clause
      }
      case None => ()
    }


    ruleText ++= ") { return true; } else { return false; }}"

    ruleText.toString
  }
}

case class ConditionTree(parent: ConditionTree, node: RuleDefNode, children: List[ConditionTree] = List[ConditionTree]()) {
  def addParent(theParent: ConditionTree): ConditionTree = {
    copy(theParent, node, children)
  }

  def addChild(defNode: RuleDefNode): ConditionTree = {
    addChild(ConditionTree(null, defNode))
  }

  def addChild(child: ConditionTree): ConditionTree = {
    copy(parent, node, (child.addParent(this)) :: children)
  }

  def visit(builder: StringBuilder): Unit = {
    require(builder != null, "Null StringBuilder passed to visit")
    val leaf = node match {
      case _: LeafNode => {
        builder ++= node.toString
        true
      }
      case _ => false
    }

    children match {
      case List(child) => child.visit(builder)
      case first :: rest => {
        builder ++= "("
        builder ++= first.visit()
        rest.foreach(child => {
          builder ++= node.toString
          child.visit(builder)
        })
        builder ++= ")"
      }
      case _ => ()
    }
  }

  def visit(): StringBuilder = {
    val builder = new StringBuilder
    visit(builder)
    builder
  }
}

object MyTest {
  def main(args: Array[String]): Unit = {
    val highNet = Expression("cause", Eq, "high net")
    val platcust = Expression("platcust", Eq, "plat cust")
    val causeExp = ConditionTree(null, Or).addChild(highNet).addChild(platcust)
    val rule = RuleDefinition("recordType1A", "myorg", "item1", Option("foobar"), Option(causeExp))

    val ruleText = rule.buildJSRule()

    //Evaluate it
    val factory = new ScriptEngineManager();
    val engine = factory.getEngineByName("JavaScript")

    //Work item as JSON
    val item =
      """
            var item = {"itemtype":"item1","subtype":"foobar","cause":"high net"} ;

      """.stripMargin

    engine.eval(item)
    engine.eval(ruleText)
    println(ruleText)
    println(engine.eval("recordType1A(item)"))
  }
}

object SetTest {
  def main(args: Array[String]): Unit = {
    val cause = SetExpression("cause", In, "['high net','plat cust']")
    val worm = Expression("wormtype", Eq, "mealy")
    val itemtype = Expression("itemtype", Eq, "item1")
    val conditions = ConditionTree(null, And).addChild(cause).addChild(worm)
    val rule = RuleDefinition("recordType1A", "myorg", "item1", None, Option(conditions))


    println(rule.buildJSRule)
  }
}


