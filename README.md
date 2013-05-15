Example project adding product type information as facets to the search index

To use, clone into ../epagesj-plugins/document-appender-plugin (as seen from epagesj)
and import into eclipse as gradle project.

Refresh the gradle dependencies of epagesj-server project to pick up your plugin into the
classpath of epagesj.

For deployment, run "gradle pluginZip" and deploy this zip into j/plugins
Works starting from epagesj 6.16.1.
