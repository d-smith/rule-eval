/*
 *    Copyright (c) 2011 XTRAC LLC. All Rights Reserved.
 *
 *    This software and all information contained herein is the property of
 *    XTRAC LLC.  Any dissemination, disclosure, use, or reproduction of this
 *    material for any reason inconsistent with express purpose for which it
 *    has been disclosed is strictly forbidden.
 */
package routerunner;

import rules.And$;
import rules.Eq$;
import rules.Expression;
import rules.In$;
import rules.JavaHelper;
import rules.RuleDefinition;
import rules.SetExpression;
import scala.collection.immutable.List$;

public class JavaAccess
{
    public static void main(String[] args)
    {
        SetExpression cause = new SetExpression("cause", In$.MODULE$, "['high net','plat cust']");
        Expression itemtype = new Expression("itemtype", Eq$.MODULE$, "item1");
        Expression subtype = new Expression("subtype",  Eq$.MODULE$, "foobar");
        RuleDefinition rule = new RuleDefinition(null, And$.MODULE$, JavaHelper.getEmptyList());
        rule = rule.addChild(itemtype).addChild(subtype).addChild(cause);

        System.out.println(rule.buildJSRule("recordType1A"));

    }
}
