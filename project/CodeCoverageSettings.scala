import sbt.Setting
import scoverage.ScoverageKeys.*

object CodeCoverageSettings {

  private val settings: Seq[Setting[?]] = Seq(
    coverageExcludedPackages := "<empty>;Reverse.*;..*Routes.*;.*components.*",
    coverageMinimumStmtTotal := 88,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )

  def apply(): Seq[Setting[?]] = settings

}
