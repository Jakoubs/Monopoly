ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "Monopoly",
    coverageExcludedPackages := "<empty>;.view.*",
  )
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"
libraryDependencies += "org.scalafx" %% "scalafx" % "24.0.0-R35"
Compile/mainClass := Some("de.htwg.Monopoly")

libraryDependencies ++= {
  // Determine OS and architecture for JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") =>
      val arch = System.getProperty("os.arch")
      if (arch.startsWith("aarch64")) "mac-aarch64" else "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  val classifier = osName match {
    case "linux" => "linux"
    case "win" => "win"
    case "mac" => "mac"
    case "mac-aarch64" => "mac-aarch64"
    case _ => throw new Exception(s"Unknown classifier for os: $osName")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "19" classifier classifier) // Use JavaFX 19
}