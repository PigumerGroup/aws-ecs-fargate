package com.pigumer.http

import java.lang.management.ManagementFactory

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import scala.collection.JavaConverters._

object HelloWorld extends App {

  implicit val system = ActorSystem("HelloWorld")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val logger = Logging(system, "HelloWorld")

  val server = ManagementFactory.getPlatformMBeanServer
  val names = server.queryNames(null, null).asScala
  names.foreach(name => logger.info(s"$name"))

  val route =
    pathEndOrSingleSlash {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hello World!!</h1>"))
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

  sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
