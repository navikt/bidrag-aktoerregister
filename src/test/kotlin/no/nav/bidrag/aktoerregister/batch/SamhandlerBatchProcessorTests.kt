package no.nav.bidrag.aktoerregister.batch

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.bidrag.aktoerregister.batch.samhandler.SamhandlerBatchProcessor
import no.nav.bidrag.aktoerregister.dto.enumer.Identtype
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.service.AktørregisterService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SamhandlerBatchProcessorTests {
    private val adresse = "Testgate 1"

    @MockK(relaxed = true)
    private lateinit var aktørregisterService: AktørregisterService

    @InjectMockKs
    private lateinit var samhandlerBatchProcessor: SamhandlerBatchProcessor

    private var aktør: Aktør? = null
    private var aktørFraTss: Aktør? = null

    @BeforeEach
    fun setUp() {
        val ident = "1234"
        aktør = Aktør(aktørIdent = ident, aktørType = Identtype.PERSONNUMMER.name)
        aktørFraTss = Aktør(
            aktørIdent = ident,
            aktørType = Identtype.PERSONNUMMER.name,
            adresselinje1 = adresse
        )
    }

    @Test
    fun skalOppdatereAktoerFraTss() {
        every { aktørregisterService.hentAktørFraSamhandler(any()) } returns aktørFraTss!!
        val tssAktoerProcessorResult = samhandlerBatchProcessor.process(aktør!!)
        tssAktoerProcessorResult shouldNotBe null
        tssAktoerProcessorResult!!.aktør shouldNotBe null
        tssAktoerProcessorResult.aktør.adresselinje1 shouldBe adresse
        tssAktoerProcessorResult.aktørStatus shouldBe AktørStatus.UPDATED
    }

    @Test
    fun skalIkkeOppdatereAktoerFraTssOgSkippe() {
        aktør!!.adresselinje1 = adresse
        every { aktørregisterService.hentAktørFraSamhandler(any()) } returns aktørFraTss!!
        val tssAktoerProcessorResult = samhandlerBatchProcessor.process(aktør!!)
        Assertions.assertNull(tssAktoerProcessorResult)
    }
}
