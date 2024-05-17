import mill._, scalalib._

object limit extends RootModule with JavaModule {
  object throwingutil extends JavaModule 
  object test extends JavaModuleTests with TestModule.Junit4
  object utiltest extends JavaModuleTests with TestModule.Junit4
  def moduleDeps = Seq(throwingutil)
  def mainClass = Some("limit.core.shell.Shell")
  def ivyDeps = Agg(
    // ivy"org.junit.jupiter:junit-jupiter:5.10.2",
    // ivy"org.junit.jupiter:junit-jupiter-engine:5.10.2",
    // ivy"org.junit.jupiter:junit-jupiter-api:5.10.2",
    ivy"org.assertj:assertj-core:3.25.3",
    ivy"it.unimi.dsi:fastutil:8.5.13",
    ivy"com.ibm.icu:icu4j:74.2"
  )
}
