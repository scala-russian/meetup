name := "meetup"
version := "1.0.0"
scalaVersion := "2.12.4"
organization := "center.scala.ru"

updateOptions := updateOptions.value.withCachedResolution(true)

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-deprecation",
  "-encoding",
  "UTF-8", // yes, this is 2 args
  "-language:implicitConversions",
  "-language:postfixOps",
  "-unchecked",
  "-feature",
  "-Xfatal-warnings",
  "-Ywarn-dead-code", // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ypartial-unification",
  "-Xfuture"
)

libraryDependencies ++= Seq(
  "org.typelevel"              %% "cats-core"      % "1.0.1",
  "com.github.finagle"         %% "finch-core"     % "0.17.0",
  "com.github.finagle"         %% "finch-generic"  % "0.17.0",
  "com.github.finagle"         %% "finch-circe"    % "0.17.0",
  "io.circe"                   %% "circe-generic"  % "0.9.1",
  "ch.qos.logback"             % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"  % "3.7.2"
)

enablePlugins(JavaServerAppPackaging)
//enablePlugins(LinuxPlugin)
//enablePlugins(RpmPlugin)

mainClass in Compile := Some("center.scala.ru.Meetup")

packageName := "meetup"
rpmVendor := "Scala Russian"
rpmLicense := Some("mit")
rpmRelease := "1"
rpmUrl := Some("https://github.com/scala-russian/meetup")
maintainer := "Danila Matveev <usurname.r@gmail.com>"
packageSummary := "Scala meetup landing"
packageDescription := "https://github.com/scala-russian/meetup"
fileDescriptorLimit := Some("10240")

mappings in Universal ++= {
  val assetsDir = ((resourceDirectory in Compile).value / "assets").listFiles().toSeq
  val logback = Seq(
    (resourceDirectory in Compile).value / "logback.xml" -> "conf/logback.xml"
  )
  val html = assetsDir
    .filter(a => !a.getName.endsWith(".html"))
    .map(file => file -> ("/data/assets/" + file.getName))
  val assets = assetsDir
    .filter(_.getName.endsWith(".html"))
    .map(file => file -> ("/data/" + file.getName))

  logback ++ html ++ assets
}

bashScriptExtraDefines ++= Seq(
  """addJava "-Dlogback.configurationFile=file://${app_home}/../conf/logback.xml"""",
  """addJava "-XX:+PrintGCDetails"""",
  """addJava "-XX:+PrintGCDateStamps"""",
  """addJava "-XX:+PrintGCTimeStamps"""",
  """addJava "-Xloggc:/var/log/meetup/gc.log"""",
  """addJava "-XX:+UseGCLogFileRotation"""",
  """addJava "-XX:NumberOfGCLogFiles=10"""",
  """addJava "-XX:GCLogFileSize=1M"""",
  """addJava "-XX:+HeapDumpOnOutOfMemoryError"""",
  """addJava "-Dfile.encoding=UTF-8""""
)
