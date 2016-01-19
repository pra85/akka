/**
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */

package docs.persistence.query;

import java.util.HashSet;
import java.util.Set;
import scala.runtime.BoxedUnit;

import akka.actor.ActorSystem;
import akka.persistence.journal.WriteEventAdapter;
import akka.persistence.journal.Tagged;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.PersistenceQuery;
import akka.persistence.query.journal.leveldb.javadsl.LeveldbReadJournal;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;

public class LeveldbPersistenceQueryDocTest {

  final ActorSystem system = ActorSystem.create();

  public void demonstrateReadJournal() {
    //#get-read-journal
    final ActorMaterializer mat = ActorMaterializer.create(system);
    
    LeveldbReadJournal queries =
      PersistenceQuery.get(system).getReadJournalFor(LeveldbReadJournal.class, 
          LeveldbReadJournal.Identifier());
    //#get-read-journal
  }
  
  public void demonstrateEventsByPersistenceId() {
    //#EventsByPersistenceId
    LeveldbReadJournal queries =
        PersistenceQuery.get(system).getReadJournalFor(LeveldbReadJournal.class, 
            LeveldbReadJournal.Identifier());
    
    Source<EventEnvelope, BoxedUnit> source =
        queries.eventsByPersistenceId("some-persistence-id", 0, Long.MAX_VALUE);
    //#EventsByPersistenceId
  }
  
  public void demonstrateAllPersistenceIds() {
    //#AllPersistenceIds
    LeveldbReadJournal queries =
        PersistenceQuery.get(system).getReadJournalFor(LeveldbReadJournal.class, 
            LeveldbReadJournal.Identifier());
    
    Source<String, BoxedUnit> source = queries.allPersistenceIds();
    //#AllPersistenceIds
  }
  
  public void demonstrateEventsByTag() {
    //#EventsByTag
    LeveldbReadJournal queries =
        PersistenceQuery.get(system).getReadJournalFor(LeveldbReadJournal.class, 
            LeveldbReadJournal.Identifier());
    
    Source<EventEnvelope, BoxedUnit> source =
        queries.eventsByTag("green", 0);
    //#EventsByTag
  }
  
  static 
  //#tagger
  public class MyTaggingEventAdapter implements WriteEventAdapter {

    @Override
    public Object toJournal(Object event) {
      if (event instanceof String) {
        String s = (String) event;
        Set<String> tags = new HashSet<String>();
        if (s.contains("green")) tags.add("green");
        if (s.contains("black")) tags.add("black");
        if (s.contains("blue")) tags.add("blue");
        if (tags.isEmpty())
          return event;
        else
          return new Tagged(event, tags);
      } else {
        return event;
      }
    }
    
    @Override
    public String manifest(Object event) {
      return "";
    }
  }
  //#tagger
}
