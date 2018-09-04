name := "gnib-appointment-telegram"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions := Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"
)

libraryDependencies ++= {
  val http4sVersion = "0.18.12"
  val circeVersion = "0.9.2"
  val log4CatsVersion = "0.0.6"
  val pureconfigVersion = "0.9.2"
  val typefageConfigVersion = "1.3.2"
  val lobackVersion = "1.2.3"

  Seq(
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-core" % circeVersion,
    "io.chrisdavenport" %% "log4cats-slf4j" % log4CatsVersion,
    "eu.timepit" %% "refined-pureconfig" % pureconfigVersion,
    "com.typesafe" % "config" % typefageConfigVersion,
    "com.github.pureconfig" %% "pureconfig-cats-effect" % pureconfigVersion,
    "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,
    "ch.qos.logback" % "logback-classic" % lobackVersion
  )
}
