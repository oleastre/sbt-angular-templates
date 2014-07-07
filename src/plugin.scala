package org.databrary.sbt

import sbt._
import sbt.Keys._
import com.typesafe.sbt.web.SbtWeb

object Import {
  val angularTemplates = TaskKey[Seq[File]]("angular-templates", "Compress, combine, and package angular HTML templates.")

  object AngularTemplatesKeys {
    val compress = SettingKey[Boolean]("angular-templates-compress", "Compress HTML using HtmlCompressor [true].")
    val compressRemoveComments = SettingKey[Boolean]("angular-templates-compress-comments", "Remove HTML comments [true].")
    val compressRemoveMultiSpaces = SettingKey[Boolean]("angular-templates-compress-multi-spaces", "Remove multiple whitespace characters [true].")
    val compressRemoveIntertagSpaces = SettingKey[Boolean]("angular-templates-compress-intertag-spaces", "Remove inter-tag whitespace characters [false].")
    val compressRemoveQuotes = SettingKey[Boolean]("angular-templates-compress-quotes", "Remove unnecessary tag attribute quotes [false].")
    val compressPreserveLineBreaks = SettingKey[Boolean]("angular-templates-compress-line-breaks", "Preserve original line breaks [false].")
    val compressRemoveSurroundingSpaces = SettingKey[Seq[String]]("angular-templates-compress-surrounding-spaces", "Remove spaces around provided tags.")
    val module = SettingKey[String]("angular-templates-module", "Name of angular application module variable (needed for outputJs) [\"module\"].")
    val naming = SettingKey[String => String]("angular-templates-naming", "Function to use to name templates based on their (relative) path [identity].")
    val outputHtml = SettingKey[Option[String]]("angular-templates-output-html", "Output an html file containing <script> text/ng-template tags [templates.html].")
    val outputJs = SettingKey[Option[String]]("angular-templates-output-js", "Output a js file that puts templates into $templateCache [templates.js].")
  }
}

object AngularTemplates extends AutoPlugin {

  override def requires = SbtWeb

  override def trigger = AllRequirements

  val autoImport = Import

  import SbtWeb.autoImport._
  import WebKeys._
  import autoImport._
  import AngularTemplatesKeys._
  import com.typesafe.sbt.web.js.JS

  override def projectSettings = Seq(
    compress := true,
    compressRemoveComments := true,
    compressRemoveMultiSpaces := true,
    compressRemoveIntertagSpaces := false,
    compressRemoveQuotes := false,
    compressPreserveLineBreaks := false,
    compressRemoveSurroundingSpaces := Nil,
    module := "module",
    naming := identity _,
    outputHtml := Some("templates.html"),
    outputJs := Some("templates.js"),
    angularTemplates := run.value,
    resourceGenerators in Assets <+= angularTemplates,
    managedResourceDirectories in Assets += (resourceManaged in (Assets, angularTemplates)).value
  ) ++ inTask(angularTemplates)(inConfig(Assets)(Seq(
    includeFilter := GlobFilter("*.html"),
    sources := sourceDirectory.value.descendantsExcept(includeFilter.value, excludeFilter.value).get,
    mappings := sources.value.pair(relativeTo(sourceDirectory.value)),
    resourceManaged := webTarget.value / "angular-templates" / "main"
  )))

  private def run = Def.task {
    val maps = (mappings in (Assets, angularTemplates)).value
    val outDir = (resourceManaged in (Assets, angularTemplates)).value

    val compressor =
      if (compress.value) {
	val c = new com.googlecode.htmlcompressor.compressor.HtmlCompressor
	c.setRemoveComments(compressRemoveComments.value)
	c.setRemoveMultiSpaces(compressRemoveMultiSpaces.value)
	c.setRemoveIntertagSpaces(compressRemoveIntertagSpaces.value)
	c.setRemoveQuotes(compressRemoveQuotes.value)
	c.setPreserveLineBreaks(compressPreserveLineBreaks.value)
	if (compressRemoveSurroundingSpaces.value.nonEmpty)
	  c.setRemoveSurroundingSpaces(compressRemoveSurroundingSpaces.value.mkString(","))
	Some(c)
      } else None

    def proc(html : String) : String =
      compressor.fold(html)(_.compress(html))
    val namer = naming.value

    val tpls = maps.map { case (f, n) =>
      (namer(n), proc(IO.read(f)))
    }

    outputHtml.value.map { o =>
      val f = outDir / o
      IO.writer(f, "", IO.defaultCharset) { w =>
	tpls.foreach { case (n, t) =>
	  w.write("<script type=\"text/ng-template\" id=\"" + n
	    .replaceAllLiterally("&", "&amp;")
	    .replaceAllLiterally("<", "&lt;")
	    .replaceAllLiterally(">", "&gt;")
	    .replaceAllLiterally("\"", "&quot;")
	    .replaceAllLiterally("'", "&apos;")
	    + "\">")
	  w.write(t)
	  w.write("</script>")
	}
      }
      f
    }.toSeq ++
    outputJs.value.map { o =>
      val f = outDir / o
      IO.writer(f, "", IO.defaultCharset) { w =>
	w.write(module.value + ".run(['$templateCache',function(t){")
	tpls.foreach { case (n, t) =>
	  w.write("t.put(" + JS.write(n) + "," + JS.write(t) + ");")
	}
	w.write("}]);")
      }
      f
    }
  }
}
