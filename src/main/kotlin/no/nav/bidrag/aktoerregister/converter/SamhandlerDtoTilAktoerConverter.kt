package no.nav.bidrag.aktoerregister.converter

import no.nav.bidrag.aktoerregister.dto.enumer.Identtype
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.transport.samhandler.SamhandlerDto
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class SamhandlerDtoTilAktoerConverter : Converter<SamhandlerDto, Aktør> {

    override fun convert(samhandler: SamhandlerDto): Aktør {
        return Aktør(
            aktørIdent = samhandler.tssId.verdi,
            aktørType = Identtype.AKTOERNUMMER.name,
            etternavn = samhandler.navn?.verdi,
            offentligId = samhandler.offentligId?.verdi,
            offentligIdType = samhandler.offentligIdType?.verdi,
            adresselinje1 = samhandler.adresse?.adresselinje1?.verdi,
            adresselinje2 = samhandler.adresse?.adresselinje2?.verdi,
            adresselinje3 = samhandler.adresse?.adresselinje3?.verdi,
            postnr = samhandler.adresse?.postnr?.verdi,
            poststed = samhandler.adresse?.poststed?.verdi,
            land = samhandler.adresse?.land?.verdi,
            norskKontonr = samhandler.kontonummer?.norskKontonummer?.verdi,
            bankCode = samhandler.kontonummer?.bankCode?.verdi,
            bankNavn = samhandler.kontonummer?.banknavn?.verdi,
            iban = samhandler.kontonummer?.iban?.verdi,
            bankLandkode = samhandler.kontonummer?.landkodeBank?.verdi,
            swift = samhandler.kontonummer?.swift?.verdi,
            valutaKode = samhandler.kontonummer?.valutakode?.verdi
        )
    }
}
