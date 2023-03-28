//package no.nav.bidrag.aktoerregister.service
//
//import io.mockk.impl.annotations.InjectMockKs
//import io.mockk.impl.annotations.MockK
//import io.mockk.junit5.MockKExtension
//import no.nav.bidrag.aktoerregister.converter.AktoerTilAktoerDTOConverter
//import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.AdresseDTO.adresselinje1
//import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.AktoerDTO
//import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.AktoerIdDTO
//import no.nav.bidrag.aktoerregister.dto.aktoerregister.dto.KontonummerDTO.norskKontonr
//import no.nav.bidrag.aktoerregister.dto.enumer.Identtype
//import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer
//import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer.adresselinje1
//import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer.aktoerIdent
//import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer.aktoerType
//import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer.norskKontonr
//import no.nav.bidrag.aktoerregister.repository.AktoerRepositoryMock
//import no.nav.bidrag.aktoerregister.repository.HendelseRepositoryMock
//import no.nav.bidrag.aktoerregister.repository.MockDB
//import no.nav.bidrag.felles.test.data.konto.TestKontoBuilder
//import no.nav.bidrag.felles.test.data.person.TestPersonBuilder
//import no.nav.bidrag.felles.test.data.samhandler.TestSamhandler
//import no.nav.bidrag.felles.test.data.samhandler.TestSamhandlerBuilder
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.ArgumentMatchers
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.junit.jupiter.MockitoExtension
//import org.springframework.core.convert.ConversionService
//
//@ExtendWith(MockKExtension::class)
//class AktoerregisterServiceTest {
//    private val aktoerTilAktoerDTOConverter = AktoerTilAktoerDTOConverter()
//    private var mockDB: MockDB? = null
//
//    @MockK
//    private lateinit var tpsService: AktoerService
//
//    @MockK
//    private lateinit var tssService: AktoerService
//
//    @MockK
//    private lateinit var conversionService: ConversionService
//
//    @InjectMockKs
//    private lateinit var aktoerregisterService: AktoerregisterService
//
//    @BeforeEach
//    fun SetUp() {
//        mockDB = MockDB()
//        val aktoerRepository = AktoerRepositoryMock(mockDB!!)
//        val hendelseRepositoryMock = HendelseRepositoryMock(mockDB!!)
//        aktoerregisterService = AktoerregisterServiceImpl(
//            aktoerRepository, hendelseRepositoryMock, conversionService
//        )
//    }
//
//    @Test
//    fun skalHenteAktoerMedPersonnummerOgAktoerIkkeFinnes() {
//        val aktoer = opprettTPSAktoerDTOMedNorskKontonr(PERSON1.personIdent, KONTO1.norskKontonummer)
//        Mockito.`when`(tpsService!!.hentAktoer(ArgumentMatchers.any())).thenReturn(aktoer)
//        Mockito.`when`(
//            conversionService!!.convert(
//                ArgumentMatchers.any(Aktoer::class.java), ArgumentMatchers.eq(
//                    AktoerDTO::class.java
//                )
//            )
//        )
//            .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer))
//        val aktoerFromTPS = aktoerregisterService!!.hentAktoer(
//            AktoerIdDTO.builder()
//                .aktoerId(aktoer.aktoerIdent)
//                .identtype(Identtype.valueOf(aktoer.aktoerType))
//                .build()
//        )
//        Mockito.verify(tpsService, Mockito.times(1)).hentAktoer(aktoer.aktoerIdent)
//        Assertions.assertNotNull(aktoerFromTPS)
//        Assertions.assertEquals(aktoerFromTPS.kontonummer!!.norskKontonr, aktoer.norskKontonr)
//        Assertions.assertEquals(mockDB!!.aktoerMap.size, 1)
//        Assertions.assertEquals(mockDB!!.hendelseMap.size, 1)
//    }
//
//    @Test
//    fun skalHenteAktoerMedAktoernummerOgAktoerIkkeFinnes() {
//        val aktoer = opprettTSSAktoerDTO(SAMHANDLER1)
//        val aktoerIdDTO: AktoerIdDTO = AktoerIdDTO.builder()
//            .aktoerId(aktoer.aktoerIdent)
//            .identtype(Identtype.valueOf(aktoer.aktoerType))
//            .build()
//        Mockito.`when`(tssService!!.hentAktoer(ArgumentMatchers.any())).thenReturn(aktoer)
//        Mockito.`when`(
//            conversionService!!.convert(
//                ArgumentMatchers.any(Aktoer::class.java), ArgumentMatchers.eq(
//                    AktoerDTO::class.java
//                )
//            )
//        )
//            .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer))
//        val aktoerFromTSS = aktoerregisterService!!.hentAktoer(aktoerIdDTO)
//        Mockito.verify(tssService, Mockito.times(1)).hentAktoer(aktoer.aktoerIdent)
//        Assertions.assertNotNull(aktoerFromTSS)
//        Assertions.assertEquals(aktoerFromTSS.kontonummer!!.norskKontonr, aktoer.norskKontonr)
//        Assertions.assertEquals(aktoerFromTSS.adresse!!.adresselinje1, aktoer.adresselinje1)
//        Assertions.assertEquals(1, mockDB!!.aktoerMap.size)
//        Assertions.assertEquals(1, mockDB!!.hendelseMap.size)
//    }
//
//    @Test
//    fun skalOppdatereAktoer() {
//        val aktoer = opprettTSSAktoerDTO(SAMHANDLER1)
//        val aktoerIdDTO: AktoerIdDTO = AktoerIdDTO.builder()
//            .aktoerId(aktoer.aktoerIdent)
//            .identtype(Identtype.valueOf(aktoer.aktoerType))
//            .build()
//        Mockito.`when`(tssService!!.hentAktoer(ArgumentMatchers.any())).thenReturn(aktoer)
//        Mockito.`when`(
//            conversionService!!.convert(
//                ArgumentMatchers.any(Aktoer::class.java), ArgumentMatchers.eq(
//                    AktoerDTO::class.java
//                )
//            )
//        )
//            .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer))
//        aktoerregisterService!!.hentAktoer(aktoerIdDTO)
//        aktoer.adresselinje1 = "Testgate 2"
//        aktoerregisterService!!.oppdaterAktoer(aktoer)
//        Mockito.`when`(
//            conversionService.convert(
//                ArgumentMatchers.any(Aktoer::class.java), ArgumentMatchers.eq(
//                    AktoerDTO::class.java
//                )
//            )
//        )
//            .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer))
//        val (_, _, _, _, _, adresse) = aktoerregisterService!!.hentAktoer(aktoerIdDTO)
//        Mockito.verify(tssService, Mockito.times(1)).hentAktoer(aktoer.aktoerIdent)
//        Assertions.assertEquals("Testgate 2", adresse!!.adresselinje1)
//        Assertions.assertEquals(1, mockDB!!.aktoerMap.size)
//        Assertions.assertEquals(2, mockDB!!.hendelseMap.size)
//        Assertions.assertEquals(
//            SAMHANDLER1.samhandlerIdent, mockDB!!.hendelseMap[1]!!.aktoer.aktoerIdent
//        )
//        Assertions.assertEquals(
//            SAMHANDLER1.samhandlerIdent, mockDB!!.hendelseMap[2]!!.aktoer.aktoerIdent
//        )
//    }
//
//    @Test
//    fun TestHentHendelser() {
//        val aktoer = opprettTSSAktoerDTO(SAMHANDLER1)
//        val aktoerIdDTO: AktoerIdDTO = AktoerIdDTO.builder()
//            .aktoerId(aktoer.aktoerIdent)
//            .identtype(Identtype.valueOf(aktoer.aktoerType))
//            .build()
//        Mockito.`when`(tssService!!.hentAktoer(ArgumentMatchers.any())).thenReturn(aktoer)
//        Mockito.`when`(
//            conversionService!!.convert(
//                ArgumentMatchers.any(Aktoer::class.java), ArgumentMatchers.eq(
//                    AktoerDTO::class.java
//                )
//            )
//        )
//            .thenReturn(aktoerTilAktoerDTOConverter.convert(aktoer))
//        aktoerregisterService!!.hentAktoer(aktoerIdDTO)
//        aktoer.adresselinje1 = "Testgate 2"
//        aktoerregisterService!!.oppdaterAktoer(aktoer)
//        aktoer.adresselinje1 = "Testgate 3"
//        aktoerregisterService!!.oppdaterAktoer(aktoer)
//        aktoer.adresselinje1 = "Testgate 4"
//        aktoerregisterService!!.oppdaterAktoer(aktoer)
//        var hendelseDTOList = aktoerregisterService!!.hentHendelser(0, 10)
//        Assertions.assertEquals(1, hendelseDTOList.size)
//        Assertions.assertEquals(4, hendelseDTOList[0].sekvensnummer)
//        val aktoer2 = opprettTSSAktoerDTO(SAMHANDLER2)
//        val aktoerIdDTO2: AktoerIdDTO = AktoerIdDTO.builder()
//            .aktoerId(aktoer2.aktoerIdent)
//            .identtype(Identtype.valueOf(aktoer2.aktoerType))
//            .build()
//        Mockito.`when`(tssService.hentAktoer(ArgumentMatchers.any())).thenReturn(aktoer2)
//        aktoerregisterService!!.hentAktoer(aktoerIdDTO2)
//        aktoer2.adresselinje1 = "Testgate 2"
//        aktoerregisterService!!.oppdaterAktoer(aktoer2)
//        aktoer2.adresselinje1 = "Testgate 3"
//        aktoerregisterService!!.oppdaterAktoer(aktoer2)
//        aktoer2.adresselinje1 = "Testgate 4"
//        aktoerregisterService!!.oppdaterAktoer(aktoer2)
//        hendelseDTOList = aktoerregisterService!!.hentHendelser(0, 10)
//        Assertions.assertEquals(2, hendelseDTOList.size)
//        Assertions.assertEquals(4, hendelseDTOList[0].sekvensnummer)
//        Assertions.assertEquals(8, hendelseDTOList[1].sekvensnummer)
//    }
//
//    private fun opprettTPSAktoerDTOMedNorskKontonr(fnr: String, kontonummer: String): Aktoer {
//        val aktoer = Aktoer()
//        aktoer.aktoerIdent = fnr
//        aktoer.aktoerType = Identtype.PERSONNUMMER.name
//        aktoer.norskKontonr = kontonummer
//        return aktoer
//    }
//
//    private fun opprettTSSAktoerDTO(samhandler: TestSamhandler): Aktoer {
//        return Aktoer.builder()
//            .aktoerIdent(samhandler.samhandlerIdent)
//            .aktoerType(Identtype.AKTOERNUMMER.name)
//            .norskKontonr(KONTO1.norskKontonummer)
//            .adresselinje1("Testgate 1")
//            .build()
//    }
//
//    companion object {
//        private val PERSON1 = TestPersonBuilder.person().opprett()
//        private val KONTO1 = TestKontoBuilder.konto().opprett()
//        private val SAMHANDLER1 = TestSamhandlerBuilder.samhandler().opprett()
//        private val SAMHANDLER2 = TestSamhandlerBuilder.samhandler().opprett()
//    }
//}