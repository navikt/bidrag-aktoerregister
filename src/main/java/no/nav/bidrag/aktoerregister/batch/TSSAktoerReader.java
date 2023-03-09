package no.nav.bidrag.aktoerregister.batch;

import java.util.Collections;
import java.util.Map;
import no.nav.bidrag.aktoerregister.dto.enumer.Identtype;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerJpaRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class TSSAktoerReader extends RepositoryItemReader<Aktoer> implements ItemReader<Aktoer> {

  @Autowired
  public TSSAktoerReader(AktoerJpaRepository aktoerRepository) {
    Map<String, Direction> sorts = Collections.singletonMap("aktoerIdent", Direction.ASC);
    setRepository(aktoerRepository);
    setMethodName("findAllByAktoerType");
    setArguments(Collections.singletonList(Identtype.AKTOERNUMMER.name()));
    setPageSize(100);
    setSort(sorts);
  }
}
