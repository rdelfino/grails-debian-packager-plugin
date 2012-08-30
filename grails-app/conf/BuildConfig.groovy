grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
	}

	dependencies {
		compile('org.vafer:jdeb:0.11') {
			excludes 'commons-io', 'maven-core', 'maven-plugin-api', 'maven-project', 'maven-artifact', 'plexus-utils', 'ant', 'junit'
		}
	}

	plugins {
		build(':release:2.0.4', ':rest-client-builder:1.0.2') {
			export = false
		}
	}
}
