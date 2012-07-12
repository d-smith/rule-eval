package route

import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.LoggingLevel

/*
 *    Copyright (c) 2011 XTRAC LLC. All Rights Reserved.
 *
 *    This software and all information contained herein is the property of
 *    XTRAC LLC.  Any dissemination, disclosure, use, or reproduction of this
 *    material for any reason inconsistent with express purpose for which it
 *    has been disclosed is strictly forbidden.
 */

class QueueLoaderRoute extends RouteBuilder
{
    handle[Exception] {
      to("log:rulelog?level=ERROR")
    }

    ("jetty:http://0.0.0.0:12345/queueloader" --> "activemq:queue:workitem").inOnly.routeId("queue loader route")
}