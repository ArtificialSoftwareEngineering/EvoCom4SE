name := """bio-rimp"""

version := "1.0"

scalaVersion := "2.11.8"

lazy val jsonLibs = Seq("io.spray" %% "spray-json" % "1.3.2")

lazy val scalaCache = Seq("com.github.cb372" % "scalacache-guava_2.12.0-RC1" % "0.9.2")

//lazy val gremlinScala = Seq("com.michaelpollmeier" %% "gremlin-scala" % "3.2.3.1" % "test")

lazy val graphTitan = Seq("com.thinkaurelius.titan" % "titan" % "1.0.0" % "test")

lazy val tinkerPop = Seq("org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.2.3" % "test")

lazy val akkaVersion = "2.4.9"

lazy val ccodec = Seq("commons-codec" % "commons-codec" % "1.10")

lazy val log4j = Seq("log4j" % "log4j" % "1.2.17")

lazy val guava = Seq("com.google.guava" % "guava" % "19.0")

lazy val cli = Seq("commons-cli" % "commons-cli" % "1.3.1")

lazy val processing = Seq("org.processing" % "core" % "3.2.1")

lazy val slf4j = Seq("org.slf4j" % "slf4j-log4j12" % "1.7.21")

lazy val gson = Seq("com.google.code.gson" % "gson" % "2.7")

lazy val xmlapis = Seq("xml-apis" % "xml-apis" % "2.0.2")

lazy val xalan = Seq("xalan" % "xalan" % "2.7.2")

lazy val mysql = Seq("mysql" % "mysql-connector-java" % "5.1.38")

lazy val ccommonsio = Seq("commons-io" % "commons-io" % "2.4")


libraryDependencies ++= (jsonLibs  ++ scalaCache ++ graphTitan ++
  tinkerPop ++ ccodec ++ log4j ++ guava ++ cli ++
  processing ++ slf4j ++ gson ++ xmlapis ++ xalan ++ mysql ++
  ccommonsio)