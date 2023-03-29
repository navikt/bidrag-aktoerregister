package no.nav.bidrag.aktoerregister.repository

import no.nav.bidrag.aktoerregister.AktoerregisterApplicationTest
import no.nav.bidrag.aktoerregister.dto.aktoerregister.enumer.Identtype
import no.nav.bidrag.aktoerregister.persistence.entities.Aktør
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse
import no.nav.bidrag.aktoerregister.persistence.repository.AktørJpaRepository
import no.nav.bidrag.aktoerregister.persistence.repository.AktørRepository
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseJpaRepository
import no.nav.bidrag.aktoerregister.persistence.repository.HendelseRepository
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions
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
import java.util.function.Consumer

@SpringBootTest(classes = [AktoerregisterApplicationTest::class])
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableMockOAuth2Server
class AktørRepositoryTest {

    @Autowired
    private lateinit var aktørRepository: AktørRepository

    @Autowired
    private lateinit var aktørJpaRepository: AktørJpaRepository

    @Autowired
    private lateinit var hendelseRepository: HendelseRepository

    @Autowired
    private lateinit var hendelseJpaRepository: HendelseJpaRepository

    @BeforeEach
    fun Setup() {
        aktørJpaRepository.deleteAll()
    }

    @Test
    fun skalTesteOpprettEllerOppdatertAktoerer() {
        val aktoerer = opprettAktoerListeMed20Aktører()
        for (aktør in aktoerer) {
            aktørRepository.opprettEllerOppdaterAktør(aktør)
        }
        var savedAktoerer = aktørJpaRepository.findAll()
        var savedHendelser = hendelseJpaRepository.findAll()
        Assertions.assertEquals(20, savedAktoerer.size)
        Assertions.assertEquals(20, savedHendelser.size)

        // Updating the same aktoerer to test that new hendelser are created
        for (aktør in aktoerer) {
            aktørRepository.opprettEllerOppdaterAktør(aktør)
        }
        savedAktoerer = aktørJpaRepository.findAll()
        savedHendelser = hendelseJpaRepository.findAll()
        Assertions.assertEquals(20, savedAktoerer.size)
        Assertions.assertEquals(40, savedHendelser.size)
    }

    @Test
    fun skalOppretteEllerOppdatereAktoerMedListe() {
        val aktoerer = opprettAktoerListeMed20Aktører()
        aktørRepository.opprettEllerOppdaterAktører(aktoerer)
        hendelseRepository.opprettHendelser(aktoerer)
        var lagredeAktoerer = aktørJpaRepository
            .findAllByAktørType(Identtype.PERSONNUMMER.name, Pageable.ofSize(100))
            .stream()
            .toList()
        var lagredeHendelser = hendelseJpaRepository.findAll()
        Assertions.assertEquals(20, lagredeAktoerer.size)
        Assertions.assertEquals(20, lagredeHendelser.size)
        val sublist: List<Aktør> = lagredeAktoerer.subList(0, 10)
        aktørRepository.opprettEllerOppdaterAktører(sublist)
        hendelseRepository.opprettHendelser(sublist)
        lagredeAktoerer = aktørJpaRepository
            .findAllByAktørType(Identtype.PERSONNUMMER.name, Pageable.ofSize(100))
            .stream()
            .toList()
        lagredeHendelser = hendelseJpaRepository.findAll()
        Assertions.assertEquals(20, lagredeAktoerer.size)
        Assertions.assertEquals(30, lagredeHendelser.size)
        val aktoerIds = sublist.stream().map(Aktør::aktørIdent).toList()
        val aktoerHendelser = lagredeHendelser.stream()
            .filter { (_, aktør): Hendelse -> aktoerIds.contains(aktør.aktørIdent) }
            .toList()
        val hendelseMap: MutableMap<String, MutableList<Hendelse>> = HashMap()
        for (hendelse in aktoerHendelser) {
            if (!hendelseMap.containsKey(hendelse.aktør.aktørIdent)) {
                hendelseMap[hendelse.aktør.aktørIdent] = ArrayList()
            }
            val hendelser = hendelseMap[hendelse.aktør.aktørIdent]!!
            hendelser.add(hendelse)
            hendelseMap[hendelse.aktør.aktørIdent] = hendelser
        }
        sublist.forEach(
            Consumer { (_, aktørIdent): Aktør -> Assertions.assertEquals(2, hendelseMap[aktørIdent]!!.size) }
        )
    }

    private fun opprettAktoerListeMed20Aktører(): List<Aktør> {
        val aktørListe: MutableList<Aktør> = ArrayList()
        for (i in 0 until 20) {
            aktørListe.add(
                Aktør(
                    aktørIdent = UUID.randomUUID().toString(),
                    aktørType = Identtype.PERSONNUMMER.name,
                    land = "Norge",
                    postnr = "0682",
                    poststed = "Oslo",
                    adresselinje1 = "Testgate $i",
                    norskKontonr = i.toString()

                )
            )
        }
        return aktørListe
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
