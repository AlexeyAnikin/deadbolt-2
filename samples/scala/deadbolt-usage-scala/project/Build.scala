import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "deadbolt-usage-scala"
    val appVersion      = "1.1.3"

    val appDependencies = Seq(
      "be.objectify" % "deadbolt-2_2.9.1" % "1.1.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Change this to point to your local play repository
      resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns)
    )

}
