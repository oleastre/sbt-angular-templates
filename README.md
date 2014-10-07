# Angular template generation and compression for SBT-WEB

This simple SBT plugin generates a single JavaScript or HTML file to package up individual [Angular templates](https://docs.angularjs.org/guide/templates).
Templates are loaded into Angular using the [$templateCache](https://docs.angularjs.org/api/ng/service/$templateCache).
Each individual template file must contain a single top-level element.

## Installation

Add this to your project/plugins.sbt:

```scala
addSbtPlugin("org.databrary" % "sbt-angular-templates" % "0.2")
```

You also must enable sbt-web plugins for your main project using `.enablePlugins(SbtWeb)`.

## Configuration

You can control how the templates are generated using settings under AngularTemplatesKeys.
The main ones are:

* `outputJs`: File to generate containing loadable JavaScript templates, which can be directly sourced and loads all templates into the template cache, or `None` to disable generation.  Defaults to `templates.js`.  To use this you must also set `module` correctly.
* `outputHtml`: File to generate containing includable HTML templates, embedded in `<script> text/ng-template` tags, or `None` to disable generation.  Defaults to `templates.html`.  
* `compress`: Compress HTML using [HtmlCompressor](https://code.google.com/p/htmlcompressor/#Using_HTML_Compressor_from_Java_API).  Most of the options listed there (such as `RemoveIntertagSpaces`) have equivalent settings here (`compressRemoveIntertagSpaces`).
* `module`: Name of angular application module variable.  Defaults to "module".
* `naming`: Function to use to map each template source file path to its template id.
* `includeFilter`: Which source templates to include.  Defaults to all .html files under assets.

