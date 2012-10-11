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

.notes: Complected, braided together

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


---

# Deconstructing the Information Model

* Traditional: relations vs. objects, impedance mismatch

![EAVT](resources/eavt.png)

* Datomic: facts, EAVT, Datoms

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

# Datomic vs. Document Stores

![Doc](resources/icon-doc.png)

* Datoms are not JSON Documents
* Documents are comparable to entities with attributes and values
* Not the unit of storage

![MongoDB](resources/mongo.png)

---

# Datomic vs. Graph Databases

![Graph](resources/icon-graph.png)

* Neo4j has reified edges
* Datomic has reified transactions
* Example: [Back To The Future with Datomic](http://architects.dzone.com/articles/back-future-datomic)
* [blueprints-datomic-graph](https://github.com/datablend/blueprints/tree/master/blueprints-datomic-graph) implements [Blueprints API](https://github.com/tinkerpop/blueprints/wiki)

![Neo4j](resources/neo4j.png)

---

# Datomic vs. Column-Family Stores

![Column](resources/icon-column.png)

* Datomic AEVT indexing ~ a column store
* Sparse, irregular data
* Single and multi-valued attributes

![HBase](resources/hbase.png)

---

# Datomic vs. Key-Value Stores

![KV](resources/icon-kv.png)

* Key-Value Stores have no leverage
* Datomic uses KV-stores for storage
* Adds leverage with query and transactions and consistency

![Riak](resources/riak.png)

---

# Datoms vs. Rectangles

![Rect](resources/icon-rect.png)

* Datomic: No rectangles = no structural rigidity
* NewSQL: transactional writes serialized using a single writer
* VoltDB: single-threaded model, no overhead of write contention
* VoltDB: focus on TP vs. analytics

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
