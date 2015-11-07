NextGenTel - Akka Tools
==========================
[![Build Status](https://travis-ci.org/NextGenTel/akka-tools.svg)](https://travis-ci.org/NextGenTel/akka-tools)
[![Coverage Status](https://coveralls.io/repos/NextGenTel/akka-tools/badge.svg?branch=master&service=github)](https://coveralls.io/github/NextGenTel/akka-tools?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/no.nextgentel.oss.akka-tools/akka-tools-parent_2.11/badge.svg)](http://mvnrepository.com/artifact/no.nextgentel.oss.akka-tools/)



This project contains various addons to Akka useful when working with:

* Cluster
* Sharding
* Persistence and Eventsourcing
* Serializing

This code has turned out to be quite useful for NextGenTel and is used in production.

Version-information:
----------------------------

| Akka  | akka-tools     | branch                                                               |
| ----- | -------------- | -------------------------------------------------------------------- |
| 2.4   | 1.0.2          | [master](https://github.com/NextGenTel/akka-tools)                   |
| 2.3.x | 0.9.0          | [akka_2.3](https://github.com/NextGenTel/akka-tools/tree/akka_2.3)   |

Changelog
----------------------------

Version 1.0.3-SNAPSHOT    - Not released yet

* Improved ResultingEvents-API
* successHandler is now executed at the right time - after all events are actually persisted

Version 1.0.2 - 26/10-2015

* Fixing [#9](https://github.com/NextGenTel/akka-tools/issues/9) - Restructured how persistenceId's are resolved - Now using persitenceIdBase - Introduces some changes to the GeneralAggregate-API [commit](https://github.com/NextGenTel/akka-tools/commit/ab45697)

Version 1.0.1 - 23/10-2015

* Fixes [#8](https://github.com/NextGenTel/akka-tools/issues/8) Explicit closing sql statements to prevent leakage [commit](https://github.com/NextGenTel/akka-tools/commit/95baa1)

Version 1.0.0 - 21/10-2015

* Changed GeneralAggregateBuilder so that it is possible to initiate views with initialState based on aggregateId [commit](https://github.com/NextGenTel/akka-tools/commit/3b9cfea)
* Added ActorWithDMSupport which makes it easy to implement regular Actors interacting with DMs [commit](https://github.com/NextGenTel/akka-tools/commit/1a8511d)
* Using **Akka 2.4**
* Added another example: Trust Account Creation System [commit](https://github.com/NextGenTel/akka-tools/commit/64671b)
* Changed signature of onCmdToEvent to use AggregateCmd instead of AnyRef [commit](https://github.com/NextGenTel/akka-tools/commit/b0be41b)

Version 0.9.0 - 23/9-2015

* First released open-source version of the original internal NextGenTel project - using Akka 2.3
 


Below is a summary of the various modules

akka-tools-persistence
-------------------------------

[akka-tools-persistence](akka-tools-persistence/README.md)'s main purpose is to make Akka Persistence more easy to use.

The main components are:

* **GeneralAggregate** - which is built on top of PersistentActor
* **GeneralAggregateView** - which is built on top of PersistentView 


It includes features like:

* Separation of aggregate, view and state(machine)
* both aggregate and view understands the same events and uses the same "state machine"
* integrated cluster/sharding-support
* Simplified AtLeastOnceDelivery-support
* Automatic starting and stopping (of idle) aggregates and views
* Automatic working view that supports getting the current state (ie. to be used from REST) and the full event history (nice when debugging)



akka-tools-json-serializing
-------------------------------

[akka-tools-json-serializing](akka-tools-json-serializing/README.md) is an Akka Serializer implementation that uses Jackson Json.

Json is a good match for evolving data-structures, but this serializer also supports coded data-structure-upgrading

akka-tools-jdbc-journal
-------------------------------

[akka-tools-jdbc-journal](akka-tools-jdbc-journal/README.md) is a JDBC journal-plugin for Akka Persistence.

When used together with **akka-tools-json-serializing**, it also writes the json as 'plain-text' so that a human can understand the written data.
 
It also has a special feature which allows a PersistentView to read events not just from one PersistentActor-instance,
but **read all events from a group of PersistentActors**.


akka-tools-cluster
-------------------------------

[akka-tools-cluster](akka-tools-cluster/README.md) contains cluster-related utilities like:

* dynamic seedNode-resolving
* Split-brain detection and recovering
* ClusterSingletonHelper

examples
-------------------------------

[examples](examples/README.md) contains example code
