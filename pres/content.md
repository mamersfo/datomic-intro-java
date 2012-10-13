# NoSQL Techsessie **Datomic**

---

# Topics

* Introduction
* Deconstructing the Database
* Datomic Architecture
* NoSQL, NewSQL and Datomic
* Datalog Crash Course
* Lab Exercises

---

![Datamic](resources/logo.png)

![Datamic](resources/pebbles.jpg)

---

# Rich Hickey

![TimeTravel](resources/timetravel.png)

---

# Complexity

![Simple-Made-Easy](resources/simple-made-easy.png)

Rich Hickey, at [Strangeloop 2012](http://www.infoq.com/presentations/Simple-Made-Easy)

.notes: Complected, braided together. Design means: taking things apart.

---

# Deconstructing the Database

---

# Deconstruction

* Information Model
* State Model
* Coordination Model
* Distribution Model

![Derrida](resources/derrida.gif)

<center>Jacques Derrida (1930-2004)</center>

.notes: Deconstruction also means taking things apart. The term is a contamination of 'destruction' and 'construction' and aims to take things apart but also to put things together to provide new meaning.

---

# Deconstructing the Information Model

* Traditional: relations vs. objects, impedance mismatch

![EAVT](resources/eavt.png)

* Datomic: facts, EAVT, Datoms - combined with a declarative, relational query language to store and retrieve those facts (no SQL by the way) 

---

# Deconstructing the State Model

* Traditional: update in place, contention, "the basis problem"

![Treerings](resources/tree_rings.gif)

* Datomic: Accretion of immutable facts, the database as an expanding value

.notes: quite reasonable in a time where storage is expensive, but that is not the case anymore. Over there, a place.

---

# Deconstructing the Coordination Model

* Traditional: heavy coordination for reads and writes, need to poll for novelty

![Perception](resources/eyeman.gif)

* Datomic: splits "perception" (reads) and "process" (writes), reactive - not polling

--- 

# Deconstructing the Distribution Model

* Traditional: Client-server, partitions between service providers and service requesters

![Peers](resources/peers.png)

* Datomic: Peers and Storage, and Transactors too, empower applications by coordinating change and storage

---

# Deconstructing the Database

![Deconstructed](resources/deconstructed.png)

Source: Rich Hickey, at [GOTO 2012](http://www.infoq.com/presentations/Datomic)

.notes: Basis problem with client-server, "over there"

---

# Datomic Architecture

---

# Datomic Architecture

![Architecture](resources/architecture.png)

---

# Storage Services

![AmazonDB](resources/aws.png)

![Infinispan](resources/infinispan.png)

![PostgreSQL](resources/postgresql.jpg) 

![H2](resources/h2.png)

![Riak](resources/riak.png)

---

# Indexing

* EAVT ~ Relational
* AEVT ~ Column
* VEAT ~ Reverse Indexing
* AVET ~ Range Queries

---

# Indexing

![Index Storage](resources/index-storage.png)

---

# NoSQL, NewSQL and Datomic

---

# Datomic vs. No/New/SQL

![Icons](resources/icons.png)

---

# Datomic vs. No/New/SQL

![Icons](resources/icons.png)

* Datoms
* Documents
* Graphs
* Columns
* Key-Value Pairs
* Rectangles

---

# Shimmer (SNL)

![Shimmer](resources/shimmer.png)

http://snltranscripts.jt.org/75/75ishimmer.phtml

---

# Is Datomic the new Shimmer?

Datomic may be your next:

* Document Store
* Graph Database
* Column-Family Store
* Key-Value Store
* Relational Database

(sort of)

However...

---

# Datomic vs. Document Stores

![Datom](resources/icon-datom.png) ![KV](resources/icon-doc.png)

* Documents are comparable to entities with attributes and values
* But: Datoms are not JSON Documents
* Datoms, not documents, are the unit of storage

![MongoDB](resources/mongo.png)

---

# Datomic vs. Graph Databases

![Datom](resources/icon-datom.png) ![KV](resources/icon-graph.png)

* Strong similaries, e.g. [Back To The Future with Datomic](http://architects.dzone.com/articles/back-future-datomic)
* And [blueprints-datomic-graph](https://github.com/datablend/blueprints/tree/master/blueprints-datomic-graph) implements [Blueprints API](https://github.com/tinkerpop/blueprints/wiki)
* But: Neo4j has reified edges, Datomic has reified transactions

![Neo4j](resources/neo4j.png)

---

# Datomic vs. Column-Family Stores

![Datom](resources/icon-datom.png) ![KV](resources/icon-column.png)

* Datomic AEVT indexing ~ a column store
* Sparse, irregular data, single and multi-valued attributes
* But: in Datomic, it's more straightforward to work with entities

![HBase](resources/hbase.png)

---

# Datomic vs. Key-Value Stores

![Datom](resources/icon-datom.png) ![KV](resources/icon-kv.png)

* Datomic uses DynamoDB KV-store for storage
* Datomic uses Memcached for caching
* But: Datomic adds leverage with query, transactions and consistency

![Riak](resources/riak.png)

.notes: SH on Google Groups: "(1) Datomic's distributed caching eliminates the common use case for running Redis alongside a persistent store, and (2) Redis does not seem to encourage conditional put, which is one straightforward way to map to Datomic's coordination requirements. Not carved in stone, though"

---

# Datoms vs. (New) Relational Databases

![Datom](resources/icon-datom.png) ![KV](resources/icon-rect.png)

* Transactional serial writes, using just a single writer
* But, Datomic = no rectangles = no structural rigidity
* And: not only focus on TP, also on analytics

![VoltDB](resources/voltdb.jpg)

.notes: VoltDB write throughput = 40-50x faster than traditional databases

---

# Agility

![Agility](resources/agility.png)

Source: Stuart Halloway, from "Day of Datomic" training

---

# Crash Course Datalog

---

# Datalog in 6 minutes

![Stuart Halloway](resources/stuhalloway.png)

Source: Stuart Halloway, at [EuroClojure](http://vimeo.com/45136215) @ 24:30

See also [this](http://www.datomic.com/videos.html#query) tutorial

---

# Query Anatomy

Clojure

	!clojure
	(q ('[:find ...
	      :in ...
	      :where ...]
	      input1
	      ...
	      inputN))
	
Java

	!java
	q( "[:find ...
	     :in ...
	     :where ...]",
	     input1,
	     ...,
	     inputN);

.notes: :where - constraints, :in - inputs, :find - variables to return
	
---

# Variables and Constants

Variables

* ?customer
* ?product
* ?orderId
* ?email
	
Constants

* 42
* :email
* "john"
* :order/id
* \#instant "2012-02-29"

---

# Data Pattern: E-A-V

	!html
	-------------------------------------------
	| entity | attribute | value              |
	-------------------------------------------
	| 42     | :email	  | jdoe@example.com  |
	| 43     | :email     | jane@example.com  |
	| 42     | :orders    | 107               |
	| 42     | :orders    | 141               |
	-------------------------------------------

Constrain the results returned, binds variables

	!clojure
	[?customer :email ?email]
-> jdoe@example.com, jane@example.com

	!clojure
	[42 :email ?email]
-> jdoe@example.com
	
---

# Data Pattern: E-A-V

	!html
	-------------------------------------------
	| entity | attribute | value              |
	-------------------------------------------
	| 42     | :email	  | jdoe@example.com  |
	| 43     | :email     | jane@example.com  |
	| 42     | :orders    | 107               |
	| 42     | :orders    | 141               |
	-------------------------------------------

What attributes does customer 42 have?

	!clojure
	[42 ?attribute]
-> :email, :orders

What attributes and values does customer 42 have?

	!clojure
	[42 ?attribute ?value]
-> :email - jdoe@example.com, :orders - 107, 141

--- 

# Where Clause

Where to put the data pattern?

	!clojure
	[:find ?customer
	 :where [?customer :email]]
	
Implicit Join

	!clojure
	[:find ?customer
	 :where [?customer :email]
	        [?customer :orders]]

---

# Input(s)

	!java
	import static datomic.Peer.q;
	
	q("[:find ?customer :in $ :where [?customer :id] [?customer :orders]]", 
	    db);
	
Find using $database and ?email:

	!java
	q("[:find ?customer" +
	   ":in $ ?email " +
	   ":where [?customer :email ?email]]",
	    db, "jdoe@example.com");
	
--- 

# DB and non-DB resources

	!java
	q("[:find ?a ?v :in $ :where [$ ?a ?v]]", 
	  System.getProperties());

---

# Predicates

Functional constraints that can appear in a :where clause

	!clojure
	[(< 50.0 ?price)]
	
Find the expensive items

	!clojure
	[:find ?item
	 :where [?item :item/price ?price]
	        [(< 50.0 ?price)]]
	
---

# Aggregates

The syntax is incorporated in the :find clause:

    !clojure
    [:find ?a (min ?b) (max ?b) ?c (sample 12 ?d) :where ...]

The list expressions are aggregate expressions. 
Query variables not in aggregate expressions will group the results and appear intact in the result. 

The included aggregation functions are:

* min, max
* count, count-distinct
* sum, avg, median
* variance, stddev
* rand
	
---

# Functions

	!clojure
	[(shipping ?zip ?weight) ?cost]
	
Call functions by binding inputs:

	!clojure
	[:find ?customer ?product
	 :where [?customer :shipAddress ?address]
	        [?address :zip ?zip]
	        [?product :product/weight ?weight]
	        [?product :product/price ?price]
	        [(Shipping/estimate ?zip ?weight) ?shipCost]
	        [(<= ?price ?shipCost)]]
	
Or: find me the customer/product combinations where the shipping cost dominates the product cost.

---

# Lab Exercises

---

# Soccer Players

![Whiteboard](resources/whiteboard.png)

https://github.com/mamersfo/datomic-intro-java
