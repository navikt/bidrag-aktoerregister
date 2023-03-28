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
class PersoninformasjonDtoTilAktoerConverter : Converter<PersonDetaljerDto, Aktør> {

    override fun convert(personinformasjon: PersonDetaljerDto): Aktør {
        return Aktør(
            aktørIdent = personinformasjon.person.ident,
            aktørType = Identtype.PERSONNUMMER.name,
            fornavn = personinformasjon.person.fornavn,
            etternavn = personinformasjon.person.etternavn,
            adresselinje1 = personinformasjon.adresse?.adresselinje1,
            adresselinje2 = personinformasjon.adresse?.adresselinje2,
            adresselinje3 = personinformasjon.adresse?.adresselinje3,
            leilighetsnummer = personinformasjon.adresse?.bruksenhetsnummer,
            postnr = personinformasjon.adresse?.postnummer,
            poststed = personinformasjon.adresse?.poststed,
            land = personinformasjon.adresse?.land,
            norskKontonr = personinformasjon.kontonummer?.norskKontonr,
            bankCode = personinformasjon.kontonummer?.bankcode,
            bankNavn = personinformasjon.kontonummer?.banknavn,
            iban = personinformasjon.kontonummer?.iban,
            bankLandkode = personinformasjon.kontonummer?.banklandkode,
            swift = personinformasjon.kontonummer?.swift,
            valutaKode = personinformasjon.kontonummer?.valutakode,
            språkkode = personinformasjon.språk,
            fødtDato = personinformasjon.person.foedselsdato,
            dødDato = personinformasjon.person.doedsdato,
            gradering = finnGradering(personinformasjon),
            tidligereIdenter = opprettTidligereIndenter(personinformasjon),
            dødsbo = opprettDodsbo(personinformasjon)
        )
    }

    private fun opprettDodsbo(personinformasjon: PersonDetaljerDto): Dødsbo? {
        return personinformasjon.dødsbo?.let {
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

    private fun opprettTidligereIndenter(personinformasjon: PersonDetaljerDto): List<TidligereIdenter> {
        return personinformasjon.tidligereIdenter?.map { TidligereIdenter(tidligereAktoerIdent = it, identtype = Identtype.PERSONNUMMER.name) } ?: emptyList()
    }

    private fun finnGradering(personinformasjon: PersonDetaljerDto): String? {
        return Gradering.from(Diskresjonskode.valueOf(personinformasjon.person.diskresjonskode))?.name
    }
}