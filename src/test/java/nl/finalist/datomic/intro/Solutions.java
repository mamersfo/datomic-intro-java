package nl.finalist.datomic.intro;

public class Solutions
{
    // E1: Find all entities
    String exercise1 = "[:find ?p :in $ :where [?p :name]]";
    
    // E2: Find all persons
    String exercise2 = "[:find ?p :in $ :where [?p :person/height _]]";
    
    // E3: Find team and salary for Zlatan Ibrahimovic
    String exercise3 = "[:find ?team ?salary ?instant :in $ ?n :where " +
                            "[?p :name ?n] " +
                            "[?p :player/team ?t] " + 
                            "[?t :name ?team] " +
                            "[?p :player/salary ?salary ?tx]]";
    
    // E4: List name, team and salary, ordered by salary (desc) for 2011
    // do: db.asOf(year2011)
    
    // E5: Find Twitter screenName and followersCount where followersCount > a million
    String exercise5 = "[:find ?s ?c :in $ :where [?t :twitter/screenName ?s] [?t :twitter/followersCount ?c] [(> ?c 1000000)]]";
    
    // E6: Find names of players who are following Robin van Persie on Twitter
    String exercise6 = "[:find ?name :in $ ?a1 :where " +
                            "[?p1 :name ?a1] " + 
                            "[?p1 :player/twitter.screenName ?s1] " +
                            "[?tw :twitter/screenName ?s1] " +
                            "[?tw :twitter/followers ?fs] " +
                            "[?fs :twitter/screenName ?s2] " +
                            "[?p2 :player/twitter.screenName ?s2] " +
                            "[?p2 :name ?name]]";
}
