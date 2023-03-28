package no.nav.bidrag.aktoerregister.converter

import no.nav.bidrag.aktoerregister.dto.aktoerregister.enumer.Diskresjonskode
import no.nav.bidrag.aktoerregister.dto.aktoerregister.enumer.Gradering
import no.nav.bidrag.aktoerregister.dto.aktoerregister.enumer.Identtype
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Dødsbo
import no.nav.bidrag.aktoerregister.persistence.entities.TidligereIdenter
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import person.dto.PersonDetaljerDto

@Component
class PersonDetaljerDtoTilAktoerConverter : Converter<PersonDetaljerDto, Aktør> {

    override fun convert(personDetaljer: PersonDetaljerDto): Aktør {
        return Aktør(
            aktørIdent = personDetaljer.person.ident,
            aktørType = Identtype.PERSONNUMMER.name,
            fornavn = personDetaljer.person.fornavn,
            etternavn = personDetaljer.person.etternavn,
            adresselinje1 = personDetaljer.adresse?.adresselinje1,
            adresselinje2 = personDetaljer.adresse?.adresselinje2,
            adresselinje3 = personDetaljer.adresse?.adresselinje3,
            leilighetsnummer = personDetaljer.adresse?.bruksenhetsnummer,
            postnr = personDetaljer.adresse?.postnummer,
            poststed = personDetaljer.adresse?.poststed,
            land = personDetaljer.adresse?.land,
            norskKontonr = personDetaljer.kontonummer?.norskKontonr,
            bankCode = personDetaljer.kontonummer?.bankcode,
            bankNavn = personDetaljer.kontonummer?.banknavn,
            iban = personDetaljer.kontonummer?.iban,
            bankLandkode = personDetaljer.kontonummer?.banklandkode,
            swift = personDetaljer.kontonummer?.swift,
            valutaKode = personDetaljer.kontonummer?.valutakode,
            språkkode = personDetaljer.språk,
            fødtDato = personDetaljer.person.foedselsdato,
            dødDato = personDetaljer.person.doedsdato,
            gradering = finnGradering(personDetaljer),
            tidligereIdenter = opprettTidligereIndenter(personDetaljer),
            dødsbo = opprettDodsbo(personDetaljer)
        )
    }

    private fun opprettDodsbo(personDetaljer: PersonDetaljerDto): Dødsbo? {
        return personDetaljer.dødsbo?.let {
            Dødsbo(
                kontaktperson = it.kontaktperson,
                adresselinje1 = it.kontaktadresse.adresselinje1,
                adresselinje2 = it.kontaktadresse.adresselinje2,
                postnr = it.kontaktadresse.postnummer,
                poststed = it.kontaktadresse.poststed,
                land = it.kontaktadresse.land3,
            )
        }
    }

    private fun opprettTidligereIndenter(personDetaljer: PersonDetaljerDto): List<TidligereIdenter> {
        return personDetaljer.tidligereIdenter?.map { TidligereIdenter(tidligereAktoerIdent = it, identtype = Identtype.PERSONNUMMER.name) } ?: emptyList()
    }

    private fun finnGradering(personDetaljer: PersonDetaljerDto): String? {
        return Gradering.from(Diskresjonskode.valueOf(personDetaljer.person.diskresjonskode))?.name
    }
}