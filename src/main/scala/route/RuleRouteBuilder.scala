package route

import org.apache.camel.scala.dsl.builder.RouteBuilder

/*
 *    Copyright (c) 2011 XTRAC LLC. All Rights Reserved.
 *
 *    This software and all information contained herein is the property of
 *    XTRAC LLC.  Any dissemination, disclosure, use, or reproduction of this
 *    material for any reason inconsistent with express purpose for which it
 *    has been disclosed is strictly forbidden.
 */

class RuleRouteBuilder  extends RouteBuilder
{
  "activemq:queue:workitem"  --> "bean:ruleEvaluator" --> "log:rulelog?level=INFO" routeId("rule evaluation route")
}