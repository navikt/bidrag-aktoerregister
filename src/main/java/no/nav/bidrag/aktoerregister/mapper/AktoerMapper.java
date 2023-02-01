package no.nav.bidrag.aktoerregister.mapper;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public class AktoerMapper implements Mapper<AktoerDTO, Aktoer> {

  private final ModelMapper modelMapper;

  public AktoerMapper() {
    modelMapper = new ModelMapper();
    mappingForAktoerDtoTilAktoer();
    mapperingForAktoerTilAktoerDto();
  }

  @Override
  public Aktoer toPersistence(AktoerDTO aktoerDTO) {
    return modelMapper.map(aktoerDTO, Aktoer.class);
  }

  @Override
  public AktoerDTO toDomain(Aktoer aktoer) {
    return modelMapper.map(aktoer, AktoerDTO.class);
  }

  private void mapperingForAktoerTilAktoerDto() {
    Converter<String, IdenttypeDTO> converter1 =
        mappingContext -> IdenttypeDTO.valueOf(mappingContext.getSource());
    modelMapper
        .typeMap(Aktoer.class, AktoerDTO.class)
        .addMappings(
            mapper -> {
              mapper.<String>map(
                  Aktoer::getAktoerIdent,
                  (aktoerDTO, aktoerIdent) -> aktoerDTO.getAktoerId().setAktoerId(aktoerIdent));
              mapper
                  .using(converter1)
                  .<IdenttypeDTO>map(
                      Aktoer::getAktoerType,
                      (aktoerDTO, aktoerType) -> aktoerDTO.getAktoerId().setIdenttype(aktoerType));
            });
  }

  private void mappingForAktoerDtoTilAktoer() {
    Converter<IdenttypeDTO, String> converter = mappingContext -> mappingContext.getSource().name();
    modelMapper
        .typeMap(AktoerDTO.class, Aktoer.class)
        .addMappings(
            mapper -> {
              mapper.skip(Aktoer::setId);
              mapper.map(src -> src.getAktoerId().getAktoerId(), Aktoer::setAktoerIdent);
              mapper
                  .using(converter)
                  .map(src -> src.getAktoerId().getIdenttype(), Aktoer::setAktoerType);
            });
  }
}
