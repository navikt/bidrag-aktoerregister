package no.nav.bidrag.aktoerregister.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import no.nav.bidrag.aktoerregister.config.RestConfig
import no.nav.bidrag.aktoerregister.dto.enumer.Identtype
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.commons.util.Kjonn
import no.nav.bidrag.commons.util.PersonidentGenerator
import no.nav.bidrag.domain.ident.Ident
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class PersonHendelseServiceTest {

    @MockK(relaxed = true)
    private lateinit var aktørService: AktørService

    @MockkBean
    private var objectMapper: ObjectMapper = RestConfig().objectMapper()

    @InjectMockKs
    private lateinit var personHendelseService: PersonHendelseService

    companion object {
        val personIdent1 = PersonidentGenerator.genererFødselsnummer(LocalDate.now().minusYears(30), Kjonn.MANN)
        val personIdent2 = PersonidentGenerator.genererFødselsnummer(LocalDate.now().minusYears(30), Kjonn.KVINNE)
        val aktør = Aktør(id = 0, aktørIdent = personIdent2, aktørType = Identtype.PERSONNUMMER.name)
        val hendelse = "{" +
            "\"aktørid\":\"123456\"," +
            "\"personidenter\":[" +
            "\"$personIdent1\"," +
            "\"$personIdent2\"" +
            "]}"
    }

    @Test
    fun `skal behandle hendelse med ny ident`() {
        every { aktørService.hentAktørFraDatabase(Ident(personIdent1)) } returns null
        every { aktørService.hentAktørFraDatabase(Ident(personIdent2)) } returns aktør
        every { aktørService.hentAktørFraPerson(Ident(personIdent2)) } returns aktør

        personHendelseService.behandleHendelse(hendelse)

        verify(exactly = 1) { aktørService.lagreEllerOppdaterAktør(aktør, personIdent2) }
    }

    @Test
    fun `skal behandle hendelse for ident som ikke finne si aktørregisteret`() {
        every { aktørService.hentAktørFraDatabase(Ident(personIdent1)) } returns null
        every { aktørService.hentAktørFraDatabase(Ident(personIdent2)) } returns null

        personHendelseService.behandleHendelse(hendelse)

        verify(exactly = 0) { aktørService.lagreEllerOppdaterAktør(aktør, personIdent2) }
    }
}
