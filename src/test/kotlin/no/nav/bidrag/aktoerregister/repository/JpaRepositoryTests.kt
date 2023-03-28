package no.nav.bidrag.aktoerregister.repository

import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import no.nav.bidrag.aktoerregister.AktoerregisterApplicationTest
import no.nav.bidrag.aktoerregister.dto.aktoerregister.enumer.Identtype
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse
import no.nav.bidrag.aktoerregister.persistence.repository.AktørJpaRepository
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseJpaRepository
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

@SpringBootTest(classes = [AktoerregisterApplicationTest::class])
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableMockOAuth2Server
class JpaRepositoryTests {

    @Autowired
    private lateinit var hendelseJpaRepository: HendelseJpaRepository

    @Autowired
    private lateinit var aktørJpaRepository: AktørJpaRepository


    @BeforeEach
    fun Setup() {
        aktørJpaRepository.deleteAll()
    }

    @Test
    fun `Skal lagre og slette aktør med hendelser`() {
        val aktoerer = generateAktørListe(2)
        aktørJpaRepository.saveAll(aktoerer)

        var hendelser = hendelseJpaRepository.findAll()
        var lagdredeAktoerer = aktørJpaRepository.findAll()

        hendelser.size shouldBe 40
        lagdredeAktoerer.size shouldBe 20

        aktørJpaRepository.delete(lagdredeAktoerer[0])

        hendelser = hendelseJpaRepository.findAll()
        lagdredeAktoerer = aktørJpaRepository.findAll()

        hendelser.size shouldBe 38
        lagdredeAktoerer.size shouldBe 19
    }

    @Test
    fun validateHendelsePagination() {
        aktørJpaRepository.count() shouldBe 0

        val aktoerer = generateAktørListe(3)
        aktørJpaRepository.saveAll(aktoerer)
        var sisteHendelser = hendelseJpaRepository.hentHendelserMedUnikAktoer(0, Pageable.ofSize(5))

        sisteHendelser.size shouldBe 5
        sisteHendelser.map { it.aktør.aktørIdent }.shouldNotContainDuplicates().size shouldBe 5
        sisteHendelser[0].sekvensnummer shouldBeGreaterThan  0

        var lastReceivedSekvensnummer = sisteHendelser[sisteHendelser.size - 1].sekvensnummer
        sisteHendelser = hendelseJpaRepository.hentHendelserMedUnikAktoer(lastReceivedSekvensnummer + 1, Pageable.ofSize(10))

        sisteHendelser.size shouldBe 10
        sisteHendelser.map { it.aktør.aktørIdent }.shouldNotContainDuplicates().size shouldBe 10
        sisteHendelser[0].sekvensnummer shouldBeGreaterThanOrEqual lastReceivedSekvensnummer

        lastReceivedSekvensnummer = sisteHendelser[sisteHendelser.size - 1].sekvensnummer
        sisteHendelser = hendelseJpaRepository.hentHendelserMedUnikAktoer(lastReceivedSekvensnummer + 1, Pageable.ofSize(20))

        sisteHendelser.size shouldBe 5
        sisteHendelser.map { it.aktør.aktørIdent }.shouldNotContainDuplicates().size shouldBe 5
        sisteHendelser[0].sekvensnummer shouldBeGreaterThanOrEqual lastReceivedSekvensnummer
    }

    private fun generateAktørListe(antallHendelser: Int): List<Aktør> {
        val aktørListe: MutableList<Aktør> = ArrayList()
        for (i in 0..19) {
            val aktør = Aktør(
                aktørIdent = UUID.randomUUID().toString(),
                aktørType = Identtype.PERSONNUMMER.name,
                land = "Norge",
                postnr = "0682",
                poststed = "Oslo",
                adresselinje1 = "Testgate $i",
                norskKontonr = i.toString()
            )
            opprettHendelser(antallHendelser, aktør)
            aktørListe.add(aktør)
        }
        return aktørListe
    }

    private fun opprettHendelser(numberOfHendelser: Int, aktør: Aktør) {
        for (j in 0 until numberOfHendelser) {
            aktør.addHendelse(Hendelse(aktør = aktør, aktørIdent = aktør.aktørIdent))
        }
    }

    companion object {
        @Container
        var database: PostgreSQLContainer<*> = PostgreSQLContainer("postgres")
            .withDatabaseName("test_db")
            .withUsername("root")
            .withPassword("root")
            .withInitScript("db-setup.sql")

        @JvmStatic
        @DynamicPropertySource
        fun properties(propertyRegistry: DynamicPropertyRegistry) {
            propertyRegistry.add("spring.datasource.url") { database.jdbcUrl }
        }
    }
}