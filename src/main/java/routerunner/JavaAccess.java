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
import rules.ConditionTree;
import rules.Neq$;
import rules.RuleDefinition;
import rules.SetExpression;
import scala.Some;

public class JavaAccess
{
    public static void main(String[] args)
    {
        SetExpression cause = new SetExpression("cause", In$.MODULE$, "['high net','plat cust']");
        Expression foo = new Expression("foo", Neq$.MODULE$, "bar");
        ConditionTree andTree = new ConditionTree(null, And$.MODULE$, JavaHelper.getEmptyList())
                .addChild(cause).addChild(foo);
        ConditionTree conditions = new ConditionTree(null, And$.MODULE$, JavaHelper.getEmptyList());
        RuleDefinition rule = new RuleDefinition(
                "recordType1A",
                "myorg",
                "item1",
                new Some<String>("foobar"),
                new Some<ConditionTree>(conditions)
        ) ;

        System.out.println(rule.buildJSRule());

    }
}
