package no.nav.bidrag.aktoerregister.mapper;

public interface Mapper<DomainClass, PersistenceClass> {

  PersistenceClass toPersistence(DomainClass domainObject);

  DomainClass toDomain(PersistenceClass persistenceObject);
}
