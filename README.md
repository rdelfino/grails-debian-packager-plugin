Grails Debian Packager Plugin
=============

Debian Packager plugin allows generation of debian packages from a Grails application.

Installation
-------

To install the latest stable version of the plugin run:

	grails install-plugin debian-packager

Build configuration
-------

The plugin reads the following grails-app/conf/BuildConfig.groovy
configurations:

<dl>

	<dt>debian.name</dt>
	<dd>Name of debian package file. Defaults to ${appName}_${appVersion}</dd>
	
	<dt>debian.install.home</dt>
	<dd>Default instalation directory of package contents, defaults to /opt/${appName}</dd>
	
	<dt>debian.install.war.target</dt>
	<dd>
	Directory where the war file generated with 'grails war' will be placed
	in the package. Defaults to ${debian.install.home}/webapps
	</dd>
	
	<dt>debian.install.war.name</dt>
	<dd>
	Name of the war file in the target directory. 
	Defaults to current war file. Useful for renaming the 
	generated war in the debian package.
	</dd>
	
	<dt>debian.install.user</dt>
	<dd>
	User that may be created during the debian package installation. 
	Defaults to ${appName}. 
	</dd>
	
	<dt>debian.install.group</dt>
	<dd>
	Group that may be created during the debian package installation. 
	Defaults to ${appName}. 
	</dd>
	
	<dt>debian.install.data</dt>
	<dd>
	List of data items that will be included in the package.
	These items can be directories, files or tar.gz archives.
	Each item is described by a map with the following configuration 
	keys:
		<dl>
			<dt>type</dt>
			<dd>Either of file, directory or archive.</dd>
			
			<dt>src</dt>
			<dd>Path to the item to be included in the package. 
			If the type is directory or archive, all the item's contents are copied to the package.
			</dd>
			
			<dt>target</dt>
			<dd>Destination directory to the item in the package. Defaults to ${debian.install.home}</dd>
			
			<dt>name</dt>
			<dd>Valid only for items with type == 'file'. 
			Useful for renaming items in the generated package. 
			Defaults to the current file name.
			</dd>
		</dl>
	</dd>
</dl>

The following example includes a directory, a file and an archive in the generated package

<pre>
debian.data = [
	[
		src: "a",
		target: "/opt/test-src",
		type: "directory"
	],
	[
		src: "b/application.properties",
		target: "/opt/test",
		type: "file",
		name: "x.properties"
	],
	[
		src: "b/archive.tar.gz",
		target: "/opt/test/exploded",
		type: "archive"
	]
]
</pre>

Control Files
-------

When the plugin is installed it creates a directory named debian
in the project. In this directory are placed the package control 
files:

<dl>

<dt>control</dt>
<dd>package descriptor, see: 
<a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html">http://www.debian.org/doc/debian-policy/ch-controlfields.html</a> 
for its description.</dd>

<dt>preinst</dt>
<dd>commands to be executed before the package is installed</dd>

<dt>postinst</dt>
<dd>commands to be executed after the package is installed</dd>

<dt>prerm</dt>
<dd>commands to be executed before the package is removed</dd>

<dt>postrm</dt>
<dd>commands to be executed after the package is removed</dd>

<dt>conffiles</dt>
<dd>list of package configuration files, see: <a href="http://www.debian.org/doc/manuals/debian-faq/ch-pkg_basics.en.html#s-conffile">http://www.debian.org/doc/manuals/debian-faq/ch-pkg_basics.en.html#s-conffile</a> for further information.</dd>
</dl>

These files are actually Groovy SimpleTemplate templates (see: <a href="">http://groovy.codehaus.org/Groovy+Templates</a>)
that are processed before the generation of the package. The following values configured on BuildConfig.groovy are available to these templates:

<dl>

<dt>installHome (${debian.install.home}) </dt>
<dd>default instalation directory of package contents, defaults to /opt/${appName}</dd>

<dt>appName</dt>
<dd>name of grails application</dd>

<dt>appVersion</dt>
<dd>application version</dd>

<dt>user (${debian.install.user})</dt>
<dd>a user that may be created by this package, defaults to ${appName}</dd>

<dt>group (${debian.install.group})</dt>
<dd>a group that may be created by this package, defaults to ${appName}</dd>

</dl>

Packaging
-------

To generate the debian package, use the following command:

	grails debian

