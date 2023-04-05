package no.nav.bidrag.aktoerregister.converter

import no.nav.bidrag.aktoerregister.dto.aktoerregister.enumer.Diskresjonskode
import no.nav.bidrag.aktoerregister.dto.aktoerregister.enumer.Gradering
import no.nav.bidrag.aktoerregister.dto.aktoerregister.enumer.Identtype
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Dødsbo
import no.nav.bidrag.aktoerregister.persistence.entities.TidligereIdenter
import no.nav.bidrag.transport.person.PersondetaljerDto
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class PersonDetaljerDtoTilAktoerConverter : Converter<PersondetaljerDto, Aktør> {

    override fun convert(personDetaljer: PersondetaljerDto): Aktør {
        return Aktør(
            aktørIdent = personDetaljer.person.ident.verdi,
            aktørType = Identtype.PERSONNUMMER.name,
            fornavn = personDetaljer.person.fornavn?.verdi,
            etternavn = personDetaljer.person.etternavn?.verdi,
            adresselinje1 = personDetaljer.adresse?.adresselinje1?.verdi,
            adresselinje2 = personDetaljer.adresse?.adresselinje2?.verdi,
            adresselinje3 = personDetaljer.adresse?.adresselinje3?.verdi,
            leilighetsnummer = personDetaljer.adresse?.bruksenhetsnummer?.verdi,
            postnr = personDetaljer.adresse?.postnummer?.verdi,
            poststed = personDetaljer.adresse?.poststed?.verdi,
            land = personDetaljer.adresse?.land?.verdi,
            norskKontonr = personDetaljer.kontonummer?.norskKontonr?.verdi,
            bankCode = personDetaljer.kontonummer?.bankkode?.verdi,
            bankNavn = personDetaljer.kontonummer?.banknavn?.verdi,
            iban = personDetaljer.kontonummer?.iban?.verdi,
            bankLandkode = personDetaljer.kontonummer?.banklandkode?.verdi,
            swift = personDetaljer.kontonummer?.swift?.verdi,
            valutaKode = personDetaljer.kontonummer?.valutakode,
            språkkode = personDetaljer.språk?.verdi,
            fødtDato = personDetaljer.person.fødselsdato?.verdi,
            dødDato = personDetaljer.person.dødsdato?.verdi,
            gradering = finnGradering(personDetaljer),
            tidligereIdenter = opprettTidligereIndenter(personDetaljer),
            dødsbo = opprettDodsbo(personDetaljer)
        )
    }

    private fun opprettDodsbo(personDetaljer: PersondetaljerDto): Dødsbo? {
        return personDetaljer.dødsbo?.let {
            Dødsbo(
                kontaktperson = it.kontaktperson.verdi,
                adresselinje1 = it.kontaktadresse.adresselinje1.verdi,
                adresselinje2 = it.kontaktadresse.adresselinje2?.verdi,
                postnr = it.kontaktadresse.postnummer.verdi,
                poststed = it.kontaktadresse.poststed.verdi,
                land = it.kontaktadresse.land3?.verdi
            )
        }
    }

    private fun opprettTidligereIndenter(personDetaljer: PersondetaljerDto): MutableSet<TidligereIdenter> {
        return personDetaljer.tidligereIdenter?.map {
            TidligereIdenter(
                tidligereAktoerIdent = it.verdi,
                identtype = Identtype.PERSONNUMMER.name
            )
        }?.toMutableSet() ?: mutableSetOf()
    }

    private fun finnGradering(personDetaljer: PersondetaljerDto): String? {
        return Gradering.from(Diskresjonskode.valueOf(personDetaljer.person.diskresjonskode?.name))?.name
    }
}
