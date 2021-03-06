import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "assignment"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "com.rabbitmq" % "amqp-client" % "3.1.4",
    "com.google.inject" % "guice" % "3.0",
    "com.google.inject.extensions" % "guice-assistedinject" % "3.0",
    "com.google.guava" % "guava" % "14.0.1",
    "com.lambdaworks" % "lettuce" % "2.3.3"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )
}
